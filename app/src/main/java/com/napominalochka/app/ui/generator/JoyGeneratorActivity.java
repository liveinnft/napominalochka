package com.napominalochka.app.ui.generator;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.napominalochka.app.R;

public class JoyGeneratorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView textView = new TextView(this);
        textView.setText("😊 Генератор радости\n\nВ разработке...\n\nЗдесь будут ежедневные:\n• Анекдот дня\n• Факт дня\n• Цитата дня\n• Мем дня\n\nС кнопкой 'Еще!' для дополнительного контента");
        textView.setPadding(32, 32, 32, 32);
        textView.setTextSize(16);
        setContentView(textView);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.joy_generator));
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}