package com.napominalochka.app.ui.diary;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.napominalochka.app.R;

public class DiaryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView textView = new TextView(this);
        textView.setText("📔 Дневник путешествия\n\nВ разработке...\n\nЗдесь будет личный дневник с:\n• Созданием записей\n• Шаблонами записей\n• Добавлением фото\n• Категориями\n• Поиском\n• Экспортом в PDF");
        textView.setPadding(32, 32, 32, 32);
        textView.setTextSize(16);
        setContentView(textView);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.diary));
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}