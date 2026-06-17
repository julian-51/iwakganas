package com.julian.iwakganas;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.julian.iwakganas.controller.GameManager;
import com.julian.iwakganas.controller.HighScoreManager;
import com.julian.iwakganas.model.HighScore;
import com.julian.iwakganas.view.GameSurfaceView;

import java.util.Date;

public class GameActivity extends AppCompatActivity {

    private GameSurfaceView gameSurfaceView;
    private GameManager gameManager;
    private LinearLayout pauseOverlay;
    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Keep screen on and go fullscreen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);

        gameSurfaceView = findViewById(R.id.game_surface_view);
        pauseOverlay = findViewById(R.id.pause_overlay);

        gameManager = new GameManager(this, gameSurfaceView);

        gameSurfaceView.setGameOverListener(() -> runOnUiThread(this::showGameOverDialog));

        // Pause button
        findViewById(R.id.btn_pause).setOnClickListener(v -> togglePause());

        // Pause overlay buttons
        findViewById(R.id.btn_resume).setOnClickListener(v -> togglePause());
        findViewById(R.id.btn_restart).setOnClickListener(v -> {
            pauseOverlay.setVisibility(View.GONE);
            isPaused = false;
            gameManager.startGame(true);
        });
        findViewById(R.id.btn_quit_to_menu).setOnClickListener(v -> {
            gameManager.stopGame();
            finish();
        });

        boolean isNewGame = getIntent().getBooleanExtra("new_game", true);
        gameManager.startGame(isNewGame);
    }

    private void togglePause() {
        if (isPaused) {
            isPaused = false;
            pauseOverlay.setVisibility(View.GONE);
            gameManager.resumeGame();
        } else {
            isPaused = true;
            pauseOverlay.setVisibility(View.VISIBLE);
            gameManager.pauseGame();
        }
    }

    private void showGameOverDialog() {
        int score = gameManager.getScore();

        // Ask for player name
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Enter your name");

        new AlertDialog.Builder(this)
                .setTitle("Game Over!")
                .setMessage("Your score: " + score)
                .setView(input)
                .setPositiveButton("Save Score", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) name = "Player";
                    HighScore hs = new HighScore(name, score, new Date());
                    HighScoreManager.getInstance().writeScoreToFile(hs);
                    finish();
                })
                .setNegativeButton("Skip", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isPaused) {
            gameManager.pauseGame();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isPaused) {
            gameManager.resumeGame();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameManager.stopGame();
    }

    @Override
    public void onBackPressed() {
        togglePause();
    }
}
