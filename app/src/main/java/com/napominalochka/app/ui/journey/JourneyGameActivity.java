package com.napominalochka.app.ui.journey;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.napominalochka.app.R;
import com.napominalochka.app.config.AppTexts;
import com.napominalochka.app.utils.SharedPrefsManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JourneyGameActivity extends AppCompatActivity {

    private GridLayout gameBoard;
    private TextView daysRemainingText;
    private TextView progressText;
    private SharedPrefsManager prefsManager;

    private static final int TOTAL_DAYS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_game);

        prefsManager = new SharedPrefsManager(this);

        initViews();
        setupGameBoard();
        updateProgress();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.journey_game));
        }
    }

    private void initViews() {
        gameBoard = findViewById(R.id.game_board);
        daysRemainingText = findViewById(R.id.days_remaining_text);
        progressText = findViewById(R.id.progress_text);
    }

    private void setupGameBoard() {
        gameBoard.removeAllViews();

        int currentDay = getCurrentDay();
        int completedDays = prefsManager.getJourneyProgress();

        for (int i = 1; i <= TOTAL_DAYS; i++) {
            CardView cellCard = createGameCell(i, currentDay, completedDays);
            gameBoard.addView(cellCard);
        }
    }

    private CardView createGameCell(int day, int currentDay, int completedDays) {
        CardView card = new CardView(this);

        // Set layout parameters
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 200;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(8, 8, 8, 8);
        card.setLayoutParams(params);

        // Set card properties
        card.setRadius(16f);
        card.setCardElevation(8f);
        card.setUseCompatPadding(true);

        // Определяем статус дня
        boolean isUnlocked = day <= currentDay;
        boolean isCompleted = day <= completedDays;
        boolean isAvailable = isUnlocked && !isCompleted;

        // Set background color based on status
        if (isCompleted) {
            card.setCardBackgroundColor(getColor(R.color.warm_primary)); // Completed - зеленый
        } else if (isAvailable) {
            card.setCardBackgroundColor(getColor(R.color.orange_medium)); // Available - оранжевый
        } else {
            card.setCardBackgroundColor(getColor(R.color.background_secondary)); // Locked - серый
        }

        // Create cell content
        TextView cellText = new TextView(this);

        String displayText;
        int textColor;

        if (isCompleted) {
            displayText = "✅\n" + day;
            textColor = getColor(R.color.white);
        } else if (isAvailable) {
            displayText = getEmojiForDay(day) + "\n" + day;
            textColor = getColor(R.color.white);
        } else {
            displayText = "🔒\n" + day;
            textColor = getColor(R.color.text_secondary);
        }

        cellText.setText(displayText);
        cellText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        cellText.setTextSize(18);
        cellText.setTextColor(textColor);
        cellText.setPadding(16, 16, 16, 16);

        card.addView(cellText);

        // Set click listener для всех открытых дней (доступных И завершенных)
        if (isUnlocked) {
            card.setOnClickListener(v -> openDayContent(day));
            card.setClickable(true);
            card.setFocusable(true);

            // Добавляем визуальную обратную связь
            card.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        v.setAlpha(0.7f);
                        v.setScaleX(0.95f);
                        v.setScaleY(0.95f);
                        break;
                    case android.view.MotionEvent.ACTION_UP:
                    case android.view.MotionEvent.ACTION_CANCEL:
                        v.setAlpha(1.0f);
                        v.setScaleX(1.0f);
                        v.setScaleY(1.0f);
                        break;
                }
                return false; // Позволяем обработку onClick
            });
        } else {
            // Для заблокированных дней отключаем клики
            card.setClickable(false);
            card.setFocusable(false);
        }

        return card;
    }

    private String getEmojiForDay(int day) {
        String[] emojis = {"🌅", "💌", "🎵", "📸", "🌸", "☕", "🌟", "📖", "🎨", "🦋",
                "🌙", "🎭", "🍯", "🌈", "💫", "🎪", "🌺", "🎁", "✨", "🎉"};
        return emojis[(day - 1) % emojis.length];
    }

    private void openDayContent(int day) {
        // Проверяем, что контент существует
        if (day > AppTexts.JOURNEY_CONTENT.length) {
            new AlertDialog.Builder(this)
                    .setTitle("❗ Ошибка")
                    .setMessage("Контент для дня " + day + " еще не добавлен!")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Get content from centralized config
        String[] content = AppTexts.JOURNEY_CONTENT[day - 1]; // day is 1-based, array is 0-based

        // Animate card selection
        animateCardSelection(day);

        // Show content dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("День " + day + " 🎁");
        builder.setMessage(content[0] + "\n\n" + content[1]); // [0] = title, [1] = content

        // Если день уже завершен, показываем только кнопку "Перечитать"
        int completedDays = prefsManager.getJourneyProgress();
        if (day <= completedDays) {
            builder.setPositiveButton("Перечитать 📖", null);
        } else {
            // Если день еще не завершен, показываем кнопку завершения
            builder.setPositiveButton("Прочитано! ❤️", (dialog, which) -> {
                markDayCompleted(day);
                updateProgress();
                setupGameBoard(); // Refresh the board
            });
            builder.setNegativeButton("Позже", null);
        }

        builder.setCancelable(false) // Предотвращаем случайное закрытие
                .show();
    }

    private void animateCardSelection(int day) {
        if (day > 0 && day <= gameBoard.getChildCount()) {
            View selectedCard = gameBoard.getChildAt(day - 1);
            if (selectedCard != null) {
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(selectedCard, "scaleX", 1.0f, 1.1f, 1.0f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(selectedCard, "scaleY", 1.0f, 1.1f, 1.0f);
                scaleX.setDuration(300);
                scaleY.setDuration(300);
                scaleX.start();
                scaleY.start();
            }
        }
    }

    private void markDayCompleted(int day) {
        prefsManager.setJourneyProgress(day);

        // Add some mood points
        prefsManager.addMissionPoints(15);

        if (day == TOTAL_DAYS) {
            showFinalSurprise();
        }
    }

    private void showFinalSurprise() {
        new AlertDialog.Builder(this)
                .setTitle("🎉 Поздравляю!")
                .setMessage("Ты прошла все 20 дней путешествия! \n\n💝 Особый сюрприз ждет тебя в секретном разделе...")
                .setPositiveButton("Ура! 🎊", null)
                .show();
    }

    private int getCurrentDay() {
        // Check when app was first launched and unlock one day per day since then
        SharedPreferences appPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String firstLaunchDate = appPrefs.getString("first_launch_date", "");

        if (firstLaunchDate.isEmpty()) {
            // First launch - set current date and return day 1
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String today = sdf.format(new Date());
            appPrefs.edit().putString("first_launch_date", today).apply();
            return 1;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date firstLaunch = sdf.parse(firstLaunchDate);
            Date current = new Date();

            if (firstLaunch != null) {
                long diffInMillies = current.getTime() - firstLaunch.getTime();
                int daysPassed = (int) (diffInMillies / (1000 * 60 * 60 * 24)) + 1;
                return Math.min(Math.max(1, daysPassed), TOTAL_DAYS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1; // Safe default
    }

    private void updateProgress() {
        int currentDay = getCurrentDay();
        int completedDays = prefsManager.getJourneyProgress();
        int remainingDays = Math.max(0, TOTAL_DAYS - completedDays);

        daysRemainingText.setText(getString(R.string.days_remaining, remainingDays));

        // Safe percentage calculation
        int percentage = (TOTAL_DAYS > 0) ? (completedDays * 100 / TOTAL_DAYS) : 0;
        progressText.setText("Прогресс: " + completedDays + " из " + TOTAL_DAYS + " дней (" +
                percentage + "%)");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}