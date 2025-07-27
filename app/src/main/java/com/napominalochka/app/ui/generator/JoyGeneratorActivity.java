package com.napominalochka.app.ui.generator;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.napominalochka.app.R;
import com.napominalochka.app.config.AppTexts;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class JoyGeneratorActivity extends AppCompatActivity {
    
    private TextView jokeText, factText, quoteText, memeText;
    private Button moreJokesBtn, moreFactsBtn, moreQuotesBtn, moreMemesBtn;
    private CardView jokeCard, factCard, quoteCard, memeCard;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joy_generator);


        
        initViews();
        loadDailyContent();
        setupButtons();
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.joy_generator));
        }
    }

    private void initViews() {
        jokeText = findViewById(R.id.joke_text);
        factText = findViewById(R.id.fact_text);
        quoteText = findViewById(R.id.quote_text);
        memeText = findViewById(R.id.meme_text);
        
        moreJokesBtn = findViewById(R.id.more_jokes_btn);
        moreFactsBtn = findViewById(R.id.more_facts_btn);
        moreQuotesBtn = findViewById(R.id.more_quotes_btn);
        moreMemesBtn = findViewById(R.id.more_memes_btn);
        
        jokeCard = findViewById(R.id.joke_card);
        factCard = findViewById(R.id.fact_card);
        quoteCard = findViewById(R.id.quote_card);
        memeCard = findViewById(R.id.meme_card);
    }

    private void loadDailyContent() {
        // Load daily content from central config
        int dayOfYear = getDayOfYear();
        
        jokeText.setText(getDailyContent(AppTexts.JOKES, dayOfYear));
        factText.setText(getDailyContent(AppTexts.FACTS, dayOfYear));
        quoteText.setText("💕 " + getDailyContent(AppTexts.LOVE_MESSAGES, dayOfYear));
        memeText.setText("🎭 Сегодня особенный день для смеха и радости!");
    }

    private void setupButtons() {
                moreJokesBtn.setOnClickListener(v -> {
            animateCard(jokeCard);
            jokeText.setText(getRandomContent(AppTexts.JOKES));
        });

        moreFactsBtn.setOnClickListener(v -> {
            animateCard(factCard);
            factText.setText(getRandomContent(AppTexts.FACTS));
        });

        moreQuotesBtn.setOnClickListener(v -> {
            animateCard(quoteCard);
            quoteText.setText("💕 " + getRandomContent(AppTexts.LOVE_MESSAGES));
        });

        moreMemesBtn.setOnClickListener(v -> {
            animateCard(memeCard);
            memeText.setText("🎭 " + getRandomContent(AppTexts.LOVE_MESSAGES));
        });
    }

    private void animateCard(CardView card) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(card, "rotationY", 0f, 360f);
        rotation.setDuration(600);
        rotation.start();
    }

    private int getDayOfYear() {
        SimpleDateFormat dayFormat = new SimpleDateFormat("DDD", Locale.getDefault());
        return Integer.parseInt(dayFormat.format(new Date()));
    }

    private String getDailyContent(String[] contentArray, int dayOfYear) {
        if (contentArray.length == 0) return "Контент скоро появится!";
        return contentArray[dayOfYear % contentArray.length];
    }
    
    private String getRandomContent(String[] contentArray) {
        if (contentArray.length == 0) return "Контент скоро появится!";
        return contentArray[new java.util.Random().nextInt(contentArray.length)];
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}