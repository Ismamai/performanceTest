package com.test.geocode;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.PendingResult;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.test.monitor.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.maps.model.AddressType.UNKNOWN;

public class GoogleGeocoder {
    private static final Logger LOG = LoggerFactory.getLogger(GoogleGeocoder.class);
    public static final int TIMEOUT = 30;

    private GeoApiContext geoApiContext;
    private TaskMonitor monitor;

    public GoogleGeocoder(GeoApiContext geoApiContext, TaskMonitor monitor) {
        this.geoApiContext = geoApiContext;
        this.monitor = monitor;
    }

    public List<GeocodeFeatureResult> geocodeBatch(List<String> batch) {

        Map<String, GoogleGeocodeFuture> cache = new HashMap<>();
        List<GoogleGeocodeRequest> googleGeocodeRequests = new ArrayList<>();

        for (String address : batch) {

            GoogleGeocodeFuture googleGeocodeFuture;
            googleGeocodeFuture = cache.get(address);
            if (googleGeocodeFuture == null) {
                googleGeocodeFuture = geocode(address);
                cache.put(address, googleGeocodeFuture);
            }
            googleGeocodeRequests.add(new GoogleGeocodeRequest(googleGeocodeFuture, address));
        }

        // We use streams for asking the futures in parallel and speedup things.
        // Using a loop was causing the timeout to grow:
        // 60 seconds for 1st, 120 for second and so on
        List<GeocodeFeatureResult> updateFeatures = googleGeocodeRequests.parallelStream()
                .map(processGeocodeResult(monitor))
                .collect(Collectors.toList());

        return updateFeatures;
    }

    private Function<GoogleGeocodeRequest, GeocodeFeatureResult> processGeocodeResult(TaskMonitor monitor) {
        return googleGeocodeRequest -> {
            GeocodeResult geocodeResult;
            GoogleGeocodeFuture geocodeFuture = googleGeocodeRequest.getGeocodeFuture();
            try {
                LOG.debug("Asking for the GeocodeFuture. Request " + googleGeocodeRequest.getGeocodeRequest());
                geocodeResult = geocodeFuture.get(TIMEOUT, TimeUnit.SECONDS);
                LOG.debug("GoogleGeocode httpResponseBody retrieved ok. Request " + googleGeocodeRequest.getGeocodeRequest());
            } catch (Exception e) {
                geocodeResult = new GeocodeResult("Google Geocoder request timed out");
                boolean cancelled = geocodeFuture.cancel(false);
                LOG.error("Google geocoder request timed out. Request cancelled " + cancelled + " Request was " + googleGeocodeRequest.getGeocodeRequest(), e);
            }
            if (geocodeResult.isGeocoded()) {
                monitor.incrementOk();
            } else {
                monitor.incrementError();
            }

            return new GeocodeFeatureResult(googleGeocodeRequest, geocodeResult);
        };
    }

    private GoogleGeocodeFuture geocode(String geocodeRequest) {


        GeocodingApiRequest request;
        GoogleGeocodeFuture geocodeFuture;
        if (geocodeRequest.length() == 0) {
            request = null;
            geocodeFuture = new GoogleGeocodeFuture(request, null);
            geocodeFuture.setResult(new GeocodeResult("Address was empty"));
        } else {
            request = GeocodingApi.newRequest(geoApiContext)
                    .address(geocodeRequest).region("USA");
        }
        Monitor mon = MonitorFactory.start(monitor.getMonitor().getLabel());

        geocodeFuture = new GoogleGeocodeFuture(request, mon);
        GoogleGeocodeFuture finalGeocodeFuture = geocodeFuture;
        request.setCallback(new PendingResult.Callback<GeocodingResult[]>() {
            @Override
            public void onResult(GeocodingResult[] result) {
                LOG.debug("GoogleLibrary returned result, setting it on the GeocodeFuture.");
                finalGeocodeFuture.setResult(getResultFromResponse(result));
            }

            @Override
            public void onFailure(Throwable e) {
                if (!finalGeocodeFuture.isCancelled()) {
                    LOG.error("Exception geocoding address: " + geocodeRequest, e);
                    finalGeocodeFuture.setResult(new GeocodeResult(e.getMessage()));
                }
            }
        });


        return geocodeFuture;
    }

    private GeocodeResult getResultFromResponse(GeocodingResult[] response) {
        if (response.length == 0) {
            return new GeocodeResult("Zero results returned by Google");
        }
        GeocodingResult result = response[0];

        double latitude = result.geometry.location.lat;
        double longitude = result.geometry.location.lng;

        AddressType type = UNKNOWN;
        if (result.types.length > 0) {
            type = result.types[0];
        } else if (result.addressComponents.length > 0) {
            LOG.error("result.types is empty. Trying to recover");
            if (result.addressComponents[0].types.length > 0) {
                LOG.error("Found data on result.addressComponents[0].types[0] " + result.addressComponents[0].types[0]);
                type = AddressType.valueOf(result.addressComponents[0].types[0].name());
            } else {
                LOG.error("Unable to guess type based on result.addressComponents[0].types[0]. It is empty");
            }
        }
        return new GeocodeResult(latitude, longitude, type.toString());
    }
}
