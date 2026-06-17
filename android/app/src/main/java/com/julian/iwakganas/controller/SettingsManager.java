package com.julian.iwakganas.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.julian.iwakganas.model.GraphicsQuality;
import com.julian.iwakganas.model.Settings;

public class SettingsManager {

    private static final String PREFS_NAME = "FeedingBobbySettings";
    private static final String KEY_GRAPHICS = "graphics_quality";
    private static final String KEY_AUDIO_ENABLED = "audio_enabled";
    private static final String KEY_AUDIO_LEVEL = "audio_level";

    private static SettingsManager instance = null;
    private Context context;
    private Settings settings;

    public static synchronized SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
    }

    public void writeSettingsToFile(Settings settings) {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_GRAPHICS, settings.getGraphicsQuality().name());
        editor.putBoolean(KEY_AUDIO_ENABLED, settings.isAudioEnabled());
        editor.putInt(KEY_AUDIO_LEVEL, settings.getAudioLevel());
        editor.apply();
        this.settings = settings;
    }

    public Settings getSettings() {
        if (settings != null) return settings;
        if (context == null) {
            return new Settings();
        }
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String graphicsStr = prefs.getString(KEY_GRAPHICS, GraphicsQuality.HIGH.name());
        boolean audioEnabled = prefs.getBoolean(KEY_AUDIO_ENABLED, true);
        int audioLevel = prefs.getInt(KEY_AUDIO_LEVEL, 100);

        GraphicsQuality quality;
        try {
            quality = GraphicsQuality.valueOf(graphicsStr);
        } catch (IllegalArgumentException e) {
            quality = GraphicsQuality.HIGH;
        }

        settings = new Settings(quality, audioEnabled, audioLevel);
        return settings;
    }
}
