package com.example.sportify;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "HistoryActivity";

    RecyclerView recyclerHistory;
    WorkoutAdapter adapter;
    ArrayList<Workout> workoutList;
    FitnessDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerHistory = findViewById(R.id.recyclerHistory);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new FitnessDatabaseHelper(this);
        workoutList = new ArrayList<>();

        loadWorkouts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWorkouts(); // Refresh list when returning from summary
    }

    private void loadWorkouts() {
        workoutList.clear(); // Clear previous data
        Cursor cursor = null;

        try {
            cursor = dbHelper.getAllWorkouts();
            if (cursor == null) {
                Log.w(TAG, "Cursor is null when loading workouts");
                Toast.makeText(this, "Error loading workout history", Toast.LENGTH_SHORT).show();
                return;
            }

            while (cursor.moveToNext()) {
                Workout workout = new Workout(
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("activity_type")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("duration_min")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("sets")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("reps")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("weight_kg")),
                        cursor.getString(cursor.getColumnIndexOrThrow("notes"))
                );
                workoutList.add(workout);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading workouts from database", e);
            Toast.makeText(this, "Error loading workout history", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (adapter == null) {
            adapter = new WorkoutAdapter(workoutList);
            recyclerHistory.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged(); // refresh UI
        }
    }
}
