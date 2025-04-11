package com.example.sportify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FitnessDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fitness_tracker.db";
    private static final int DATABASE_VERSION = 3;

    public FitnessDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS workouts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date TEXT NOT NULL," +
                "start_time TEXT," +
                "end_time TEXT," +
                "activity_type TEXT NOT NULL," +
                "duration_min REAL," +
                "distance_km REAL," +
                "calories_burned REAL," +
                "sets INTEGER," +
                "reps INTEGER," +
                "weight_kg REAL," +
                "notes TEXT," +
                "day_id INTEGER," +
                "FOREIGN KEY(day_id) REFERENCES plan_days(id))");

        db.execSQL("CREATE TABLE IF NOT EXISTS user_profile (" +
                "id INTEGER PRIMARY KEY CHECK (id = 1)," +
                "name TEXT," +
                "age INTEGER," +
                "weight_kg REAL," +
                "height_cm REAL," +
                "unit_pref TEXT CHECK (unit_pref IN ('metric', 'imperial')))");

        db.execSQL("CREATE TABLE IF NOT EXISTS goals (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "goal_type TEXT NOT NULL," +
                "target_value REAL NOT NULL," +
                "period TEXT CHECK (period IN ('daily', 'weekly', 'monthly'))," +
                "start_date TEXT NOT NULL," +
                "end_date TEXT NOT NULL)");

        db.execSQL("CREATE TABLE IF NOT EXISTS plans (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "description TEXT," +
                "goals TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS plan_days (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "plan_id INTEGER NOT NULL," +
                "day_title TEXT NOT NULL," +
                "FOREIGN KEY (plan_id) REFERENCES plans(id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS plan_exercises (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "day_id INTEGER NOT NULL," +
                "name TEXT NOT NULL," +
                "sets INTEGER DEFAULT 3," +
                "reps INTEGER DEFAULT 8," +
                "FOREIGN KEY (day_id) REFERENCES plan_days(id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS workouts");
        db.execSQL("DROP TABLE IF EXISTS user_profile");
        db.execSQL("DROP TABLE IF EXISTS goals");
        db.execSQL("DROP TABLE IF EXISTS plans");
        db.execSQL("DROP TABLE IF EXISTS plan_days");
        db.execSQL("DROP TABLE IF EXISTS plan_exercises");
        onCreate(db);
    }

    public void insertCompletedWorkout(String date, String planName, String dayName, long dayId, String durationStr, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("date", date);
        values.put("start_time", "");
        values.put("end_time", "");
        values.put("activity_type", planName + " - " + dayName);
        values.put("duration_min", convertDurationToMinutes(durationStr));
        values.put("distance_km", 0.0);
        values.put("calories_burned", 0.0);
        values.put("notes", notes);
        values.put("day_id", dayId);

        db.insert("workouts", null, values);
        db.close();
    }

    public void insertWorkout(String date, String startTime, String endTime, String activityType,
                              double durationMin, double distanceKm, double caloriesBurned,
                              int sets, int reps, double weightKg, String notes) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("start_time", startTime);
        values.put("end_time", endTime);
        values.put("activity_type", activityType);
        values.put("duration_min", durationMin);
        values.put("distance_km", distanceKm);
        values.put("calories_burned", caloriesBurned);
        values.put("sets", sets);
        values.put("reps", reps);
        values.put("weight_kg", weightKg);
        values.put("notes", notes);

        db.insert("workouts", null, values);
        db.close();
    }

    public double getUserWeightKg() {
        SQLiteDatabase db = this.getReadableDatabase();
        double weight = 70.0; // default fallback

        Cursor cursor = db.rawQuery("SELECT weight_kg FROM user_profile WHERE id = 1", null);
        if (cursor.moveToFirst()) {
            weight = cursor.getDouble(cursor.getColumnIndexOrThrow("weight_kg"));
        }
        cursor.close();
        db.close();
        return weight;
    }

    private double convertDurationToMinutes(String durationStr) {
        try {
            String[] parts = durationStr.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);
            return hours * 60 + minutes + (seconds / 60.0);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public long insertPlan(String name, String description, String goals) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("goals", goals);
        return db.insert("plans", null, values);
    }

    public long insertPlanDay(long planId, String dayTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("plan_id", planId);
        values.put("day_title", dayTitle);
        return db.insert("plan_days", null, values);
    }

    public void insertExercise(long dayId, String name, int sets, int reps) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("day_id", dayId);
        values.put("name", name);
        values.put("sets", sets);
        values.put("reps", reps);
        db.insert("plan_exercises", null, values);
    }

    public void seedTestData() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM plan_exercises");
        db.execSQL("DELETE FROM plan_days");
        db.execSQL("DELETE FROM plans");

        // STRONGLIFTS
        long planId = insertPlan("Stronglifts 5x5", "A simple full-body strength training program with compound lifts.", "Build strength, muscle, and improve form on barbell lifts.");
        long day1 = insertPlanDay(planId, "Day 1 - Full Body A");
        long day2 = insertPlanDay(planId, "Day 2 - Full Body B");
        long day3 = insertPlanDay(planId, "Day 3 - Full Body C");
        insertExercise(day1, "Barbell Squat", 5, 5);
        insertExercise(day1, "Bench Press", 5, 5);
        insertExercise(day1, "Barbell Row", 5, 5);
        insertExercise(day2, "Barbell Squat", 5, 5);
        insertExercise(day2, "Overhead Press", 5, 5);
        insertExercise(day2, "Deadlift", 1, 5);
        insertExercise(day3, "Front Squat", 5, 5);
        insertExercise(day3, "Incline Bench Press", 5, 5);
        insertExercise(day3, "Pull-ups", 3, 10);

        // PHUL
        long phulPlanId = insertPlan(
                "PHUL",
                "Power Hypertrophy Upper Lower program focused on size and strength.",
                "Alternate upper/lower body strength and hypertrophy days."
        );

        long phulDay1 = insertPlanDay(phulPlanId, "Day 1 - Upper Power");
        insertExercise(phulDay1, "Barbell Bench Press", 3, 5);
        insertExercise(phulDay1, "Incline Dumbbell Press", 3, 8);
        insertExercise(phulDay1, "Bent Over Row", 3, 5);
        insertExercise(phulDay1, "Lat Pulldown", 3, 10);
        insertExercise(phulDay1, "Overhead Press", 3, 6);
        insertExercise(phulDay1, "Barbell Curl", 3, 10);
        insertExercise(phulDay1, "Skull Crushers", 3, 10);

        long phulDay2 = insertPlanDay(phulPlanId, "Day 2 - Lower Power");
        insertExercise(phulDay2, "Back Squat", 3, 5);
        insertExercise(phulDay2, "Romanian Deadlift", 3, 8);
        insertExercise(phulDay2, "Leg Press", 3, 10);
        insertExercise(phulDay2, "Leg Curl", 3, 10);
        insertExercise(phulDay2, "Standing Calf Raise", 4, 12);
        insertExercise(phulDay2, "Seated Calf Raise", 4, 15);

// Day 3 is rest, so no insert

        long phulDay4 = insertPlanDay(phulPlanId, "Day 4 - Upper Hypertrophy");
        insertExercise(phulDay4, "Incline Machine Press", 3, 12);
        insertExercise(phulDay4, "Dumbbell Flyes", 3, 15);
        insertExercise(phulDay4, "Cable Row", 3, 12);
        insertExercise(phulDay4, "Face Pulls", 3, 15);
        insertExercise(phulDay4, "Lateral Raise", 3, 15);
        insertExercise(phulDay4, "Preacher Curl", 3, 12);
        insertExercise(phulDay4, "Tricep Pushdown", 3, 12);

        long phulDay5 = insertPlanDay(phulPlanId, "Day 5 - Lower Hypertrophy");
        insertExercise(phulDay5, "Front Squat", 3, 10);
        insertExercise(phulDay5, "Walking Lunges", 3, 12);
        insertExercise(phulDay5, "Leg Extension", 3, 15);
        insertExercise(phulDay5, "Hamstring Curl", 3, 15);
        insertExercise(phulDay5, "Donkey Calf Raise", 4, 20);

        // PHAT
        long phatPlanId = insertPlan(
                "PHAT",
                "Power Hypertrophy Adaptive Training split by Layne Norton.",
                "Combine heavy strength work with volume hypertrophy."
        );

        long phatDay1 = insertPlanDay(phatPlanId, "Day 1 - Upper Power");
        insertExercise(phatDay1, "Barbell Bench Press", 3, 5);
        insertExercise(phatDay1, "Bent Over Row", 3, 5);
        insertExercise(phatDay1, "Seated Dumbbell Shoulder Press", 3, 6);
        insertExercise(phatDay1, "Weighted Pull-ups", 3, 6);
        insertExercise(phatDay1, "Barbell Curl", 3, 10);
        insertExercise(phatDay1, "Skull Crushers", 3, 10);

        long phatDay2 = insertPlanDay(phatPlanId, "Day 2 - Lower Power");
        insertExercise(phatDay2, "Squat", 3, 5);
        insertExercise(phatDay2, "Romanian Deadlift", 3, 6);
        insertExercise(phatDay2, "Leg Press", 3, 10);
        insertExercise(phatDay2, "Calf Raises", 4, 15);

        long phatDay3 = insertPlanDay(phatPlanId, "Day 3 - Back and Shoulders Hypertrophy");
        insertExercise(phatDay3, "T-Bar Row", 3, 12);
        insertExercise(phatDay3, "Lat Pulldown", 3, 15);
        insertExercise(phatDay3, "Reverse Pec Deck", 3, 15);
        insertExercise(phatDay3, "Upright Row", 3, 12);
        insertExercise(phatDay3, "Dumbbell Lateral Raise", 3, 15);

        long phatDay4 = insertPlanDay(phatPlanId, "Day 4 - Lower Body Hypertrophy");
        insertExercise(phatDay4, "Front Squat", 3, 10);
        insertExercise(phatDay4, "Walking Lunges", 3, 12);
        insertExercise(phatDay4, "Hamstring Curl", 3, 15);
        insertExercise(phatDay4, "Leg Extension", 3, 15);
        insertExercise(phatDay4, "Standing Calf Raise", 4, 20);

        long phatDay5 = insertPlanDay(phatPlanId, "Day 5 - Chest and Arms Hypertrophy");
        insertExercise(phatDay5, "Incline Dumbbell Press", 3, 12);
        insertExercise(phatDay5, "Cable Crossovers", 3, 15);
        insertExercise(phatDay5, "EZ-Bar Curl", 3, 12);
        insertExercise(phatDay5, "Tricep Dips", 3, 12);
        insertExercise(phatDay5, "Hammer Curl", 3, 12);
        insertExercise(phatDay5, "Overhead Tricep Extension", 3, 12);

        // Stronglifts
        insertCompletedWorkout("2025-04-02", "Stronglifts 5x5", "Day 1 - Full Body A", day1, "00:45:00", "Solid session. Bench felt light.");
        insertCompletedWorkout("2025-04-03", "Stronglifts 5x5", "Day 2 - Full Body B", day2, "00:48:00", "Struggled on deadlifts.");
        insertCompletedWorkout("2025-04-04", "Stronglifts 5x5", "Day 3 - Full Body C", day3, "00:50:00", "Pull-ups felt strong.");

// PHUL
        insertCompletedWorkout("2025-04-05", "PHUL", "Day 1 - Upper Power", phulDay1, "01:00:00", "Good pump. PR on incline DB press.");
        insertCompletedWorkout("2025-04-06", "PHUL", "Day 2 - Lower Power", phulDay2, "00:55:00", "Quads were toast.");
        insertCompletedWorkout("2025-04-07", "PHUL", "Day 4 - Upper Hypertrophy", phulDay4, "01:10:00", "High volume. Arms burning.");
        insertCompletedWorkout("2025-04-08", "PHUL", "Day 5 - Lower Hypertrophy", phulDay5, "00:58:00", "Hamstring curls were killer.");

// PHAT
        insertCompletedWorkout("2025-04-09", "PHAT", "Day 1 - Upper Power", phatDay1, "01:05:00", "Hit 5 reps on bench with 90kg.");
        insertCompletedWorkout("2025-04-10", "PHAT", "Day 2 - Lower Power", phatDay2, "01:00:00", "Legs shaking by the end.");
    }

    public Cursor getAllPlans() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM plans ORDER BY id DESC", null);
    }

    public Cursor getDaysForPlan(long planId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM plan_days WHERE plan_id = ? ORDER BY id ASC", new String[]{String.valueOf(planId)});
    }

    public Cursor getExercisesForDay(long dayId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM plan_exercises WHERE day_id = ? ORDER BY id ASC", new String[]{String.valueOf(dayId)});
    }

    public String getPlanNameById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM plans WHERE id = ?", new String[]{String.valueOf(id)});
        String name = "Unknown";
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        }
        cursor.close();
        return name;
    }

    public Cursor getAllWorkouts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM workouts ORDER BY date DESC", null);
    }
}
