/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class EarthquakeActivity extends AppCompatActivity {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    // Constant for USGS endpoint with desired earthquake data
    private static final String USGS_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=6&limit=10";

    // Create a global variable to hold list of earthquakes.
    List<Earthquake> earthquakesList = null;

    // Create a global variable to hold ListView
    ListView earthquakeListView = null;

    // Create a global variable to hold the EarthquakeAdapter
    EarthquakeAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        FetchEarthquakesTask fetchEarthquakesTask = new FetchEarthquakesTask();
        fetchEarthquakesTask.execute(USGS_URL);

    }

    private class FetchEarthquakesTask extends AsyncTask<String, Void, List<Earthquake>> {

        @Override
        protected List<Earthquake> doInBackground(String... strings) {
            Log.v(LOG_TAG, "Entering the doInBackground method.");
            // Fetch List of earthquake events from the USGS server
            return QueryUtils.extractEarthquakes(USGS_URL);
        }

        @Override
        protected void onPostExecute(List<Earthquake> earthquakes) {
            Log.v(LOG_TAG, "Entering the onPostExecute method.");
            if (earthquakes != null) {
                if (earthquakes.size() > 0) {
                    earthquakesList = earthquakes;
                } else {
                    Log.e(LOG_TAG, "AsyncTask is returning a list with no Earthquakes.");
                }
            } else {
                Log.e(LOG_TAG, "AsyncTask is returning a null object instead of a valid List of Earthquake events.");
            }

            Log.v(LOG_TAG, "Executing onPostExecute method after commenting out the if conditions...");

            // Find a reference to the {@link ListView} in the layout
            earthquakeListView = (ListView) findViewById(R.id.list);

            Log.v(LOG_TAG, "In onPostExecute method; creating new EarthquakeAdapter object.");
            // Initialize a {@link EarthquakeAdapter} of earthquakes and assign it to our global variable
            adapter = new EarthquakeAdapter(getApplicationContext(), earthquakesList);


            Log.v(LOG_TAG, "In onPostExecute method; setting new adapter on the ListView.");
            // Set the adapter on the {@link ListView}
            // so the list can be populated in the user interface
            earthquakeListView.setAdapter(adapter);

            Log.v(LOG_TAG, "In onPostExecute method; creating & setting new OnItemClickListener.");
            earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    // Get the URL from the selected item and store in a variable
                    String webpageUrl = earthquakesList.get(i).getUrl();

                    // Create Intent
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webpageUrl));

                    // Check whether intent can be resolved. If so, start activity to view URL
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });
        }
    }
}
