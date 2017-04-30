package com.example.janirefernandez.planevent.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

/**
 * Created by JanireFernandez on 06/04/2017.
 */

public class SQLiteEvents extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandlerUsers.class.getSimpleName();
   // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "event_db";

    // Login table name
    private static final String TABLE_EVENT = "events";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_PLACE= "place";
    private static final String KEY_DATE = "date";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_TAG = "tag";
    private static final String KEY_UID = "uid";

    private SQLiteHandlerUsers db;

    public SQLiteEvents(Context context, String databaseName) {

        //super(context, DATABASE_NAME, null, DATABASE_VERSION);
        super(context, databaseName, null, DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENT_TABLE = "CREATE TABLE " + TABLE_EVENT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"
                + KEY_PLACE + " TEXT," +KEY_DATE + " TEXT," +KEY_DESCRIPTION +" TEXT,"
                +KEY_TAG + " TEXT," + KEY_UID + " TEXT" + ")";

        db.execSQL(CREATE_EVENT_TABLE);
     Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addEvent(String title,String place,String date, String description, String tag,
                         String uid) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        long id = 0;

        System.out.println(checkEventTitleIsNotTheSame(title));

        if(checkEventTitleIsNotTheSame(title)){
            values.put(KEY_TITLE, title); // Title
            values.put(KEY_PLACE, place); // Place
            values.put(KEY_DATE, date); //date
            values.put(KEY_DESCRIPTION, description); //description
            values.put(KEY_TAG, tag); //tag
            values.put(KEY_UID, uid); // uid

            // Inserting Row
            id = db.insert(TABLE_EVENT, null, values);
            db.close(); // Closing database connection
        }


        Log.d(TAG, "New event inserted into sqlite: " + id);
    }

    public void deleteEvent(String title)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_EVENT+ " WHERE "+KEY_TITLE+"='"+title+"'");
        db.close();

        Log.d(TAG, "Deleted event where title is: "+title);

    }
    /**
     * Getting event data from database
     * */
    public List<Event> getEventTableDetails() {

        String selectQuery = "SELECT  * FROM " + TABLE_EVENT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        try{
            cursor = db.rawQuery(selectQuery, null);
        }catch(Exception e)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
            // Create tables again
            onCreate(db);
            System.out.println("Table doesnt exists: "+TABLE_EVENT+" Creating a new one");
            cursor = db.rawQuery(selectQuery, null);
        }





        ArrayEvent eventArray = new ArrayEvent();

        for(int i =0 ; i< cursor.getCount(); i++)
        {
            cursor.moveToPosition(i);
            Event e = new Event();
            e.setTitle(cursor.getString(1));
            e.setPlace(cursor.getString(2));
            e.setDate(cursor.getString(3));
            e.setDescription(cursor.getString(4));
            e.setTag(cursor.getString(5));
            e.setUnique_id(cursor.getString(6));
            eventArray.addArrayEvents(e);

        }

        cursor.close();
        db.close();
        Log.d(TAG, "Fetching event from Sqlite");

        for(int i =0 ; i < eventArray.getArrayEvents().size(); i++)
        {
            System.out.println(eventArray.getArrayEvents().get(i).getTitle());
        }

        return eventArray.getArrayEvents();
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_EVENT, null, null);
        db.close();

        Log.d(TAG, "Deleted all events info from sqlite");
    }


    /*check the event title to not the repear the same event in the sqlite*/
    public boolean checkEventTitleIsNotTheSame(String title)
    {
        String selectQuery = "SELECT  * FROM " + TABLE_EVENT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        ArrayEvent eventArray = new ArrayEvent();

        for(int i =0 ; i< cursor.getCount(); i++) {

            cursor.moveToPosition(i);
            String titleEvent = cursor.getString(1);
            if(titleEvent.equalsIgnoreCase(title))
            {
                return false;
            }
        }
        return true;
    }
}
