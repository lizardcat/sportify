package com.example.sportify;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TrackWorkoutActivity extends AppCompatActivity {

    private TextView tvTimer;
    private LinearLayout setContainer;
    private Button btnFinish, btnPauseResume;

    private boolean isRunning = false;
    private boolean isPaused = false;
    private long startTime;
    private long pausedTime;
    private Handler handler = new Handler();
    private Runnable timerRunnable;

    private String planName;
    private String dayName;
    private long dayId = -1;

    private FitnessDatabaseHelper dbHelper;

    private EditText editNotes;
    private ArrayList<String> exercisesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_workout);

        dbHelper = new FitnessDatabaseHelper(this);

        tvTimer = findViewById(R.id.tvWorkoutTimer);
        setContainer = findViewById(R.id.setContainer);
        btnFinish = findViewById(R.id.btnFinishWorkout);
        btnPauseResume = findViewById(R.id.btnPauseResume);
        editNotes = findViewById(R.id.editNotes);

        planName = getIntent().getStringExtra("plan_name");
        dayName = getIntent().getStringExtra("day_name");
        dayId = getIntent().getLongExtra("day_id", -1);

        loadExercisesFromDatabase();
        startTimer();

        btnPauseResume.setOnClickListener(v -> {
            if (isPaused) {
                long now = System.currentTimeMillis();
                startTime += (now - pausedTime);
                isPaused = false;
                btnPauseResume.setText("Pause");
                handler.post(timerRunnable);
            } else {
                isPaused = true;
                pausedTime = System.currentTimeMillis();
                btnPauseResume.setText("Resume");
                handler.removeCallbacks(timerRunnable);
            }
        });

        btnFinish.setOnClickListener(v -> {
            stopTimer();

            String timeStr = tvTimer.getText().toString();
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String notes = editNotes.getText().toString().trim();

            dbHelper.insertCompletedWorkout(
                    date,
                    planName != null ? planName : "Custom Plan",
                    dayName != null ? dayName : "Workout Session",
                    dayId,
                    timeStr,
                    notes
            );


            Toast.makeText(this, "Workout complete!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(TrackWorkoutActivity.this, WorkoutSummaryActivity.class);
            intent.putExtra("plan_name", planName);
            intent.putExtra("day_name", dayName);
            intent.putExtra("duration", timeStr);
            intent.putExtra("exercises", exercisesList.toArray(new String[0]));
            intent.putExtra("notes", notes); // ← Add this
            startActivity(intent);

            startActivity(intent);
            finish();
        });
    }

    private void loadExercisesFromDatabase() {
        if (dayId == -1) {
            Toast.makeText(this, "Error: No day ID provided!", Toast.LENGTH_SHORT).show();
            return;
        }


        Log.d("TrackWorkout", "Loading exercises for day ID: " + dayId);
        Cursor cursor = dbHelper.getExercisesForDay(dayId);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No exercises found for this workout day.", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            int sets = cursor.getInt(cursor.getColumnIndexOrThrow("sets"));
            int reps = cursor.getInt(cursor.getColumnIndexOrThrow("reps"));

            exercisesList.add(name + " - " + sets + " x " + reps);
            addExerciseCard(name, sets, reps);
        }
        cursor.close();
    }

    private void addExerciseCard(String name, int sets, int reps) {
        View exerciseCard = getLayoutInflater().inflate(R.layout.item_exercise_set_card, null);

        TextView tvExerciseName = exerciseCard.findViewById(R.id.tvExerciseSetTitle);
        tvExerciseName.setText(name);

        LinearLayout setList = exerciseCard.findViewById(R.id.exerciseSetList);

        for (int i = 1; i <= sets; i++) {
            View setEntry = getLayoutInflater().inflate(R.layout.item_set_entry, null);

            TextView tvSetLabel = setEntry.findViewById(R.id.tvSetNumber);
            tvSetLabel.setText("Set " + i);

            // Optionally access EditTexts:
            EditText editRepsDone = setEntry.findViewById(R.id.editRepsDone);
            EditText editWeightUsed = setEntry.findViewById(R.id.editWeightUsed);

            setList.addView(setEntry);
        }


        setContainer.addView(exerciseCard);
    }

    private void startTimer() {
        isRunning = true;
        startTime = System.currentTimeMillis();

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isRunning || isPaused) return;

                long elapsed = System.currentTimeMillis() - startTime;
                int seconds = (int) (elapsed / 1000);
                int minutes = seconds / 60;
                int hours = minutes / 60;
                seconds = seconds % 60;
                minutes = minutes % 60;

                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(timerRunnable);
    }

    private void stopTimer() {
        isRunning = false;
        handler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timerRunnable);
    }
}
