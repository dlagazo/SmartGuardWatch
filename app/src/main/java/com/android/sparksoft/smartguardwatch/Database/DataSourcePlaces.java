package com.android.sparksoft.smartguardwatch.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.android.sparksoft.smartguardwatch.Models.Place;

import java.util.ArrayList;

public class DataSourcePlaces {

    // Database fields
    private SQLiteDatabase database;
    private AdapterPlaces adapterPlaces;
    private String[] allColumns = { adapterPlaces.PLACE_ID,
            adapterPlaces.PLACE_NAME, adapterPlaces.PLACE_LAT,
            adapterPlaces.PLACE_LONG
    };

    public DataSourcePlaces(Context context) {
        adapterPlaces = new AdapterPlaces(context);
    }

    public void open() throws SQLException {
        database = adapterPlaces.getWritableDatabase();
    }

    public void close() {
        adapterPlaces.close();
    }

    public void createPlace(Place place) {
        ContentValues values = new ContentValues();
        values.put(adapterPlaces.PLACE_ID, place.getId());
        values.put(adapterPlaces.PLACE_NAME, place.getPlaceName());
        values.put(adapterPlaces.PLACE_LAT, place.getPlaceLat());
        values.put(adapterPlaces.PLACE_LONG, place.getPlaceLong());

        long insertId = database.insert(adapterPlaces.TABLE_PLACES, null,
                values);

    }

    public void deletePlace(int id) {

        database.delete(adapterPlaces.TABLE_PLACES, adapterPlaces.PLACE_ID
                + " = " + id, null);
    }

    public void deleteAllPlaces(){
        database.delete(adapterPlaces.TABLE_PLACES, adapterPlaces.PLACE_ID + " > 0", null);
    }

    public ArrayList<Place> getAllPlaces() {
        ArrayList<Place> places = new ArrayList<Place>();

        Cursor cursor = database.query(adapterPlaces.TABLE_PLACES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Place place = cursorToPlace(cursor);
            places.add(place);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return places;
    }

    private Place cursorToPlace(Cursor cursor) {
        Place place = new Place(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3));

        return place;
    }
}

