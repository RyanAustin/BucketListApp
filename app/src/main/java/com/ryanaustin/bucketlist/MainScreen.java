package com.ryanaustin.bucketlist;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;

import android.database.Cursor;
import android.view.*;
import android.widget.*;

import ryanaustin.com.bucketlist.R;


public class MainScreen extends Activity {
    private ListView locationListView;
    private WebView webView;
    private SharedPreferences preferences;
    private DbAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(Settings.PREF_NAME, 0);
        locationListView = (ListView)findViewById(R.id.listView);
        webView = (WebView) findViewById(R.id.imageWebView);
        webView.setInitialScale(30);
        WebSettings wSettings = webView.getSettings();
        wSettings.setUseWideViewPort(true);

        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editLocationIntent = new Intent(view.getContext(), LocationScreen.class);
                Cursor c = dbAdapter.fetchLocation(id);
                Locations locations = new Locations(c.getLong(c.getColumnIndexOrThrow(DbAdapter.KEY_ROWID)),
                        c.getString(c.getColumnIndexOrThrow(DbAdapter.KEY_NAME)),
                        c.getString(c.getColumnIndexOrThrow(DbAdapter.KEY_LAT)),
                        c.getString(c.getColumnIndexOrThrow(DbAdapter.KEY_LON)),
                        c.getInt(c.getColumnIndexOrThrow(DbAdapter.KEY_VISITED)));

                editLocationIntent.putExtra("Edit", locations);

                startActivityForResult(editLocationIntent, 1);
            }
        });
        registerForContextMenu(locationListView);

        openDB();

        // Use this method to populate test data on first run
//        testInputData();

        populateScreen();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        TextView textView = (TextView) info.targetView.findViewById(R.id.rowIdTextView);
        dbAdapter.deleteLocation(Long.parseLong(textView.getText().toString()));

        populateScreen();

        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        preferences = getSharedPreferences(Settings.PREF_NAME, 0);
        webView.clearView();
        openDB();
        populateScreen();
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
                startActivityForResult(settingsIntent, 3);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        dbAdapter.close();
        super.onDestroy();
    }

    private void testInputData(){
        dbAdapter.createLocation("Chicago", "41.869536", "-87.629502", 0);
        dbAdapter.createLocation("New York", "40.719968", "-73.99032", 1);
        dbAdapter.createLocation("Dallas", "32.792962", "-96.776315", 0);
        dbAdapter.createLocation("Kansas City", "39.074863", "-94.562673", 0);
    }

    private void populateScreen() {
        Cursor c = dbAdapter.fetchAllLocations();

        urlBuilder(c);

        startManagingCursor(c);

        String[] from = new String[] {DbAdapter.KEY_NAME, DbAdapter.KEY_ROWID};
        int[] to = new int[] {R.id.nameTextView, R.id.rowIdTextView};

        SimpleCursorAdapter cursorAdapter =
                new SimpleCursorAdapter(this,
                    R.layout.item_layout,
                    c,
                    from,
                    to);

        locationListView.setAdapter(cursorAdapter);
    }

    private void urlBuilder(Cursor c) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://maps.googleapis.com/maps/api/staticmap?size=800x800&maptype=hybrid&markers=size:mid|color:" +
                preferences.getString("visited", "green"));

        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            if (c.getInt(c.getColumnIndexOrThrow(DbAdapter.KEY_VISITED))==1) {
                sb.append("|");
                sb.append(c.getString(c.getColumnIndexOrThrow(DbAdapter.KEY_LAT)));
                sb.append(",");
                sb.append(c.getString(c.getColumnIndexOrThrow(DbAdapter.KEY_LON)));
            }
        }

        sb.append("&markers=size:mid|color:" + preferences.getString("notVisited", "red"));

        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);
            if (c.getInt(c.getColumnIndexOrThrow(DbAdapter.KEY_VISITED))==0) {
                sb.append("|");
                sb.append(c.getString(c.getColumnIndexOrThrow(DbAdapter.KEY_LAT)));
                sb.append(",");
                sb.append(c.getString(c.getColumnIndexOrThrow(DbAdapter.KEY_LON)));
            }
        }

        webView.loadUrl(sb.toString());
    }

    private void openDB() {
        dbAdapter = new DbAdapter(this);
        dbAdapter.open();
    }
}
