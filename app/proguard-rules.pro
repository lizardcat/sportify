# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line numbers and source file info for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep data models used with database cursors
-keepclassmembers class com.example.sportify.Workout {
    <init>(...);
    public <fields>;
}

# Keep database helper methods that are called via reflection or cursors
-keep class com.example.sportify.FitnessDatabaseHelper {
    public <methods>;
}

# Keep all activities (they're referenced in AndroidManifest.xml)
-keep public class * extends android.app.Activity
-keep public class * extends androidx.appcompat.app.AppCompatActivity

# Keep RecyclerView adapter
-keep class com.example.sportify.WorkoutAdapter {
    <init>(...);
    public <methods>;
}

# Keep constants class
-keep class com.example.sportify.Constants {
    public static final *;
}

# Keep annotation processing
-keepattributes *Annotation*

# Remove logging in release builds for better performance and security
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep warning logs and errors for production debugging
-assumenosideeffects class android.util.Log {
    public static *** w(...) return false;
    public static *** e(...) return false;
}

# Preserve enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# AndroidX and Material Components
-keep class com.google.android.material.** { *; }
-keep class androidx.** { *; }
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep view constructors for inflation
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep onClick methods referenced in XML layouts
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}