package com.napominalochka.app.ui.journey;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.napominalochka.app.R;

public class JourneyGameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView textView = new TextView(this);
        textView.setText("🎲 Ходилки: Путешествие к нам\n\nВ разработке...\n\nЗдесь будет игровая доска 5x4 с 20 клетками, где каждый день открывается новый сюрприз!");
        textView.setPadding(32, 32, 32, 32);
        textView.setTextSize(16);
        setContentView(textView);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.journey_game));
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}