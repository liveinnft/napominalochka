package com.napominalochka.app.ui.mission;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.napominalochka.app.R;

public class MissionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView textView = new TextView(this);
        textView.setText("🎯 Миссия: Не скучать\n\nВ разработке...\n\nЗдесь будут ежедневные задания:\n• Сфоткай закат\n• Напиши, о чем думаешь\n• Улыбнись 3 раза\n• И еще 30+ заданий!");
        textView.setPadding(32, 32, 32, 32);
        textView.setTextSize(16);
        setContentView(textView);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.mission));
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}