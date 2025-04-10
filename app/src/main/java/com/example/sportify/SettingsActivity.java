package com.example.sportify;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SettingsActivity extends AppCompatActivity {

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
        Cursor cursor = db.rawQuery("SELECT * FROM user_profile WHERE id = 1", null);
        if (cursor.moveToFirst()) {
            editName.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            editAge.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("age"))));
            editWeight.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("weight_kg"))));
            editHeight.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("height_cm"))));

            String unit = cursor.getString(cursor.getColumnIndexOrThrow("unit_pref"));
            int pos = adapterPositionForUnit(unit);
            spinnerUnit.setSelection(pos);
        }
        cursor.close();
        db.close();
    }

    private int adapterPositionForUnit(String unit) {
        return unit.equals("imperial") ? 1 : 0;
    }

    private void saveProfile() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", 1);
        values.put("name", editName.getText().toString());
        values.put("age", Integer.parseInt(editAge.getText().toString()));
        values.put("weight_kg", Double.parseDouble(editWeight.getText().toString()));
        values.put("height_cm", Double.parseDouble(editHeight.getText().toString()));
        values.put("unit_pref", spinnerUnit.getSelectedItem().toString());

        int rows = db.update("user_profile", values, "id = 1", null);
        if (rows == 0) {
            db.insert("user_profile", null, values); // insert if not exists
        }

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        db.close();
    }
}
