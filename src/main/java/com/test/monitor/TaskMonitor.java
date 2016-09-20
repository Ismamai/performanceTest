package com.test.monitor;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class TaskMonitor {
    public static final String GEOCODER_MONITOR = "GeocoderMonitor";
    public static final String URL_MONITOR = "UrlMonitor";
    private AtomicInteger ok = new AtomicInteger(0);
    private AtomicInteger error = new AtomicInteger(0);
    private final Monitor monitor;

    public TaskMonitor(String label) {
        monitor = MonitorFactory.getTimeMonitor(label);
    }

    public int incrementOk() {
        return ok.incrementAndGet();
    }
    public int incrementError() {
        return error.incrementAndGet();
    }

    public Monitor getMonitor() {
        return monitor;
    }

    @Override
    public String toString() {
        return "TaskMonitor{" +
                "ok=" + ok +
                ", error=" + error +
                ", monitor=" + monitor +
                '}';
    }
}
