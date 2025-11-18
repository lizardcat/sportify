package com.example.sportify;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class AddPlanExercisesActivity extends AppCompatActivity {

    private static final String TAG = "AddPlanExercisesActivity";
    private static final int MAX_EXERCISE_NAME_LENGTH = 100;
    private static final int MIN_SETS = 1;
    private static final int MAX_SETS = 20;
    private static final int MIN_REPS = 1;
    private static final int MAX_REPS = 100;

    private LinearLayout exerciseContainer;
    private Spinner daySelector;
    private Button btnAddExercise, btnSaveExercises;
    private EditText editExerciseName, editSets, editReps;

    private FitnessDatabaseHelper dbHelper;
    private long planId;
    private ArrayList<Long> dayIds = new ArrayList<>();
    private ArrayList<String> dayTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan_exercises);

        dbHelper = new FitnessDatabaseHelper(this);
        planId = getIntent().getLongExtra("plan_id", -1);

        if (planId == -1) {
            Toast.makeText(this, "No plan ID received.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        daySelector = findViewById(R.id.spinnerDaySelector);
        editExerciseName = findViewById(R.id.editExerciseName);
        editSets = findViewById(R.id.editSets);
        editReps = findViewById(R.id.editReps);
        btnAddExercise = findViewById(R.id.btnAddExercise);
        btnSaveExercises = findViewById(R.id.btnSaveExercises);
        exerciseContainer = findViewById(R.id.exerciseContainer);

        loadPlanDays();

        btnAddExercise.setOnClickListener(v -> addExerciseEntry());
        btnSaveExercises.setOnClickListener(v -> saveExercises());
    }

    private void loadPlanDays() {
        Cursor cursor = null;
        try {
            cursor = dbHelper.getDaysForPlan(planId);
            dayIds.clear();
            dayTitles.clear();

            if (cursor == null) {
                Log.e(TAG, "Cursor is null when loading plan days");
                Toast.makeText(this, "Error loading workout days", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("day_title"));
                dayIds.add(id);
                dayTitles.add(title);
            }

            if (dayTitles.isEmpty()) {
                Log.w(TAG, "No workout days found for plan ID: " + planId);
                Toast.makeText(this, "No workout days found. Please add days first.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dayTitles);
            daySelector.setAdapter(adapter);
            Log.d(TAG, "Loaded " + dayTitles.size() + " workout days");
        } catch (Exception e) {
            Log.e(TAG, "Error loading plan days", e);
            Toast.makeText(this, "Error loading workout days", Toast.LENGTH_SHORT).show();
            finish();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void addExerciseEntry() {
        String name = editExerciseName.getText().toString().trim();
        String setsStr = editSets.getText().toString().trim();
        String repsStr = editReps.getText().toString().trim();

        // Validate exercise name
        if (name.isEmpty()) {
            editExerciseName.setError("Exercise name is required");
            Toast.makeText(this, "Please enter an exercise name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.length() > MAX_EXERCISE_NAME_LENGTH) {
            editExerciseName.setError("Name too long (max " + MAX_EXERCISE_NAME_LENGTH + " characters)");
            Toast.makeText(this, "Exercise name is too long", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate sets and reps
        if (setsStr.isEmpty()) {
            editSets.setError("Sets required");
            Toast.makeText(this, "Please enter number of sets", Toast.LENGTH_SHORT).show();
            return;
        }

        if (repsStr.isEmpty()) {
            editReps.setError("Reps required");
            Toast.makeText(this, "Please enter number of reps", Toast.LENGTH_SHORT).show();
            return;
        }

        int sets, reps;
        try {
            sets = Integer.parseInt(setsStr);
            reps = Integer.parseInt(repsStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid number format for sets/reps", e);
            Toast.makeText(this, "Sets and reps must be valid numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate ranges
        if (sets < MIN_SETS || sets > MAX_SETS) {
            editSets.setError("Sets must be between " + MIN_SETS + " and " + MAX_SETS);
            Toast.makeText(this, "Invalid number of sets", Toast.LENGTH_SHORT).show();
            return;
        }

        if (reps < MIN_REPS || reps > MAX_REPS) {
            editReps.setError("Reps must be between " + MIN_REPS + " and " + MAX_REPS);
            Toast.makeText(this, "Invalid number of reps", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert exercise
        try {
            long selectedDayId = dayIds.get(daySelector.getSelectedItemPosition());
            dbHelper.insertExercise(selectedDayId, name, sets, reps);

            String entry = name + " - " + sets + " sets x " + reps + " reps";
            TextView entryView = new TextView(this);
            entryView.setText(entry);
            entryView.setTextColor(getColor(android.R.color.white));
            entryView.setPadding(0, 8, 0, 8);
            exerciseContainer.addView(entryView);

            // Clear input fields
            editExerciseName.setText("");
            editSets.setText("");
            editReps.setText("");

            Log.d(TAG, "Added exercise: " + name + " (" + sets + "x" + reps + ")");
            Toast.makeText(this, "Exercise added", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error adding exercise", e);
            Toast.makeText(this, "Error adding exercise", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveExercises() {
        Toast.makeText(this, "Exercises saved successfully!", Toast.LENGTH_SHORT).show();
        showNextStepDialog();
    }

    private void showNextStepDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What’s next?");
        builder.setMessage("You've added exercises. Would you like to add another day or start your workout plan now?");

        builder.setPositiveButton("Start Workout Plan", (dialog, which) -> {
            Intent intent = new Intent(AddPlanExercisesActivity.this, PlanDaysActivity.class);
            intent.putExtra("plan_id", planId);
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton("Add Another Day", (dialog, which) -> {
            Intent intent = new Intent(AddPlanExercisesActivity.this, AddPlanDaysActivity.class);
            intent.putExtra("plan_id", planId);
            startActivity(intent);
            finish();
        });

        builder.setCancelable(false);
        builder.show();
    }
}
