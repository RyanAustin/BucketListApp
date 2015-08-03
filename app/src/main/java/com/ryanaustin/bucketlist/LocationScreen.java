package com.ryanaustin.bucketlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;

import ryanaustin.com.bucketlist.R;


public class LocationScreen extends Activity {

    private Double lat;
    private Double lon;
    private String TAG = "myApp";
    private DbAdapter dbAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_screen);

        openDB();

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        final Button saveButton = (Button) findViewById(R.id.saveButton);
        final Button gpsButton = (Button) findViewById(R.id.gpsButton);
        final EditText locationEditText = (EditText) findViewById(R.id.locationEditText);
        final EditText latitudeEditText = (EditText) findViewById(R.id.latitudeEditText);
        final EditText longitudeEditText = (EditText) findViewById(R.id.longitudeEditText);
        final CheckBox visitedCheckBox = (CheckBox) findViewById(R.id.visitedCheckBox);

        final Serializable extra = getIntent().getSerializableExtra("Edit");
        if (extra != null) {
            Locations locations = (Locations)extra;
            locationEditText.setText(locations.getLocation());
            latitudeEditText.setText(locations.getLatitude());
            longitudeEditText.setText(locations.getLongitude());
            if (locations.getVisited() == 1) {
                visitedCheckBox.setChecked(true);
            }
        }

        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lat!=null) {
                    latitudeEditText.setText(Double.toString(lat));
                    longitudeEditText.setText(Double.toString(lon));
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extra != null) {
                    Intent returnIntent = new Intent();
                    Locations locations = (Locations)extra;

                    locations.setLocation(locationEditText.getText().toString());
                    locations.setLatitude(latitudeEditText.getText().toString());
                    locations.setLongitude(longitudeEditText.getText().toString());
                    locations.setVisited(visitedCheckBox.isChecked() ? 1 : 0);

                    dbAdapter.updateLocation(locations.getRowID(), locations.getLocation(), locations.getLatitude(),
                            locations.getLongitude(), locations.getVisited());

                    setResult(RESULT_OK);
                    finish();
                } else {
                    Intent returnIntent = new Intent();
                    Locations locations = new Locations();

                    locations.setLocation(locationEditText.getText().toString());
                    locations.setLatitude(latitudeEditText.getText().toString());
                    locations.setLongitude(longitudeEditText.getText().toString());
                    locations.setVisited(visitedCheckBox.isChecked() ? 1 : 0);

                    dbAdapter.createLocation(locations.getLocation(), locations.getLatitude(),
                            locations.getLongitude(), locations.getVisited());

                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, new Intent());
                finish();
            }
        });
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if(location!=null) {
                lat = location.getLatitude();
                lon = location.getLongitude();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    protected void onDestroy() {
        dbAdapter.close();
        super.onDestroy();
    }

    private void openDB() {
        dbAdapter = new DbAdapter(this);
        dbAdapter.open();
    }
}
