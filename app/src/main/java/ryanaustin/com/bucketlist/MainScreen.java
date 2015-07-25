package ryanaustin.com.bucketlist;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import android.database.Cursor;
import android.view.*;
import android.widget.*;


public class MainScreen extends Activity {
    private ListView locationListView;
    private DbAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView imageView = (ImageView) findViewById(R.id.imageView);

        SharedPreferences preferences = getSharedPreferences(Settings.PREF_NAME, 0);
        locationListView = (ListView)findViewById(R.id.listView);

        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editLocationIntent = new Intent(view.getContext(), LocationScreen.class);
                Cursor c = dbAdapter.fetchLocation(id);
                Location location = new Location(c.getLong(c.getColumnIndexOrThrow(DbAdapter.KEY_ROWID)),
                        c.getString(c.getColumnIndexOrThrow(DbAdapter.KEY_NAME)),
                        c.getString(c.getColumnIndexOrThrow(DbAdapter.KEY_LAT)),
                        c.getString(c.getColumnIndexOrThrow(DbAdapter.KEY_LON)),
                        c.getInt(c.getColumnIndexOrThrow(DbAdapter.KEY_VISITED)));

                editLocationIntent.putExtra("Edit", location);

                startActivityForResult(editLocationIntent, 1);
            }
        });
        registerForContextMenu(locationListView);

        openDB();
        populateList(dbAdapter);


//        // show The Image
//        new DownloadImageTask(imageView).execute("http://maps.googleapis.com/maps/api/staticmap?size=800x800&maptype=hybrid");
    }

//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//
//        public DownloadImageTask(ImageView bmImage) {
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//        }
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        dbAdapter.deleteLocation();
        populateList(dbAdapter);

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED){
            return;
        }

        openDB();
        populateList(dbAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.addLocation:
                Intent addLocationIntent = new Intent(getApplicationContext(), LocationScreen.class);
                startActivityForResult(addLocationIntent, 2);
                return true;
            case R.id.mainSettings:
                Intent settingsIntent = new Intent(getApplicationContext(), Settings.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void testInputData(DbAdapter adapter){
        adapter.createLocation("Chicago", "36.258", "78.654", 0);
        adapter.createLocation("New York", "92.147", "15.243", 1);
        adapter.createLocation("Dallas", "78.246", "32.721", 0);
        adapter.createLocation("Kansas City", "80.692", "36.519", 0);
    }

    private void populateList(DbAdapter adapter) {
        Cursor c = adapter.fetchAllLocations();

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter();

        locationListView.setAdapter(cursorAdapter);
    }

    private void openDB() {
        dbAdapter = new DbAdapter(getApplicationContext());
        dbAdapter.open();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }

    private void closeDB() {
        dbAdapter.close();
    }
}
