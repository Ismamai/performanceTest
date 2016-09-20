package com.test.main;

import com.google.maps.GeoApiContext;


/**
 * Created by iblesa on 08/09/16.
 */
public class GoogleContext {
    GeoApiContext geoApiContext;
    private int qps;
    private boolean freeAccount = true;


    public GoogleContext() {

    }

    public GeoApiContext getGeoApiContext() {
        if (geoApiContext == null) {
            geoApiContext = contexts();

        }
        return geoApiContext;
    }

    public GeoApiContext contexts() {
        GeoApiContext context = null;
        qps = Integer.parseInt(System.getenv("QPS"));
        String apiKey = System.getenv("API_KEY");
        if (apiKey == null) {
            apiKey = System.getProperty("api.key");
        }

        if (apiKey != null && !apiKey.equalsIgnoreCase("")) {
            context = new GeoApiContext()
                    .setQueryRateLimit(qps)
                    .setApiKey(apiKey);
        } else {

            String clientId = System.getenv("CLIENT_ID");
            String clientSecret = System.getenv("CLIENT_SECRET");
            if (clientId == null && clientSecret == null) {
                clientId = System.getProperty("client.id");
                clientSecret = System.getProperty("client.secret");
            }

            if (!(clientId == null || clientId.equals("") || clientSecret == null || clientSecret.equals(""))) {
                context = new GeoApiContext()
                        .setQueryRateLimit(qps)
                        .setEnterpriseCredentials(clientId, clientSecret);
                freeAccount = false;
            }
        }
        if (context == null) {
            throw new IllegalArgumentException("No credentials found! Set the API_KEY or CLIENT_ID and "
                    + "CLIENT_SECRET environment variables to run tests requiring authentication.");
        }
        return context;

    }

    public int getQps() {
        return qps;
    }

    public boolean isFreeAccount() {
        return freeAccount;
    }
}
