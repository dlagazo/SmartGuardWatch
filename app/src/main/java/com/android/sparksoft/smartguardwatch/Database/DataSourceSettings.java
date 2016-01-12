package com.android.sparksoft.smartguardwatch.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.android.sparksoft.smartguardwatch.Models.Settings;

import java.util.ArrayList;

public class DataSourceSettings {

    // Database fields
    private SQLiteDatabase database;
    private AdapterSettings adapterSettings;
    private String[] allColumns = { adapterSettings.SETTINGS_KEY,
            adapterSettings.SETTINGS_VALUE };

    public DataSourceSettings(Context context)
    {
        adapterSettings = new AdapterSettings(context);
    }

    public void open() throws SQLException {
        database = adapterSettings.getWritableDatabase();
    }

    public void close() {
        adapterSettings.close();
    }

    public void createSetting(Settings settings) {
        ContentValues values = new ContentValues();
        values.put(adapterSettings.SETTINGS_KEY, settings.getKey());
        values.put(adapterSettings.SETTINGS_VALUE, settings.getValue());

        long insertId = database.insert(adapterSettings.TABLE_SETTINGS, null,
                values);

    }

    public void deleteSetting(String key) {

        database.delete(adapterSettings.TABLE_SETTINGS, adapterSettings.SETTINGS_KEY
                + " = " + key, null);
    }

    public ArrayList<Settings> getAllSettings() {
        ArrayList<Settings> settings = new ArrayList<Settings>();

        Cursor cursor = database.query(adapterSettings.TABLE_SETTINGS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Settings set = cursorToSetting(cursor);
            settings.add(set);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return settings;
    }

    private Settings cursorToSetting(Cursor cursor) {
        Settings settings = new Settings(cursor.getString(0), cursor.getString(1));

        return settings;
    }
}

