package com.android.sparksoft.smartguardwatch.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AdapterReminders extends SQLiteOpenHelper {

    public static final String TABLE_REMINDERS = "reminders";
    public static final String REMINDER_ID = "id";
    public static final String REMINDER_DESC = "desc";
    public static final String REMINDER_DATE = "stamp";

    private static final String DATABASE_NAME = "smartguard.db.reminders";
    private static final int DATABASE_VERSION = 6;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_REMINDERS + "(" + REMINDER_ID + " integer primary key autoincrement, " +
            REMINDER_DESC + " text," +
            REMINDER_DATE + " date);";

    public AdapterReminders(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(AdapterReminders.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
        onCreate(db);
    }

}
