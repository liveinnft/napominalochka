package com.napominalochka.app.ui.gallery;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
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
            // Video not found, show video icon
            imageView.setImageResource(R.drawable.ic_video_play);
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
        // Полная версия с воспроизведением видео
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_video_player, null);
        
        TextView titleText = dialogView.findViewById(R.id.video_title);
        TextView descriptionText = dialogView.findViewById(R.id.video_description);
        SurfaceView surfaceView = dialogView.findViewById(R.id.surface_view);
        ProgressBar loadingProgress = dialogView.findViewById(R.id.loading_progress);
        TextView statusText = dialogView.findViewById(R.id.status_text);
        Button playPauseButton = dialogView.findViewById(R.id.play_pause_button);
        Button closeButton = dialogView.findViewById(R.id.close_button);
        
        // Set basic info
        titleText.setText("🎬 " + GalleryConfig.getTitle(item));
        descriptionText.setText(GalleryConfig.getDescription(item));
        
        String fileName = GalleryConfig.getFileName(item);
        String resourceName = fileName.replaceAll("\\.[^.]*$", ""); // Remove extension if any
        
        // Check if video exists
        int resourceId = getResources().getIdentifier(resourceName, "raw", getPackageName());
        
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();
        
        if (resourceId != 0) {
            // Video found - initialize MediaPlayer
            setupVideoPlayer(surfaceView, resourceId, loadingProgress, statusText, playPauseButton, dialog);
        } else {
            // Video not found
            loadingProgress.setVisibility(View.GONE);
            statusText.setText("❌ Видео не найдено: " + fileName + 
                "\n\n📁 Разместите видео в:\napp/src/main/res/raw/" + resourceName);
            playPauseButton.setEnabled(false);
        }
        
        closeButton.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    private void setupVideoPlayer(SurfaceView surfaceView, int resourceId, ProgressBar loadingProgress, 
                                 TextView statusText, Button playPauseButton, AlertDialog dialog) {
        
        MediaPlayer mediaPlayer = new MediaPlayer();
        final boolean[] isPlaying = {false};
        final boolean[] isPrepared = {false};
        
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mediaPlayer.setDisplay(holder);
                    
                    // Load video from raw resources
                    Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);
                    mediaPlayer.setDataSource(GalleryActivity.this, videoUri);
                    
                    statusText.setText("⏳ Подготовка видео...");
                    
                    mediaPlayer.setOnPreparedListener(mp -> {
                        isPrepared[0] = true;
                        loadingProgress.setVisibility(View.GONE);
                        statusText.setVisibility(View.GONE);
                        playPauseButton.setEnabled(true);
                        
                        // Adjust SurfaceView size to video aspect ratio
                        int videoWidth = mp.getVideoWidth();
                        int videoHeight = mp.getVideoHeight();
                        if (videoWidth > 0 && videoHeight > 0) {
                            // Use post to ensure container is properly laid out
                            surfaceView.post(() -> adjustSurfaceViewSize(surfaceView, videoWidth, videoHeight));
                        }
                        
                        // Auto-start video
                        mp.setLooping(true);
                        mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                        mp.start();
                        isPlaying[0] = true;
                        playPauseButton.setText("⏸️ Пауза");
                        
                        // Mute audio
                        mp.setVolume(0f, 0f);
                    });
                    
                    mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                        loadingProgress.setVisibility(View.GONE);
                        String errorMsg = "❌ Ошибка воспроизведения\n";
                        
                        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                            errorMsg += "🔧 Медиа-сервер недоступен";
                        } else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                            errorMsg += "❓ Неизвестная ошибка";
                        } else {
                            errorMsg += "📋 Код: " + what + "/" + extra;
                        }
                        
                        statusText.setText(errorMsg);
                        statusText.setVisibility(View.VISIBLE);
                        playPauseButton.setEnabled(false);
                        return true;
                    });
                    
                    mediaPlayer.prepareAsync();
                    
                } catch (Exception e) {
                    loadingProgress.setVisibility(View.GONE);
                    statusText.setText("❌ Ошибка загрузки:\n" + e.getMessage());
                    statusText.setVisibility(View.VISIBLE);
                    playPauseButton.setEnabled(false);
                }
            }
            
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                // Surface changed
            }
            
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer.release();
                }
            }
        });
        
        // Play/Pause button logic
        playPauseButton.setOnClickListener(v -> {
            if (isPrepared[0] && mediaPlayer != null) {
                if (isPlaying[0]) {
                    mediaPlayer.pause();
                    isPlaying[0] = false;
                    playPauseButton.setText("▶️ Играть");
                } else {
                    mediaPlayer.start();
                    isPlaying[0] = true;
                    playPauseButton.setText("⏸️ Пауза");
                }
            }
        });
        
        // Cleanup on dialog dismiss
        dialog.setOnDismissListener(d -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            }
        });
    }
    
    private void adjustSurfaceViewSize(SurfaceView surfaceView, int videoWidth, int videoHeight) {
        // Get the container size
        View container = (View) surfaceView.getParent();
        int containerWidth = container.getWidth();
        int containerHeight = container.getHeight();
        
        if (containerWidth <= 0 || containerHeight <= 0) {
            // Container not ready yet, use default values
            containerWidth = 800;
            containerHeight = 600;
        }
        
        // Calculate aspect ratios
        float videoAspect = (float) videoWidth / videoHeight;
        float containerAspect = (float) containerWidth / containerHeight;
        
        int newWidth, newHeight;
        
        if (videoAspect > containerAspect) {
            // Video is wider than container - fit by width
            newWidth = containerWidth;
            newHeight = (int) (containerWidth / videoAspect);
        } else {
            // Video is taller than container - fit by height
            newHeight = containerHeight;
            newWidth = (int) (containerHeight * videoAspect);
        }
        
        // Apply new size to SurfaceView
        ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
        layoutParams.width = newWidth;
        layoutParams.height = newHeight;
        surfaceView.setLayoutParams(layoutParams);
        
        // Log for debugging
        android.util.Log.d("VideoResize", String.format(
            "Video: %dx%d (%.2f), Container: %dx%d (%.2f), New: %dx%d", 
            videoWidth, videoHeight, videoAspect,
            containerWidth, containerHeight, containerAspect,
            newWidth, newHeight
        ));
    }
    
    private void showVideoInfo(String resourceName, int resourceId) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);
            retriever.setDataSource(this, videoUri);
            
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            
            StringBuilder info = new StringBuilder();
            info.append("📹 Информация о видео:\n\n");
            info.append("📁 Файл: ").append(resourceName).append("\n");
            info.append("🆔 ID ресурса: ").append(resourceId).append("\n");
            
            if (duration != null) {
                long durationMs = Long.parseLong(duration);
                long seconds = durationMs / 1000;
                info.append("⏱️ Длительность: ").append(seconds).append(" сек\n");
            }
            
            if (width != null && height != null) {
                info.append("📐 Разрешение: ").append(width).append("x").append(height).append("\n");
            }
            
            info.append("\n✅ Видео корректно загружено!");
            
            retriever.release();
            
            new AlertDialog.Builder(this)
                    .setTitle("🔧 Техническая информация")
                    .setMessage(info.toString())
                    .setPositiveButton("OK", null)
                    .show();
                    
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setTitle("❌ Ошибка")
                    .setMessage("Не удалось получить информацию о видео:\n" + e.getMessage())
                    .setPositiveButton("OK", null)
                    .show();
        }
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
    
    private void addPlayIconOverlay(ImageView imageView) {
        // Простой способ показать что это видео - изменить прозрачность
        imageView.setAlpha(0.8f);
        
        // В реальном приложении здесь можно добавить overlay с иконкой play
        // Для простоты просто делаем изображение немного прозрачным
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}