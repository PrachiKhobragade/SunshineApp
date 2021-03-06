package com.prachi.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Prachi on 1/7/2015.
 */
public class ForecastAdapter extends CursorAdapter {


    private final int VIEW_TYPE_TODAY =0;
    private final int VIEW_TYPE_FUTURE_DAY =1;
    private String LOG_TAG = ForecastAdapter.class.getSimpleName();
    private boolean mUseTodayLayout = false;

    public ForecastAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);
        Log.i(LOG_TAG,"Inside ForecastAdaptor Constructor");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        Log.i(LOG_TAG,"Inside newView");
        int viewType = getItemViewType(cursor.getPosition());
        int layoutID = -1;
        layoutID =(viewType == 0)?R.layout.list_item_forecast_today:R.layout.list_item_forecast;
        View view =LayoutInflater.from(context).inflate(layoutID, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        Log.i(LOG_TAG,"The layout id inside newView is " +layoutID);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {

        ViewHolder viewHolder = (ViewHolder)view.getTag();

        int viewType = getItemViewType(cursor.getPosition());
       switch (viewType) {
         case VIEW_TYPE_TODAY: {
             // Get weather icon
                     viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(
                             cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
             break;
             }
         case VIEW_TYPE_FUTURE_DAY: {
             // Get weather icon
                     viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(
                             cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
             break;
             }
         }
        // Read weather icon ID from cursor

// Use placeholder image for now


// Read date from cursor
        String dateString = cursor.getString(ForecastFragment.COL_WEATHER_DATE);
// Find TextView and set formatted date on it

        Log.i(LOG_TAG,"The dateString  is "+dateString);
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));
// Read weather forecast from cursor
        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
// Find TextView and set weather forecast on it

        viewHolder.descriptionView.setText(description);
// Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);
// Read high temperature from cursor
        float high = cursor.getFloat(ForecastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highView.setText(Utility.formatTemperature(context,high, isMetric));

// Read low temperature from cursor
        float low = cursor.getFloat(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.lowView.setText(Utility.formatTemperature(context,low, isMetric));

    }

    @Override
    public int getViewTypeCount() {
       return 2;
    }

    public void setUseTodayLayout(boolean useTodayLayout)
    {
        mUseTodayLayout = useTodayLayout;
    }
    @Override
    public int getItemViewType(int position) {
        return (position ==0 && mUseTodayLayout)?VIEW_TYPE_TODAY:VIEW_TYPE_FUTURE_DAY;
    }


    public static class ViewHolder
    {

        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highView;
        public final TextView lowView;

        public ViewHolder(View view)
        {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }


    }
}
