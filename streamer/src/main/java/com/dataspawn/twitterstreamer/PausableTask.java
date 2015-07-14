package com.dataspawn.twitterstreamer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by griffins on 7/14/15.
 */

public abstract class PausableTask implements Runnable {

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> publisher;

    abstract boolean task();

    @Override
    public void run() {
        while (!Thread.currentThread().interrupted()) {
            if (!task()) {
               // Thread.currentThread().interrupt();
            }
        }
    }

    public void start() {

        publisher = executor.submit(this);
    }

    public void pause() {
        publisher.cancel(true);
    }

    public void resume() {
        start();
    }

    public void stop() {
        Thread.currentThread().interrupt();
        executor.shutdownNow();
    }
}