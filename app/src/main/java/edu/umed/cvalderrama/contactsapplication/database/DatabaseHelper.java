package edu.umed.cvalderrama.contactsapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import edu.umed.cvalderrama.contactsapplication.bean.Contact;
import edu.umed.cvalderrama.contactsapplication.database.model.ContactModel;

/**
 * Database Helper class to interact with SQL Lite
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "contacts_db";

    /**
     * Create a helper instance
     *
     * @param context Android Context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Triggered when the application is installed
     *
     * @param db The writable database instance
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSql = "CREATE TABLE " + ContactModel.TABLE_NAME + "("
                + ContactModel.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ContactModel.FIELD_FIRSTNAME + " TEXT NOT NULL,"
                + ContactModel.FIELD_LASTNAME + " TEXT NOT NULL,"
                + ContactModel.FIELD_TELEPHONE + " TEXT,"
                + ContactModel.FIELD_PHOTOURI + " TEXT"
                + ")";

        db.execSQL(createSql);
    }

    /**
     * Triggered when a upgrade is detected
     *
     * @param db The writable database instance
     * @param oldVersion The old version of the DB
     * @param newVersion The new version of the DB
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + DATABASE_NAME);

        onCreate(db);
    }

    /**
     * Return a list of all Contacts
     *
     * @return The list of all contacts
     */
    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<Contact>();

        String query = "SELECT * FROM " + ContactModel.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                contacts.add(hydrateContactFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        db.close();

        return contacts;
    }

    /**
     * Return a contact by it's Id
     *
     * @param id The id of the contact
     * @return The contact found or empty contact
     */
    public Contact getContact(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(ContactModel.TABLE_NAME,
                new String[]{
                    ContactModel.FIELD_ID, ContactModel.FIELD_FIRSTNAME,
                    ContactModel.FIELD_LASTNAME, ContactModel.FIELD_TELEPHONE,
                    ContactModel.FIELD_PHOTOURI
                },
                ContactModel.FIELD_ID + "=?",
                new String[] {
                    String.valueOf(id)
                }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        Contact c = hydrateContactFromCursor(cursor);

        db.close();

        return c;
    }

    /**
     * Return the number of contacts
     *
     * @return The number of contacts
     */
    public long getContactsCount() {
        SQLiteDatabase db = this.getReadableDatabase();

        return DatabaseUtils.queryNumEntries(db, ContactModel.TABLE_NAME);
    }

    /**
     * Persists a Contact to the database
     *
     * @param c Contact to persist
     */
    public void saveContact(Contact c) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        // Append basic fields
        values.put(ContactModel.FIELD_FIRSTNAME, c.getFirstName());
        values.put(ContactModel.FIELD_LASTNAME, c.getLastName());
        values.put(ContactModel.FIELD_TELEPHONE, c.getTelephone());
        values.put(ContactModel.FIELD_PHOTOURI, c.getPhotoUri());

        if (c.getId() > 0) {
            values.put(ContactModel.FIELD_ID, c.getId());
            long id = c.getId();
            db.update(ContactModel.TABLE_NAME, values, ContactModel.FIELD_ID + "=?",
                    new String[]{ String.valueOf(id) });
        } else {
            long id = db.insert(ContactModel.TABLE_NAME, null, values);
            c.setId(id);
        }

        db.close();
    }

    /**
     * Deletes a Contact from the database
     *
     * @param c Contact to delete
     */
    public void deleteContact(Contact c) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(ContactModel.TABLE_NAME, ContactModel.FIELD_ID + "=?",
                new String[]{String.valueOf(c.getId())});
        db.close();
    }

    /**
     * Hydrates a Contact from a Cursor entry
     *
     * @param cursor The cursor returned from a select query
     * @return The hydrated contact
     */
    private Contact hydrateContactFromCursor(@Nullable Cursor cursor) {
        Contact c = new Contact();
        if (cursor == null) {
            return c;
        }

        c.setId(cursor.getInt(cursor.getColumnIndex(ContactModel.FIELD_ID)));
        c.setFirstName(cursor.getString(cursor.getColumnIndex(ContactModel.FIELD_FIRSTNAME)));
        c.setLastName(cursor.getString(cursor.getColumnIndex(ContactModel.FIELD_LASTNAME)));

        String telephone = cursor.getString(cursor.getColumnIndex(ContactModel.FIELD_TELEPHONE));
        if (!TextUtils.isEmpty(telephone)) {
            c.setTelephone(telephone);
        }
        String photoUri = cursor.getString(cursor.getColumnIndex(ContactModel.FIELD_PHOTOURI));
        if (!TextUtils.isEmpty(photoUri)) {
            c.setPhotoUri(photoUri);
        }

        return c;
    }
}
