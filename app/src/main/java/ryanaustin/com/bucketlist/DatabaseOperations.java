package ryanaustin.com.bucketlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ryanaustin.com.bucketlist.TableData.TableInfo;

/**
 * Created by Ryan on 7/24/2015.
 */
public class DatabaseOperations extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "CREATE TABLE " + TableInfo.DATABASE_TABLE + " (" +
            TableInfo.KEY_NAME + " text not null, " +
            TableInfo.KEY_LAT + " text not null, " +
            TableInfo.KEY_LON + " text not null, " +
            TableInfo.KEY_VISITED + " integer not null);";

    public DatabaseOperations(Context context) {
        super(context, TableInfo.DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("Database operations", "Database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.d("Database operations", "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("Database operations", "Upgrading the database from version " + oldVersion + " to " +
                newVersion + ". This will destroy all old data.");
        db.execSQL("DROP TABLE IF EXISTS " + TableInfo.DATABASE_TABLE);
        onCreate(db);
    }

    public void createLocation(DatabaseOperations dOP, String name, String lat, String lon, int visited) {
        SQLiteDatabase SQ = dOP.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TableInfo.KEY_NAME, name);
        cv.put(TableInfo.KEY_LAT, lat);
        cv.put(TableInfo.KEY_LON, lon);
        cv.put(TableInfo.KEY_VISITED, visited);

        long k = SQ.insert(TableInfo.DATABASE_TABLE, null, cv);
        Log.d("Database operations", "Location created");
    }
}
