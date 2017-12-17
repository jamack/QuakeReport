package com.example.android.quakereport;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Earthquake> extractEarthquakes(String dataSourceUrl) {
        Log.v(LOG_TAG,"Entering the extractEarthquakes method.");
        // Create an empty ArrayList that we can start adding earthquakes to
        List<Earthquake> earthquakes = new ArrayList<>();

        // TODO: GET JSON RESPONSE STRING
        URL formattedURL = null;
        String queryString = null;
        // Create formatted URL, via helper method
        if (dataSourceUrl != null && dataSourceUrl != "") {
            Log.v(LOG_TAG, "In extractEarthquake method & dataSourceUrl: " + dataSourceUrl);
            formattedURL = formatURL(dataSourceUrl);
        }
        // Use URL to make HTTP connection and receive String of data, via helper method
        if (formattedURL != null) {
            try {
                queryString = makeHttpRequest(formattedURL);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Unable to complete URL connection.", e);
            }
        }

        if (queryString != null && queryString != "") {
            // Try to parse the queryString response. If there's a problem with the way the JSON
            // is formatted, a JSONException exception object will be thrown.
            // Catch the exception so the app doesn't crash, and print the error message to the logs.
            try {

                // Parse the response given by the SAMPLE_JSON_RESPONSE string and
                // build up a list of Earthquake objects with the corresponding data.

                // Convert the Json string into a Json object
                JSONObject queriedEarthquakes = new JSONObject(queryString);

                //Extract “features”JSONArray
                JSONArray earthquakeFeatures = queriedEarthquakes.getJSONArray("features");

                //Loop through each feature in the array
                for (int i = 0; i < earthquakeFeatures.length(); i++) {
                    //Get earthquake JSONObject at position i
                    JSONObject earthquakeJsonObject = earthquakeFeatures.getJSONObject(i);

                    //Get “properties” JSONObject
                    JSONObject properties = earthquakeJsonObject.getJSONObject("properties");

                    //Extract “mag” for magnitude
                    double magnitude = properties.getDouble("mag");
                    //Extract “place” for location
                    String place = properties.getString("place");
                    //Extract “time” for time
                    long timeInMilliseconds = properties.getLong("time");
                    // Extract "url" for USGS webpage url
                    String url = properties.getString("url");

                    //Create Earthquake java object from magnitude, location, and time
                    Earthquake earthquake = new Earthquake(magnitude, place, timeInMilliseconds, url);

                    //Add earthquake to list of earthquakes
                    earthquakes.add(earthquake);
                }

            } catch (JSONException e) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
            }
        } else {
            Log.e(LOG_TAG, "The USGS string is either null or empty; nothing to parse!");
        }

        Log.v(LOG_TAG,"In extractEarthquakes method. At final return statement, and value of earthquake list is: " + earthquakes.toString());
        // Return the list of earthquakes
        return earthquakes;
    }

    /**
     * Helper method to convert URL in String format to URL object.
     *
     * @param urlString
     * @return
     */
    public static URL formatURL(String urlString) {
        Log.v(LOG_TAG,"Entering the formatURL method.");
        // Return early if we have a null or empty String.
        if (urlString == null || urlString == "") {
            Log.v(LOG_TAG,"In formatURL method & urlString is null.");
            return null;
        }

        // Convert URL String into URL object.
        URL url;
        try {
            url = new URL(urlString);
            Log.v(LOG_TAG,"In the formatURL method's try block...");
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem creating URL object.", e);
            return null;
        }

        Log.v(LOG_TAG, "In formatURL method. Made it to final return statement; returning: " + url.toString());
        return url;
    }

    /**
     * @param urlObject formatted URL instance for data source
     * @return String representing a response from the server, with data in JSON format
     * @throws IOException
     */
    public static String makeHttpRequest(URL urlObject) throws IOException {
        Log.v(LOG_TAG,"Entering the makeHttpRequest method. Passed URL argument: " + urlObject.toString());
        // Return early if URL object is null
        if (urlObject == null) {
            return null;
        }

        String jsonResponse = "";

        HttpURLConnection httpConnection = null;
        InputStream inputStream = null;
        try {
            // Obtain an HttpURLConnection object by casting the URL connection
            httpConnection = (HttpURLConnection) urlObject.openConnection();

            // Prepare the request / Set the parameters
            httpConnection.setConnectTimeout(10000 /* milliseconds */);
            httpConnection.setReadTimeout(15000 /* milliseconds */);
            httpConnection.setRequestMethod("GET");

            // Make the connection
            httpConnection.connect();

            // Check whether connection was successful
            if (httpConnection.getResponseCode() == 200) {
                // Get the InputStream
                inputStream = httpConnection.getInputStream();
                // Save full contents of InputStream to a String, via helper method
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "HTTP connection error code:" + httpConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving data from URL.", e);
            return null;
        } finally { /* Release any open, but no longer needed, resources */
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        Log.v(LOG_TAG, "In makeHttpRequest method. Made it to final return statement and value of jsonResponse is: " + jsonResponse.toString());
        return jsonResponse;
    }

    /**
     * @param inputStream with data stream
     * @return String with all read data
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            // Create new InputStreamReader and wrap it in a new BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            // Create variable to hold each line of read text
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }

}
