package com.example.sportify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreatePlanActivity extends AppCompatActivity {

    private static final String TAG = "CreatePlanActivity";
    private static final int MAX_PLAN_NAME_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 500;
    private static final int MAX_GOALS_LENGTH = 500;

    private EditText editPlanName, editPlanDesc, editPlanGoals;
    private Button btnNext;
    private FitnessDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        dbHelper = new FitnessDatabaseHelper(this);

        editPlanName = findViewById(R.id.editPlanName);
        editPlanDesc = findViewById(R.id.editPlanDesc);
        editPlanGoals = findViewById(R.id.editPlanGoals);
        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> {
            String name = editPlanName.getText().toString().trim();
            String desc = editPlanDesc.getText().toString().trim();
            String goals = editPlanGoals.getText().toString().trim();

            // Validate plan name
            if (name.isEmpty()) {
                editPlanName.setError("Plan name is required");
                Toast.makeText(this, "Please enter a plan name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (name.length() > MAX_PLAN_NAME_LENGTH) {
                editPlanName.setError("Plan name too long (max " + MAX_PLAN_NAME_LENGTH + " characters)");
                Toast.makeText(this, "Plan name is too long", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate description
            if (desc.length() > MAX_DESCRIPTION_LENGTH) {
                editPlanDesc.setError("Description too long (max " + MAX_DESCRIPTION_LENGTH + " characters)");
                Toast.makeText(this, "Description is too long", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate goals
            if (goals.length() > MAX_GOALS_LENGTH) {
                editPlanGoals.setError("Goals too long (max " + MAX_GOALS_LENGTH + " characters)");
                Toast.makeText(this, "Goals text is too long", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert plan into database
            try {
                long planId = dbHelper.insertPlan(name, desc, goals);
                if (planId > 0) {
                    Log.i(TAG, "Plan created with ID: " + planId);
                    Toast.makeText(this, "Plan created! Now add workout days.", Toast.LENGTH_SHORT).show();

                    // Go to AddPlanDaysActivity and pass the plan ID
                    Intent intent = new Intent(CreatePlanActivity.this, AddPlanDaysActivity.class);
                    intent.putExtra("plan_id", planId);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e(TAG, "Failed to create plan - insertPlan returned: " + planId);
                    Toast.makeText(this, "Failed to create plan. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error creating plan", e);
                Toast.makeText(this, "Error creating plan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
