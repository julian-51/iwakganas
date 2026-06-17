package com.julian.iwakganas;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.julian.iwakganas.controller.HighScoreManager;
import com.julian.iwakganas.model.HighScore;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HighScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_high_score);

        LinearLayout scoresContainer = findViewById(R.id.scores_container);
        List<HighScore> scores = HighScoreManager.getInstance().getHighScores();
        Collections.sort(scores);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        if (scores.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No high scores yet. Play a game!");
            empty.setTextSize(18);
            empty.setPadding(16, 16, 16, 16);
            scoresContainer.addView(empty);
        } else {
            for (int i = 0; i < scores.size(); i++) {
                HighScore hs = scores.get(i);
                TextView row = new TextView(this);
                String dateStr = hs.getDate() != null ? sdf.format(hs.getDate()) : "N/A";
                row.setText(String.format(Locale.getDefault(),
                        "%d.  %s  —  %d pts  (%s)",
                        i + 1, hs.getName(), hs.getScore(), dateStr));
                row.setTextSize(16);
                row.setPadding(16, 12, 16, 12);
                scoresContainer.addView(row);
            }
        }

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
}
