package ryanaustin.com.bucketlist;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


public class Settings extends Activity {

    public static final String PREF_NAME = "MyPrefsFile";
    private static final String[] SPINNER_OPTIONS = new String[] {"red", "green", "blue", "black", "purple"};
    private String visitedSelection;
    private String notVisitedSelection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, 0);

        // Create objects
        final Button backButton = (Button) findViewById(R.id.backButton);
        final Spinner visitedSpinner = (Spinner) findViewById(R.id.visitedSpinner);
        final Spinner notVisitedSpinner = (Spinner) findViewById(R.id.notVisitedSpinner);

        // Set adapters
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, SPINNER_OPTIONS);
        visitedSpinner.setAdapter(adapter);
        notVisitedSpinner.setAdapter(adapter);

        // Create the Listeners
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, new Intent());
                finish();
            }
        });
        visitedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                visitedSelection = SPINNER_OPTIONS[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        notVisitedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                notVisitedSelection = SPINNER_OPTIONS[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Get the default preferences and update the spinner values
        visitedSelection = preferences.getString("visited", "green");
        notVisitedSelection = preferences.getString("notVisited", "red");
        visitedSpinner.setSelection(getSpinnerOptionPosition(visitedSelection));
        notVisitedSpinner.setSelection(getSpinnerOptionPosition(notVisitedSelection));
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences preferences = getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("visited", visitedSelection);
        editor.putString("notVisited", notVisitedSelection);
        editor.commit();
    }

    private int getSpinnerOptionPosition(String preferences) {
        for (int i = 0; i < SPINNER_OPTIONS.length; i++) {
            if (SPINNER_OPTIONS[i].equals(preferences)) {
                return i;
            }
        }
        return 0;
    }
}
