package com.test.geocode;

public class GoogleGeocodeRequest {
    private GoogleGeocodeFuture geocodeFuture;
    private String geocodeRequest;


    public GoogleGeocodeRequest(GoogleGeocodeFuture geocodeFuture, String geocodeRequest) {
        this.geocodeFuture = geocodeFuture;
        this.geocodeRequest = geocodeRequest;
    }

    public GoogleGeocodeFuture getGeocodeFuture() {
        return geocodeFuture;
    }
    public String getGeocodeRequest() {
        return geocodeRequest;
    }

}