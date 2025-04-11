package com.example.sportify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private Button btnChoosePlan, btnViewHistory;
    private ImageButton btnSettings;
    private FitnessDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new FitnessDatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        if (!prefs.getBoolean("seeded", false)) {
            dbHelper.seedTestData();
            prefs.edit().putBoolean("seeded", true).apply();
        }

        // Initialize views
        btnChoosePlan = findViewById(R.id.btnChoosePlan);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        btnSettings = findViewById(R.id.btnSettings);
        Button btnStartFreestyle = findViewById(R.id.btnStartFreestyle);

        // Go to settings
        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
        });

        // Go to choose plan screen
        btnChoosePlan.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, WorkoutPlansActivity.class));
        });

        // Go to history
        btnViewHistory.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, HistoryActivity.class));
        });

        // Go to freestyle workouts
        btnStartFreestyle.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, StartWorkoutActivity.class));
        });
    }
}
