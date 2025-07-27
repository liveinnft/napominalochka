package com.napominalochka.app.ui.generator;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.napominalochka.app.R;
import com.napominalochka.app.data.JoyGeneratorData;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class JoyGeneratorActivity extends AppCompatActivity {
    
    private TextView jokeText, factText, quoteText, memeText;
    private Button moreJokesBtn, moreFactsBtn, moreQuotesBtn, moreMemesBtn;
    private CardView jokeCard, factCard, quoteCard, memeCard;
    
    private JoyGeneratorData joyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joy_generator);

        joyData = new JoyGeneratorData();
        
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
        // Load daily content based on current date
        int dayOfYear = getDayOfYear();
        
        jokeText.setText(joyData.getDailyJoke(dayOfYear));
        factText.setText(joyData.getDailyFact(dayOfYear));
        quoteText.setText(joyData.getDailyQuote(dayOfYear));
        memeText.setText(joyData.getDailyMeme(dayOfYear));
    }

    private void setupButtons() {
        moreJokesBtn.setOnClickListener(v -> {
            animateCard(jokeCard);
            jokeText.setText(joyData.getRandomJoke());
        });
        
        moreFactsBtn.setOnClickListener(v -> {
            animateCard(factCard);
            factText.setText(joyData.getRandomFact());
        });
        
        moreQuotesBtn.setOnClickListener(v -> {
            animateCard(quoteCard);
            quoteText.setText(joyData.getRandomQuote());
        });
        
        moreMemesBtn.setOnClickListener(v -> {
            animateCard(memeCard);
            memeText.setText(joyData.getRandomMeme());
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}