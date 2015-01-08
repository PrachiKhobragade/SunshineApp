package com.prachi.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private String LOG_TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(LOG_TAG,"Inside main activity onCreate");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.i(LOG_TAG,"Inside main activity onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingIntent);
            return true;
        }
        else if (id==R.id.action_map)
        {
            openPreferredLocation();
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocation()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String location = prefs.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        Uri geoLocation =new Uri.Builder().scheme("geo").encodedOpaquePart("0,0?q= " +Uri.encode(location)).build();
                //Uri.parse("geo:0,0").buildUpon().
               // appendQueryParameter("q", location).build();
        Log.i("LocationStringBefore",geoLocation.toString());
        geoLocation.getEncodedQuery();
        Log.i("LocationString",geoLocation.toString());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }


    }

    /**
     * A placeholder fragment containing a simple view.
     */

}
