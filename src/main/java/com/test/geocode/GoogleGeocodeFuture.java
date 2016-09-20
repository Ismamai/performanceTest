package com.test.geocode;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.maps.GeocodingApiRequest;
import com.jamonapi.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Future to hold a google geocoding result...
 *
 */
class GoogleGeocodeFuture implements Future<GeocodeResult> {
    private /* final */ GeocodeResult result;
    private final GeocodingApiRequest googleApiRequest;
    private Monitor monitor;
    private static final Logger LOG = LoggerFactory.getLogger(GoogleGeocodeFuture.class);

    private boolean isResultSet;
    private boolean isCancelled;
    private final Object lock;

    GoogleGeocodeFuture(GeocodingApiRequest googleApiRequest, Monitor monitor) {
        this.googleApiRequest = googleApiRequest;
        this.monitor = monitor;
        this.isResultSet = false;
        this.isCancelled = false;
        this.lock = new Object();
    }

    void setResult(GeocodeResult result) {
        LOG.debug("Setting result = {} for request {}", result, googleApiRequest);
        synchronized (lock) {
            if (monitor != null) {
                monitor.stop();
            }
            if (isDone()) {
                LOG.error("Result was already set !!!");
                return;
            }
            LOG.debug("storing value and setting isResultSet to true");
            this.isResultSet = true;
            this.result = result;
            LOG.debug("NotifyAll to all waiting threads");
            lock.notifyAll();
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (lock) {
            if (isDone()) {
                return false;
            }
            isCancelled = true;
            googleApiRequest.cancel();

            lock.notifyAll();
        }
        return true;
    }

    @Override
    public GeocodeResult get() throws InterruptedException, ExecutionException {
        synchronized (lock) {
            if (result != null) {
                return result;
            }

            if (!isDone()) {
                lock.wait();
            }
        }
        return result;
    }
    @Override
    public GeocodeResult get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        synchronized (lock) {
            if (isCancelled) {
                throw new CancellationException();
            }

            if (!isDone()) {
                lock.wait(unit.toMillis(timeout));
            }

            if (isCancelled) {
                throw new CancellationException();
            }

            if (!isDone()) {
                throw new TimeoutException("Waiting for " + timeout + " " + unit);
            }
        }

        return result;
    }

    @Override
    public boolean isCancelled() {
        synchronized (lock) {
            return isCancelled;
        }
    }

    @Override
    public boolean isDone() {
        synchronized (lock) {
            return isResultSet || isCancelled;
        }
    }
}