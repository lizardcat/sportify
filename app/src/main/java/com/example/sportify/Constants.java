package com.example.sportify;

/**
 * Application-wide constants for Sportify fitness tracker.
 * Centralizes magic strings, keys, and default values.
 */
public final class Constants {

    // Prevent instantiation
    private Constants() {
        throw new AssertionError("Constants class should not be instantiated");
    }

    /**
     * Intent extra keys used for passing data between activities
     */
    public static final class IntentExtras {
        public static final String PLAN_ID = "plan_id";
        public static final String PLAN_NAME = "plan_name";
        public static final String DAY_ID = "day_id";
        public static final String DAY_NAME = "day_name";
        public static final String EXERCISES = "exercises";
        public static final String SUMMARY = "summary";
        public static final String ACTIVITY_TYPE = "activityType";
        public static final String DURATION = "duration";
        public static final String CALORIES = "calories";
        public static final String NOTES = "notes";
    }

    /**
     * Database-related constants
     */
    public static final class Database {
        public static final String NAME = "fitness_tracker.db";
        public static final int VERSION = 3;
        public static final double DEFAULT_WEIGHT_KG = 70.0;
    }

    /**
     * Validation constants for user input
     */
    public static final class Validation {
        public static final int MIN_AGE = 1;
        public static final int MAX_AGE = 150;
        public static final double MIN_WEIGHT_KG = 1.0;
        public static final double MAX_WEIGHT_KG = 500.0;
        public static final double MIN_HEIGHT_CM = 1.0;
        public static final double MAX_HEIGHT_CM = 300.0;
    }

    /**
     * MET (Metabolic Equivalent of Task) values for calorie estimation
     */
    public static final class MET {
        public static final double RUNNING = 9.8;
        public static final double CYCLING = 7.5;
        public static final double WALKING = 3.5;
        public static final double STRENGTH = 6.0;
        public static final double DEFAULT = 4.0;
    }

    /**
     * Date and time format patterns
     */
    public static final class DateFormat {
        public static final String DATE_PATTERN = "yyyy-MM-dd";
        public static final String TIME_PATTERN = "HH:mm:ss";
        public static final String TIMER_PATTERN = "%02d:%02d:%02d";
    }

    /**
     * Activity types
     */
    public static final class ActivityType {
        public static final String RUNNING = "Running";
        public static final String CYCLING = "Cycling";
        public static final String WALKING = "Walking";
        public static final String STRENGTH = "Strength";
    }

    /**
     * Unit preferences
     */
    public static final class Units {
        public static final String METRIC = "metric";
        public static final String IMPERIAL = "imperial";
    }
}
