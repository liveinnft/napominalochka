package com.napominalochka.app.ui.diary;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.napominalochka.app.R;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DiaryActivity extends AppCompatActivity {

    private LinearLayout entriesContainer;
    private Button newEntryButton;
    private SharedPreferences diaryPrefs;

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView selectedImageView;
    private String selectedImageBase64 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        diaryPrefs = getSharedPreferences("diary", MODE_PRIVATE);

        initViews();
        loadDiaryEntries();
        setupButtons();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Дневник");
        }
    }

    private void initViews() {
        entriesContainer = findViewById(R.id.entries_container);
        newEntryButton = findViewById(R.id.new_entry_button);
    }

    private void setupButtons() {
        newEntryButton.setOnClickListener(v -> showNewEntryDialog());
    }

    private void showNewEntryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("✍️ Новая запись");

        // Create main layout
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(20, 20, 20, 20);

        // Text input
        EditText input = new EditText(this);
        input.setHint("Напиши что-то особенное...");
        input.setMinLines(3);
        input.setMaxLines(10);
        mainLayout.addView(input);

        // Photo section
        LinearLayout photoLayout = new LinearLayout(this);
        photoLayout.setOrientation(LinearLayout.VERTICAL);
        photoLayout.setPadding(0, 16, 0, 0);

        Button addPhotoButton = new Button(this);
        addPhotoButton.setText("📷 Добавить фото");
        addPhotoButton.setOnClickListener(v -> openImagePicker());
        photoLayout.addView(addPhotoButton);

        selectedImageView = new ImageView(this);
        selectedImageView.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
        selectedImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        selectedImageView.setVisibility(ImageView.GONE);
        photoLayout.addView(selectedImageView);

        Button removePhotoButton = new Button(this);
        removePhotoButton.setText("❌ Убрать фото");
        removePhotoButton.setVisibility(Button.GONE);
        removePhotoButton.setOnClickListener(v -> {
            selectedImageView.setVisibility(ImageView.GONE);
            removePhotoButton.setVisibility(Button.GONE);
            selectedImageBase64 = null;
        });
        photoLayout.addView(removePhotoButton);

        mainLayout.addView(photoLayout);
        builder.setView(mainLayout);

        builder.setPositiveButton("💾 Сохранить", (dialog, which) -> {
            String text = input.getText().toString().trim();
            // Разрешаем сохранение если есть либо текст, либо фото
            if (!text.isEmpty() || selectedImageBase64 != null) {
                // Если текст пустой но есть фото, добавляем дефолтный текст
                if (text.isEmpty() && selectedImageBase64 != null) {
                    text = "📷"; // Просто эмодзи камеры как placeholder
                }
                addDiaryEntry(text, selectedImageBase64);
                selectedImageBase64 = null; // Reset
            }
        });

        builder.setNegativeButton("❌ Отмена", (dialog, which) -> {
            selectedImageBase64 = null; // Reset
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Store reference for image picker callback
        addPhotoButton.setOnClickListener(v -> {
            openImagePicker();
            // Update visibility when image is selected
            addPhotoButton.postDelayed(() -> {
                if (selectedImageBase64 != null) {
                    selectedImageView.setVisibility(ImageView.VISIBLE);
                    removePhotoButton.setVisibility(Button.VISIBLE);
                }
            }, 100);
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выбери фото"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                // Resize image to reasonable size
                Bitmap resizedBitmap = resizeBitmap(bitmap, 800, 600);
                selectedImageBase64 = bitmapToBase64(resizedBitmap);

                if (selectedImageView != null) {
                    selectedImageView.setImageBitmap(resizedBitmap);
                    selectedImageView.setVisibility(ImageView.VISIBLE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float aspectRatio = (float) width / height;

        if (width > height) {
            width = maxWidth;
            height = (int) (width / aspectRatio);
        } else {
            height = maxHeight;
            width = (int) (height * aspectRatio);
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap base64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void addDiaryEntry(String text, String imageBase64) {
        String currentDate = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date());
        long timestamp = System.currentTimeMillis();

        // Save to preferences with timestamp as key
        saveEntry(timestamp, currentDate, text, imageBase64);

        // Create and add card to UI
        createAndAddEntryCard(currentDate, text, imageBase64, timestamp);
    }

    private void createAndAddEntryCard(String date, String text, String imageBase64, long timestamp) {
        // Create entry card
        CardView entryCard = new CardView(this);
        entryCard.setRadius(12f);
        entryCard.setCardElevation(4f);
        entryCard.setUseCompatPadding(true);
        entryCard.setCardBackgroundColor(getColor(android.R.color.white));

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
        dateText.setText("📅 " + date);
        dateText.setTextSize(12);
        dateText.setTextColor(getColor(android.R.color.darker_gray));
        contentLayout.addView(dateText);

        // Entry text
        TextView entryText = new TextView(this);
        entryText.setText(text);
        entryText.setTextSize(16);
        entryText.setTextColor(getColor(android.R.color.black));
        entryText.setPadding(0, 8, 0, 8);
        entryText.setLineSpacing(4, 1.2f);
        contentLayout.addView(entryText);

        // Image if exists
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            ImageView entryImage = new ImageView(this);
            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    400
            );
            imageParams.setMargins(0, 8, 0, 8);
            entryImage.setLayoutParams(imageParams);
            entryImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            entryImage.setImageBitmap(base64ToBitmap(imageBase64));

            // Add click to view fullscreen
            entryImage.setOnClickListener(v -> showFullscreenImage(base64ToBitmap(imageBase64)));

            contentLayout.addView(entryImage);
        }

        // Delete button
        TextView deleteButton = new TextView(this);
        deleteButton.setText("🗑️ Удалить");
        deleteButton.setTextSize(12);
        deleteButton.setTextColor(getColor(android.R.color.holo_red_light));
        deleteButton.setPadding(0, 8, 0, 0);
        deleteButton.setClickable(true);
        deleteButton.setOnClickListener(v -> showDeleteConfirmation(entryCard, timestamp));
        contentLayout.addView(deleteButton);

        entryCard.addView(contentLayout);

        // Add to top of container (newest first)
        entriesContainer.addView(entryCard, 0);
    }

    private void showFullscreenImage(Bitmap bitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ImageView fullscreenImage = new ImageView(this);
        fullscreenImage.setImageBitmap(bitmap);
        fullscreenImage.setScaleType(ImageView.ScaleType.FIT_CENTER);

        builder.setView(fullscreenImage);
        builder.setPositiveButton("❌ Закрыть", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteConfirmation(CardView entryCard, long timestamp) {
        new AlertDialog.Builder(this)
                .setTitle("🗑️ Удалить запись?")
                .setMessage("Эта запись будет удалена навсегда. Продолжить?")
                .setPositiveButton("✅ Да, удалить", (dialog, which) -> {
                    deleteEntry(entryCard, timestamp);
                })
                .setNegativeButton("❌ Отмена", null)
                .show();
    }

    private void deleteEntry(CardView entryCard, long timestamp) {
        // Remove from UI
        entriesContainer.removeView(entryCard);

        // Remove from SharedPreferences
        diaryPrefs.edit().remove("entry_" + timestamp).apply();
    }

    private void saveEntry(long timestamp, String date, String text, String imageBase64) {
        String entryData = date + "|" + text;
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            entryData += "|" + imageBase64;
        }

        diaryPrefs.edit()
                .putString("entry_" + timestamp, entryData)
                .apply();
    }

    private void loadDiaryEntries() {
        entriesContainer.removeAllViews();

        Map<String, ?> allEntries = diaryPrefs.getAll();
        List<DiaryEntry> entries = new ArrayList<>();

        // Parse all saved entries
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            String value = (String) entry.getValue();

            if (key.startsWith("entry_") && value != null) {
                try {
                    long timestamp = Long.parseLong(key.substring(6));
                    String[] parts = value.split("\\|", 3);
                    if (parts.length >= 2) {
                        String imageBase64 = parts.length > 2 ? parts[2] : null;
                        entries.add(new DiaryEntry(timestamp, parts[0], parts[1], imageBase64));
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid entries
                }
            }
        }

        // Sort by timestamp (newest first)
        Collections.sort(entries, (a, b) -> Long.compare(b.timestamp, a.timestamp));

        // Create UI cards for all entries
        for (DiaryEntry entry : entries) {
            createAndAddEntryCard(entry.date, entry.text, entry.imageBase64, entry.timestamp);
        }

        // Show welcome message if no entries
        if (entries.isEmpty()) {
            showWelcomeMessage();
        }
    }

    private void showWelcomeMessage() {
        TextView welcomeText = new TextView(this);
        welcomeText.setText("📝 Добро пожаловать в твой личный дневник!\n\n" +
                "Здесь ты можешь записывать свои мысли, воспоминания и добавлять фото.\n\n" +
                "Нажми \"Новая запись\" чтобы начать!");
        welcomeText.setTextSize(16);
        welcomeText.setTextColor(getColor(android.R.color.darker_gray));
        welcomeText.setPadding(32, 32, 32, 32);
        welcomeText.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

        entriesContainer.addView(welcomeText);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Helper class for diary entries
    private static class DiaryEntry {
        long timestamp;
        String date;
        String text;
        String imageBase64;

        DiaryEntry(long timestamp, String date, String text, String imageBase64) {
            this.timestamp = timestamp;
            this.date = date;
            this.text = text;
            this.imageBase64 = imageBase64;
        }
    }
}