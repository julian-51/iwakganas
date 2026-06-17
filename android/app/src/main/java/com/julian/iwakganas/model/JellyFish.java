package com.julian.iwakganas.model;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.julian.iwakganas.controller.GameManager;
import com.julian.iwakganas.controller.GameMapManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class JellyFish extends SpecialFish {

    private static final int size = 5;

    public JellyFish(Context context) {
        try {
            InputStream is = context.getAssets().open("fishes/jellyfish.png");
            image = BitmapFactory.decodeStream(is);
            is.close();
            width = image.getWidth();
            height = image.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random random = new Random();
        x = random.nextInt((GameMapManager.GAME_WIDTH - width * 2)) + width / 2;
        y = GameMapManager.GAME_HEIGHT + 1;
        lookingDirection = Direction.UP;
    }

    @Override
    public void move() {
        if (y + height < 0) {
            setMarkedForDestroying(true);
            return;
        }

        Random random = new Random();
        y -= random.nextInt(1) + 1;

        if (random.nextInt(5) == 0) {
            if (random.nextBoolean()) {
                x++;
            } else {
                x--;
            }
        }

        if (x < 5) {
            x = 5;
        }
        if (x + width > GameMapManager.GAME_WIDTH - 5) {
            x = GameMapManager.GAME_WIDTH - width - 5;
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
