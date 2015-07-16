package com.dataspawn.twitterstreamer;

/**
 * Created by griffins on 6/25/15.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Using port: " + Settings.getServerPort());
        ServerRunner.executeInstance(new InterfaceServer(Settings.getServerPort()));
        System.exit(0);
    }
}
