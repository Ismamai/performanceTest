package com.test.main;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.test.monitor.TaskMonitor;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class URLLoader {

    public static void main(String[] args) {
        String file;
        if (args.length == 1) {
            file = args[0];
            String fileName = "result_URL_" + System.currentTimeMillis() + ".txt";
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) {
                List<String> addresses = AddressesLoader.loadAddresses(file);
                if (addresses.size() == 0 || !addresses.get(0).startsWith("https://maps.googleapis.com/maps/api/geocode/json")) {
                    System.out.println("Input file does not contain calls to google api. Check your configuration.");
                    return;
                }
                TaskMonitor monitor = new TaskMonitor(TaskMonitor.URL_MONITOR);
                for (String request : addresses) {
                    System.out.print(".");
                    Monitor mon = MonitorFactory.start(monitor.getMonitor().getLabel());
                    writer.write("\"" + request + "\",");
                    String result = readURL(request);
                    writer.write("\"" + result + "");
                    writer.newLine();
                    mon.stop();

                }
                writer.newLine();
                writer.write("Using filename " + file);
                writer.newLine();
                writer.write(monitor.toString());
                System.out.println(monitor);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Usage:");
            System.out.println("app fileName");
        }

    }

    private static String readURL(String urlString) throws IOException {
        // create the url
        URL url = new URL(urlString);

        // open the url stream, wrap it an a few "readers"
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        // write the output to stdout
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        // close our reader
        reader.close();
        return result.toString();
    }
}
