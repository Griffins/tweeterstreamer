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

    boolean runnable = true;

    public void run() {
        while (!Thread.currentThread().interrupted() && runnable) {
            if (task()) {
                System.out.println("Restarting streaming job");
            }

        }
    }

    public void start() {
        runnable = true;
        publisher = executor.submit(this);
    }

    public void pause() {
        runnable = false;
        Thread.currentThread().interrupt();
        publisher.cancel(true);
    }

    public void resume() {
        runnable = true;
        start();
    }

    public void stop() {
        runnable = false;
        Thread.currentThread().interrupt();
        executor.shutdownNow();
    }
}