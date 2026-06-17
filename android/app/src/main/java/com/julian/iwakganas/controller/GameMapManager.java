package com.julian.iwakganas.controller;

import android.content.Context;

import com.julian.iwakganas.model.Bubble;
import com.julian.iwakganas.model.Direction;
import com.julian.iwakganas.model.EnemyFish;
import com.julian.iwakganas.model.GameObject;
import com.julian.iwakganas.model.JellyFish;
import com.julian.iwakganas.model.PufferFish;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameMapManager {

    // Virtual game resolution (same as original desktop game)
    public static final int GAME_WIDTH = 800;
    public static final int GAME_HEIGHT = 600;

    private int level = 0;
    private int initialNumberOfFish = 4;
    private Context context;

    public GameMapManager(Context context) {
        this.context = context;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getNumberOfFish() {
        return initialNumberOfFish;
    }

    public void setNumberOfFish(int numberOfFish) {
        this.initialNumberOfFish = numberOfFish;
    }

    public GameObject getSpecialFish() {
        Random random = new Random();
        switch (random.nextInt(3)) {
            case 0:
                return new JellyFish(context);
            case 1:
                return new PufferFish(context);
        }
        return null;
    }

    public List<GameObject> getMapObjects() {
        level++;
        initialNumberOfFish += 2;

        List<GameObject> initialGameObjects = new ArrayList<>();
        int spaceBetweenFish = GAME_HEIGHT / (initialNumberOfFish + 1);

        for (int i = 0; i < initialNumberOfFish; i++) {
            boolean onLeft = i % 2 == 0;
            EnemyFish enemyFish = new EnemyFish(context, 0);
            if (onLeft) {
                enemyFish.setPositon(-enemyFish.getWidth(), (i + 1) * spaceBetweenFish);
                enemyFish.setDirection(Direction.RIGHT);
            } else {
                enemyFish.setPositon(GAME_WIDTH + enemyFish.getWidth(), i * spaceBetweenFish + 10);
                enemyFish.setDirection(Direction.LEFT);
            }
            initialGameObjects.add(enemyFish);
        }
        return initialGameObjects;
    }

    public GameObject getNewEnemyFish(List<GameObject> gameObjects, int size) {
        Random random = new Random();

        int eatableNumbers = 0;
        for (GameObject gameObject : gameObjects) {
            if (gameObject instanceof EnemyFish && ((EnemyFish) gameObject).getSize() < size) {
                eatableNumbers++;
            }
        }

        EnemyFish enemyFish;
        if (eatableNumbers >= 2) {
            enemyFish = new EnemyFish(context, random.nextInt(size + 1));
        } else {
            enemyFish = new EnemyFish(context, random.nextInt(Math.max(size, 1)));
        }

        if (random.nextBoolean()) {
            enemyFish.setPositon(-enemyFish.getWidth(), random.nextInt(GAME_HEIGHT) + 10);
            enemyFish.setDirection(Direction.RIGHT);
        } else {
            enemyFish.setPositon(GAME_WIDTH + enemyFish.getWidth(), random.nextInt(GAME_HEIGHT) + 10);
            enemyFish.setDirection(Direction.LEFT);
        }

        return enemyFish;
    }

    public List<GameObject> addBubbles() {
        List<GameObject> bubbles = new ArrayList<>();
        Random random = new Random();
        int bubbleSize = random.nextInt(7) + 7;
        int y = GAME_HEIGHT + bubbleSize;
        int x = random.nextInt((GAME_WIDTH - bubbleSize * 2)) + bubbleSize / 2;
        for (int i = 0; i < random.nextInt(4) + 2; i++) {
            Bubble bubble = new Bubble(context);
            bubble.setWidth(bubbleSize);
            bubble.setHeight(bubbleSize);
            bubble.setX(x);
            bubble.setY(y + i * bubbleSize + 3);
            bubbles.add(bubble);
        }
        return bubbles;
    }
}
