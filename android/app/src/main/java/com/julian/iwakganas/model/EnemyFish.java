package com.julian.iwakganas.model;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.julian.iwakganas.controller.GameManager;
import com.julian.iwakganas.controller.GameMapManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class EnemyFish extends Fish {

    private int size;

    public EnemyFish(Context context, int size) {
        this.size = size;
        try {
            InputStream is = context.getAssets().open("fishes/enemy_fish_" + size + ".png");
            image = BitmapFactory.decodeStream(is);
            is.close();
            width = image.getWidth();
            height = image.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lookingDirection = Direction.LEFT;
    }

    public EnemyFish(Context context, int size, Point position) {
        this(context, size);
        this.x = position.x;
        this.y = position.y;
    }

    @Override
    public void move() {
        Random random = new Random();
        if (lookingDirection == Direction.LEFT) {
            x -= random.nextInt(1) + 1;
        } else if (lookingDirection == Direction.RIGHT) {
            x += random.nextInt(1) + 1;
        }

        if (random.nextInt(5) == 0) {
            if (random.nextBoolean()) {
                y++;
            } else {
                y--;
            }
        }

        if (x > GameMapManager.GAME_WIDTH) {
            x = -width;
        }
        if (x + width < 0) {
            x = GameMapManager.GAME_WIDTH;
        } else {
            if (y > GameMapManager.GAME_HEIGHT || y + height < 0) {
                setMarkedForDestroying(true);
            }
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public void updateState(GameManager gameManager, GameMapManager gameMapManager, PlayerFish playerFish) {
        if (size < playerFish.getSize() || playerFish.getFrenzy() == 100) {
            playerFish.setGrowth(playerFish.getGrowth() + (size + 1) * 5);
            playerFish.setFrenzy(playerFish.getFrenzy() + (size + 1) * 2);
            playerFish.setScore(playerFish.getScore() + (size + 1) * 5);
            setMarkedForDestroying(true);
        } else {
            playerFish.setLives(playerFish.getLives() - 1);
            playerFish.setFrenzy(0);
            playerFish.setDamaged(true);
            playerFish.setCurrentlyActive(false);
        }
    }
}
