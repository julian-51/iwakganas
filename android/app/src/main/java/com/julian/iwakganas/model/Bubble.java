package com.julian.iwakganas.model;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.julian.iwakganas.controller.GameManager;
import com.julian.iwakganas.controller.GameMapManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class Bubble extends GameObject {

    private Random random;

    public Bubble(Context context) {
        try {
            InputStream is = context.getAssets().open("bubble.png");
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        random = new Random();
        isControlledByAi = true;
    }

    @Override
    public void move() {
        if (y + height < 0) {
            y = GameMapManager.GAME_HEIGHT + height;
            x = random.nextInt((GameMapManager.GAME_WIDTH - width * 2)) + width / 2;
            return;
        }

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
    }
}
