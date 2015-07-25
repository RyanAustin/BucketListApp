package ryanaustin.com.bucketlist;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.Serializable;
import java.sql.SQLException;


public class LocationScreen extends Activity {

    private BucketListDbAdapter blAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_screen);
        blAdapter = new BucketListDbAdapter(this);
        try {
            blAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        final Button saveButton = (Button) findViewById(R.id.saveButton);
        final EditText locationEditText = (EditText) findViewById(R.id.locationEditText);
        final EditText latitudeEditText = (EditText) findViewById(R.id.latitudeEditText);
        final EditText longitudeEditText = (EditText) findViewById(R.id.longitudeEditText);
        final CheckBox visitedCheckBox = (CheckBox) findViewById(R.id.visitedCheckBox);

        final Serializable extra = getIntent().getSerializableExtra("Edit");
        if (extra != null) {
            Location location = (Location)extra;
            locationEditText.setText(location.getLocation());
            latitudeEditText.setText(location.getLatitude());
            longitudeEditText.setText(location.getLongitude());
            if (location.getVisited() == 1) {
                visitedCheckBox.setChecked(true);
            }
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extra != null) {
                    Intent returnIntent = new Intent();
                    Location location = (Location)extra;

                    location.setLocation(locationEditText.getText().toString());
                    location.setLatitude(latitudeEditText.getText().toString());
                    location.setLongitude(longitudeEditText.getText().toString());
                    location.setVisited(visitedCheckBox.isChecked() ? 1 : 0);

                    blAdapter.updateLocation(location.getRowID(), location.getLocation(), location.getLatitude(),
                            location.getLongitude(), location.getVisited());

                    returnIntent.putExtra("Edit", location);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    Intent returnIntent = new Intent();
                    Location location = new Location();

                    location.setLocation(locationEditText.getText().toString());
                    location.setLatitude(latitudeEditText.getText().toString());
                    location.setLongitude(longitudeEditText.getText().toString());
                    location.setVisited(visitedCheckBox.isChecked() ? 1 : 0);

                    blAdapter.createLocation(location.getLocation(), location.getLatitude(),
                            location.getLongitude(), location.getVisited());

                    returnIntent.putExtra("Add", location);
                    setResult(RESULT_OK, returnIntent);
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
}
