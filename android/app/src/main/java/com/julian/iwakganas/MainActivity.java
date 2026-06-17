package com.julian.iwakganas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.julian.iwakganas.controller.HighScoreManager;
import com.julian.iwakganas.controller.SettingsManager;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Init managers
        SettingsManager.getInstance().init(this);
        HighScoreManager.getInstance().init(this);

        setContentView(R.layout.activity_main);

        // Set background image from assets
        RelativeLayout rootLayout = findViewById(R.id.main_root_layout);
        try {
            InputStream is = getAssets().open("backgrounds/main_menu_background.jpg");
            Bitmap bg = BitmapFactory.decodeStream(is);
            is.close();
            rootLayout.setBackground(new BitmapDrawable(getResources(), bg));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Button listeners
        findViewById(R.id.btn_new_game).setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("new_game", true);
            startActivity(intent);
        });

        findViewById(R.id.btn_high_scores).setOnClickListener(v ->
                startActivity(new Intent(this, HighScoreActivity.class)));

        findViewById(R.id.btn_help).setOnClickListener(v ->
                startActivity(new Intent(this, HelpActivity.class)));

        findViewById(R.id.btn_settings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        findViewById(R.id.btn_about).setOnClickListener(v ->
                startActivity(new Intent(this, AboutActivity.class)));

        findViewById(R.id.btn_exit).setOnClickListener(v ->
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Exit Game?")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", (d, w) -> finishAffinity())
                        .setNegativeButton("No", null)
                        .show());
    }
}
