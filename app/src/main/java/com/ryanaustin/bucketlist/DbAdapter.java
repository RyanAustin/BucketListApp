package com.ryanaustin.bucketlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

/**
 * User: Ryan
 * Date: 7/20/2015
 * Time: 5:25 PM
 */
public class DbAdapter {

    /**
     * Database column names
     */
    public static final String KEY_NAME = "location_name";
    public static final String KEY_LAT = "location_latitude";
    public static final String KEY_LON = "location_longitude";
    public static final String KEY_VISITED = "have_visited";
    public static final String KEY_ROWID = "_id";

    /**
     * Database variables and creation sql statement
     */
    private static final String DATABASE_NAME = "bucketlist.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "locations";
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            KEY_ROWID + " integer primary key autoincrement, " +
            KEY_NAME + " text not null, " +
            KEY_LAT + " text not null, " +
            KEY_LON + " text not null, " +
            KEY_VISITED + " integer not null);";

    private static final String TAG = "DbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE);
            } catch (android.database.SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading the database from version " + oldVersion + " to " +
                    newVersion + ". This will destroy all old data.");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
        mDbHelper = new DatabaseHelper(ctx);
    }

    /**
     * Open the location database. If the database cannot be opened, try to create a new
     * instance of the database. If the database cannot be created, throw an exception
     * to signal the method has failed.
     *
     * @return this
     * @throws SQLException if the database could be neither opened or created
     */
    public DbAdapter open(){
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    /**
     * Create a new location using the provided information.
     * If the location is successfully created return the new rowId for the location,
     * otherwise return -1 indicating failure to create the location.
     *
     * @param name the name of the location
     * @param lat the latitude of the location
     * @param lon the longitude of the location
     * @param visited boolean for if the location has been visited
     * @return rowId or -1 if failed
     */
    public long createLocation(String name, String lat, String lon, int visited) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_LAT, lat);
        initialValues.put(KEY_LON, lon);
        initialValues.put(KEY_VISITED, visited);

        return mDb.insert(TABLE_NAME, null, initialValues);
    }

    /**
     * Delete the location with the given rowId
     *
     * @param rowId id of location to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteLocation(long rowId) {
        return mDb.delete(TABLE_NAME, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all locations in the database
     *
     * @return Cursor over all locations
     */
    public Cursor fetchAllLocations() {
        return mDb.query(TABLE_NAME, new String[] {KEY_ROWID, KEY_NAME,
                KEY_LAT, KEY_LON, KEY_VISITED}, null, null, null, null, null);

    }

    /**
     * Return a Cursor positioned at the location that matches the given rowId
     *
     * @param rowId id of location to retrieve
     * @return Cursor positioned to matching location, if found
     * @throws SQLException if location could not be found/retrieved
     */
    public Cursor fetchLocation(long rowId){
        Cursor mCursor = mDb.query(true, TABLE_NAME, new String[] {KEY_ROWID, KEY_NAME, KEY_LAT,
                        KEY_LON, KEY_VISITED}, KEY_ROWID + "=" + rowId, null,
                null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Update the location using the details provided. The location to be updated is
     * specified using the rowId, and it is altered to use the name, lat, lon, and visited
     * values passed in
     *
     * @param rowId id of the location to update
     * @param name value to set location name to
     * @param lat value to set location latitude to
     * @param lon value to set location longitude to
     * @param visited value to set location visited boolean to
     * @return true if the location was successfully updated, false otherwise
     */
    public boolean updateLocation(long rowId, String name, String lat, String lon, int visited) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_LAT, lat);
        args.put(KEY_LON, lon);
        args.put(KEY_VISITED, visited);

        return mDb.update(TABLE_NAME, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
