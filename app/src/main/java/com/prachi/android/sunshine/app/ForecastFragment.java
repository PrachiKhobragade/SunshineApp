package com.prachi.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.prachi.android.sunshine.app.data.WeatherContract;
import com.prachi.android.sunshine.app.data.WeatherContract.LocationEntry;
import com.prachi.android.sunshine.app.data.WeatherContract.WeatherEntry;
import com.prachi.android.sunshine.app.sync.SunshineSyncAdapter;

import java.util.Date;

/**
 * Created by Prachi on 12/25/2014.
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class ForecastFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final String SELECTED_KEY = "SelectionPosition";
    private ForecastAdapter mForecastAdapter= null;

    private static final int FORECAST_LOADER = 0;
    private String mLocation;

    private String LOG_TAG = ForecastFragment.class.getSimpleName();


    private static final String[] FORECAST_COLUMNS = {
// In this case the id needs to be fully qualified with a table name, since
// the content provider joins thjava.lang.Stringe location & weather tables in the background
// (both have an _id column)
// On the one hand, that's annoying. On the other, you can search the weather table
// using the location set by the user, which is only in the Location table.
// So the convenience is worth it.
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherEntry.COLUMN_WEATHER_ID,
            LocationEntry.COLUMN_COORD_LAT,
            LocationEntry.COLUMN_COORD_LONG
    };
    // These indices are tied to FORECAST_COLUMNS. If FORECAST_COLUMNS changes, these
// must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_WEATHER_CONDITION_ID = 6;
    public static final int COL_COORD_LAT = 7;
    public static final int COL_COORD_LONG = 8;

    private int mPosition = ListView.INVALID_POSITION;
    private ListView mlistView= null;
    private boolean mUseTodayLayout = false;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id==R.id.action_map)
        {
            openPreferredLocation();
        }

        return super.onOptionsItemSelected(item);
    }


    private void openPreferredLocation()
    {
        if ( null != mForecastAdapter ) {
            Cursor c = mForecastAdapter.getCursor();
            if (null != c) {
                c.moveToPosition(0);
                String posLat = c.getString(COL_COORD_LAT);
                String posLong = c.getString(COL_COORD_LONG);
                Log.i("LocationString", posLat+" "+posLong);
               /* Uri geoLocation = new Uri.Builder().scheme("geo").encodedOpaquePart("0,0?q= " +
                        posLat + "," + posLong).build();*/
                Uri geoLocation = Uri.parse("geo:"+posLat+","+posLong);
                //Uri.parse("geo:0,0").buildUpon().
                // appendQueryParameter("q", location).build();
                Log.i("LocationStringBefore", geoLocation.toString());
                geoLocation.getEncodedQuery();
                Log.i("LocationString", geoLocation.toString());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(geoLocation);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        }
    }
    public void setUseTodayLayout(boolean useTodayLayout)
    {
        mUseTodayLayout = useTodayLayout;
        if(mForecastAdapter != null)
        {
            mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Log.i(LOG_TAG,"INside OnCreateView");

        mForecastAdapter = new ForecastAdapter(getActivity(),null,0);

        mForecastAdapter.setUseTodayLayout(mUseTodayLayout);

         mlistView = (ListView)rootView.findViewById(R.id.listview_forecast);

        mlistView.setAdapter(mForecastAdapter);
        Cursor cursor = mForecastAdapter.getCursor();
      //  Log.i(LOG_TAG,"On create View , the num of columns in cursor "+ cursor.getColumnCount());
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mForecastAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback)getActivity()).onItemSelected( cursor.getString(COL_WEATHER_DATE));

                }
                mPosition = position;

            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY))
        {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate the currently selected list item Should be saved.
        // When no selection, mPosition will be set to ListView.INVALID_POSITION
        // so check that before storing
        if(mPosition != ListView.INVALID_POSITION)
            outState.putInt(SELECTED_KEY, mPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(LOG_TAG,"Inside onActivityCreated");
        getLoaderManager().initLoader(FORECAST_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateWeather()
    {
        SunshineSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public void onResume() {
         super.onResume();
        Log.i(LOG_TAG,"Inside on Resume");
         if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
             getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
             }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created. This
// fragment only uses one loader, so we don't care about checking the id.
// To only show current and future dates, get the String representation for today,
// and filter the query to return weather only for dates after or including today.
// Only return data after today.
        String startDate = WeatherContract.getDbDateString(new Date());
// Sort order: Ascending, by date.
        String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";
        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startDate);

        Log.i(LOG_TAG,"Inside onCreateLoader ; the location is "+mLocation);
// Now create and return a CursorLoader that will take care of
// creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
        if(mPosition != ListView.INVALID_POSITION)
        {
            mlistView.setSelection(mPosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String date);
    }


}