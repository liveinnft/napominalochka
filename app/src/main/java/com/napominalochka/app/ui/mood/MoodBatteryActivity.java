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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Батарейка настроения");
        }
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
        // Start active decay while app is open
        startActiveBatteryDecay();
    }

    private void startActiveBatteryDecay() {
        // Active decay every 10 seconds while app is open
        batteryProgressBar.postDelayed(activeDecayRunnable, 10000);
    }

    private final Runnable activeDecayRunnable = new Runnable() {
        @Override
        public void run() {
            int currentLevel = prefsManager.getLoveLevel();
            if (currentLevel > 0) {
                int newLevel = Math.max(0, currentLevel - 1); // Decrease by 1% every 10 seconds
                prefsManager.setLoveLevel(newLevel);
                updateBatteryDisplay(newLevel);

                // Schedule next decay
                batteryProgressBar.postDelayed(this, 10000);
            } else {
                // If battery is at 0%, still schedule next check in case it gets charged
                batteryProgressBar.postDelayed(this, 10000);
            }
        }
    };

    private void updateBatteryDisplay(int level) {
        // Animate progress bar
        ObjectAnimator animator = ObjectAnimator.ofInt(batteryProgressBar, "progress", level);
        animator.setDuration(1000);
        animator.start();

        // Update text
        loveLevelText.setText("Уровень любви: " + level + "%");

        // Update status text based on level
        String statusText;
        int textColor;

        if (level < 25) {
            statusText = "коть, мне нужна подзарядочка от тебя!! 💔";
            textColor = getColor(android.R.color.holo_red_light);
        } else if (level < 50) {
            statusText = "котенок, дай немножко любви пожалуйста 💛";
            textColor = getColor(android.R.color.holo_orange_light);
        } else if (level < 75) {
            statusText = "кис, так хорошо!! я чувствую твою любовь 💚";
            textColor = getColor(android.R.color.holo_green_light);
        } else {
            statusText = "я до одурения заряжен твоей любовью!! 💖";
            textColor = getColor(android.R.color.holo_blue_light);
        }

        batteryStatusText.setText(statusText);
        batteryStatusText.setTextColor(textColor);
    }

    private void setupChargeButton() {
        chargeButton.setOnClickListener(v -> {
            // Add charge animation
            animateChargeButton();

            // Get current level before charging
            int currentLevel = prefsManager.getLoveLevel();

            // Charge battery by 2%
            prefsManager.chargeBattery();
            int newLevel = prefsManager.getLoveLevel();
            updateBatteryDisplay(newLevel);

            // Show love message only when battery reaches 100%
            if (currentLevel < 100 && newLevel >= 100) {
                showLoveMessage();
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
        // Используем встроенные сообщения или fallback
        String randomMessage;
        try {
            randomMessage = AppTexts.LOVE_MESSAGES[new Random().nextInt(AppTexts.LOVE_MESSAGES.length)];
        } catch (Exception e) {
            // Fallback сообщения если AppTexts недоступен
            String[] fallbackMessages = {
                    "🤍 коткночек мой любимый, ты у меня самая самая!!",
                    "💕 мы со всем всем справимся, я клянусь!!",
                    "🌟 ты моя огромная умничка и я горжусь тобой прям!!",
                    "🫂 кис, я тебя не оставлю какая ситуация бы не случилась",
                    "💪 при любой возможности тебе помочь я это сделаю!!"
            };
            randomMessage = fallbackMessages[new Random().nextInt(fallbackMessages.length)];
        }

        new AlertDialog.Builder(this)
                .setTitle("💖 Батарейка полностью заряжена!")
                .setMessage("коть, ты меня до одурения зарядил своей любовью!! 💕\n\n" + randomMessage)
                .setPositiveButton("Ура! 🥰", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update display when returning to activity (battery may have decayed)
        int currentLevel = prefsManager.getLoveLevel();
        updateBatteryDisplay(currentLevel);
        // Restart active decay
        startActiveBatteryDecay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop active decay when app is paused
        if (batteryProgressBar != null) {
            batteryProgressBar.removeCallbacks(activeDecayRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop active decay and let background decay take over
        if (batteryProgressBar != null) {
            batteryProgressBar.removeCallbacks(activeDecayRunnable);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}