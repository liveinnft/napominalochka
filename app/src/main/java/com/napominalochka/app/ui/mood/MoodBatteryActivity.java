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
import com.napominalochka.app.config.AppTexts;
import com.napominalochka.app.utils.SharedPrefsManager;

import java.util.Random;

public class MoodBatteryActivity extends AppCompatActivity {

    private ProgressBar batteryProgressBar;
    private TextView loveLevelText;
    private TextView batteryStatusText;
    private Button chargeButton;
    
    private SharedPrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_battery);

        prefsManager = new SharedPrefsManager(this);

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
        // Battery now decays in background automatically
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
            statusText = "коть, мне нужна подзарядочка от тебя!! 💔";
            batteryStatusText.setTextColor(getColor(R.color.battery_low));
        } else if (level < 50) {
            statusText = "котенок, дай немножко любви пожалуйста 💛";
            batteryStatusText.setTextColor(getColor(R.color.battery_medium));
        } else if (level < 75) {
            statusText = "кис, так хорошо!! я чувствую твою любовь 💚";
            batteryStatusText.setTextColor(getColor(R.color.battery_high));
        } else {
            statusText = "я до одурения заряжен твоей любовью!! 💖";
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
            
            // Charge battery by 2%
            prefsManager.chargeBattery();
            updateBatteryDisplay(prefsManager.getLoveLevel());
                
            // Add cooldown to prevent spam (2 seconds)
            chargeButton.setEnabled(false);
            chargeButton.postDelayed(() -> chargeButton.setEnabled(true), 2000);
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
        String randomMessage = AppTexts.LOVE_MESSAGES[new Random().nextInt(AppTexts.LOVE_MESSAGES.length)];
        
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.charge_message_title))
                .setMessage(randomMessage)
                .setPositiveButton(getString(R.string.ok), null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update display when returning to activity (battery may have decayed)
        int currentLevel = prefsManager.getLoveLevel();
        updateBatteryDisplay(currentLevel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Background decay continues automatically via SharedPreferences
    }
}