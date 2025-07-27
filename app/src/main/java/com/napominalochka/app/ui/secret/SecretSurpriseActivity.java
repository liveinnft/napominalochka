package com.napominalochka.app.ui.secret;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.napominalochka.app.R;
import com.napominalochka.app.utils.SharedPrefsManager;

public class SecretSurpriseActivity extends AppCompatActivity {
    
    private TextView statusText, secretContentText;
    private Button unlockButton;
    private SharedPrefsManager prefsManager;
    private static final String SECRET_CODE = "ЛЮБОВЬ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_surprise);
        
        prefsManager = new SharedPrefsManager(this);
        
        initViews();
        updateUnlockStatus();
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.secret_surprise));
        }
    }

    private void initViews() {
        statusText = findViewById(R.id.status_text);
        secretContentText = findViewById(R.id.secret_content_text);
        unlockButton = findViewById(R.id.unlock_button);
        
        unlockButton.setOnClickListener(v -> showUnlockDialog());
    }

    private void updateUnlockStatus() {
        boolean isUnlocked = prefsManager.isSecretUnlocked();
        
        if (isUnlocked) {
            showUnlockedContent();
        } else {
            showLockedContent();
        }
    }

    private void showLockedContent() {
        statusText.setText("🔒 ЗАБЛОКИРОВАНО");
        secretContentText.setText("Этот раздел будет разблокирован:\n\n" +
                "• По кодовому слову 🗝️\n" +
                "• После завершения путешествия 🎲\n" +
                "• Когда наступит особый день 📅\n\n" +
                "Внутри ждет самый важный сюрприз! 💝\n\n" +
                "Подсказка: это слово из 6 букв и описывает то, что я к тебе чувствую... ❤️");
        
        unlockButton.setText(getString(R.string.enter_code));
        unlockButton.setVisibility(Button.VISIBLE);
    }

    private void showUnlockedContent() {
        statusText.setText("🎁 РАЗБЛОКИРОВАНО!");
        secretContentText.setText("💖 ОСОБОЕ ПОСЛАНИЕ ДЛЯ ТЕБЯ 💖\n\n" +
                "Поздравляю! Ты нашла секретный ключ! 🗝️\n\n" +
                "Этот сюрприз был создан специально для тебя, потому что:\n\n" +
                "🌟 Ты освещаешь каждый мой день\n" +
                "💫 С тобой я чувствую себя самым счастливым\n" +
                "🦋 Ты превращаешь обычные моменты в волшебство\n" +
                "🌈 Твоя улыбка - мой самый любимый цвет\n" +
                "⭐ Ты - звезда, по которой я выбираю путь\n\n" +
                "Это приложение - маленькая частичка моей души, которую я дарю тебе. " +
                "Каждая строчка кода написана с мыслями о тебе. 💻💕\n\n" +
                "Спасибо за то, что ты есть в моей жизни! 🙏\n\n" +
                "С бесконечной любовью,\nТвой любящий разработчик 👨‍💻❤️");
        
        unlockButton.setVisibility(Button.GONE);
    }

    private void showUnlockDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🗝️ " + getString(R.string.enter_code));
        
        EditText input = new EditText(this);
        input.setHint("Введите кодовое слово...");
        builder.setView(input);
        
        builder.setPositiveButton(getString(R.string.unlock), (dialog, which) -> {
            String enteredCode = input.getText().toString().trim().toUpperCase();
            
            if (enteredCode.equals(SECRET_CODE)) {
                prefsManager.setSecretUnlocked(true);
                showUnlockSuccess();
                updateUnlockStatus();
            } else {
                showUnlockFailure();
            }
        });
        
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.show();
    }

    private void showUnlockSuccess() {
        new AlertDialog.Builder(this)
                .setTitle("🎉 Поздравляю!")
                .setMessage("Ты нашла правильное кодовое слово! 🔓\n\n" +
                           "Секретный сюрприз теперь доступен! 💝")
                .setPositiveButton("Ура! 🥳", null)
                .show();
    }

    private void showUnlockFailure() {
        new AlertDialog.Builder(this)
                .setTitle("🔒 Неверный код")
                .setMessage("Попробуй еще раз! 💭\n\n" +
                           "Подсказка: это самое важное чувство между нами... ❤️")
                .setPositiveButton("Понятно 🤔", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}