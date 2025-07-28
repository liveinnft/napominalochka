package com.napominalochka.app.ui.stats;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircularDiagram extends View {
    private Paint backgroundPaint, progressPaint, textPaint, markerPaint;
    private RectF rectF;
    private int days = 0;
    private String type = "";
    private int yearDays = 365;

    public CircularDiagram(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularDiagram(Context context) {
        super(context);
        init();
    }

    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.parseColor("#F0E6D2"));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(20);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(Color.parseColor("#D4A574"));
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(20);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#3E3427"));
        textPaint.setTextSize(48);
        textPaint.setTextAlign(Paint.Align.CENTER);

        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(Color.parseColor("#C17B5C"));
        markerPaint.setStrokeWidth(6);

        rectF = new RectF();
    }

    public void setDays(int days, String type) {
        this.days = days;
        this.type = type;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 50;
        
        int centerX = width / 2;
        int centerY = height / 2;

        rectF.set(centerX - radius, centerY - radius, 
                 centerX + radius, centerY + radius);

        // Draw background circle
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint);

        // Calculate progress (relative to year)
        float progress = Math.min(1.0f, (float) days / yearDays);
        float sweepAngle = 360 * progress;

        // Draw progress arc
        canvas.drawArc(rectF, -90, sweepAngle, false, progressPaint);

        // Draw month markers
        drawMonthMarkers(canvas, centerX, centerY, radius);

        // Draw center text
        String centerText = days + "\nдней";
        String[] lines = centerText.split("\n");
        
        textPaint.setTextSize(36);
        canvas.drawText(lines[0], centerX, centerY - 10, textPaint);
        textPaint.setTextSize(24);
        canvas.drawText(lines[1], centerX, centerY + 25, textPaint);
    }

    private void drawMonthMarkers(Canvas canvas, int centerX, int centerY, int radius) {
        // Draw ALL month markers (past and future)
        for (int month = 1; month <= 12; month++) {
            int monthDays = month * 30; // Approximate
            float angle = (float) (360.0 * monthDays / yearDays - 90);
            double radians = Math.toRadians(angle);
            
            // Choose color based on whether month is reached
            Paint monthMarkerPaint = new Paint(markerPaint);
            Paint monthTextPaint = new Paint(textPaint);
            monthTextPaint.setTextSize(16);
            
            if (monthDays <= days) {
                // Past/current months - bright color
                monthMarkerPaint.setColor(Color.parseColor("#C17B5C"));
                monthTextPaint.setColor(Color.parseColor("#C17B5C"));
            } else {
                // Future months - light gray
                monthMarkerPaint.setColor(Color.parseColor("#D0D0D0"));
                monthTextPaint.setColor(Color.parseColor("#B0B0B0"));
            }
            
            float startX = (float) (centerX + (radius - 15) * Math.cos(radians));
            float startY = (float) (centerY + (radius - 15) * Math.sin(radians));
            float endX = (float) (centerX + (radius + 15) * Math.cos(radians));
            float endY = (float) (centerY + (radius + 15) * Math.sin(radians));
            
            canvas.drawLine(startX, startY, endX, endY, monthMarkerPaint);
            
            // Draw month number
            float textX = (float) (centerX + (radius + 35) * Math.cos(radians));
            float textY = (float) (centerY + (radius + 35) * Math.sin(radians));
            canvas.drawText(month + "м", textX, textY, monthTextPaint);
        }
        
        // Draw birthday markers
        drawBirthdayMarkers(canvas, centerX, centerY, radius);
    }
    
    private void drawBirthdayMarkers(Canvas canvas, int centerX, int centerY, int radius) {
        // Boy's birthday: April 3 (day 93 approximately)
        int boyBirthdayDay = 31 + 29 + 31 + 3; // Jan + Feb + Mar + 3 days = 94
        drawBirthdayMarker(canvas, centerX, centerY, radius, boyBirthdayDay, "👦", Color.parseColor("#4A90E2"));
        
        // Girl's birthday: May 6 (day 126 approximately) 
        int girlBirthdayDay = 31 + 29 + 31 + 30 + 6; // Jan + Feb + Mar + Apr + 6 days = 127
        drawBirthdayMarker(canvas, centerX, centerY, radius, girlBirthdayDay, "👧", Color.parseColor("#E24A90"));
    }
    
    private void drawBirthdayMarker(Canvas canvas, int centerX, int centerY, int radius, int dayOfYear, String emoji, int color) {
        float angle = (float) (360.0 * dayOfYear / yearDays - 90);
        double radians = Math.toRadians(angle);
        
        // Draw heart marker
        Paint birthdayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        birthdayPaint.setColor(color);
        birthdayPaint.setStrokeWidth(8);
        
        float markerX = (float) (centerX + radius * Math.cos(radians));
        float markerY = (float) (centerY + radius * Math.sin(radians));
        
        // Draw heart shape (simplified as circle with heart emoji)
        canvas.drawCircle(markerX, markerY, 12, birthdayPaint);
        
        // Draw emoji
        Paint emojiPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emojiPaint.setTextSize(20);
        emojiPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(emoji, markerX, markerY + 7, emojiPaint);
        
        // Draw date label outside
        Paint datePaint = new Paint(textPaint);
        datePaint.setTextSize(12);
        datePaint.setColor(color);
        datePaint.setTextAlign(Paint.Align.CENTER);
        
        float labelX = (float) (centerX + (radius + 50) * Math.cos(radians));
        float labelY = (float) (centerY + (radius + 50) * Math.sin(radians));
        
        String dateLabel = emoji.equals("👦") ? "3 апр" : "6 мая";
        canvas.drawText(dateLabel, labelX, labelY, datePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), 
                           MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(size, size);
    }
}