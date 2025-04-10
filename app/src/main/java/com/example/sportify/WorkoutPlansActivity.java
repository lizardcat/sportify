package com.example.sportify;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WorkoutPlansActivity extends AppCompatActivity {

    private LinearLayout planListLayout;
    private FitnessDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_plans);

        dbHelper = new FitnessDatabaseHelper(this);
        planListLayout = findViewById(R.id.planListLayout);

        loadPlansFromDatabase();
        addCreateCustomPlanCard();
    }

    private void loadPlansFromDatabase() {
        Cursor cursor = dbHelper.getAllPlans();
        while (cursor.moveToNext()) {
            long planId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));

            addPlanCard(planId, name, desc);
        }
        cursor.close();
    }

    private void addPlanCard(long planId, String name, String description) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_plan_card, planListLayout, false);

        TextView tvTitle = card.findViewById(R.id.tvPlanTitle);
        TextView tvDesc = card.findViewById(R.id.tvPlanDesc);
        ImageView imgThumb = card.findViewById(R.id.imgPlanImage);

        tvTitle.setText(name);
        tvDesc.setText(description);

        // Pick a default image based on plan name
        if (name.toLowerCase().contains("stronglift")) {
            imgThumb.setImageResource(R.drawable.bg_stronglifts);
        } else if (name.toLowerCase().contains("phul")) {
            imgThumb.setImageResource(R.drawable.bg_phul);
        } else if (name.toLowerCase().contains("phat")) {
            imgThumb.setImageResource(R.drawable.bg_phat);
        } else {
            imgThumb.setImageResource(R.drawable.bg_hybrid); // fallback
        }

        Button btnStartNow = card.findViewById(R.id.btnStartNow);
        btnStartNow.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutPlansActivity.this, PlanOverviewActivity.class);
            intent.putExtra("plan_id", planId);
            startActivity(intent);
        });


        card.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutPlansActivity.this, PlanOverviewActivity.class);
            intent.putExtra("plan_id", planId);
            startActivity(intent);
        });

        planListLayout.addView(card);
    }

    private void addCreateCustomPlanCard() {
        View card = LayoutInflater.from(this).inflate(R.layout.item_plan_card, planListLayout, false);

        TextView tvTitle = card.findViewById(R.id.tvPlanTitle);
        TextView tvDesc = card.findViewById(R.id.tvPlanDesc);
        ImageView imgThumb = card.findViewById(R.id.imgPlanImage);
        Button btnStartNow = card.findViewById(R.id.btnStartNow);

        tvTitle.setText("Build Your Own Plan");
        tvDesc.setText("Create a personalized workout plan tailored to your goals.");
        imgThumb.setImageResource(R.drawable.bg_settings);

        btnStartNow.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutPlansActivity.this, CreatePlanActivity.class);
            startActivity(intent);
        });

        card.setOnClickListener(v -> {
            Intent intent = new Intent(WorkoutPlansActivity.this, CreatePlanActivity.class);
            startActivity(intent);
        });

        planListLayout.addView(card);
    }
}
