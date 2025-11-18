# Sportify - Developer Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [Prerequisites](#prerequisites)
3. [Setup Instructions](#setup-instructions)
4. [Project Structure](#project-structure)
5. [Database Schema](#database-schema)
6. [Testing](#testing)
7. [Building for Production](#building-for-production)
8. [Code Quality Guidelines](#code-quality-guidelines)
9. [Common Issues & Solutions](#common-issues--solutions)

---

## Project Overview

Sportify is a fitness tracking Android application built with Java and SQLite. The app allows users to:
- Follow pre-built workout plans (Stronglifts 5x5, PHUL, PHAT)
- Create custom workout plans
- Track workout sessions with timer
- View workout history
- Manage user profile and preferences

**Tech Stack:**
- Java 8
- Android SDK 35 (Android 15)
- SQLite for local data persistence
- RecyclerView for lists
- Material Design components
- JUnit & AndroidJUnit for testing

---

## Prerequisites

Before you begin, ensure you have the following installed:

- **Android Studio**: Arctic Fox or later (recommended: Latest stable version)
- **JDK**: OpenJDK 11 or higher
- **Android SDK**: API Level 24 (minimum) to API Level 35 (target)
- **Git**: For version control

---

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/lizardcat/sportify.git
cd sportify
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Click "Open an Existing Project"
3. Navigate to the cloned `sportify` directory
4. Click "OK"

### 3. Sync Project with Gradle Files

Android Studio should automatically prompt you to sync. If not:
- Click `File > Sync Project with Gradle Files`
- Wait for the sync to complete

### 4. Configure Android SDK

Ensure you have the required SDK components:
- Go to `Tools > SDK Manager`
- Install:
  - Android 15.0 (API 35)
  - Android SDK Build-Tools 35.x.x
  - Android Emulator (if testing on emulator)

### 5. Run the App

#### On an Emulator:
1. `Tools > Device Manager`
2. Create a new Virtual Device (recommended: Pixel 5, API 35)
3. Click the "Run" button or press `Shift + F10`

#### On a Physical Device:
1. Enable Developer Options on your device
2. Enable USB Debugging
3. Connect via USB
4. Select your device from the device dropdown
5. Click "Run"

---

## Project Structure

```
sportify/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/sportify/
│   │   │   │   ├── Constants.java                 # App-wide constants
│   │   │   │   ├── FitnessDatabaseHelper.java    # SQLite database manager
│   │   │   │   ├── Workout.java                  # Workout data model
│   │   │   │   ├── WorkoutAdapter.java           # RecyclerView adapter
│   │   │   │   ├── DashboardActivity.java        # Main screen
│   │   │   │   ├── SettingsActivity.java         # User settings
│   │   │   │   ├── WorkoutPlansActivity.java     # View all plans
│   │   │   │   ├── CreatePlanActivity.java       # Create custom plan
│   │   │   │   ├── PlanOverviewActivity.java     # Plan details
│   │   │   │   ├── PlanDaysActivity.java         # View plan days
│   │   │   │   ├── TrackWorkoutActivity.java     # Active workout session
│   │   │   │   ├── WorkoutSummaryActivity.java   # Post-workout summary
│   │   │   │   ├── HistoryActivity.java          # Workout history
│   │   │   │   └── ...
│   │   │   ├── res/                              # Resources (layouts, drawables, strings)
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                                 # Unit tests
│   │   │   └── java/com/example/sportify/
│   │   │       └── ConstantsTest.java
│   │   └── androidTest/                          # Instrumentation tests
│   │       └── java/com/example/sportify/
│   │           └── FitnessDatabaseHelperTest.java
│   ├── build.gradle.kts                          # App-level Gradle config
│   └── proguard-rules.pro                        # ProGuard configuration
├── build.gradle.kts                              # Project-level Gradle config
├── settings.gradle.kts
├── README.md                                     # User-facing documentation
└── DEVELOPER_README.md                           # This file
```

---

## Database Schema

### Tables

#### 1. **workouts**
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER PRIMARY KEY | Auto-increment ID |
| date | TEXT NOT NULL | Workout date (YYYY-MM-DD) |
| start_time | TEXT | Start time (HH:MM:SS) |
| end_time | TEXT | End time (HH:MM:SS) |
| activity_type | TEXT NOT NULL | Type of workout |
| duration_min | REAL | Duration in minutes |
| distance_km | REAL | Distance covered |
| calories_burned | REAL | Calories burned |
| sets | INTEGER | Number of sets |
| reps | INTEGER | Number of reps |
| weight_kg | REAL | Weight used |
| notes | TEXT | User notes |
| day_id | INTEGER | Foreign key to plan_days |

#### 2. **user_profile**
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER PRIMARY KEY CHECK (id = 1) | Always 1 (singleton) |
| name | TEXT | User's name |
| age | INTEGER | User's age |
| weight_kg | REAL | Weight in kg |
| height_cm | REAL | Height in cm |
| unit_pref | TEXT CHECK (...) | 'metric' or 'imperial' |

#### 3. **plans**
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER PRIMARY KEY | Auto-increment ID |
| name | TEXT NOT NULL | Plan name |
| description | TEXT | Plan description |
| goals | TEXT | Plan goals |

#### 4. **plan_days**
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER PRIMARY KEY | Auto-increment ID |
| plan_id | INTEGER NOT NULL | Foreign key to plans |
| day_title | TEXT NOT NULL | Day name/title |

**Constraint**: ON DELETE CASCADE (deleting a plan deletes its days)

#### 5. **plan_exercises**
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER PRIMARY KEY | Auto-increment ID |
| day_id | INTEGER NOT NULL | Foreign key to plan_days |
| name | TEXT NOT NULL | Exercise name |
| sets | INTEGER DEFAULT 3 | Number of sets |
| reps | INTEGER DEFAULT 8 | Number of reps |

**Constraint**: ON DELETE CASCADE (deleting a day deletes its exercises)

---

## Testing

### Running Unit Tests

```bash
# From Android Studio
Right-click on `app/src/test/java` > Run 'Tests in 'java''

# From command line
./gradlew test
```

Unit tests cover:
- Constants validation
- Data model integrity

### Running Instrumentation Tests

```bash
# Requires a connected device or running emulator

# From Android Studio
Right-click on `app/src/androidTest/java` > Run 'Tests in 'java''

# From command line
./gradlew connectedAndroidTest
```

Instrumentation tests cover:
- Database CRUD operations
- Foreign key constraints
- Cascade deletes
- Edge cases

### Test Coverage

Current test coverage:
- **Unit Tests**: 11 tests (Constants class)
- **Instrumentation Tests**: 13 tests (Database operations)
- **Total**: 24 tests

---

## Building for Production

### 1. Update Version

Edit `app/build.gradle.kts`:

```kotlin
defaultConfig {
    versionCode = 2  // Increment this
    versionName = "1.1"  // Update this
}
```

### 2. Build Release APK

```bash
# From Android Studio
Build > Generate Signed Bundle / APK > APK

# From command line
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

### 3. ProGuard/R8

ProGuard is **enabled** for release builds and configured in `app/proguard-rules.pro`.

Key rules:
- Preserves database models and helpers
- Keeps Activities and AndroidX components
- Removes debug logs (keeps warning/error logs)
- Maintains line numbers for crash reports

### 4. Testing Release Build

Before publishing:

1. Install release APK on a test device
2. Test all critical flows:
   - Creating a plan
   - Starting a workout
   - Viewing history
   - Settings changes
3. Check ProGuard hasn't broken functionality
4. Verify logs are removed (use Logcat to confirm)

---

## Code Quality Guidelines

### Naming Conventions

- **Classes**: PascalCase (e.g., `WorkoutPlansActivity`)
- **Methods**: camelCase (e.g., `loadPlanDays()`)
- **Variables**: camelCase (e.g., `planId`, `editPlanName`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_PLAN_NAME_LENGTH`)
- **Resources**: snake_case (e.g., `activity_dashboard.xml`)

### Error Handling

Always use try-catch for:
- Database operations
- User input parsing
- File I/O

Example:
```java
Cursor cursor = null;
try {
    cursor = dbHelper.getAllPlans();
    // ... process cursor
} catch (Exception e) {
    Log.e(TAG, "Error loading plans", e);
    Toast.makeText(this, "Error loading plans", Toast.LENGTH_SHORT).show();
} finally {
    if (cursor != null) cursor.close();
}
```

### Logging

Use Android Log with appropriate levels:
- `Log.d(TAG, ...)` - Debug info (removed in release)
- `Log.i(TAG, ...)` - General info (removed in release)
- `Log.w(TAG, ...)` - Warnings (kept in release)
- `Log.e(TAG, ...)` - Errors (kept in release)

All classes should define:
```java
private static final String TAG = "ClassName";
```

### Input Validation

Always validate user input:
1. Check for empty/null
2. Check length limits
3. Check numeric ranges
4. Provide clear error messages

Example:
```java
if (name.isEmpty()) {
    editName.setError("Name is required");
    return;
}
if (name.length() > MAX_LENGTH) {
    editName.setError("Name too long");
    return;
}
```

---

## Common Issues & Solutions

### Issue: Database Version Conflict

**Symptom**: App crashes on launch after code changes
**Solution**:
1. Uninstall the app from the device/emulator
2. Reinstall from Android Studio

**Why**: Database version mismatch between old and new schema

### Issue: ProGuard Breaking Release Build

**Symptom**: Release APK crashes but debug works fine
**Solution**:
1. Check `proguard-rules.pro` has keep rules for your classes
2. Add keep rules for any classes accessed via reflection
3. Test release build thoroughly before publishing

### Issue: Cursor Not Closed Warning

**Symptom**: Memory leaks or warnings in Logcat
**Solution**: Always use try-finally:
```java
Cursor cursor = null;
try {
    cursor = db.query(...);
    // use cursor
} finally {
    if (cursor != null) cursor.close();
}
```

### Issue: Handler Memory Leaks

**Symptom**: Activity leaks detected by LeakCanary
**Solution**: Remove callbacks in `onPause()` and `onDestroy()`:
```java
@Override
protected void onDestroy() {
    super.onDestroy();
    if (handler != null && runnable != null) {
        handler.removeCallbacks(runnable);
    }
}
```

---

## Contributing

1. Create a feature branch
2. Make your changes
3. Add tests for new functionality
4. Ensure all tests pass
5. Update documentation if needed
6. Submit a pull request

---

## Resources

- [Android Developer Documentation](https://developer.android.com/)
- [Material Design Guidelines](https://material.io/design)
- [SQLite Documentation](https://www.sqlite.org/docs.html)
- [ProGuard Manual](https://www.guardsquare.com/manual/home)

---

## Support

For issues or questions:
- Check the [Common Issues](#common-issues--solutions) section
- Review existing [GitHub Issues](https://github.com/lizardcat/sportify/issues)
- Create a new issue with detailed information

---

**Last Updated**: 2025-01-18
**Maintainer**: Sportify Development Team
