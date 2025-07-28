package com.napominalochka.app.ui.stats;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.napominalochka.app.R;
import com.napominalochka.app.utils.SharedPrefsManager;

public class RelationshipStatsActivity extends AppCompatActivity {
    
    private SharedPrefsManager prefsManager;
    private TextView relationshipTitleText, relationshipDaysText;
    private TextView communicationTitleText, communicationDaysText;
    private CircularDiagram relationshipDiagram, communicationDiagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationship_stats);

        prefsManager = new SharedPrefsManager(this);
        
        initViews();
        updateStatistics();
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.relationship_stats));
        }
    }

    private void initViews() {
        relationshipTitleText = findViewById(R.id.relationship_title);
        relationshipDaysText = findViewById(R.id.relationship_days);
        relationshipDiagram = findViewById(R.id.relationship_diagram);
        
        communicationTitleText = findViewById(R.id.communication_title);
        communicationDaysText = findViewById(R.id.communication_days);
        communicationDiagram = findViewById(R.id.communication_diagram);
    }

    private void updateStatistics() {
        // Relationship stats
        int relationshipDays = prefsManager.getDaysTogether();
        relationshipTitleText.setText("💕 Дней в отношениях");
        relationshipDaysText.setText(relationshipDays + " дней");
        relationshipDiagram.setDays(relationshipDays, "relationship");
        
        // Communication stats  
        int communicationDays = prefsManager.getDaysCommunicating();
        communicationTitleText.setText("💬 Дней общения");
        communicationDaysText.setText(communicationDays + " дней");
        communicationDiagram.setDays(communicationDays, "communication");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}