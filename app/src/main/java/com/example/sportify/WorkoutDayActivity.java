package com.example.sportify;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WorkoutDayActivity extends AppCompatActivity {

    private static final String TAG = "WorkoutDayActivity";

    private TextView tvDayTitle;
    private LinearLayout exerciseListLayout;
    private Button btnStartWorkout;
    private long dayId;
    private String dayTitle;
    private FitnessDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_day);

        tvDayTitle = findViewById(R.id.tvDayTitle);
        exerciseListLayout = findViewById(R.id.exerciseListLayout);
        btnStartWorkout = findViewById(R.id.btnStartWorkout);
        dbHelper = new FitnessDatabaseHelper(this);

        // Safely get intent extras with null checks
        if (getIntent() != null) {
            dayId = getIntent().getLongExtra("day_id", -1);
            dayTitle = getIntent().getStringExtra("day_title");
        } else {
            Log.w(TAG, "Intent is null in WorkoutDayActivity");
            dayId = -1;
        }

        if (dayId == -1) {
            Log.e(TAG, "Invalid day ID");
            tvDayTitle.setText("Missing Day");
            Toast.makeText(this, "Error: Invalid workout day", Toast.LENGTH_SHORT).show();
            return;
        }

        tvDayTitle.setText(dayTitle != null ? dayTitle : "Workout Day");
        loadExercises();

        btnStartWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(this, TrackWorkoutActivity.class);
            intent.putExtra("day_id", dayId);
            startActivity(intent);
        });
    }

    private void loadExercises() {
        Cursor cursor = null;
        try {
            cursor = dbHelper.getExercisesForDay(dayId);
            if (cursor == null) {
                Log.w(TAG, "Cursor is null when loading exercises");
                Toast.makeText(this, "Error loading exercises", Toast.LENGTH_SHORT).show();
                return;
            }

            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                int sets = cursor.getInt(cursor.getColumnIndexOrThrow("sets"));
                int reps = cursor.getInt(cursor.getColumnIndexOrThrow("reps"));

                addExerciseCard(name, sets, reps);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading exercises", e);
            Toast.makeText(this, "Error loading exercises", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void addExerciseCard(String name, int sets, int reps) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_exercise_card, exerciseListLayout, false);

        TextView tvExerciseName = card.findViewById(R.id.tvExerciseName);
        TextView tvExerciseDetails = card.findViewById(R.id.tvExerciseDetails);

        tvExerciseName.setText(name);
        tvExerciseDetails.setText(sets + " sets × " + reps + " reps");

        exerciseListLayout.addView(card);
    }
}
