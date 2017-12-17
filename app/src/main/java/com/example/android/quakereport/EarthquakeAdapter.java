package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by James on 11/16/2017.
 */

public class EarthquakeAdapter extends ArrayAdapter {

    private static final String LOCATION_SEPARATOR = "of";

    static class ViewHolder {

        // Cached reference to magnitude TextView
        private TextView magnitudeTextView;

        // Cached reference to location offset TextView
        private TextView locationOffsetTextView;

        // Cached reference to primary location TextView
        private TextView primaryLocationTextView;

        // Cached reference to date TextView
        private TextView dateTextView;

        // Cached reference to time TextView
        private TextView timeTextView;
    }

    /**
     * Constructor for the com.example.android.quakereport.EarthquakeAdapter
     *
     * @param context - Context to be used in inflating the View
     * @param objects - ArrayList of objects to be used to populate the ListView
     */
    public EarthquakeAdapter(@NonNull Context context, @NonNull List objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Create reference to a ViewHolder object.
        ViewHolder viewHolder;

        // Check whether the convertView already exists (is not null) or whether we need to create a new one.
        // We will also be setting a tag with a new ViewHolder for newly inflated convertViews.
        if (convertView == null) { // convertView has not previously been inflated & we must do so
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.magnitudeTextView = (TextView) convertView.findViewById(R.id.magnitude);
            viewHolder.locationOffsetTextView = (TextView) convertView.findViewById(R.id.location_offset);
            viewHolder.primaryLocationTextView = (TextView) convertView.findViewById(R.id.primary_location);
            viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.date);
            viewHolder.timeTextView = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the Earthquake object stored at the designated position in the data array & create a reference.
        Earthquake currentEarthquake = (Earthquake) getItem(position);

//        // Set background for the magnitude TextView
//        viewHolder.magnitudeTextView.setBackground(getContext().getResources().getDrawable(R.drawable.magnitude_circle));

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) viewHolder.magnitudeTextView.getBackground();
        // Get the appropriate background color - via helper method - based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentEarthquake.getMagnitude());
        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        // Process magnitude for single decimal point - via helper method - and set on the appropriate TextView.
        viewHolder.magnitudeTextView.setText(formatMagnitude(currentEarthquake.getMagnitude()));

        // Process location offset from general Location string - via helper method - and set on the appropriate TextView.
        viewHolder.locationOffsetTextView.setText(formatLocationOffset(currentEarthquake.getLocation()));

        // Process primary location from general Location string - via helper method - and set on the appropriate TextView.
        viewHolder.primaryLocationTextView.setText(formatPrimaryLocation(currentEarthquake.getLocation()));

        // Get time (in milliseconds) from the current Earthquake object.
        Long timeInMilliseconds = currentEarthquake.getTimeInMilliseconds();
        // Get time (in milliseconds) from the current Earthquake object and convert into a new Date object.
        Date dateObject = new Date(currentEarthquake.getTimeInMilliseconds());
        // Process Date object into a formatted date - via helper method - &and set on the appropriate TextView.
        viewHolder.dateTextView.setText(formatDate(dateObject));
        // Process Date object into a formatted time - via helper method - &and set on the appropriate TextView.
        viewHolder.timeTextView.setText(formatTime(dateObject));

        return convertView;
    }

    private int getMagnitudeColor(double magnitude) {
        // Variable to hold formatted magnitude to check for in switch statement
        int magnitudeInteger = new Double(magnitude).intValue();
        // Variable to hold switch statement color resource id value to be assigned
        int magnitudeColorResourceId;

        // Switch statement to determine which color resource ID (int) to return
        switch (magnitudeInteger) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            case 10:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }

        // Obtain actual color from the color resource ID and return it
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }

    /**
     * Helper method to display magnitude with a single decimal point.
     * @param magnitude is the eathquake's strength, in the form of a double with any number of decimal points
     * @return magnitude in the form of a double with single decimal point.
     */
    private String formatMagnitude(double magnitude) {
        DecimalFormat formatter = new DecimalFormat("0.0");
        return formatter.format(magnitude);
    }

    /**
     * Process general location string to isolate/return offset string.
     *
     * @param rawLocationString is the raw location string returned from USGS Json query.
     * @return locationOffsetString is the processed/generated string.
     */
    private String formatLocationOffset(String rawLocationString) {
        // Declare a string to hold formatted text
        String locationOffsetString = null;

        // Check whether there is any offset language (for example, "87 km NNE of").
        if (!rawLocationString.contains(LOCATION_SEPARATOR)) { // If there is NOT, we will use generate standard language.
            locationOffsetString = getContext().getString(R.string.near_the);
        } else { // if there IS offset language, we need to process/extract it.
            // Identify the index position where the substring "of" begins. Add 2 to return index of the space following "of".
            int breakPoint = rawLocationString.indexOf(LOCATION_SEPARATOR) + 2;

            // Extract the substring from beginning of general location string up to end of "of".
            locationOffsetString = rawLocationString.substring(0, breakPoint);
        }

        return locationOffsetString;
    }

    /**
     * Process general location string to isolate/return primary location string.
     *
     * @param rawLocationString is the raw location string returned from USGS Json query.
     * @return
     */
    private String formatPrimaryLocation(String rawLocationString) {

        // Check whether there is any offset language (for example, "87 km NNE of").
        if (!rawLocationString.contains(LOCATION_SEPARATOR)) { // If there is NOT, we return full string.
            return rawLocationString;
        } else { // if there IS offset language, we need to process the raw string to extract the primary location portion.
            // Identify the index position where the substring "of" begins. Add 3 to return index of beginning of word following "of".
            int breakPoint = rawLocationString.indexOf(LOCATION_SEPARATOR) + 3;

            // Extract and return the substring from beginning of general location string up to end of "of".
            return rawLocationString.substring(breakPoint, rawLocationString.length());
        }
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

}
