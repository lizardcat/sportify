package com.example.sportify;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddPlanDaysActivity extends AppCompatActivity {

    private LinearLayout dayContainer;
    private Button btnAddDay, btnNext;
    private long planId;

    private ArrayList<EditText> dayInputs = new ArrayList<>();
    private FitnessDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan_days);

        planId = getIntent().getLongExtra("plan_id", -1);
        dbHelper = new FitnessDatabaseHelper(this);

        dayContainer = findViewById(R.id.dayContainer);
        btnAddDay = findViewById(R.id.btnAddDay);
        btnNext = findViewById(R.id.btnNext);

        // Add the first day field automatically
        addDayInputField("Day 1 - Full Body");

        btnAddDay.setOnClickListener(v -> {
            int count = dayInputs.size() + 1;
            addDayInputField("Day " + count + " - ");
        });

        btnNext.setOnClickListener(v -> {
            if (validateAndSaveDays()) {
                Intent intent = new Intent(AddPlanDaysActivity.this, AddPlanExercisesActivity.class);
                intent.putExtra("plan_id", planId);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addDayInputField(String hint) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setHintTextColor(Color.GRAY);
        input.setTextColor(Color.WHITE);
        input.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        input.setPadding(20, 20, 20, 20);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        input.setLayoutParams(params);

        dayContainer.addView(input);
        dayInputs.add(input);

        // Focus on the new field
        input.requestFocus();
    }

    private boolean validateAndSaveDays() {
        boolean isValid = true;
        for (EditText input : dayInputs) {
            String title = input.getText().toString().trim();
            if (title.isEmpty()) {
                input.setError("Please enter a day title");
                isValid = false;
            }
        }

        if (!isValid) {
            Toast.makeText(this, "Please fill in all day titles", Toast.LENGTH_SHORT).show();
            return false;
        }

        for (EditText input : dayInputs) {
            String title = input.getText().toString().trim();
            dbHelper.insertPlanDay(planId, title);
        }

        return true;
    }
}
