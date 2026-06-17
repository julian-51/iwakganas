package com.julian.iwakganas;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.julian.iwakganas.controller.SettingsManager;
import com.julian.iwakganas.model.GraphicsQuality;
import com.julian.iwakganas.model.Settings;

public class SettingsActivity extends AppCompatActivity {

    private Switch audioSwitch;
    private SeekBar audioSeekBar;
    private RadioGroup graphicsGroup;
    private RadioButton radioLow, radioNormal, radioHigh;
    private TextView audioLevelText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);

        audioSwitch = findViewById(R.id.switch_audio);
        audioSeekBar = findViewById(R.id.seekbar_audio);
        graphicsGroup = findViewById(R.id.radio_graphics);
        radioLow = findViewById(R.id.radio_low);
        radioNormal = findViewById(R.id.radio_normal);
        radioHigh = findViewById(R.id.radio_high);
        audioLevelText = findViewById(R.id.tv_audio_level);

        // Load current settings
        Settings settings = SettingsManager.getInstance().getSettings();
        if (settings == null) settings = new Settings();

        audioSwitch.setChecked(settings.isAudioEnabled());
        audioSeekBar.setProgress(settings.getAudioLevel());
        audioLevelText.setText("Audio Level: " + settings.getAudioLevel() + "%");

        switch (settings.getGraphicsQuality()) {
            case LOW:    radioLow.setChecked(true);    break;
            case NORMAL: radioNormal.setChecked(true); break;
            case HIGH:   radioHigh.setChecked(true);   break;
        }

        audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioLevelText.setText("Audio Level: " + progress + "%");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        findViewById(R.id.btn_save_settings).setOnClickListener(v -> saveSettings());
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void saveSettings() {
        GraphicsQuality quality = GraphicsQuality.HIGH;
        int checkedId = graphicsGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radio_low) quality = GraphicsQuality.LOW;
        else if (checkedId == R.id.radio_normal) quality = GraphicsQuality.NORMAL;
        else if (checkedId == R.id.radio_high) quality = GraphicsQuality.HIGH;

        Settings settings = new Settings(quality, audioSwitch.isChecked(), audioSeekBar.getProgress());
        SettingsManager.getInstance().writeSettingsToFile(settings);
        finish();
    }
}
