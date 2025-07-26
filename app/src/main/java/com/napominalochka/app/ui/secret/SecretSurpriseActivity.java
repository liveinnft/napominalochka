package com.napominalochka.app.ui.secret;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.napominalochka.app.R;

public class SecretSurpriseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView textView = new TextView(this);
        textView.setText("🎁 Секретный сюрприз\n\n🔒 ЗАБЛОКИРОВАНО\n\nЭтот раздел будет разблокирован:\n• По специальной дате\n• По кодовому слову\n• После выполнения всех заданий\n\nВнутри ждет особый сюрприз! 💝");
        textView.setPadding(32, 32, 32, 32);
        textView.setTextSize(16);
        setContentView(textView);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.secret_surprise));
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}