package com.example.sportify;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PlanDaysActivity extends AppCompatActivity {

    private LinearLayout dayListContainer;
    private FitnessDatabaseHelper dbHelper;
    private long planId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_days);

        dayListContainer = findViewById(R.id.dayListContainer);
        dbHelper = new FitnessDatabaseHelper(this);

        planId = getIntent().getLongExtra("plan_id", -1);
        if (planId != -1) {
            loadPlanDays();
        }
    }

    private void loadPlanDays() {
        Cursor cursor = dbHelper.getDaysForPlan(planId);

        while (cursor.moveToNext()) {
            long dayId = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("day_title"));

            View dayCard = createDayCard(dayId, title);
            dayListContainer.addView(dayCard);
        }

        cursor.close();
    }

    private View createDayCard(long dayId, String title) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.card_background); // You can define this drawable
        card.setPadding(32, 32, 32, 32);
        card.setElevation(8f);
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(20f);
        tvTitle.setTextColor(getResources().getColor(android.R.color.white));
        tvTitle.setGravity(Gravity.START);
        card.addView(tvTitle);

        Button btnStart = new Button(this);
        btnStart.setText("View Exercises");
        btnStart.setBackgroundColor(getResources().getColor(R.color.teal_700));
        btnStart.setTextColor(getResources().getColor(android.R.color.white));
        btnStart.setOnClickListener(v -> {
            String planName = dbHelper.getPlanNameById(planId);

            Intent intent = new Intent(PlanDaysActivity.this, TrackWorkoutActivity.class);
            intent.putExtra("day_id", dayId);
            intent.putExtra("plan_name", planName);
            intent.putExtra("day_name", title);
            startActivity(intent);
        });


        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btnParams.topMargin = 20;
        btnStart.setLayoutParams(btnParams);
        card.addView(btnStart);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 16, 0, 16);
        card.setLayoutParams(cardParams);

        return card;
    }
}
