package com.julian.iwakganas.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.julian.iwakganas.model.HighScore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HighScoreManager {

    private static final String PREFS_NAME = "FeedingBobbyHighScores";
    private static final String KEY_SCORES = "high_scores";

    private static HighScoreManager instance = null;
    private Context context;
    private List<HighScore> highScores = new ArrayList<>();

    public static synchronized HighScoreManager getInstance() {
        if (instance == null) {
            instance = new HighScoreManager();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
    }

    public void writeScoreToFile(HighScore highScore) {
        if (context == null) return;
        highScores.add(highScore);
        try {
            JSONArray jsonArray = new JSONArray();
            for (HighScore hs : highScores) {
                JSONObject obj = new JSONObject();
                obj.put("name", hs.getName());
                obj.put("score", hs.getScore());
                obj.put("date", hs.getDate() != null ? hs.getDate().getTime() : 0);
                jsonArray.put(obj);
            }
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_SCORES, jsonArray.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<HighScore> getHighScores() {
        if (context == null) return highScores;
        highScores.clear();
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String json = prefs.getString(KEY_SCORES, "[]");
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                HighScore hs = new HighScore();
                hs.setName(obj.getString("name"));
                hs.setScore(obj.getInt("score"));
                hs.setDate(new Date(obj.getLong("date")));
                highScores.add(hs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return highScores;
    }
}
