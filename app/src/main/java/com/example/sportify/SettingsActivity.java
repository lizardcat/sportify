package com.example.sportify;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    EditText editName, editAge, editWeight, editHeight;
    Spinner spinnerUnit;
    Button btnSave;

    FitnessDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = new FitnessDatabaseHelper(this);

        editName = findViewById(R.id.editName);
        editAge = findViewById(R.id.editAge);
        editWeight = findViewById(R.id.editWeight);
        editHeight = findViewById(R.id.editHeight);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        btnSave = findViewById(R.id.btnSave);

        // Load unit spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapter);

        loadProfile();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void loadProfile() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM user_profile WHERE id = 1", null);
            if (cursor != null && cursor.moveToFirst()) {
                editName.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                editAge.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("age"))));
                editWeight.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("weight_kg"))));
                editHeight.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("height_cm"))));

                String unit = cursor.getString(cursor.getColumnIndexOrThrow("unit_pref"));
                int pos = adapterPositionForUnit(unit);
                spinnerUnit.setSelection(pos);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading profile", e);
            Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // Don't close db - let SQLiteOpenHelper manage the connection
        }
    }

    private int adapterPositionForUnit(String unit) {
        return unit.equals("imperial") ? 1 : 0;
    }

    private void saveProfile() {
        // Validate input
        String name = editName.getText().toString().trim();
        String ageStr = editAge.getText().toString().trim();
        String weightStr = editWeight.getText().toString().trim();
        String heightStr = editHeight.getText().toString().trim();

        // Check for empty fields
        if (name.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Parse and validate numeric values
            int age = Integer.parseInt(ageStr);
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);

            // Validate ranges
            if (age <= 0 || age > 150) {
                Toast.makeText(this, "Please enter a valid age (1-150)", Toast.LENGTH_SHORT).show();
                return;
            }

            if (weight <= 0 || weight > 500) {
                Toast.makeText(this, "Please enter a valid weight (1-500 kg)", Toast.LENGTH_SHORT).show();
                return;
            }

            if (height <= 0 || height > 300) {
                Toast.makeText(this, "Please enter a valid height (1-300 cm)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save to database
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", 1);
            values.put("name", name);
            values.put("age", age);
            values.put("weight_kg", weight);
            values.put("height_cm", height);
            values.put("unit_pref", spinnerUnit.getSelectedItem().toString());

            int rows = db.update("user_profile", values, "id = 1", null);
            if (rows == 0) {
                long result = db.insert("user_profile", null, values);
                if (result == -1) {
                    Log.e(TAG, "Failed to insert profile");
                    Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Profile saved successfully");
            // Don't close db - let SQLiteOpenHelper manage the connection

        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid number format", e);
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error saving profile", e);
            Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show();
        }
    }
}
