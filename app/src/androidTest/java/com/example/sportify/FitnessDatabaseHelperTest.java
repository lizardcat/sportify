package com.example.sportify;

import android.content.Context;
import android.database.Cursor;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented tests for FitnessDatabaseHelper
 * These tests run on an Android device and verify database operations
 */
@RunWith(AndroidJUnit4.class)
public class FitnessDatabaseHelperTest {

    private Context context;
    private FitnessDatabaseHelper dbHelper;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // Use a test database
        context.deleteDatabase("fitness_tracker.db");
        dbHelper = new FitnessDatabaseHelper(context);
    }

    @After
    public void tearDown() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        context.deleteDatabase("fitness_tracker.db");
    }

    @Test
    public void database_isCreatedSuccessfully() {
        // Verify database is created and can be opened
        assertNotNull(dbHelper);
        assertNotNull(dbHelper.getReadableDatabase());
        assertNotNull(dbHelper.getWritableDatabase());
    }

    @Test
    public void insertPlan_createsNewPlan() {
        // Test inserting a plan
        long planId = dbHelper.insertPlan("Test Plan", "Test Description", "Test Goals");

        assertTrue("Plan ID should be positive", planId > 0);

        // Verify the plan exists
        Cursor cursor = null;
        try {
            cursor = dbHelper.getAllPlans();
            assertNotNull(cursor);
            assertTrue("Should have at least one plan", cursor.getCount() > 0);

            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            assertEquals("Test Plan", name);
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    @Test
    public void insertPlanDay_createsNewDay() {
        // First create a plan
        long planId = dbHelper.insertPlan("Test Plan", "Description", "Goals");

        // Then insert a day for that plan
        long dayId = dbHelper.insertPlanDay(planId, "Day 1");

        assertTrue("Day ID should be positive", dayId > 0);

        // Verify the day exists
        Cursor cursor = null;
        try {
            cursor = dbHelper.getDaysForPlan(planId);
            assertNotNull(cursor);
            assertEquals("Should have exactly one day", 1, cursor.getCount());

            cursor.moveToFirst();
            String dayTitle = cursor.getString(cursor.getColumnIndexOrThrow("day_title"));
            assertEquals("Day 1", dayTitle);
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    @Test
    public void insertExercise_createsNewExercise() {
        // Create plan and day first
        long planId = dbHelper.insertPlan("Test Plan", "Description", "Goals");
        long dayId = dbHelper.insertPlanDay(planId, "Day 1");

        // Insert exercise
        dbHelper.insertExercise(dayId, "Squats", 5, 5);

        // Verify exercise exists
        Cursor cursor = null;
        try {
            cursor = dbHelper.getExercisesForDay(dayId);
            assertNotNull(cursor);
            assertEquals("Should have one exercise", 1, cursor.getCount());

            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            int sets = cursor.getInt(cursor.getColumnIndexOrThrow("sets"));
            int reps = cursor.getInt(cursor.getColumnIndexOrThrow("reps"));

            assertEquals("Squats", name);
            assertEquals(5, sets);
            assertEquals(5, reps);
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    @Test
    public void insertWorkout_createsWorkoutRecord() {
        // Insert a workout
        dbHelper.insertWorkout(
                "2025-01-15",
                "08:00:00",
                "09:00:00",
                "Strength",
                60.0,
                0.0,
                300.0,
                5,
                5,
                100.0,
                "Good workout"
        );

        // Verify workout exists
        Cursor cursor = null;
        try {
            cursor = dbHelper.getAllWorkouts();
            assertNotNull(cursor);
            assertTrue("Should have at least one workout", cursor.getCount() > 0);

            cursor.moveToFirst();
            String activityType = cursor.getString(cursor.getColumnIndexOrThrow("activity_type"));
            double duration = cursor.getDouble(cursor.getColumnIndexOrThrow("duration_min"));
            int sets = cursor.getInt(cursor.getColumnIndexOrThrow("sets"));

            assertEquals("Strength", activityType);
            assertEquals(60.0, duration, 0.01);
            assertEquals(5, sets);
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    @Test
    public void getUserWeightKg_returnsDefaultWhenNoProfile() {
        // When no profile exists, should return default weight
        double weight = dbHelper.getUserWeightKg();

        assertEquals("Should return default weight",
                Constants.Database.DEFAULT_WEIGHT_KG, weight, 0.01);
    }

    @Test
    public void getPlanNameById_returnsCorrectName() {
        // Insert a plan
        long planId = dbHelper.insertPlan("My Test Plan", "Description", "Goals");

        // Get plan name by ID
        String name = dbHelper.getPlanNameById(planId);

        assertEquals("My Test Plan", name);
    }

    @Test
    public void getPlanNameById_returnsUnknownForInvalidId() {
        // Try to get name for non-existent plan
        String name = dbHelper.getPlanNameById(99999);

        assertEquals("Unknown", name);
    }

    @Test
    public void insertCompletedWorkout_createsWorkoutWithDuration() {
        // Create plan and day
        long planId = dbHelper.insertPlan("Test Plan", "Description", "Goals");
        long dayId = dbHelper.insertPlanDay(planId, "Day 1");

        // Insert completed workout
        dbHelper.insertCompletedWorkout(
                "2025-01-15",
                "Test Plan",
                "Day 1",
                dayId,
                "01:15:30",
                "Great session"
        );

        // Verify workout was created
        Cursor cursor = null;
        try {
            cursor = dbHelper.getAllWorkouts();
            assertNotNull(cursor);
            assertTrue("Should have workout", cursor.getCount() > 0);

            cursor.moveToFirst();
            String activityType = cursor.getString(cursor.getColumnIndexOrThrow("activity_type"));
            double duration = cursor.getDouble(cursor.getColumnIndexOrThrow("duration_min"));
            String notes = cursor.getString(cursor.getColumnIndexOrThrow("notes"));

            assertTrue(activityType.contains("Test Plan"));
            assertTrue(activityType.contains("Day 1"));
            assertTrue("Duration should be > 0", duration > 0);
            assertEquals("Great session", notes);
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    @Test
    public void cascadeDelete_deletesRelatedRecords() {
        // Create plan with days and exercises
        long planId = dbHelper.insertPlan("Test Plan", "Description", "Goals");
        long dayId = dbHelper.insertPlanDay(planId, "Day 1");
        dbHelper.insertExercise(dayId, "Squats", 5, 5);

        // Verify exercise exists
        Cursor cursor = dbHelper.getExercisesForDay(dayId);
        assertNotNull(cursor);
        int exerciseCount = cursor.getCount();
        cursor.close();

        assertTrue("Should have exercises", exerciseCount > 0);

        // Delete the plan (should cascade to days and exercises via ON DELETE CASCADE)
        dbHelper.getWritableDatabase().delete("plans", "id = ?",
                new String[]{String.valueOf(planId)});

        // Verify days are deleted
        cursor = dbHelper.getDaysForPlan(planId);
        assertNotNull(cursor);
        assertEquals("Days should be deleted", 0, cursor.getCount());
        cursor.close();
    }

    @Test
    public void multipleOperations_maintainDataIntegrity() {
        // Test multiple operations to ensure data integrity
        long plan1 = dbHelper.insertPlan("Plan 1", "Desc 1", "Goals 1");
        long plan2 = dbHelper.insertPlan("Plan 2", "Desc 2", "Goals 2");

        long day1 = dbHelper.insertPlanDay(plan1, "Day 1");
        long day2 = dbHelper.insertPlanDay(plan2, "Day 2");

        dbHelper.insertExercise(day1, "Exercise 1", 3, 10);
        dbHelper.insertExercise(day2, "Exercise 2", 4, 8);

        // Verify all data exists correctly
        Cursor plansCursor = dbHelper.getAllPlans();
        assertEquals("Should have 2 plans", 2, plansCursor.getCount());
        plansCursor.close();

        Cursor day1Cursor = dbHelper.getDaysForPlan(plan1);
        assertEquals("Plan 1 should have 1 day", 1, day1Cursor.getCount());
        day1Cursor.close();

        Cursor exercises1 = dbHelper.getExercisesForDay(day1);
        assertEquals("Day 1 should have 1 exercise", 1, exercises1.getCount());
        exercises1.close();

        Cursor exercises2 = dbHelper.getExercisesForDay(day2);
        assertEquals("Day 2 should have 1 exercise", 1, exercises2.getCount());
        exercises2.close();
    }
}
