package com.example.sportify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private Button btnChoosePlan, btnViewHistory, btnContinuePlan;
    private TextView tvCurrentPlan, tvPlanProgress, tvNextWorkout;
    private ImageButton btnSettings;

    private FitnessDatabaseHelper dbHelper;
    private long currentPlanId = -1;

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
        btnContinuePlan = findViewById(R.id.btnContinuePlan);
        btnSettings = findViewById(R.id.btnSettings);
        tvCurrentPlan = findViewById(R.id.tvCurrentPlan);
        tvPlanProgress = findViewById(R.id.tvPlanProgress);
        tvNextWorkout = findViewById(R.id.tvNextWorkout);

        dbHelper = new FitnessDatabaseHelper(this);
        loadCurrentPlanInfo();

        // Go to settings
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Go to choose plan screen
        btnChoosePlan.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, WorkoutPlansActivity.class);
            startActivity(intent);
        });

        // Go to plan days
        btnContinuePlan.setOnClickListener(v -> {
            if (currentPlanId != -1) {
                Intent intent = new Intent(DashboardActivity.this, PlanDaysActivity.class);
                intent.putExtra("plan_id", currentPlanId);
                startActivity(intent);
            }
        });

        // Go to history
        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        // Go to freestyle workouts
        Button btnStartFreestyle = findViewById(R.id.btnStartFreestyle);
        btnStartFreestyle.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, StartWorkoutActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCurrentPlanInfo();
    }


    private void loadCurrentPlanInfo() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        currentPlanId = dbHelper.getFirstPlanId(); // You can improve this to get last selected plan

        if (currentPlanId != -1) {
            String planName = dbHelper.getPlanNameById(currentPlanId);
            FitnessDatabaseHelper.PlanProgress progress = dbHelper.getPlanProgress(currentPlanId);

            tvCurrentPlan.setText("Current Plan: " + planName);
            tvPlanProgress.setText("Progress: Day " + (progress.completedDays + 1) + " of " + progress.totalDays);
            tvNextWorkout.setText("Next: " + progress.nextDayTitle);
        } else {
            tvCurrentPlan.setText("No active plan");
            tvPlanProgress.setText("");
            tvNextWorkout.setText("");
            btnContinuePlan.setEnabled(false);
        }
    }
}
