package com.napominalochka.app.ui.diary;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.napominalochka.app.R;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiaryActivity extends AppCompatActivity {
    
    private LinearLayout entriesContainer;
    private Button newEntryButton;
    private Button templateButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        
        initViews();
        loadDiaryEntries();
        setupButtons();
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.diary));
        }
    }

    private void initViews() {
        entriesContainer = findViewById(R.id.entries_container);
        newEntryButton = findViewById(R.id.new_entry_button);
        templateButton = findViewById(R.id.template_button);
    }

    private void setupButtons() {
        newEntryButton.setOnClickListener(v -> showNewEntryDialog(""));
        templateButton.setOnClickListener(v -> showTemplateSelector());
    }

    private void showTemplateSelector() {
        String[] templates = {
            getString(R.string.template_remember),
            getString(R.string.template_if_you_were),
            getString(R.string.template_funny)
        };
        
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.entry_templates))
                .setItems(templates, (dialog, which) -> {
                    showNewEntryDialog(templates[which]);
                })
                .show();
    }

    private void showNewEntryDialog(String template) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.new_entry));
        
        EditText input = new EditText(this);
        input.setHint("Напиши что-то особенное...");
        if (!template.isEmpty()) {
            input.setText(template);
        }
        input.setMinLines(3);
        builder.setView(input);
        
        builder.setPositiveButton(getString(R.string.save_entry), (dialog, which) -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                addDiaryEntry(text);
            }
        });
        
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.show();
    }

    private void addDiaryEntry(String text) {
        String currentDate = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date());
        
        // Create entry card
        CardView entryCard = new CardView(this);
        entryCard.setRadius(12f);
        entryCard.setCardElevation(4f);
        entryCard.setUseCompatPadding(true);
        entryCard.setCardBackgroundColor(getColor(R.color.background_card));
        
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 16);
        entryCard.setLayoutParams(cardParams);
        
        // Create content layout
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(20, 20, 20, 20);
        
        // Date header
        TextView dateText = new TextView(this);
        dateText.setText(currentDate);
        dateText.setTextSize(12);
        dateText.setTextColor(getColor(R.color.text_secondary));
        contentLayout.addView(dateText);
        
        // Entry text
        TextView entryText = new TextView(this);
        entryText.setText(text);
        entryText.setTextSize(16);
        entryText.setTextColor(getColor(R.color.text_primary));
        entryText.setPadding(0, 8, 0, 0);
        contentLayout.addView(entryText);
        
        entryCard.addView(contentLayout);
        
        // Add to top of container
        entriesContainer.addView(entryCard, 0);
        
        // Save to preferences (simple implementation)
        saveEntry(currentDate, text);
    }

    private void saveEntry(String date, String text) {
        // Simple implementation - in real app might use Room database
        getSharedPreferences("diary", MODE_PRIVATE)
                .edit()
                .putString("entry_" + System.currentTimeMillis(), date + "|" + text)
                .apply();
    }

    private void loadDiaryEntries() {
        // Load sample entries for demonstration
        addSampleEntries();
    }

    private void addSampleEntries() {
        String[] sampleEntries = {
            "Сегодня особенный день! Думала о том, как мы смеялись вчера. Твой смех - самая красивая мелодия в мире. 💕",
            "Если бы ты был здесь... мы бы гуляли по парку, держась за руки. Представляю, как мы кормим уток и строим планы на будущее. 🦆",
            "Самое смешное за день: пыталась приготовить твое любимое блюдо и почти сожгла кухню! 😅 Хорошо, что ты любишь меня не за кулинарные таланты!",
            "Сегодня видела пару, которая напомнила мне о нас. Они так нежно смотрели друг на друга... Скорее бы увидеться! ❤️"
        };
        
        for (int i = 0; i < sampleEntries.length; i++) {
            String date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date(System.currentTimeMillis() - (i * 24 * 60 * 60 * 1000)));
            createEntryCard(date + " 20:00", sampleEntries[i]);
        }
    }

    private void createEntryCard(String date, String text) {
        CardView entryCard = new CardView(this);
        entryCard.setRadius(12f);
        entryCard.setCardElevation(4f);
        entryCard.setUseCompatPadding(true);
        entryCard.setCardBackgroundColor(getColor(R.color.background_card));
        
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 16);
        entryCard.setLayoutParams(cardParams);
        
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(20, 20, 20, 20);
        
        TextView dateText = new TextView(this);
        dateText.setText(date);
        dateText.setTextSize(12);
        dateText.setTextColor(getColor(R.color.text_secondary));
        contentLayout.addView(dateText);
        
        TextView entryText = new TextView(this);
        entryText.setText(text);
        entryText.setTextSize(16);
        entryText.setTextColor(getColor(R.color.text_primary));
        entryText.setPadding(0, 8, 0, 0);
        contentLayout.addView(entryText);
        
        entryCard.addView(contentLayout);
        entriesContainer.addView(entryCard);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}