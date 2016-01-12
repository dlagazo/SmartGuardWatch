package com.android.sparksoft.smartguardwatch.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AdapterPlaces extends SQLiteOpenHelper {

    public static final String TABLE_PLACES = "place";
    public static final String PLACE_ID = "id";
    public static final String PLACE_NAME = "name";
    public static final String PLACE_LAT = "lat";
    public static final String PLACE_LONG = "long";



    private static final String DATABASE_NAME = "smartguardplaces.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PLACES + "(" + PLACE_ID + " integer primary key autoincrement, " +
            PLACE_NAME + " text not null," +
            PLACE_LAT + " text not null," +
            PLACE_LONG + " text not null);";

    public AdapterPlaces(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(AdapterPlaces.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        onCreate(db);
    }

}
