package ryanaustin.com.bucketlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.sql.SQLException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.*;
import android.widget.*;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MainScreen extends Activity {

    private List<Location> locations = new ArrayList<>();
    private ListView locationListView;
    Context ctx = this;

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
                editLocationIntent.putExtra("Edit", locations.get(position));
                startActivityForResult(editLocationIntent, 1);
            }
        });
        registerForContextMenu(locationListView);

        BucketListDbAdapter blAdapter = new BucketListDbAdapter(getApplicationContext());

        blAdapter.open();
        fetchData(blAdapter);
        populateList();
        blAdapter.close();


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
        BucketListDbAdapter blAdapter = new BucketListDbAdapter(getApplicationContext());

        blAdapter.open();
        blAdapter.deleteLocation(locations.get(info.position).getRowID());
        locations.remove(info.position);
        populateList();

        blAdapter.close();

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED){
            return;
        }

        Serializable extraAdd = data.getSerializableExtra("Add");
        Serializable extraEdit = data.getSerializableExtra("Edit");

        if(extraAdd != null) {
            Location newLocation = (Location)extraAdd;
            locations.add(newLocation);
            populateList();
        } else if (extraEdit != null) {
            Location newLocation = (Location)extraEdit;
            for (int i = 0; i < locations.size(); i++) {
                if (newLocation.getRowID() == locations.get(i).getRowID()) {
                    locations.set(i, newLocation);
                }
            }
            populateList();
        }
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

    private void fetchData(BucketListDbAdapter adapter) {
        Cursor c = adapter.fetchAllLocations();

        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            Location location = new Location(c.getLong(c.getColumnIndexOrThrow(BucketListDbAdapter.KEY_ROWID)),
                    c.getString(c.getColumnIndexOrThrow(BucketListDbAdapter.KEY_NAME)),
                    c.getString(c.getColumnIndexOrThrow(BucketListDbAdapter.KEY_LAT)),
                    c.getString(c.getColumnIndexOrThrow(BucketListDbAdapter.KEY_LON)),
                    c.getInt(c.getColumnIndexOrThrow(BucketListDbAdapter.KEY_VISITED)));
            locations.add(location);
        }
    }

    private void testInputData(BucketListDbAdapter adapter){
        adapter.createLocation("Chicago", "36.258", "78.654", 0);
        adapter.createLocation("New York", "92.147", "15.243", 1);
        adapter.createLocation("Dallas", "78.246", "32.721", 0);
        adapter.createLocation("Kansas City", "80.692", "36.519", 0);
    }

    private void populateList() {
        List<String> values = new ArrayList<>();

        for (Location location : locations) {
            values.add(location.getLocation());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, values);

        locationListView.setAdapter(adapter);
    }
}
