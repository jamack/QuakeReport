package com.example.android.quakereport;

/**
 * Contains data for an earthquake event
 */

public class Earthquake {

    /** Magnitude of the quake. Default value of 0.0 until initialized */
    private double mMagnitude = 0.0;

    /** Nearest city (or region, if no nearby city) to the earthquake */
    private String mLocation = null;

    /** Time of the earthquake */
    private long mTimeInMilliseconds;

    /** URL for USGS webpage with details of the earthquake */
    private String mWebpageUrl;

    /**
     * Constructor for the {@link com.example.android.quakereport.Earthquake} class.
     *
     * @param Magnitude is the strength/size of the quake.
     * @param Location is the nearest city to quake's epicenter, or region quake occurred in.
     * @param timeInMilliseconds is the time in milliseconds (from the Epoch) when the
     *  earthquake happened
     */
    public Earthquake(double Magnitude, String Location, Long timeInMilliseconds, String webpageUrl) {
        this.mMagnitude = Magnitude;
        this.mLocation = Location;
        this.mTimeInMilliseconds = timeInMilliseconds;
        this.mWebpageUrl = webpageUrl;
    }

    /**
     * Returns the magnitude/size of the earthquake.
     */
    public double getMagnitude() {
        return mMagnitude;
    }

    /**
     * Returns the location of the earthquake.
     */
    public String getLocation() {
        return mLocation;
    }

    /**
     * Returns the time of the earthquake.
     */
    public Long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    /**
     * Returns a URL for the USGS webpage with details for this earthquake.
     */
    public String getUrl() {
        return mWebpageUrl;
    }

}
