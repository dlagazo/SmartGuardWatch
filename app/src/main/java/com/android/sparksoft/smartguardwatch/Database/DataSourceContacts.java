package com.android.sparksoft.smartguardwatch.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.android.sparksoft.smartguardwatch.Models.Contact;

import java.util.ArrayList;

public class DataSourceContacts {

    // Database fields
    private SQLiteDatabase database;
    private AdapterContacts adapterContacts;
    private String[] allColumns = { adapterContacts.CONTACT_ID,
            adapterContacts.CONTACT_FIRST, adapterContacts.CONTACT_LAST,
            adapterContacts.CONTACT_EMAIL, adapterContacts.CONTACT_MOBILE,
            adapterContacts.CONTACT_RELATION, adapterContacts.CONTACT_RANK, adapterContacts.CONTACT_SCHED
    };

    public DataSourceContacts(Context context) {

        adapterContacts = new AdapterContacts(context);
    }

    public void open() throws SQLException {
        database = adapterContacts.getWritableDatabase();
    }

    public void close() {
        adapterContacts.close();
    }

    public void createContact(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(adapterContacts.CONTACT_ID, contact.getId());
        values.put(adapterContacts.CONTACT_FIRST, contact.getFirstName());
        values.put(adapterContacts.CONTACT_LAST, contact.getLastName());
        values.put(adapterContacts.CONTACT_EMAIL, contact.getEmail());
        values.put(adapterContacts.CONTACT_MOBILE, contact.getMobile());
        values.put(adapterContacts.CONTACT_RELATION, contact.getRelation());
        values.put(adapterContacts.CONTACT_RANK, contact.getRank());
        values.put(adapterContacts.CONTACT_SCHED, contact.getSchedule());
        long insertId = database.insert(adapterContacts.TABLE_CONTACTS, null,
                values);

    }

    public void deleteContact(int id) {

        database.delete(adapterContacts.TABLE_CONTACTS, adapterContacts.CONTACT_ID
                + " = " + id, null);
    }

    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        Cursor cursor = database.query(adapterContacts.TABLE_CONTACTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Contact contact = cursorToContact(cursor);
            contacts.add(contact);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return contacts;
    }

    private Contact cursorToContact(Cursor cursor) {
        Contact contact = new Contact(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6), cursor.getString(7));

        return contact;
    }
}

