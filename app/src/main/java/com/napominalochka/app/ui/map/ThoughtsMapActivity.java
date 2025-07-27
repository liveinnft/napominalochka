package com.napominalochka.app.ui.map;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.napominalochka.app.R;

public class ThoughtsMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thoughts_map);
        
        setupIslands();
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.thoughts_map));
        }
    }

    private void setupIslands() {
        GridLayout mapGrid = findViewById(R.id.map_grid);
        
        String[] islands = {"🏝️ Воспоминания", "😄 Шутки", "💪 Поддержка", "🌟 Планы", "🔒 Секретный"};
        String[][] content = {
            {"💕 Наша первая встреча", "🎬 Первый фильм вместе", "🍕 Первый ужин", "💋 Первый поцелуй"},
            {"😂 Анекдоты про нас", "🤪 Смешные моменты", "😅 Забавные истории", "🎭 Комичные ситуации"},
            {"💪 Ты сильная!", "🌈 Все будет хорошо", "⭐ Ты справишься", "💖 Я верю в тебя"},
            {"✈️ Путешествие мечты", "🏠 Наш будущий дом", "👶 Планы на будущее", "🎯 Цели вместе"},
            {"🎁 Скоро откроется...", "💎 Особый сюрприз", "🔮 Тайна любви", "🌟 Что-то волшебное"}
        };
        
        for (int i = 0; i < islands.length; i++) {
            CardView islandCard = createIslandCard(islands[i], content[i], i == 4);
            mapGrid.addView(islandCard);
        }
    }

    private CardView createIslandCard(String title, String[] content, boolean isLocked) {
        CardView card = new CardView(this);
        card.setRadius(16f);
        card.setCardElevation(8f);
        card.setUseCompatPadding(true);
        
        // Set different colors for each island
        int[] colors = {R.color.orange_medium, R.color.sage_medium, R.color.warm_primary, R.color.terracotta, R.color.moss};
        card.setCardBackgroundColor(getColor(colors[0])); // Will be updated based on position
        
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 200;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.setMargins(8, 8, 8, 8);
        card.setLayoutParams(params);

        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        titleText.setTextSize(16);
        titleText.setTextColor(getColor(R.color.white));
        titleText.setPadding(16, 16, 16, 16);
        
        card.addView(titleText);
        
        if (!isLocked) {
            card.setOnClickListener(v -> showIslandContent(title, content));
            card.setClickable(true);
            card.setFocusable(true);
        }
        
        return card;
    }

    private void showIslandContent(String title, String[] content) {
        StringBuilder contentText = new StringBuilder();
        for (String item : content) {
            contentText.append("• ").append(item).append("\n");
        }
        
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(contentText.toString().trim())
                .setPositiveButton("Закрыть 💕", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}