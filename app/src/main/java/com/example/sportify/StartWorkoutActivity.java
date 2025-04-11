package com.example.sportify;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StartWorkoutActivity extends AppCompatActivity {

    private Spinner spinnerActivityType;
    private TextView textTimer;
    private Button btnStartStop;

    private Button btnFinishWorkout;
    private EditText editNotes, editSets, editReps, editWeight;
    private LinearLayout workoutSection;
    private View strengthFields;

    private boolean isRunning = false;
    private long startTime = 0L;
    private Handler handler = new Handler();
    private Runnable timerRunnable;

    private FitnessDatabaseHelper dbHelper;
    private double userWeightKg = 70.0;
    private boolean isPaused = false;
    private long pausedTime = 0L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_workout);

        spinnerActivityType = findViewById(R.id.spinnerActivityType);
        textTimer = findViewById(R.id.textTimer);
        btnStartStop = findViewById(R.id.btnStartStop);
        editNotes = findViewById(R.id.editNotes);
        editSets = findViewById(R.id.editSets);
        editReps = findViewById(R.id.editReps);
        editWeight = findViewById(R.id.editWeight);
        workoutSection = findViewById(R.id.workoutSection);
        strengthFields = findViewById(R.id.strengthFields);
        btnFinishWorkout = findViewById(R.id.btnFinishWorkout);

        dbHelper = new FitnessDatabaseHelper(this);
        userWeightKg = dbHelper.getUserWeightKg();

        // Set spinner items
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.activity_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivityType.setAdapter(adapter);

        spinnerActivityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = parent.getItemAtPosition(pos).toString();
                if (!selected.equals("Select Workout")) {
                    workoutSection.setVisibility(View.VISIBLE);
                    if (selected.equalsIgnoreCase("Strength")) {
                        strengthFields.setVisibility(View.VISIBLE);
                    } else {
                        strengthFields.setVisibility(View.GONE);
                    }
                } else {
                    workoutSection.setVisibility(View.GONE);
                    strengthFields.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Timer logic
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedMillis = System.currentTimeMillis() - startTime;
                int seconds = (int) (elapsedMillis / 1000);
                int minutes = seconds / 60;
                int hours = minutes / 60;
                seconds = seconds % 60;
                minutes = minutes % 60;

                String timeStr = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                textTimer.setText(timeStr);
                handler.postDelayed(this, 1000);
            }
        };

        btnStartStop.setOnClickListener(v -> {
            if (!isRunning && !isPaused) {
                // START
                isRunning = true;
                startTime = System.currentTimeMillis();
                handler.post(timerRunnable);
                btnStartStop.setText("Pause");

            } else if (isRunning && !isPaused) {
                // PAUSE
                isPaused = true;
                pausedTime = System.currentTimeMillis();
                handler.removeCallbacks(timerRunnable);
                btnStartStop.setText("Resume");

            } else if (isRunning && isPaused) {
                // RESUME
                isPaused = false;
                long now = System.currentTimeMillis();
                // Adjust start time to account for pause duration
                startTime += (now - pausedTime);
                handler.post(timerRunnable);
                btnStartStop.setText("Pause");

            } else {
                // STOP (final step, not triggered by this button for now)
                handler.removeCallbacks(timerRunnable);
                btnStartStop.setText("Start");
                isRunning = false;
                isPaused = false;

                long endTime = System.currentTimeMillis();
                double durationMin = (endTime - startTime) / 60000.0;

                saveWorkout(durationMin);
            }
        });

        btnFinishWorkout.setOnClickListener(v -> {
            if (isRunning) {
                handler.removeCallbacks(timerRunnable);
                isRunning = false;
                isPaused = false;

                double durationMin = (System.currentTimeMillis() - startTime) / 60000.0;
                saveWorkout(durationMin);
            } else {
                Toast.makeText(this, "Workout is not running.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void saveWorkout(double durationMin) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String startTimeStr = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date(startTime));
        String endTimeStr = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String activityType = spinnerActivityType.getSelectedItem().toString();
        String notes = editNotes.getText().toString();

        // Strength details
        int setsVal = 0, repsVal = 0;
        double weightVal = 0.0;

        if (activityType.equalsIgnoreCase("Strength")) {
            try {
                setsVal = Integer.parseInt(editSets.getText().toString().trim());
                repsVal = Integer.parseInt(editReps.getText().toString().trim());
                weightVal = Double.parseDouble(editWeight.getText().toString().trim());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid sets/reps/weight input.", Toast.LENGTH_SHORT).show();
            }
        }

        double calories = estimateCalories(activityType, durationMin);

        dbHelper.insertWorkout(date, startTimeStr, endTimeStr, activityType, durationMin, 0.0, calories, setsVal, repsVal, weightVal, notes);

        // Format summary string
        String summary = "Workout Summary:\n"
                + "Type: " + activityType + "\n"
                + "Duration: " + String.format(Locale.getDefault(), "%.1f", durationMin) + " minutes\n";

        if (activityType.equalsIgnoreCase("Strength")) {
            summary += "Sets: " + setsVal + ", Reps: " + repsVal + ", Weight: " + weightVal + "kg\n";
        }

        if (!notes.isEmpty()) {
            summary += "Notes: " + notes;
        }

        // Launch summary screen
        Intent intent = new Intent(StartWorkoutActivity.this, WorkoutSummaryActivity.class);
        intent.putExtra("summary", summary);
        intent.putExtra("activityType", activityType);
        intent.putExtra("duration", durationMin);
        intent.putExtra("calories", calories);
        intent.putExtra("notes", notes);
        startActivity(intent);
        finish();
    }


    private double estimateCalories(String activityType, double durationMin) {
        double met;
        switch (activityType) {
            case "Running": met = 9.8; break;
            case "Cycling": met = 7.5; break;
            case "Walking": met = 3.5; break;
            case "Strength": met = 6.0; break;
            default: met = 4.0;
        }
        return (met * userWeightKg * (durationMin / 60.0));
    }
}
