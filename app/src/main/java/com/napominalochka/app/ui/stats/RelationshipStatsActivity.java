package com.napominalochka.app.ui.stats;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.napominalochka.app.R;
import com.napominalochka.app.utils.SharedPrefsManager;
import java.util.Random;

public class RelationshipStatsActivity extends AppCompatActivity {
    
    private TextView daysTogetherText, messagesText, videoCallsText, smilesText;
    private TextView romanceLevelText, totalPointsText, achievementsText;
    private ProgressBar romanceProgressBar;
    private Button editStatsButton;
    
    private SharedPrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationship_stats);

        prefsManager = new SharedPrefsManager(this);
        
        initViews();
        updateAllStats();
        setupEditButton();
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.relationship_stats));
        }
    }

    private void initViews() {
        daysTogetherText = findViewById(R.id.days_together_text);
        messagesText = findViewById(R.id.messages_text);
        videoCallsText = findViewById(R.id.video_calls_text);
        smilesText = findViewById(R.id.smiles_text);
        romanceLevelText = findViewById(R.id.romance_level_text);
        totalPointsText = findViewById(R.id.total_points_text);
        achievementsText = findViewById(R.id.achievements_text);
        romanceProgressBar = findViewById(R.id.romance_progress_bar);
        editStatsButton = findViewById(R.id.edit_stats_button);
    }

    private void updateAllStats() {
        // Days together
        int daysTogether = prefsManager.getDaysTogether();
        daysTogetherText.setText(daysTogether + " дней");
        
        // Messages
        int messages = prefsManager.getMessagesSent();
        if (messages == 0) {
            messages = 1247; // Default romantic number
            prefsManager.setMessagesSent(messages);
        }
        messagesText.setText(String.format("%,d сообщений", messages));
        
        // Video calls
        int videoCalls = prefsManager.getVideoCalls();
        if (videoCalls == 0) {
            videoCalls = 89; // Default number
            prefsManager.setVideoCalls(videoCalls);
        }
        videoCallsText.setText(videoCalls + " звонков");
        
        // Smiles
        int smiles = prefsManager.getSmilesSent();
        if (smiles == 0) {
            smiles = 3456; // Default number
            prefsManager.setSmilesSent(smiles);
        }
        smilesText.setText(String.format("%,d смайликов", smiles));
        
        // Romance level (random daily)
        int romanceLevel = getDailyRomanceLevel();
        romanceLevelText.setText(romanceLevel + "%");
        animateProgressBar(romanceLevel);
        
        // Total points
        int totalPoints = prefsManager.getMissionPoints();
        totalPointsText.setText(totalPoints + " очков");
        
        // Achievements
        updateAchievements(daysTogether, totalPoints);
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

    private void updateAchievements(int days, int points) {
        StringBuilder achievements = new StringBuilder();
        
        if (days >= 1) achievements.append("🌟 Первый день вместе\n");
        if (days >= 7) achievements.append("💕 Неделя любви\n");
        if (days >= 30) achievements.append("🎉 Месяц счастья\n");
        if (days >= 100) achievements.append("💎 100 дней вместе\n");
        if (days >= 365) achievements.append("👑 Год любви\n");
        
        if (points >= 50) achievements.append("⭐ Активный игрок\n");
        if (points >= 100) achievements.append("🏆 Мастер миссий\n");
        if (points >= 200) achievements.append("🎯 Легенда активности\n");
        
        if (achievements.length() == 0) {
            achievements.append("🌱 Только начинаем!");
        }
        
        achievementsText.setText(achievements.toString().trim());
    }

    private void setupEditButton() {
        editStatsButton.setOnClickListener(v -> showEditDialog());
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("📝 Редактировать статистику");
        
        // Create input fields
        EditText messagesInput = new EditText(this);
        messagesInput.setHint("Количество сообщений");
        messagesInput.setText(String.valueOf(prefsManager.getMessagesSent()));
        
        EditText callsInput = new EditText(this);
        callsInput.setHint("Количество видео-звонков");
        callsInput.setText(String.valueOf(prefsManager.getVideoCalls()));
        
        EditText smilesInput = new EditText(this);
        smilesInput.setHint("Количество смайликов");
        smilesInput.setText(String.valueOf(prefsManager.getSmilesSent()));
        
        // Create layout
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.addView(messagesInput);
        layout.addView(callsInput);
        layout.addView(smilesInput);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Сохранить 💾", (dialog, which) -> {
            try {
                int messages = Integer.parseInt(messagesInput.getText().toString());
                int calls = Integer.parseInt(callsInput.getText().toString());
                int smiles = Integer.parseInt(smilesInput.getText().toString());
                
                prefsManager.setMessagesSent(messages);
                prefsManager.setVideoCalls(calls);
                prefsManager.setSmilesSent(smiles);
                
                updateAllStats();
                
                new AlertDialog.Builder(this)
                        .setTitle("✅ Успешно!")
                        .setMessage("Статистика обновлена! Теперь цифры отражают вашу реальную активность 💕")
                        .setPositiveButton("Отлично!", null)
                        .show();
                        
            } catch (NumberFormatException e) {
                new AlertDialog.Builder(this)
                        .setTitle("❌ Ошибка")
                        .setMessage("Пожалуйста, введите корректные числа")
                        .setPositiveButton("Понятно", null)
                        .show();
            }
        });
        
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}