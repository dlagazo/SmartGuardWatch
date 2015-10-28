package com.android.sparksoft.smartguardwatch.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AdapterContacts extends SQLiteOpenHelper {

    public static final String TABLE_CONTACTS = "contacts";
    public static final String CONTACT_ID = "id";
    public static final String CONTACT_FIRST = "first";
    public static final String CONTACT_LAST = "last";
    public static final String CONTACT_EMAIL = "email";
    public static final String CONTACT_MOBILE = "mobile";
    public static final String CONTACT_RELATION = "relation";
    public static final String CONTACT_RANK = "rank";

    private static final String DATABASE_NAME = "smartguard.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_CONTACTS + "(" + CONTACT_ID + " integer primary key autoincrement, " +
            CONTACT_FIRST + " text," +
            CONTACT_LAST + " text," +
            CONTACT_EMAIL + " text," +
            CONTACT_MOBILE + " text not null," +
            CONTACT_RELATION + " text," +
            CONTACT_RANK + " integer not null" +
            ");";

    public AdapterContacts(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(AdapterContacts.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

}
