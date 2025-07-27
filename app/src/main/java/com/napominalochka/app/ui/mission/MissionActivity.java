package com.napominalochka.app.ui.mission;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.napominalochka.app.R;
import com.napominalochka.app.config.AppTexts;
import com.napominalochka.app.utils.SharedPrefsManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MissionActivity extends AppCompatActivity {
    
    private TextView todayMissionText;
    private TextView missionDescriptionText;
    private TextView pointsText;
    private TextView completedMissionsText;
    private Button completeMissionButton;
    private Button getMissionButton;
    
    private SharedPrefsManager prefsManager;
    private String[] currentMission; // [0] = title, [1] = description

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        prefsManager = new SharedPrefsManager(this);

        initViews();
        setupMission();
        updateStats();
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.mission));
        }
    }

    private void initViews() {
        todayMissionText = findViewById(R.id.today_mission_text);
        missionDescriptionText = findViewById(R.id.mission_description_text);
        pointsText = findViewById(R.id.points_text);
        completedMissionsText = findViewById(R.id.completed_missions_text);
        completeMissionButton = findViewById(R.id.complete_mission_button);
        getMissionButton = findViewById(R.id.get_mission_button);

        completeMissionButton.setOnClickListener(v -> completeMission());
        getMissionButton.setOnClickListener(v -> getNewMission());
    }

    private void setupMission() {
        if (prefsManager.canGetNewMission()) {
            showGetMissionState();
        } else {
            showCurrentMission();
        }
    }

    private void showGetMissionState() {
        todayMissionText.setText("🎯 Готов к новой миссии?");
        missionDescriptionText.setText("Нажми кнопку, чтобы получить задание дня!");
        
        getMissionButton.setVisibility(Button.VISIBLE);
        completeMissionButton.setVisibility(Button.GONE);
    }

    private void showCurrentMission() {
        String todayMission = getTodayMission();
        if (todayMission != null) {
            currentMission = findMissionByTitle(todayMission);
            if (currentMission != null) {
                todayMissionText.setText("🎯 " + currentMission[0]);
                missionDescriptionText.setText(currentMission[1]);
                
                getMissionButton.setVisibility(Button.GONE);
                completeMissionButton.setVisibility(Button.VISIBLE);
            }
        } else {
            showGetMissionState();
        }
    }

    private void getNewMission() {
        // Animate button
        animateButton(getMissionButton);
        
        // Get random mission from central config
        int randomIndex = new Random().nextInt(AppTexts.MISSIONS.length);
        currentMission = AppTexts.MISSIONS[randomIndex];
        
        // Save mission for today
        prefsManager.setLastMissionDate(getCurrentDate());
        saveTodayMission(currentMission[0]);
        
        // Update UI
        todayMissionText.setText("🎯 " + currentMission[0]);
        missionDescriptionText.setText(currentMission[1]);
        
        getMissionButton.setVisibility(Button.GONE);
        completeMissionButton.setVisibility(Button.VISIBLE);
        
        // Show motivational message
        showMissionDialog("Новая миссия получена! 🚀", 
                         "Время показать, на что ты способна! Удачи в выполнении задания! 💪");
    }

    private void completeMission() {
        if (currentMission == null) return;
        
        // Animate button
        animateButton(completeMissionButton);
        
        // Add points
        prefsManager.addMissionPoints(10);
        prefsManager.incrementCompletedMissions();
        
        // Clear today's mission
        saveTodayMission(null);
        
        // Update stats
        updateStats();
        
        // Show completion dialog
        showCompletionDialog();
        
        // Reset to get new mission state
        showGetMissionState();
    }

    private void showCompletionDialog() {
        String message = getString(R.string.mission_completed) + "\n\n" +
                        getCompletionMessage() + "\n\n" +
                        "Текущий счет: " + prefsManager.getMissionPoints() + " очков 🌟";
        
        new AlertDialog.Builder(this)
                .setTitle("🎉 Миссия выполнена!")
                .setMessage(message)
                .setPositiveButton("Отлично! 🥳", null)
                .show();
    }

    private String getCompletionMessage() {
        String[] messages = {
            "Ты просто невероятная! 🌟",
            "Каждое задание ты выполняешь с блеском! ✨",
            "Я горжусь тобой! 💖",
            "Ты делаешь невозможное возможным! 🦄",
            "Твоя энергия заряжает меня на расстоянии! ⚡",
            "С каждым днем ты становишься еще удивительнее! 🌺",
            "Ты - моя супергероиня! 🦸‍♀️"
        };
        return messages[new Random().nextInt(messages.length)];
    }

    private void showMissionDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Понятно! 👍", null)
                .show();
    }

    private void animateButton(Button button) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 1.0f, 1.1f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 1.0f, 1.1f, 1.0f);
        scaleX.setDuration(200);
        scaleY.setDuration(200);
        scaleX.start();
        scaleY.start();
    }

    private void updateStats() {
        int points = prefsManager.getMissionPoints();
        int completed = prefsManager.getCompletedMissions();
        
        pointsText.setText(getString(R.string.good_mood_points, points));
        completedMissionsText.setText("Выполнено миссий: " + completed);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void saveTodayMission(String mission) {
        // Simple storage - in real app might use more sophisticated approach
        getSharedPreferences("missions", MODE_PRIVATE)
                .edit()
                .putString("today_mission", mission)
                .apply();
    }

    private String getTodayMission() {
        return getSharedPreferences("missions", MODE_PRIVATE)
                .getString("today_mission", null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    private String[] findMissionByTitle(String title) {
        for (String[] mission : AppTexts.MISSIONS) {
            if (mission[0].equals(title)) {
                return mission;
            }
        }
        return null;
    }
}