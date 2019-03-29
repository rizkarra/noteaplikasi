package com.example.asus.note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class data extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "notes_db";


    public data (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(model.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + model.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertNote(String note) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(model.COLUMN_NOTE, note);

        // insert row
        long id = db.insert(model.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public model getNote(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(model.TABLE_NAME,
                new String[]{model.COLUMN_ID, model.COLUMN_NOTE, model.COLUMN_TIMESTAMP},
                model.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        model note = new model(
                cursor.getInt(cursor.getColumnIndex(model.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(model.COLUMN_NOTE)),
                cursor.getString(cursor.getColumnIndex(model.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return note;
    }

    public List<model> getAllNotes() {
        List<model> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + model.TABLE_NAME + " ORDER BY " +
                model.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                model note = new model();
                note.setId(cursor.getInt(cursor.getColumnIndex(model.COLUMN_ID)));
                note.setNote(cursor.getString(cursor.getColumnIndex(model.COLUMN_NOTE)));
                note.setTimestamp(cursor.getString(cursor.getColumnIndex(model.COLUMN_TIMESTAMP)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + model.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateNote(model note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(model.COLUMN_NOTE, note.getNote());

        // updating row
        return db.update(model.TABLE_NAME, values, model.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }

    public void deleteNote(model note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(model.TABLE_NAME, model.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
        db.close();
    }

}
