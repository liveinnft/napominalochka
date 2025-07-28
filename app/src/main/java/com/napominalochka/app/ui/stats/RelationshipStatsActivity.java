package com.napominalochka.app.ui.stats;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.napominalochka.app.R;
import com.napominalochka.app.utils.SharedPrefsManager;

public class RelationshipStatsActivity extends AppCompatActivity {
    
    private SharedPrefsManager prefsManager;
    private TextView relationshipTitleText, relationshipDaysText;
    private TextView communicationTitleText, communicationDaysText;
    private CircularDiagram relationshipDiagram, communicationDiagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationship_stats);

        prefsManager = new SharedPrefsManager(this);
        
        initViews();
        updateStatistics();
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.relationship_stats));
        }
    }

    private void initViews() {
        relationshipTitleText = findViewById(R.id.relationship_title);
        relationshipDaysText = findViewById(R.id.relationship_days);
        relationshipDiagram = findViewById(R.id.relationship_diagram);
        
        communicationTitleText = findViewById(R.id.communication_title);
        communicationDaysText = findViewById(R.id.communication_days);
        communicationDiagram = findViewById(R.id.communication_diagram);
    }

    private void updateStatistics() {
        // Relationship stats
        int relationshipDays = prefsManager.getDaysTogether();
        relationshipTitleText.setText("💕 Дней в отношениях");
        relationshipDaysText.setText(relationshipDays + " дней");
        relationshipDiagram.setDays(relationshipDays, "relationship");
        
        // Communication stats  
        int communicationDays = prefsManager.getDaysCommunicating();
        communicationTitleText.setText("💬 Дней общения");
        communicationDaysText.setText(communicationDays + " дней");
        communicationDiagram.setDays(communicationDays, "communication");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Custom circular diagram view
    public static class CircularDiagram extends View {
        private Paint backgroundPaint, progressPaint, textPaint, markerPaint;
        private RectF rectF;
        private int days = 0;
        private String type = "";
        private int yearDays = 365;

        public CircularDiagram(Context context, AttributeSet attrs) {
            super(context, attrs);
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
            // Draw month markers (every 30 days approximately)
            for (int month = 1; month <= 12; month++) {
                int monthDays = month * 30; // Approximate
                if (monthDays <= days) {
                    float angle = (float) (360.0 * monthDays / yearDays - 90);
                    double radians = Math.toRadians(angle);
                    
                    float startX = (float) (centerX + (radius - 15) * Math.cos(radians));
                    float startY = (float) (centerY + (radius - 15) * Math.sin(radians));
                    float endX = (float) (centerX + (radius + 15) * Math.cos(radians));
                    float endY = (float) (centerY + (radius + 15) * Math.sin(radians));
                    
                    canvas.drawLine(startX, startY, endX, endY, markerPaint);
                    
                    // Draw month number
                    float textX = (float) (centerX + (radius + 35) * Math.cos(radians));
                    float textY = (float) (centerY + (radius + 35) * Math.sin(radians));
                    
                    Paint monthTextPaint = new Paint(textPaint);
                    monthTextPaint.setTextSize(16);
                    monthTextPaint.setColor(Color.parseColor("#C17B5C"));
                    canvas.drawText(month + "м", textX, textY, monthTextPaint);
                }
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int size = Math.min(MeasureSpec.getSize(widthMeasureSpec), 
                               MeasureSpec.getSize(heightMeasureSpec));
            setMeasuredDimension(size, size);
        }
    }
}