package com.imagine.twitterstreamer;

/**
 * Created by griffins on 6/25/15.
 */
public class Main {
    public static void main(String[] args) {
        String consumer_key = "AKf7A2wZ8fjLPJO2TepsKDQ7m";
        String consumer_secret = "Ur9AGektIEOrKWQHxoEJA8VPD9r5oae4GK1JQi2pl50kB53CqN";
        String token = "1130690946-WUpXHcCXsftRDEpXEs3Wd3dUi8lNtv4IgN9kK4L";
        String secret = "LALBngDK5HyIN45B5Ao2DzPk8ovLGZrUVjhZmi4OV2Qzh";
        try {

            if (args.length > 3) {
                Streamer.run(args[0], args[1], args[2], args[3]);
            } else {
                Streamer.run(consumer_key, consumer_secret, token, secret);

            }
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}
