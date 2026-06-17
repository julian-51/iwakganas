package com.julian.iwakganas.model;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.julian.iwakganas.controller.GameManager;
import com.julian.iwakganas.controller.GameMapManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class PufferFish extends SpecialFish {

    private static final int size = 5;

    public PufferFish(Context context) {
        try {
            InputStream is = context.getAssets().open("fishes/pufferfish.png");
            image = BitmapFactory.decodeStream(is);
            is.close();
            width = image.getWidth();
            height = image.getHeight();
            lookingDirection = Direction.LEFT;
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random random = new Random();
        if (random.nextBoolean()) {
            x = -width;
            y = random.nextInt(GameMapManager.GAME_HEIGHT - height * 2) + height / 2;
            setDirection(Direction.RIGHT);
        } else {
            x = GameMapManager.GAME_WIDTH + width;
            y = random.nextInt(GameMapManager.GAME_HEIGHT - height * 2) + height / 2;
            setDirection(Direction.LEFT);
        }
    }

    @Override
    public void move() {
        if (y + height < 0 || y > GameMapManager.GAME_HEIGHT || x + width + 5 < 0
                || x - width - 5 > GameMapManager.GAME_WIDTH) {
            setMarkedForDestroying(true);
            return;
        }

        Random random = new Random();
        if (lookingDirection == Direction.LEFT) {
            x -= random.nextInt(1) + 1;
        } else {
            x += random.nextInt(1) + 1;
        }

        if (random.nextInt(5) == 0) {
            if (random.nextBoolean()) {
                y++;
            } else {
                y--;
            }
        }
    }

    @Override
    public void updateState(GameManager gameManager, GameMapManager gameMapManager, PlayerFish playerFish) {
        if (playerFish.getFrenzy() == 100) {
            playerFish.setScore(playerFish.getScore() + size * 5);
            setMarkedForDestroying(true);
        } else {
            playerFish.setLives(playerFish.getLives() - 1);
            playerFish.setFrenzy(0);
            playerFish.setDamaged(true);
            playerFish.setCurrentlyActive(false);
        }
    }
}
