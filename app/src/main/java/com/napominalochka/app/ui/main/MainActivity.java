package com.napominalochka.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.napominalochka.app.R;
import com.napominalochka.app.ui.diary.DiaryActivity;
import com.napominalochka.app.ui.generator.JoyGeneratorActivity;
import com.napominalochka.app.ui.journey.JourneyGameActivity;
import com.napominalochka.app.ui.gallery.GalleryActivity;
import com.napominalochka.app.ui.mission.MissionActivity;
import com.napominalochka.app.ui.mood.MoodBatteryActivity;
import com.napominalochka.app.ui.secret.SecretSurpriseActivity;
import com.napominalochka.app.ui.stats.RelationshipStatsActivity;
import com.napominalochka.app.utils.NotificationScheduler;
import com.napominalochka.app.utils.SharedPrefsManager;
import android.app.AlertDialog;
import android.widget.EditText;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Schedule daily notifications
        NotificationScheduler.scheduleDailyNotifications(this);

        setupMenuCards();
        // checkFirstLaunch(); // Removed - using fixed dates now
    }

    private void setupMenuCards() {
        // Mood Battery Card
        CardView moodBatteryCard = findViewById(R.id.card_mood_battery);
        moodBatteryCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MoodBatteryActivity.class));
        });

        // Journey Game Card
        CardView journeyGameCard = findViewById(R.id.card_journey_game);
        journeyGameCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, JourneyGameActivity.class));
        });

        // Thoughts Map Card
        CardView thoughtsMapCard = findViewById(R.id.card_thoughts_map);
        thoughtsMapCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, GalleryActivity.class));
        });

        // Mission Card
        CardView missionCard = findViewById(R.id.card_mission);
        missionCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MissionActivity.class));
        });

        // Relationship Stats Card
        CardView statsCard = findViewById(R.id.card_relationship_stats);
        statsCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RelationshipStatsActivity.class));
        });

        // Joy Generator Card
        CardView joyGeneratorCard = findViewById(R.id.card_joy_generator);
        joyGeneratorCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, JoyGeneratorActivity.class));
        });

        // Diary Card
        CardView diaryCard = findViewById(R.id.card_diary);
        diaryCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, DiaryActivity.class));
        });

        // Secret Surprise Card
        CardView secretCard = findViewById(R.id.card_secret_surprise);
        secretCard.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SecretSurpriseActivity.class));
        });
    }

    private void checkFirstLaunch() {
        SharedPrefsManager prefsManager = new SharedPrefsManager(this);
        String startDate = prefsManager.getRelationshipStartDate();
        
        // Check if start date equals current date (default), indicating first launch
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if (startDate.equals(currentDate) && !getSharedPreferences("app_prefs", MODE_PRIVATE).getBoolean("setup_completed", false)) {
            showFirstLaunchDialog(prefsManager);
        }
    }

    private void showFirstLaunchDialog(SharedPrefsManager prefsManager) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("💕 Добро пожаловать в Напоминалочку!");
        builder.setMessage("Когда начались ваши отношения? Это поможет настроить игру 'Путешествие к нам'.");
        builder.setCancelable(false);
        
        EditText dateInput = new EditText(this);
        dateInput.setHint("Дата начала отношений (ГГГГ-ММ-ДД)");
        dateInput.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        builder.setView(dateInput);
        
        builder.setPositiveButton("Сохранить 💖", (dialog, which) -> {
            String inputDate = dateInput.getText().toString().trim();
            
            // Basic validation
            if (inputDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                prefsManager.setRelationshipStartDate(inputDate);
                getSharedPreferences("app_prefs", MODE_PRIVATE).edit().putBoolean("setup_completed", true).apply();
                
                new AlertDialog.Builder(this)
                        .setTitle("✅ Настройка завершена!")
                        .setMessage("Теперь все функции приложения готовы к использованию! 🎉")
                        .setPositiveButton("Начать! 🚀", null)
                        .show();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("❌ Неверный формат")
                        .setMessage("Пожалуйста, используйте формат ГГГГ-ММ-ДД (например: 2024-01-15)")
                        .setPositiveButton("Понятно", (d, w) -> showFirstLaunchDialog(prefsManager))
                        .show();
            }
        });
        
        builder.setNegativeButton("Позже", (dialog, which) -> {
            // Use current date as default
            getSharedPreferences("app_prefs", MODE_PRIVATE).edit().putBoolean("setup_completed", true).apply();
        });
        
        builder.show();
    }
}