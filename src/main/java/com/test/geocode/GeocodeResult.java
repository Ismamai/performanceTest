package com.test.geocode;


/**
 * Object to store the geocoding process result
 *
 */
public class GeocodeResult {
    private double latitude;
    private double longitude;
    private String precision;
    private String errorMessage;

    public GeocodeResult(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public GeocodeResult(double latitude, double longitude, String precision) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.precision = precision;
    }


    /**
     * get status
     *
     * @return
     */
    public boolean isGeocoded() {
        return errorMessage == null;
    }

    @Override
    public String toString() {
        return "GeocodeResult{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", precision='" + precision + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPrecision() {
        return precision;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
