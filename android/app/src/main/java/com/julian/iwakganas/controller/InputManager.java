package com.julian.iwakganas.controller;

import android.graphics.Point;
import android.view.MotionEvent;

import com.julian.iwakganas.model.Direction;

public class InputManager {

    private static InputManager instance = null;

    public static synchronized InputManager getInstance() {
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }

    private Point touchPoint;
    private Direction changeDirection;

    public InputManager() {
        touchPoint = new Point(400, 300); // default center
        changeDirection = Direction.RIGHT;
    }

    public Point getMousePoint() {
        return touchPoint;
    }

    public Direction getChangeDirection() {
        return changeDirection;
    }

    /**
     * Called from the GameSurfaceView touch handler
     */
    public void onTouchEvent(MotionEvent event, float scaleX, float scaleY) {
        int gameX = (int) (event.getX() / scaleX);
        int gameY = (int) (event.getY() / scaleY);

        if (touchPoint.x > gameX) {
            changeDirection = Direction.LEFT;
        } else {
            changeDirection = Direction.RIGHT;
        }

        touchPoint.set(gameX, gameY);
    }

    public void reset() {
        touchPoint = new Point(400, 300);
        changeDirection = Direction.RIGHT;
    }
}
