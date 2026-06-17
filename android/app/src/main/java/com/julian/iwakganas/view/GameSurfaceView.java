package com.julian.iwakganas.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.julian.iwakganas.controller.GameMapManager;
import com.julian.iwakganas.controller.InputManager;
import com.julian.iwakganas.model.GameObject;
import com.julian.iwakganas.model.PlayerFish;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    // Virtual game resolution
    public static final int GAME_WIDTH = GameMapManager.GAME_WIDTH;
    public static final int GAME_HEIGHT = GameMapManager.GAME_HEIGHT;

    private List<GameObject> gameObjects;
    private PlayerFish playerFish;
    private Bitmap backgroundImage;
    private boolean isInLevelTransition = false;
    private long seconds;
    private long minutes;

    // Scale factors from screen to virtual resolution
    private float scaleX = 1f;
    private float scaleY = 1f;

    private GameOverListener gameOverListener;

    public interface GameOverListener {
        void onGameOver();
    }

    public GameSurfaceView(Context context) {
        super(context);
        init();
    }

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        setFocusable(true);
    }

    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // Surface ready
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        scaleX = (float) width / GAME_WIDTH;
        scaleY = (float) height / GAME_HEIGHT;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputManager.getInstance().onTouchEvent(event, scaleX, scaleY);
        return true;
    }

    public void setLevel(PlayerFish playerFish, List<GameObject> gameObjects, int level) {
        try {
            InputStream is = getContext().getAssets().open("backgrounds/level_background_" + level + ".png");
            backgroundImage = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.gameObjects = gameObjects;
        this.playerFish = playerFish;
    }

    /**
     * Draw a single frame to the SurfaceView canvas.
     * Called from the game loop thread.
     */
    public void drawFrame() {
        SurfaceHolder holder = getHolder();
        if (!holder.getSurface().isValid()) return;

        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                // Scale canvas to virtual resolution
                canvas.save();
                canvas.scale(scaleX, scaleY);
                drawGame(canvas);
                canvas.restore();
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void drawGame(Canvas canvas) {
        if (gameObjects == null || playerFish == null) return;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Draw background
        if (backgroundImage != null) {
            Rect src = new Rect(0, 0, backgroundImage.getWidth(), backgroundImage.getHeight());
            Rect dst = new Rect(0, 0, GAME_WIDTH, GAME_HEIGHT);
            canvas.drawBitmap(backgroundImage, src, dst, null);
        } else {
            canvas.drawColor(Color.CYAN);
        }

        // --- Frenzy Bar Background ---
        paint.setColor(Color.DKGRAY);
        canvas.drawRoundRect(new RectF(10, 10, 220, 50), 5, 5, paint);

        // --- Growth Bar Background ---
        canvas.drawRoundRect(new RectF(GAME_WIDTH - 220, 10, GAME_WIDTH - 10, 50), 5, 5, paint);

        // --- Frenzy Bar ---
        paint.setColor(Color.rgb(255, 165, 0)); // orange
        canvas.drawRect(15, 15, 15 + playerFish.getFrenzy() * 2, 45, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(15, 15, 215, 45, paint);
        paint.setStyle(Paint.Style.FILL);

        // Frenzy text
        paint.setColor(Color.WHITE);
        paint.setTextSize(18);
        paint.setFakeBoldText(true);
        String frenzyText = playerFish.getFrenzy() == 100 ? "IN FRENZY!!!" : "Frenzy: " + playerFish.getFrenzy();
        float frenzyTextWidth = paint.measureText(frenzyText);
        canvas.drawText(frenzyText, (225 - frenzyTextWidth) / 2, 38, paint);

        // --- Growth Bar ---
        paint.setColor(Color.BLUE);
        canvas.drawRect(GAME_WIDTH - 215, 15, GAME_WIDTH - 215 + playerFish.getGrowth() * 2, 45, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(GAME_WIDTH - 215, 15, GAME_WIDTH - 15, 45, paint);
        paint.setStyle(Paint.Style.FILL);

        // Size text
        paint.setColor(Color.WHITE);
        String sizeText = "Size: " + playerFish.getSize();
        float sizeTextWidth = paint.measureText(sizeText);
        canvas.drawText(sizeText, GAME_WIDTH - (215 + sizeTextWidth) / 2, 38, paint);

        // --- Lives ---
        paint.setColor(Color.GREEN);
        String livesText = "Lives: " + playerFish.getLives();
        float livesTextWidth = paint.measureText(livesText);
        canvas.drawText(livesText, (GAME_WIDTH - livesTextWidth) / 2, 38, paint);

        // --- Time ---
        paint.setColor(Color.BLACK);
        paint.setTextSize(14);
        String timeText = String.format("%02d : %02d", minutes, seconds % 60);
        float timeTextWidth = paint.measureText(timeText);
        canvas.drawText(timeText, (GAME_WIDTH - timeTextWidth) / 2, 55, paint);

        // --- Level Transition ---
        if (isInLevelTransition) {
            paint.setColor(Color.WHITE);
            paint.setTextSize(36);
            String levelText = "LEVEL COMPLETE!";
            float levelTextWidth = paint.measureText(levelText);
            canvas.drawText(levelText, (GAME_WIDTH - levelTextWidth) / 2, GAME_HEIGHT / 2f, paint);
        }

        // --- Draw Player Fish ---
        if (playerFish.getImage() != null) {
            Rect dst = new Rect(playerFish.getX(), playerFish.getY(),
                    playerFish.getX() + playerFish.getWidth(),
                    playerFish.getY() + playerFish.getHeight());

            if (playerFish.isCurrentlyActive()) {
                if (playerFish.getFrenzy() >= 100) {
                    // Orange frenzy tint
                    paint.setColorFilter(new PorterDuffColorFilter(
                            Color.argb(153, 255, 165, 0), PorterDuff.Mode.SRC_ATOP));
                    canvas.drawBitmap(playerFish.getImage(), null, dst, paint);
                    paint.setColorFilter(null);
                } else {
                    canvas.drawBitmap(playerFish.getImage(), null, dst, null);
                }
            } else {
                if (playerFish.isDamaged()) {
                    // Red damage tint
                    paint.setColorFilter(new PorterDuffColorFilter(
                            Color.argb(128, 255, 0, 0), PorterDuff.Mode.SRC_ATOP));
                    canvas.drawBitmap(playerFish.getImage(), null, dst, paint);
                    paint.setColorFilter(null);
                } else {
                    // Semi-transparent (respawning)
                    paint.setAlpha(128);
                    canvas.drawBitmap(playerFish.getImage(), null, dst, paint);
                    paint.setAlpha(255);
                }
            }
        }

        // --- Draw Game Objects ---
        for (GameObject gameObject : gameObjects) {
            if (gameObject.getImage() != null) {
                Rect dst = new Rect(gameObject.getX(), gameObject.getY(),
                        gameObject.getX() + gameObject.getWidth(),
                        gameObject.getY() + gameObject.getHeight());
                canvas.drawBitmap(gameObject.getImage(), null, dst, null);
            }
        }
    }

    public void showGameOverScreen() {
        if (gameOverListener != null) {
            post(() -> gameOverListener.onGameOver());
        }
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public void setInLevelTransition(boolean isInLevelTransition) {
        this.isInLevelTransition = isInLevelTransition;
    }
}
