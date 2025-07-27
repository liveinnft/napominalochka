package com.napominalochka.app.ui.mood;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.napominalochka.app.R;
import com.napominalochka.app.data.MoodBatteryData;
import com.napominalochka.app.utils.SharedPrefsManager;

import java.util.Random;

public class MoodBatteryActivity extends AppCompatActivity {

    private ProgressBar batteryProgressBar;
    private TextView loveLevelText;
    private TextView batteryStatusText;
    private Button chargeButton;
    
    private SharedPrefsManager prefsManager;
    private MoodBatteryData moodData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_battery);

        prefsManager = new SharedPrefsManager(this);
        moodData = new MoodBatteryData();

        initViews();
        setupBatteryLevel();
        setupChargeButton();
    }

    private void initViews() {
        batteryProgressBar = findViewById(R.id.battery_progress_bar);
        loveLevelText = findViewById(R.id.love_level_text);
        batteryStatusText = findViewById(R.id.battery_status_text);
        chargeButton = findViewById(R.id.charge_button);
    }

    private void setupBatteryLevel() {
        int currentLevel = prefsManager.getLoveLevel();
        updateBatteryDisplay(currentLevel);
    }

    private void updateBatteryDisplay(int level) {
        // Animate progress bar
        ObjectAnimator animator = ObjectAnimator.ofInt(batteryProgressBar, "progress", level);
        animator.setDuration(1000);
        animator.start();

        // Update text
        loveLevelText.setText(getString(R.string.love_level, level));
        
        // Update status text based on level
        String statusText;
        if (level < 25) {
            statusText = "Срочно нужна подзарядка! 💔";
            batteryStatusText.setTextColor(getColor(R.color.battery_low));
        } else if (level < 50) {
            statusText = "Требуется немного любви 💛";
            batteryStatusText.setTextColor(getColor(R.color.battery_medium));
        } else if (level < 75) {
            statusText = "Хороший уровень любви! 💚";
            batteryStatusText.setTextColor(getColor(R.color.battery_high));
        } else {
            statusText = "Переполнен любовью! 💖";
            batteryStatusText.setTextColor(getColor(R.color.battery_full));
        }
        batteryStatusText.setText(statusText);
    }

    private void setupChargeButton() {
        chargeButton.setOnClickListener(v -> {
            // Prevent spam clicking
            if (!chargeButton.isEnabled()) return;
            
            // Add charge animation
            animateChargeButton();
            
            // Show random love message
            showLoveMessage();
            
            // Increase love level with limit
            int currentLevel = prefsManager.getLoveLevel();
            int increase = new Random().nextInt(20) + 10;
            int newLevel = Math.min(100, currentLevel + increase);
            
            // Only update if actually increased
            if (newLevel > currentLevel) {
                prefsManager.setLoveLevel(newLevel);
                updateBatteryDisplay(newLevel);
                
                // Add cooldown to prevent spam (2 seconds)
                chargeButton.setEnabled(false);
                chargeButton.postDelayed(() -> chargeButton.setEnabled(true), 2000);
            }
        });
    }

    private void animateChargeButton() {
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1.0f, 1.2f, 1.0f);
        scaleAnimator.setDuration(300);
        scaleAnimator.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();
            chargeButton.setScaleX(scale);
            chargeButton.setScaleY(scale);
        });
        scaleAnimator.start();
    }

    private void showLoveMessage() {
        String[] messages = moodData.getLoveMessages();
        String randomMessage = messages[new Random().nextInt(messages.length)];
        
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.charge_message_title))
                .setMessage(randomMessage)
                .setPositiveButton(getString(R.string.ok), null)
                .show();
    }
}