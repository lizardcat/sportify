package com.example.sportify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreatePlanActivity extends AppCompatActivity {

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

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a plan name.", Toast.LENGTH_SHORT).show();
                return;
            }

            long planId = dbHelper.insertPlan(name, desc, goals);
            if (planId > 0) {
                Toast.makeText(this, "Plan created! Now add workout days.", Toast.LENGTH_SHORT).show();

                // Go to AddPlanDaysActivity and pass the plan ID
                Intent intent = new Intent(CreatePlanActivity.this, AddPlanDaysActivity.class);
                intent.putExtra("plan_id", planId);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to create plan.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
