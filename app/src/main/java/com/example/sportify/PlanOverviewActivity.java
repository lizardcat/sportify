package com.example.sportify;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class PlanOverviewActivity extends AppCompatActivity {

    private static final String TAG = "PlanOverviewActivity";

    private TextView tvTitle, tvDescription, tvGoals;
    private Button btnStartPlan;
    private FitnessDatabaseHelper dbHelper;
    private long planId;

    private ImageView imgPlanImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_overview);

        tvTitle = findViewById(R.id.tvPlanTitle);
        tvDescription = findViewById(R.id.tvPlanDescription);
        tvGoals = findViewById(R.id.tvPlanGoals);
        btnStartPlan = findViewById(R.id.btnStartPlan);
        dbHelper = new FitnessDatabaseHelper(this);
        imgPlanImage = findViewById(R.id.imgPlanImage);

        planId = getIntent().getLongExtra("plan_id", -1);

        if (planId == -1) {
            Toast.makeText(this, "Missing plan ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPlanDetails(planId);

        btnStartPlan.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlanDaysActivity.class);
            intent.putExtra("plan_id", planId);
            startActivity(intent);
        });
    }

    private void loadPlanDetails(long id) {
        Cursor cursor = null;
        try {
            cursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT name, description, goals FROM plans WHERE id = ?",
                    new String[]{String.valueOf(id)}
            );

            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                tvTitle.setText(name);
                tvDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                tvGoals.setText(cursor.getString(cursor.getColumnIndexOrThrow("goals")));

                // Set image based on plan name
                if (name.toLowerCase().contains("stronglift")) {
                    imgPlanImage.setImageResource(R.drawable.bg_stronglifts);
                } else if (name.toLowerCase().contains("phul")) {
                    imgPlanImage.setImageResource(R.drawable.bg_phul);
                } else if (name.toLowerCase().contains("phat")) {
                    imgPlanImage.setImageResource(R.drawable.bg_phat);
                } else {
                    imgPlanImage.setImageResource(R.drawable.bg_hybrid); // fallback
                }

            } else {
                Log.w(TAG, "Plan not found for ID: " + id);
                Toast.makeText(this, "Plan not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading plan details for ID: " + id, e);
            Toast.makeText(this, "Error loading plan details", Toast.LENGTH_SHORT).show();
            finish();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
