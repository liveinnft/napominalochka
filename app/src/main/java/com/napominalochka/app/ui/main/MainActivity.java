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
import com.napominalochka.app.ui.map.ThoughtsMapActivity;
import com.napominalochka.app.ui.mission.MissionActivity;
import com.napominalochka.app.ui.mood.MoodBatteryActivity;
import com.napominalochka.app.ui.secret.SecretSurpriseActivity;
import com.napominalochka.app.ui.stats.RelationshipStatsActivity;
import com.napominalochka.app.utils.NotificationScheduler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Schedule daily notifications
        NotificationScheduler.scheduleDailyNotifications(this);

        setupMenuCards();
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
            startActivity(new Intent(MainActivity.this, ThoughtsMapActivity.class));
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
}