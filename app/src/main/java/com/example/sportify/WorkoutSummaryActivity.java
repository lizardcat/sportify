package com.example.sportify;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
public class WorkoutSummaryActivity extends AppCompatActivity {

    private TextView tvSummaryTitle, tvSummaryDuration, tvSummaryCalories, tvSummaryNotes, tvSummaryText;
    private Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_summary);

        tvSummaryTitle = findViewById(R.id.tvSummaryTitle);
        tvSummaryDuration = findViewById(R.id.tvSummaryDuration);
        tvSummaryCalories = findViewById(R.id.tvSummaryCalories);
        tvSummaryNotes = findViewById(R.id.tvSummaryNotes);
        tvSummaryText = findViewById(R.id.tvSummaryText);
        btnDone = findViewById(R.id.btnDone);

        // Get data from intent
        String summary = getIntent().getStringExtra("summary");
        String activityType = getIntent().getStringExtra("activityType");
        double duration = getIntent().getDoubleExtra("duration", 0.0);
        double calories = getIntent().getDoubleExtra("calories", 0.0);
        String planName = getIntent().getStringExtra("plan_name");
        String dayName = getIntent().getStringExtra("day_name");
        String notes = getIntent().getStringExtra("notes");

        // Title (Plan + Day if available)
        if (planName != null && dayName != null) {
            tvSummaryTitle.setText(planName + " - " + dayName);
        } else if (activityType != null) {
            tvSummaryTitle.setText("Workout Summary: " + activityType);
        } else {
            tvSummaryTitle.setText("Workout Summary");
        }

        // Duration
        tvSummaryDuration.setText(String.format(Locale.getDefault(), "⏱️ Duration: %.1f minutes", duration));

        // Calories
        tvSummaryCalories.setText(String.format(Locale.getDefault(), "🔥 Calories Burned: %.1f kcal", calories));

        // Notes
        if (notes != null && !notes.trim().isEmpty()) {
            tvSummaryNotes.setText("📝 Notes: " + notes);
        } else {
            tvSummaryNotes.setText("📝 Notes: None");
        }

        // Exercise summary or fallback summary string
        if (summary != null && !summary.trim().isEmpty()) {
            tvSummaryText.setText(summary.trim());
        } else {
            tvSummaryText.setText("✓ Workout completed successfully.");
        }

        // Finish button
        btnDone.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Workout Saved ✅")
                    .setMessage("Your workout has been successfully saved.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                        setResult(RESULT_OK); // optional if using startActivityForResult
                        finish(); // go back to Dashboard
                    })
                    .setCancelable(false)
                    .show();
        });

    }
}
