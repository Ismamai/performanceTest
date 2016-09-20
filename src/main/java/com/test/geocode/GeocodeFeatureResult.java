package com.test.geocode;

public class GeocodeFeatureResult {
    private GoogleGeocodeRequest googleGeocodeRequest;
    private GeocodeResult geocodeResult;

    public GeocodeFeatureResult(GoogleGeocodeRequest googleGeocodeRequest, GeocodeResult geocodeResult) {

        this.googleGeocodeRequest = googleGeocodeRequest;
        this.geocodeResult = geocodeResult;
    }

    public GoogleGeocodeRequest getGoogleGeocodeRequest() {
        return googleGeocodeRequest;
    }

    public GeocodeResult getGeocodeResult() {
        return geocodeResult;
    }
}
