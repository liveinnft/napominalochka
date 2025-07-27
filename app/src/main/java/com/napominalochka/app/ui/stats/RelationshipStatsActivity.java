package com.napominalochka.app.ui.stats;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.napominalochka.app.R;
import com.napominalochka.app.utils.SharedPrefsManager;
import java.util.Random;

public class RelationshipStatsActivity extends AppCompatActivity {
    
    private TextView daysTogetherText, communicationText;
    private TextView romanceLevelText, achievementsText;
    private ProgressBar romanceProgressBar;
    
    private SharedPrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationship_stats);

        prefsManager = new SharedPrefsManager(this);
        
        initViews();
        updateAllStats();
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.relationship_stats));
        }
    }

    private void initViews() {
        daysTogetherText = findViewById(R.id.days_together_text);
        communicationText = findViewById(R.id.messages_text); // Переиспользуем для дней общения
        romanceLevelText = findViewById(R.id.romance_level_text);
        achievementsText = findViewById(R.id.achievements_text);
        romanceProgressBar = findViewById(R.id.romance_progress_bar);
    }

    private void updateAllStats() {
        // Days together (relationship)
        int daysTogether = prefsManager.getDaysTogether();
        daysTogetherText.setText(daysTogether + " дней в отношениях 💕");
        
        // Days communicating
        int daysCommunicating = prefsManager.getDaysCommunicating();
        communicationText.setText(daysCommunicating + " дней общения 💬");
        
        // Romance level (random daily)
        int romanceLevel = getDailyRomanceLevel();
        romanceLevelText.setText(romanceLevel + "%");
        animateProgressBar(romanceLevel);
        
        // Achievements based on days
        updateAchievements(daysTogether, daysCommunicating);
    }

    private void animateProgressBar(int targetProgress) {
        ValueAnimator animator = ValueAnimator.ofInt(0, targetProgress);
        animator.setDuration(1500);
        animator.addUpdateListener(animation -> {
            int progress = (int) animation.getAnimatedValue();
            romanceProgressBar.setProgress(progress);
        });
        animator.start();
    }

    private int getDailyRomanceLevel() {
        // Generate consistent daily romance level based on date
        Random random = new Random(System.currentTimeMillis() / (1000 * 60 * 60 * 24));
        return 85 + random.nextInt(16); // 85-100%
    }

    private void updateAchievements(int daysRelationship, int daysCommunication) {
        StringBuilder achievements = new StringBuilder();
        
        // Relationship achievements
        if (daysRelationship >= 1) achievements.append("💕 Первый день вместе\n");
        if (daysRelationship >= 7) achievements.append("🌟 Неделя отношений\n");
        if (daysRelationship >= 30) achievements.append("🎉 Месяц счастья\n");
        if (daysRelationship >= 100) achievements.append("💎 100 дней вместе\n");
        if (daysRelationship >= 365) achievements.append("👑 Год любви\n");
        
        // Communication achievements  
        if (daysCommunication >= 30) achievements.append("💬 Месяц общения\n");
        if (daysCommunication >= 100) achievements.append("📱 100 дней разговоров\n");
        if (daysCommunication >= 200) achievements.append("🗣️ Болтуны профессионалы\n");
        
        if (achievements.length() == 0) {
            achievements.append("🌱 Только начинаем наше общение!");
        }
        
        achievementsText.setText(achievements.toString().trim());
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}