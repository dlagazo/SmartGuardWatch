package com.android.sparksoft.smartguardwatch.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AdapterSettings extends SQLiteOpenHelper {

    public static final String TABLE_SETTINGS = "settings";
    public static final String SETTINGS_KEY = "key";
    public static final String SETTINGS_VALUE = "value";


    private static final String DATABASE_NAME = "smartguard.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_SETTINGS + "(" + SETTINGS_KEY + " text primary key, " +
            SETTINGS_VALUE + " text);";

    public AdapterSettings(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(AdapterSettings.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        onCreate(db);
    }

}
