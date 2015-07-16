package com.dataspawn.twitterstreamer;


public class ServiceManager {
    static PausableTask task = null;

    static boolean stop() {
        if (task == null) {
            return false;
        }
        task.stop();
        return true;
    }

    static boolean start() {
        if (task == null) {
            return false;
        }

        task.start();
        return true;
    }

    static boolean pause() {
        if (task == null) {
            return false;
        }

        task.pause();
        return true;
    }

    static String setTask(PausableTask t) {
        if (t != null) {
            task = t;
        }
        return task.toString();
    }

    static boolean shutdown() {
        if (task == null) {
            return false;
        }
        task.stop();
        return true;
    }

    public static boolean resume() {
        if (task == null) {
            return false;
        }

        task.resume();
        return true;
    }
}
