package com.napominalochka.app.ui.gallery;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.napominalochka.app.R;
import com.napominalochka.app.config.GalleryConfig;

import java.io.IOException;
import java.io.InputStream;

public class GalleryActivity extends AppCompatActivity {

    private GridLayout galleryGrid;
    private TextView headerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        initViews();
        setupGallery();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Наши моменты");
        }
    }

    private void initViews() {
        galleryGrid = findViewById(R.id.gallery_grid);
        headerText = findViewById(R.id.header_text);
        
        headerText.setText("📸 Наши особенные моменты 💕\n\nКаждое фото и видео хранит частичку нашей истории...");
    }

    private void setupGallery() {
        galleryGrid.removeAllViews();
        
        // Configure grid layout
        galleryGrid.setColumnCount(2);
        galleryGrid.setRowCount((GalleryConfig.GALLERY_ITEMS.length + 1) / 2);

        for (String[] item : GalleryConfig.GALLERY_ITEMS) {
            createMediaCard(item);
        }
        
        // If no items, show instruction
        if (GalleryConfig.GALLERY_ITEMS.length == 0) {
            showInstructionCard();
        }
    }

    private void createMediaCard(String[] item) {
        CardView card = new CardView(this);
        card.setRadius(16f);
        card.setCardElevation(8f);
        card.setUseCompatPadding(true);
        
        // Set card size
        GridLayout.LayoutParams cardParams = new GridLayout.LayoutParams();
        cardParams.width = 0;
        cardParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        cardParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
        cardParams.setMargins(8, 8, 8, 8);
        card.setLayoutParams(cardParams);

        // Create content view
        View contentView = createMediaContentView(item);
        card.addView(contentView);

        // Set click listener
        card.setOnClickListener(v -> showMediaDetails(item));
        
        // Add click effect
        card.setClickable(true);
        card.setFocusable(true);

        galleryGrid.addView(card);
    }

    private View createMediaContentView(String[] item) {
        if (GalleryConfig.isVideo(item)) {
            return createVideoPreview(item);
        } else {
            return createPhotoPreview(item);
        }
    }

    private View createPhotoPreview(String[] item) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new CardView.LayoutParams(
            CardView.LayoutParams.MATCH_PARENT, 
            300
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        
        try {
            InputStream is = getAssets().open("gallery/" + GalleryConfig.getFileName(item));
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            imageView.setImageBitmap(bitmap);
            is.close();
        } catch (IOException e) {
            // Show placeholder if file not found
            imageView.setImageResource(R.drawable.ic_heart);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
        }

        return imageView;
    }

    private View createVideoPreview(String[] item) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new CardView.LayoutParams(
            CardView.LayoutParams.MATCH_PARENT, 
            300
        ));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        
        String fileName = GalleryConfig.getFileName(item);
        String resourceName = fileName.replaceAll("\\.[^.]*$", ""); // Remove extension if any
        
        // Try to load from raw resources
        int resourceId = getResources().getIdentifier(resourceName, "raw", getPackageName());
        
        if (resourceId != 0) {
            try {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);
                retriever.setDataSource(this, videoUri);
                Bitmap thumbnail = retriever.getFrameAtTime(0);
                if (thumbnail != null) {
                    imageView.setImageBitmap(thumbnail);
                } else {
                    // Show play icon if no thumbnail
                    imageView.setImageResource(R.drawable.ic_heart);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                }
                retriever.release();
            } catch (Exception e) {
                // Show play icon if thumbnail fails
                imageView.setImageResource(R.drawable.ic_heart);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
            }
        } else {
            // Video not found, show placeholder
            imageView.setImageResource(R.drawable.ic_heart);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
        }

        // Add play icon overlay (optional)
        // You could create a FrameLayout and add a play button on top

        return imageView;
    }

    private void showMediaDetails(String[] item) {
        if (GalleryConfig.isVideo(item)) {
            showVideoDialog(item);
        } else {
            showPhotoDialog(item);
        }
    }

    private void showPhotoDialog(String[] item) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_photo_viewer, null);
        ImageView photoView = dialogView.findViewById(R.id.photo_view);
        TextView titleText = dialogView.findViewById(R.id.photo_title);
        TextView descriptionText = dialogView.findViewById(R.id.photo_description);

        try {
            InputStream is = getAssets().open("gallery/" + GalleryConfig.getFileName(item));
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            photoView.setImageBitmap(bitmap);
            is.close();
        } catch (IOException e) {
            photoView.setImageResource(R.drawable.ic_heart);
        }

        titleText.setText(GalleryConfig.getTitle(item));
        descriptionText.setText(GalleryConfig.getDescription(item));

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("💕 Закрыть", null)
                .show();
    }

    private void showVideoDialog(String[] item) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_video_viewer, null);
        VideoView videoView = dialogView.findViewById(R.id.video_view);
        TextView titleText = dialogView.findViewById(R.id.video_title);
        TextView descriptionText = dialogView.findViewById(R.id.video_description);

        String fileName = GalleryConfig.getFileName(item);
        String resourceName = fileName.replaceAll("\\.[^.]*$", ""); // Remove extension if any
        
        // Try to load from raw resources
        int resourceId = getResources().getIdentifier(resourceName, "raw", getPackageName());
        
        if (resourceId != 0) {
            // Video found in raw resources
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);
            videoView.setVideoURI(videoUri);
            
            videoView.setOnPreparedListener(mp -> {
                try {
                    mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    mp.setLooping(true);
                    mp.setVolume(0f, 0f); // Mute
                    videoView.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            
            videoView.setOnErrorListener((mp, what, extra) -> {
                String errorMsg = "❌ Ошибка воспроизведения: " + fileName;
                if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    errorMsg += "\n🔧 Медиа-сервер недоступен";
                } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                    errorMsg += "\n❓ Неизвестная ошибка медиа";
                } else {
                    errorMsg += "\n📋 Код ошибки: " + what + "/" + extra;
                }
                errorMsg += "\n\n💡 Убедитесь что:\n- Файл размещен в res/raw/\n- Имя без расширения\n- Формат: .mp4, .3gp, .webm";
                descriptionText.setText(errorMsg);
                return true;
            });
            
        } else {
            // Video not found
            descriptionText.setText("❌ Видео не найдено: " + fileName + 
                "\n\n📁 Разместите видео файл в:\napp/src/main/res/raw/" + resourceName + 
                "\n\n💡 Имя файла БЕЗ расширения!\nПример: video1.mp4 → video1");
        }

        titleText.setText(GalleryConfig.getTitle(item));
        descriptionText.setText(GalleryConfig.getDescription(item));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("💕 Закрыть", null)
                .create();
                
        dialog.setOnDismissListener(d -> {
            if (videoView.isPlaying()) {
                videoView.stopPlayback();
            }
        });
        
        dialog.show();
    }

    private void showInstructionCard() {
        TextView instructionText = new TextView(this);
        instructionText.setText("📝 Инструкция:\n\n" +
                "1. Добавьте фото и видео в папку:\n" +
                "   app/src/main/assets/gallery/\n\n" +
                "2. Обновите GalleryConfig.java:\n" +
                "   - Добавьте описания к каждому файлу\n\n" +
                "3. Перезапустите приложение\n\n" +
                "💡 Поддерживаемые форматы:\n" +
                "   Фото: .jpg, .png, .jpeg\n" +
                "   Видео: .mp4, .webm, .mov");
        instructionText.setTextSize(14);
        instructionText.setPadding(32, 32, 32, 32);
        
        CardView instructionCard = new CardView(this);
        instructionCard.setRadius(16f);
        instructionCard.setCardElevation(8f);
        instructionCard.setUseCompatPadding(true);
        instructionCard.addView(instructionText);
        
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = GridLayout.LayoutParams.MATCH_PARENT;
        params.columnSpec = GridLayout.spec(0, 2);
        instructionCard.setLayoutParams(params);
        
        galleryGrid.addView(instructionCard);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}