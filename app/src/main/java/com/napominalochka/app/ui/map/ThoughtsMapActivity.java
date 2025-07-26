package com.napominalochka.app.ui.map;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.napominalochka.app.R;

public class ThoughtsMapActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView textView = new TextView(this);
        textView.setText("🗺️ Путеводитель по моим мыслям\n\nВ разработке...\n\nЗдесь будет интерактивная карта с 5 островами:\n• Остров Воспоминаний\n• Остров Шуток\n• Остров Поддержки\n• Остров Планов\n• Секретный остров");
        textView.setPadding(32, 32, 32, 32);
        textView.setTextSize(16);
        setContentView(textView);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.thoughts_map));
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}