package com.julian.iwakganas.controller;

import android.content.Context;

import com.julian.iwakganas.model.Bubble;
import com.julian.iwakganas.model.GameObject;
import com.julian.iwakganas.model.PlayerFish;
import com.julian.iwakganas.view.GameSurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameManager {

    // Cap game at 40 FPS
    private static final long REFRESH_INTERVAL_MS = 25;

    private long startTime;
    private boolean isGameRunning = false;
    private boolean isGamePaused = false;
    private boolean isGameOver = false;
    private boolean isInLevelTransition = false;
    private boolean readyForSpecialFish = false;
    private boolean shouldAddBubble = false;

    private GameSurfaceView gameSurfaceView;
    private GameMapManager gameMapManager;
    private Thread gameThread;
    private List<GameObject> gameObjects = new ArrayList<>();
    private PlayerFish playerFish;
    private Context context;

    public GameManager(Context context, GameSurfaceView gameSurfaceView) {
        this.context = context;
        this.gameSurfaceView = gameSurfaceView;
        gameMapManager = new GameMapManager(context);
    }

    public void initialize() {
        isGameRunning = true;
        isGamePaused = false;
        isGameOver = false;

        gameObjects.clear();
        List<GameObject> mapObjects = gameMapManager.getMapObjects();
        if (mapObjects != null) {
            gameObjects.addAll(mapObjects);
        }

        if (playerFish == null) {
            playerFish = new PlayerFish(context);
        } else if (isInLevelTransition) {
            playerFish.resetForNewLevel();
            isInLevelTransition = false;
        } else {
            playerFish.reset();
        }

        gameSurfaceView.setLevel(playerFish, gameObjects, gameMapManager.getLevel());
    }

    public void startGame(boolean isNewGame) {
        if (isNewGame) {
            playerFish = new PlayerFish(context);
            gameMapManager.setLevel(0);
            gameMapManager.setNumberOfFish(4);
        }
        initialize();
        if (gameThread != null && gameThread.isAlive()) {
            gameThread.interrupt();
        }
        gameThread = new Thread(new GameLoop());
        gameThread.start();
        playerFish.setCurrentlyActive(false);
    }

    public void stopGame() {
        isGamePaused = false;
        isGameRunning = false;
    }

    public void pauseGame() {
        isGamePaused = true;
    }

    public void resumeGame() {
        isGamePaused = false;
    }

    private class GameLoop implements Runnable {

        @Override
        public void run() {
            startTime = System.currentTimeMillis();

            while (isGameRunning) {

                while (isGamePaused) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                if (checkGameState()) {
                    continue;
                }

                long oneTickOfGame = System.currentTimeMillis();

                handleInput();

                if (playerFish.isCurrentlyActive()) {
                    handleLogic();
                }

                handleDrawing();

                long elapsed = System.currentTimeMillis() - oneTickOfGame;
                if (REFRESH_INTERVAL_MS - elapsed > 0) {
                    try {
                        Thread.sleep(REFRESH_INTERVAL_MS - elapsed);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            if (playerFish != null && playerFish.getLives() == 0) {
                gameSurfaceView.showGameOverScreen();
            }
        }
    }

    private synchronized boolean checkGameState() {
        if (playerFish.getLives() == 0) {
            isGamePaused = false;
            isGameRunning = false;
            isGameOver = true;
            return true;
        } else if (playerFish.getSize() == 8) {
            isGamePaused = false;
            isGameRunning = true;
            isGameOver = false;
            isInLevelTransition = true;
            gameSurfaceView.setInLevelTransition(true);
            gameSurfaceView.drawFrame();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return true;
            }
            gameSurfaceView.setInLevelTransition(false);
            initialize();
            return true;
        }
        return false;
    }

    private synchronized void handleInput() {
        playerFish.setDirection(InputManager.getInstance().getChangeDirection());
        playerFish.setPosition(InputManager.getInstance().getMousePoint());

        for (GameObject gameObject : gameObjects) {
            if (gameObject.isControlledByMouse()) {
                gameObject.setDirection(InputManager.getInstance().getChangeDirection());
                gameObject.setPosition(InputManager.getInstance().getMousePoint());
            } else if (gameObject.isControlledByAi()) {
                gameObject.move();
            }
        }
    }

    private synchronized void handleLogic() {
        boolean shouldAddNewEnemyFish = false;

        Iterator<GameObject> iterator = gameObjects.iterator();
        while (iterator.hasNext()) {
            GameObject gameObject = iterator.next();
            if (gameObject.isMarkedForDestroying()) {
                iterator.remove();
                shouldAddNewEnemyFish = true;
            }
        }

        for (GameObject gameObject : gameObjects) {
            if (gameObject.intersects(playerFish.getBoundingBox())) {
                gameObject.updateState(this, gameMapManager, playerFish);
            }
        }

        if (shouldAddNewEnemyFish) {
            gameObjects.add(gameMapManager.getNewEnemyFish(gameObjects, playerFish.getSize()));
        }

        long elapsedSeconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime);

        if (elapsedSeconds % 10 == 0 && !readyForSpecialFish) {
            GameObject specialFish = gameMapManager.getSpecialFish();
            if (specialFish != null) {
                gameObjects.add(specialFish);
                readyForSpecialFish = true;
            }
        } else if (elapsedSeconds % 10 != 0) {
            readyForSpecialFish = false;
        }

        long bubbleCount = 0;
        for (GameObject go : gameObjects) {
            if (go instanceof Bubble) bubbleCount++;
        }

        if (elapsedSeconds % 5 == 0 && bubbleCount < 20 && !shouldAddBubble) {
            gameObjects.addAll(gameMapManager.addBubbles());
            shouldAddBubble = true;
        } else if (elapsedSeconds % 5 != 0) {
            shouldAddBubble = false;
        }
    }

    private synchronized void handleDrawing() {
        long currentTime = System.currentTimeMillis() - startTime;
        gameSurfaceView.setMinutes(TimeUnit.MILLISECONDS.toMinutes(currentTime));
        gameSurfaceView.setSeconds(TimeUnit.MILLISECONDS.toSeconds(currentTime));
        gameSurfaceView.drawFrame();
    }

    public synchronized void addNewObject(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

    public int getScore() {
        return playerFish != null ? playerFish.getScore() : 0;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public boolean isGamePaused() {
        return isGamePaused;
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }
}
