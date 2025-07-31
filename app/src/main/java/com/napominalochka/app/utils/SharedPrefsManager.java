package com.napominalochka.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SharedPrefsManager {
    private static final String PREF_NAME = "napominalochka_prefs";

    // Keys
    private static final String KEY_LOVE_LEVEL = "love_level";
    private static final String KEY_LAST_UPDATE_DATE = "last_update_date";
    private static final String KEY_LAST_BATTERY_UPDATE = "last_battery_update";
    private static final String KEY_JOURNEY_PROGRESS = "journey_progress";
    private static final String KEY_MISSION_POINTS = "mission_points";
    private static final String KEY_COMPLETED_MISSIONS = "completed_missions";
    private static final String KEY_RELATIONSHIP_START_DATE = "relationship_start_date";
    private static final String KEY_MESSAGES_SENT = "messages_sent";
    private static final String KEY_VIDEO_CALLS = "video_calls";
    private static final String KEY_SMILES_SENT = "smiles_sent";
    private static final String KEY_SECRET_UNLOCKED = "secret_unlocked";
    private static final String KEY_LAST_MISSION_DATE = "last_mission_date";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Love Level Management
    public void setLoveLevel(int level) {
        editor.putInt(KEY_LOVE_LEVEL, Math.max(0, Math.min(100, level)));
        updateLastBatteryUpdate();
        editor.apply();
    }

    public int getLoveLevel() {
        // Проверяем фоновую разрядку при каждом получении уровня
        checkAndUpdateBackgroundDecay();
        return sharedPreferences.getInt(KEY_LOVE_LEVEL, 75); // Default 75%
    }

    public void chargeBattery() {
        // Сначала обновляем уровень с учетом фоновой разрядки
        checkAndUpdateBackgroundDecay();
        int currentLevel = sharedPreferences.getInt(KEY_LOVE_LEVEL, 75);
        int newLevel = Math.min(100, currentLevel + 2); // Increase by 2%
        setLoveLevel(newLevel);
    }

    private void updateLastBatteryUpdate() {
        editor.putLong(KEY_LAST_BATTERY_UPDATE, System.currentTimeMillis());
    }

    private void checkAndUpdateBackgroundDecay() {
        long lastUpdate = sharedPreferences.getLong(KEY_LAST_BATTERY_UPDATE, System.currentTimeMillis());
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - lastUpdate;

        // Фоновая разрядка: 1% в минуту = 1% за 60000 мс
        long minutesPassed = timeDiff / (60 * 1000); // 60 секунд = 60000 мс

        if (minutesPassed > 0) {
            // Уменьшаем на 1% за каждую минуту
            int currentLevel = sharedPreferences.getInt(KEY_LOVE_LEVEL, 75);
            int decreaseAmount = (int) Math.min(minutesPassed, currentLevel); // Не идем ниже 0
            int newLevel = Math.max(0, currentLevel - decreaseAmount);

            // Обновляем уровень и временную метку
            editor.putInt(KEY_LOVE_LEVEL, newLevel);
            updateLastBatteryUpdate();
            editor.apply();
        }
    }

    private void checkAndUpdateDailyDecay() {
        String lastUpdateDate = sharedPreferences.getString(KEY_LAST_UPDATE_DATE, "");
        String currentDate = getCurrentDate();

        if (!lastUpdateDate.equals(currentDate)) {
            // New day - decrease love level by 10-20%
            int currentLevel = sharedPreferences.getInt(KEY_LOVE_LEVEL, 75);
            int decrease = (int) (Math.random() * 11) + 10; // 10-20%
            int newLevel = Math.max(0, currentLevel - decrease);

            editor.putInt(KEY_LOVE_LEVEL, newLevel);
            editor.putString(KEY_LAST_UPDATE_DATE, currentDate);
            editor.apply();
        }
    }

    // Journey Game Progress
    public void setJourneyProgress(int day) {
        editor.putInt(KEY_JOURNEY_PROGRESS, day);
        editor.apply();
    }

    public int getJourneyProgress() {
        return sharedPreferences.getInt(KEY_JOURNEY_PROGRESS, 0);
    }

    // Mission Points
    public void addMissionPoints(int points) {
        int currentPoints = getMissionPoints();
        editor.putInt(KEY_MISSION_POINTS, currentPoints + points);
        editor.apply();
    }

    public int getMissionPoints() {
        return sharedPreferences.getInt(KEY_MISSION_POINTS, 0);
    }

    // Completed Missions Count
    public void incrementCompletedMissions() {
        int current = getCompletedMissions();
        editor.putInt(KEY_COMPLETED_MISSIONS, current + 1);
        editor.apply();
    }

    public int getCompletedMissions() {
        return sharedPreferences.getInt(KEY_COMPLETED_MISSIONS, 0);
    }

    // Relationship Statistics
    public void setRelationshipStartDate(String date) {
        editor.putString(KEY_RELATIONSHIP_START_DATE, date);
        editor.apply();
    }

    public String getRelationshipStartDate() {
        // Fixed relationship start date - 6 октября 2024
        return "2024-10-06";
    }

    public String getCommunicationStartDate() {
        // Fixed communication start date - 20 сентября 2024
        return "2024-09-20";
    }

    public void setMessagesSent(int count) {
        editor.putInt(KEY_MESSAGES_SENT, count);
        editor.apply();
    }

    public int getMessagesSent() {
        return sharedPreferences.getInt(KEY_MESSAGES_SENT, 0);
    }

    public void setVideoCalls(int count) {
        editor.putInt(KEY_VIDEO_CALLS, count);
        editor.apply();
    }

    public int getVideoCalls() {
        return sharedPreferences.getInt(KEY_VIDEO_CALLS, 0);
    }

    public void setSmilesSent(int count) {
        editor.putInt(KEY_SMILES_SENT, count);
        editor.apply();
    }

    public int getSmilesSent() {
        return sharedPreferences.getInt(KEY_SMILES_SENT, 0);
    }

    // Secret Surprise
    public void setSecretUnlocked(boolean unlocked) {
        editor.putBoolean(KEY_SECRET_UNLOCKED, unlocked);
        editor.apply();
    }

    public boolean isSecretUnlocked() {
        return sharedPreferences.getBoolean(KEY_SECRET_UNLOCKED, false);
    }

    // Mission Management
    public void setLastMissionDate(String date) {
        editor.putString(KEY_LAST_MISSION_DATE, date);
        editor.apply();
    }

    public String getLastMissionDate() {
        return sharedPreferences.getString(KEY_LAST_MISSION_DATE, "");
    }

    public boolean canGetNewMission() {
        String lastMissionDate = getLastMissionDate();
        String currentDate = getCurrentDate();
        return !lastMissionDate.equals(currentDate);
    }

    // Utility Methods
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Calculate days together
    public int getDaysTogether() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date startDate = sdf.parse(getRelationshipStartDate());
            Date currentDate = new Date();

            if (startDate != null) {
                long diffInMillies = currentDate.getTime() - startDate.getTime();
                // Добавляем +1 день для правильного подсчета (включая первый день)
                return (int) (diffInMillies / (1000 * 60 * 60 * 24)) + 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public int getDaysCommunicating() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date startDate = sdf.parse(getCommunicationStartDate());
            Date currentDate = new Date();

            if (startDate != null) {
                long diffInMillies = currentDate.getTime() - startDate.getTime();
                // Добавляем +1 день для правильного подсчета (включая первый день)
                return (int) (diffInMillies / (1000 * 60 * 60 * 24)) + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }
}