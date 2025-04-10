package com.example.sportify;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class AddPlanExercisesActivity extends AppCompatActivity {

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
        Cursor cursor = dbHelper.getDaysForPlan(planId);
        dayIds.clear();
        dayTitles.clear();

        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("day_title"));
            dayIds.add(id);
            dayTitles.add(title);
        }
        cursor.close();

        if (dayTitles.isEmpty()) {
            Toast.makeText(this, "No workout days found. Please add days first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dayTitles);
        daySelector.setAdapter(adapter);
    }

    private void addExerciseEntry() {
        String name = editExerciseName.getText().toString().trim();
        String setsStr = editSets.getText().toString().trim();
        String repsStr = editReps.getText().toString().trim();

        if (name.isEmpty() || setsStr.isEmpty() || repsStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        int sets, reps;
        try {
            sets = Integer.parseInt(setsStr);
            reps = Integer.parseInt(repsStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Sets and reps must be numbers.", Toast.LENGTH_SHORT).show();
            return;
        }

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
