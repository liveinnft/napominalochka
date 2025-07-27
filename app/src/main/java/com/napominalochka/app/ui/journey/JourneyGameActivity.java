package com.napominalochka.app.ui.journey;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.napominalochka.app.R;
import com.napominalochka.app.data.JourneyGameData;
import com.napominalochka.app.utils.SharedPrefsManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JourneyGameActivity extends AppCompatActivity {
    
    private GridLayout gameBoard;
    private TextView daysRemainingText;
    private TextView progressText;
    private SharedPrefsManager prefsManager;
    private JourneyGameData gameData;
    private static final int TOTAL_DAYS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_game);

        prefsManager = new SharedPrefsManager(this);
        gameData = new JourneyGameData();

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
        
        for (int i = 1; i <= TOTAL_DAYS; i++) {
            CardView cellCard = createGameCell(i, i <= currentDay);
            gameBoard.addView(cellCard);
        }
    }

    private CardView createGameCell(int day, boolean isUnlocked) {
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
        
        // Set background color based on status
        if (isUnlocked) {
            if (day <= prefsManager.getJourneyProgress()) {
                card.setCardBackgroundColor(getColor(R.color.warm_primary)); // Completed
            } else {
                card.setCardBackgroundColor(getColor(R.color.orange_medium)); // Available
            }
        } else {
            card.setCardBackgroundColor(getColor(R.color.background_secondary)); // Locked
        }

        // Create cell content
        TextView cellText = new TextView(this);
        cellText.setText(isUnlocked ? getEmojiForDay(day) + "\n" + day : "🔒\n" + day);
        cellText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        cellText.setTextSize(18);
        cellText.setTextColor(getColor(isUnlocked ? R.color.white : R.color.text_secondary));
        cellText.setPadding(16, 16, 16, 16);
        
        card.addView(cellText);

        // Set click listener
        if (isUnlocked && day > prefsManager.getJourneyProgress()) {
            card.setOnClickListener(v -> openDayContent(day));
            card.setClickable(true);
            card.setFocusable(true);
        }

        return card;
    }

    private String getEmojiForDay(int day) {
        String[] emojis = {"🌅", "💌", "🎵", "📸", "🌸", "☕", "🌟", "📖", "🎨", "🦋",
                          "🌙", "🎭", "🍯", "🌈", "💫", "🎪", "🌺", "🎁", "✨", "🎉"};
        return emojis[(day - 1) % emojis.length];
    }

    private void openDayContent(int day) {
        JourneyGameData.DayContent content = gameData.getDayContent(day);
        
        // Animate card selection
        animateCardSelection(day);
        
        // Show content dialog
        new AlertDialog.Builder(this)
                .setTitle("День " + day + " 🎁")
                .setMessage(content.getTitle() + "\n\n" + content.getContent())
                .setPositiveButton("Прочитано! ❤️", (dialog, which) -> {
                    markDayCompleted(day);
                    updateProgress();
                    setupGameBoard(); // Refresh the board
                })
                .setNegativeButton("Позже", null)
                .show();
    }

    private void animateCardSelection(int day) {
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
        // For demo purposes, unlock one day per real day
        // In real app, you might want different logic
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = sdf.format(new Date());
        String startDate = prefsManager.getRelationshipStartDate();
        
        // Safety check for start date
        if (startDate == null || startDate.isEmpty()) {
            return 1; // Safe default
        }
        
        try {
            Date start = sdf.parse(startDate);
            Date current = sdf.parse(today);
            if (start != null && current != null) {
                long diffInMillies = current.getTime() - start.getTime();
                
                // Ensure non-negative difference (start date not in future)
                if (diffInMillies < 0) {
                    return 1; // If start date is in future, start from day 1
                }
                
                int daysPassed = (int) (diffInMillies / (1000 * 60 * 60 * 24)) + 1;
                return Math.min(Math.max(1, daysPassed), TOTAL_DAYS); // Ensure between 1 and TOTAL_DAYS
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