package com.example.sportify;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for Constants class
 * Tests that all constants are properly defined and have expected values
 */
public class ConstantsTest {

    @Test
    public void intentExtras_keysAreDefined() {
        // Verify all intent extra keys are non-null and non-empty
        assertNotNull(Constants.IntentExtras.PLAN_ID);
        assertNotNull(Constants.IntentExtras.PLAN_NAME);
        assertNotNull(Constants.IntentExtras.DAY_ID);
        assertNotNull(Constants.IntentExtras.DAY_NAME);
        assertNotNull(Constants.IntentExtras.EXERCISES);
        assertNotNull(Constants.IntentExtras.SUMMARY);
        assertNotNull(Constants.IntentExtras.ACTIVITY_TYPE);
        assertNotNull(Constants.IntentExtras.DURATION);
        assertNotNull(Constants.IntentExtras.CALORIES);
        assertNotNull(Constants.IntentExtras.NOTES);

        assertFalse(Constants.IntentExtras.PLAN_ID.isEmpty());
        assertFalse(Constants.IntentExtras.PLAN_NAME.isEmpty());
    }

    @Test
    public void database_constantsAreValid() {
        // Verify database constants have reasonable values
        assertNotNull(Constants.Database.NAME);
        assertFalse(Constants.Database.NAME.isEmpty());
        assertTrue(Constants.Database.VERSION > 0);
        assertTrue(Constants.Database.DEFAULT_WEIGHT_KG > 0);
        assertTrue(Constants.Database.DEFAULT_WEIGHT_KG < 200); // Reasonable default
    }

    @Test
    public void validation_rangesAreSensible() {
        // Verify validation constants are in reasonable ranges
        assertTrue(Constants.Validation.MIN_AGE > 0);
        assertTrue(Constants.Validation.MAX_AGE > Constants.Validation.MIN_AGE);
        assertTrue(Constants.Validation.MAX_AGE <= 150);

        assertTrue(Constants.Validation.MIN_WEIGHT_KG > 0);
        assertTrue(Constants.Validation.MAX_WEIGHT_KG > Constants.Validation.MIN_WEIGHT_KG);

        assertTrue(Constants.Validation.MIN_HEIGHT_CM > 0);
        assertTrue(Constants.Validation.MAX_HEIGHT_CM > Constants.Validation.MIN_HEIGHT_CM);
    }

    @Test
    public void met_valuesArePositive() {
        // Verify MET values are positive (they represent energy expenditure)
        assertTrue(Constants.MET.RUNNING > 0);
        assertTrue(Constants.MET.CYCLING > 0);
        assertTrue(Constants.MET.WALKING > 0);
        assertTrue(Constants.MET.STRENGTH > 0);
        assertTrue(Constants.MET.DEFAULT > 0);

        // Running should have higher MET than walking
        assertTrue(Constants.MET.RUNNING > Constants.MET.WALKING);
    }

    @Test
    public void dateFormat_patternsAreValid() {
        // Verify date format patterns are defined
        assertNotNull(Constants.DateFormat.DATE_PATTERN);
        assertNotNull(Constants.DateFormat.TIME_PATTERN);
        assertNotNull(Constants.DateFormat.TIMER_PATTERN);

        // Check basic format structure
        assertTrue(Constants.DateFormat.DATE_PATTERN.contains("-"));
        assertTrue(Constants.DateFormat.TIME_PATTERN.contains(":"));
        assertTrue(Constants.DateFormat.TIMER_PATTERN.contains("%"));
    }

    @Test
    public void activityTypes_areDefined() {
        // Verify activity type constants are defined
        assertNotNull(Constants.ActivityType.RUNNING);
        assertNotNull(Constants.ActivityType.CYCLING);
        assertNotNull(Constants.ActivityType.WALKING);
        assertNotNull(Constants.ActivityType.STRENGTH);

        assertFalse(Constants.ActivityType.RUNNING.isEmpty());
        assertFalse(Constants.ActivityType.CYCLING.isEmpty());
    }

    @Test
    public void units_areDefined() {
        // Verify unit preferences are defined
        assertNotNull(Constants.Units.METRIC);
        assertNotNull(Constants.Units.IMPERIAL);

        assertEquals("metric", Constants.Units.METRIC);
        assertEquals("imperial", Constants.Units.IMPERIAL);
    }

    @Test
    public void constants_cannotBeInstantiated() {
        // Verify Constants class cannot be instantiated
        try {
            java.lang.reflect.Constructor<?> constructor = Constants.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
            fail("Constants class should not be instantiable");
        } catch (Exception e) {
            // Expected - class should throw AssertionError
            assertTrue(e.getCause() instanceof AssertionError);
        }
    }
}
