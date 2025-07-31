package com.napominalochka.app.ui.secret;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.napominalochka.app.R;
import com.napominalochka.app.config.AppTexts;
import com.napominalochka.app.utils.SharedPrefsManager;

public class SecretSurpriseActivity extends AppCompatActivity {
    
    private TextView statusText, secretContentText, progressText;
    private Button unlockButton;
    private SharedPrefsManager prefsManager;


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
        progressText = findViewById(R.id.progress_text);
        unlockButton = findViewById(R.id.unlock_button);
        
        unlockButton.setOnClickListener(v -> showUnlockDialog());
    }

    private void updateUnlockStatus() {
        boolean isUnlocked = prefsManager.isSecretUnlocked();
        int foundWords = prefsManager.getFoundSecretWordsCount();
        
        if (isUnlocked) {
            showUnlockedContent();
        } else {
            showLockedContent(foundWords);
        }
    }

    private void showLockedContent(int foundWords) {
        statusText.setText("🔒 ЗАБЛОКИРОВАНО");
        progressText.setText("Найдено слов: " + foundWords + "/10");
        secretContentText.setText("Этот раздел будет разблокирован:\n\n" +
                "• После отгадывания 10 кодовых слов 🗝️\n" +
                "• После завершения путешествия 🎲\n" +
                "• Когда наступит особый день 📅\n\n" +
                "Внутри ждет самый важный сюрприз! 💝\n\n" +
                "Подсказка: эта то чего мы часта называем только половинкуу!");
        
        unlockButton.setText(getString(R.string.enter_code));
        unlockButton.setVisibility(Button.VISIBLE);
    }

    private void showUnlockedContent() {
        statusText.setText("🎁 РАЗБЛОКИРОВАНО!");
        progressText.setText("Найдено слов: 10/10");
        secretContentText.setText(AppTexts.SECRET_MESSAGE);
        
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
            
            boolean wordFound = false;
            for (String secretCode : AppTexts.SECRET_CODES) {
                if (enteredCode.equals(secretCode)) {
                    if (!prefsManager.isSecretWordFound(secretCode)) {
                        prefsManager.addFoundSecretWord(secretCode);
                        wordFound = true;
                        showWordFoundSuccess(secretCode);
                    } else {
                        showWordAlreadyFound(secretCode);
                    }
                    break;
                }
            }
            
            if (!wordFound) {
                showUnlockFailure();
            }
            
            updateUnlockStatus();
            
            // Проверяем, нужно ли разблокировать секрет
            if (prefsManager.getFoundSecretWordsCount() >= 10) {
                prefsManager.setSecretUnlocked(true);
                showUnlockSuccess();
                updateUnlockStatus();
            }
        });
        
        builder.setNegativeButton(getString(R.string.cancel), null);
        builder.show();
    }

    private void showWordFoundSuccess(String word) {
        int foundCount = prefsManager.getFoundSecretWordsCount();
        new AlertDialog.Builder(this)
                .setTitle("🎉 Отлично!")
                .setMessage("Ты нашла слово: " + word + "! 🔓\n\n" +
                           "Прогресс: " + foundCount + "/10 слов\n\n" +
                           "Продолжай искать остальные слова! 💝")
                .setPositiveButton("Продолжаю! 🥳", null)
                .show();
    }

    private void showWordAlreadyFound(String word) {
        new AlertDialog.Builder(this)
                .setTitle("📝 Уже найдено!")
                .setMessage("Слово '" + word + "' уже было найдено ранее!\n\n" +
                           "Попробуй найти другое слово! 💭")
                .setPositiveButton("Понятно 🤔", null)
                .show();
    }

    private void showUnlockSuccess() {
        new AlertDialog.Builder(this)
                .setTitle("🎉 Поздравляю!")
                .setMessage("Ты нашла все 10 кодовых слов! 🔓\n\n" +
                           "Секретный сюрприз теперь доступен! 💝")
                .setPositiveButton("Ура! 🥳", null)
                .show();
    }

    private void showUnlockFailure() {
        new AlertDialog.Builder(this)
                .setTitle("🔒 Неверный код")
                .setMessage("Попробуй еще раз! 💭\n\n")
                .setPositiveButton("Понятно 🤔", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}