package com.julian.iwakganas.model;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;

import com.julian.iwakganas.controller.GameManager;
import com.julian.iwakganas.controller.GameMapManager;

public abstract class GameObject {

    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Bitmap image;

    protected Direction lookingDirection;

    protected boolean isControlledByMouse;
    protected boolean isControlledByKeyBoard;
    protected boolean isControlledByAi;

    /**
     * When a game object should be removed from list i.e. eaten fish or special
     * fish out of screen, it is marked for destroying so it will be removed in
     * another loop to avoid concurrency problems.
     */
    private boolean isMarkedForDestroying;

    public GameObject() {
        isControlledByMouse = false;
        isControlledByKeyBoard = false;
        isControlledByAi = false;
        isMarkedForDestroying = false;
    }

    public Rect getBoundingBox() {
        return new Rect(x, y, x + width, y + height);
    }

    /**
     * Check if two objects intersect
     */
    public boolean intersects(Rect rect) {
        return Rect.intersects(getBoundingBox(), rect);
    }

    public void setDirection(Direction changeDirection) {
        if ((changeDirection == Direction.LEFT && lookingDirection == Direction.RIGHT)
                || (changeDirection == Direction.RIGHT && lookingDirection == Direction.LEFT)) {
            flipHorizontally();
            lookingDirection = changeDirection;
        }

        if ((changeDirection == Direction.UP && lookingDirection == Direction.DOWN)
                || (changeDirection == Direction.DOWN && lookingDirection == Direction.UP)) {
            flipVertically();
            lookingDirection = changeDirection;
        }
    }

    public void flipVertically() {
        if (image == null) return;
        Matrix matrix = new Matrix();
        matrix.setScale(1, -1);
        matrix.postTranslate(0, image.getHeight());
        image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
        if (lookingDirection == Direction.LEFT) {
            lookingDirection = Direction.RIGHT;
        } else if (lookingDirection == Direction.RIGHT) {
            lookingDirection = Direction.LEFT;
        }
    }

    public void flipHorizontally() {
        if (image == null) return;
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        matrix.postTranslate(image.getWidth(), 0);
        image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
        if (lookingDirection == Direction.UP) {
            lookingDirection = Direction.DOWN;
        } else if (lookingDirection == Direction.DOWN) {
            lookingDirection = Direction.UP;
        }
    }

    public abstract void move();

    public abstract void updateState(GameManager gameManager, GameMapManager gameMapManager, PlayerFish playerFish);

    public void setPositon(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean isControlledByMouse() {
        return isControlledByMouse;
    }

    public void setControlledByMouse(boolean isControlledByMouse) {
        this.isControlledByMouse = isControlledByMouse;
    }

    public boolean isControlledByKeyBoard() {
        return isControlledByKeyBoard;
    }

    public void setControlledByKeyBoard(boolean isControlledByKeyBoard) {
        this.isControlledByKeyBoard = isControlledByKeyBoard;
    }

    public boolean isControlledByAi() {
        return isControlledByAi;
    }

    public void setControlledByAi(boolean isControlledByAi) {
        this.isControlledByAi = isControlledByAi;
    }

    public boolean isMarkedForDestroying() {
        return isMarkedForDestroying;
    }

    public void setMarkedForDestroying(boolean isMarkedForDestroying) {
        this.isMarkedForDestroying = isMarkedForDestroying;
    }
}
