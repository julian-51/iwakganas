package com.julian.iwakganas.model;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;

import com.julian.iwakganas.controller.GameManager;
import com.julian.iwakganas.controller.GameMapManager;

import java.io.IOException;
import java.io.InputStream;

public class PlayerFish extends GameObject {

    private int score = 0;
    private int size = 1;
    private int frenzy = 0;
    private int growth = 0;
    private int lives = 3;
    private boolean isCurrentlyActive = false;
    private boolean isDamaged = false;

    private Handler handler;
    private Runnable playerStartRunnable;
    private Runnable frenzyStopRunnable;

    public PlayerFish(Context context) {
        super();
        try {
            InputStream is = context.getAssets().open("fish.png");
            image = BitmapFactory.decodeStream(is);
            is.close();
            width = height = 50;
        } catch (IOException e) {
            e.printStackTrace();
        }
        isControlledByMouse = true;
        lookingDirection = Direction.LEFT;

        handler = new Handler(Looper.getMainLooper());

        playerStartRunnable = () -> {
            setCurrentlyActive(true);
            if (isDamaged) {
                isDamaged = false;
            }
        };

        frenzyStopRunnable = () -> setFrenzy(0);
    }

    public void resetForNewLevel() {
        size = 1;
        frenzy = 0;
        growth = 0;
        lives = 3;
        width = height = 50;
        isDamaged = false;
        setCurrentlyActive(false);
    }

    public void reset() {
        score = 0;
        resetForNewLevel();
    }

    @Override
    public void setPosition(Point point) {
        this.x = point.x - width / 2;
        this.y = point.y - height / 2;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getFrenzy() {
        return frenzy;
    }

    public void setFrenzy(int frenzy) {
        if (this.frenzy == 100 && frenzy != 0) {
            return;
        }
        this.frenzy = frenzy;
        if (this.frenzy > 100) {
            this.frenzy = 100;
            handler.removeCallbacks(frenzyStopRunnable);
            handler.postDelayed(frenzyStopRunnable, 5000);
        }
    }

    public int getGrowth() {
        return growth;
    }

    public void setGrowth(int growth) {
        this.growth = growth;
        if (this.growth >= 100) {
            width = height = width + 10;
            this.growth = 0;
            this.size++;
        }
    }

    public int getLives() {
        return lives;
    }

    public synchronized void setLives(int lives) {
        this.lives = lives;
    }

    public boolean isCurrentlyActive() {
        return isCurrentlyActive;
    }

    public void setCurrentlyActive(boolean isCurrentlyActive) {
        this.isCurrentlyActive = isCurrentlyActive;
        if (!this.isCurrentlyActive) {
            handler.removeCallbacks(playerStartRunnable);
            handler.postDelayed(playerStartRunnable, 3000);
        }
    }

    public boolean isDamaged() {
        return isDamaged;
    }

    public void setDamaged(boolean isDamaged) {
        this.isDamaged = isDamaged;
    }

    @Override
    public void move() {
    }

    @Override
    public void updateState(GameManager gameManager, GameMapManager gameMapManager, PlayerFish playerFish) {
    }
}
