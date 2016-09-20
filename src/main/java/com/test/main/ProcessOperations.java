package com.test.main;

import com.test.geocode.GeocodeFeatureResult;
import com.test.monitor.TaskMonitor;
import com.test.geocode.GoogleGeocoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ProcessOperations {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessOperations.class);

    public static void main(String[] args) {
        String file;
        int batchSize;
        if (args.length > 1) {
            batchSize = Integer.parseInt(args[1]);
            file = args[0];
            String fileName = "result_GEOCODE_" + System.currentTimeMillis() + ".txt";
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) {
                List<String> addresses = AddressesLoader.loadAddresses(file);
                List<List<String>> batches = AddressesLoader.splitIntoBatches(batchSize, addresses);
                GoogleContext context = new GoogleContext();
                TaskMonitor monitor = new TaskMonitor(TaskMonitor.GEOCODER_MONITOR);
                GoogleGeocoder geocoder = new GoogleGeocoder(context.getGeoApiContext(), monitor);
                LOG.debug("Starting Batches");
                for (List<String> batch : batches) {
                    LOG.debug("Starting new batches for {} elements", batch.size());
                    List<GeocodeFeatureResult> geocodeFeatureResults = geocoder.geocodeBatch(batch);
                    AddressesLoader.storeResult(writer, geocodeFeatureResults);
                }
                if (context.isFreeAccount()) {
                    writer.write("Using freeAccount with QPS = " + context.getQps());
                } else {
                    writer.write("Using paid account with QPS = " + context.getQps());
                }
                writer.newLine();
                writer.write(monitor.toString());
                System.out.println(monitor);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Usage:");
            System.out.println("app fileName batchSize");
        }

    }

}
