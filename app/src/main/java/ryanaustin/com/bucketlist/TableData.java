package ryanaustin.com.bucketlist;

import android.provider.BaseColumns;

/**
 * Created by Ryan on 7/24/2015.
 */
public class TableData {

    public TableData() {

    }

    public static abstract class TableInfo implements BaseColumns {
        public static final String KEY_NAME = "location_name";
        public static final String KEY_LAT = "location_latitude";
        public static final String KEY_LON = "location_longitude";
        public static final String KEY_VISITED = "have_visited";
        public static final String DATABASE_NAME = "bucketlist.db";
        public static final String DATABASE_TABLE = "locations";
    }
}
