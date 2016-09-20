package com.test.main;


import com.test.geocode.GeocodeFeatureResult;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iblesa on 05/09/2016.
 */
public class AddressesLoader {
    public static List<String> loadAddresses(String file) throws IOException {
        Path path = Paths.get(file);
        Charset charset = Charset.forName("ISO-8859-1");
        return Files.readAllLines(path, charset);
    }

    public static void storeResult(Writer writer, List<GeocodeFeatureResult> geocodeFeatureResults) throws IOException {
        for (GeocodeFeatureResult feature: geocodeFeatureResults) {
            writer.write("\""+feature.getGoogleGeocodeRequest().getGeocodeRequest()+"\",");
            writer.write(feature.getGeocodeResult().getLatitude()+","+feature.getGeocodeResult().getLongitude()+" \n");
        }

    }
    public static List<List<String>> splitIntoBatches(int batchSize, List<String> addresses) {
        List<List<String>> batches = new ArrayList<>();
        for (int batch = 0; batch < addresses.size(); batch += batchSize) {
            int currentBatch = Math.min(batchSize, addresses.size() - batch);
            int endOfCurrentBatch = batch + currentBatch;
            batches.add(addresses.subList(batch, endOfCurrentBatch));
        }
        return batches;
    }
}
