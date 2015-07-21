package com.dataspawn.twitterstreamer;

/**
 * Created by griffins on 6/20/15.
 */
public class Settings {


    private static int SERVERPort = 8082;
    static String consumer_key = "AKf7A2wZ8fjLPJO2TepsKDQ7m";
    static String consumer_secret = "Ur9AGektIEOrKWQHxoEJA8VPD9r5oae4GK1JQi2pl50kB53CqN";
    static String token = "1130690946-WUpXHcCXsftRDEpXEs3Wd3dUi8lNtv4IgN9kK4L";
    static String secret = "LALBngDK5HyIN45B5Ao2DzPk8ovLGZrUVjhZmi4OV2Qzh";
    private static Streamer streamer;

    static String hbaseZookeeperQuorum = "192.168.1.11";

    public static String getHbaseMaster() {
        return hbasemaster;
    }

    public static void setHbaseMaster(String hbasemaster) {
        Settings.hbasemaster = hbasemaster;
    }

    static String hbasemaster = "*192.168.1.11:2181*";
    static int hbaseZookeeperClientPort = 2181;
    static String hBaseTableName = "tweets_1";

    public static void setConsumerSecret(String consumer_secret) {
        Settings.consumer_secret = consumer_secret;
    }

    public static void setServerPort(int SERVERPort) {
        Settings.SERVERPort = SERVERPort;
    }

    public static String getHbaseZookeeperQuorum() {
        return hbaseZookeeperQuorum;
    }

    public static void setHbaseZookeeperQuorum(String hbaseZookeeperQuorum) {
        Settings.hbaseZookeeperQuorum = hbaseZookeeperQuorum;
    }

    public static int getHbaseZookeeperClientPort() {
        return hbaseZookeeperClientPort;
    }

    public static void setHbaseZookeeperClientPort(int hbaseZookeeperClientPort) {
        Settings.hbaseZookeeperClientPort = hbaseZookeeperClientPort;
    }

    public static String getHBaseTableName() {
        return hBaseTableName;
    }

    public static void setHBaseTableName(String tableName) {
        Settings.hBaseTableName = tableName;
    }

    public static void setConsumerKey(String consumer_key) {
        Settings.consumer_key = consumer_key;
    }

    public static void setToken(String token) {
        Settings.token = token;
    }

    public static void setSecret(String secret) {
        Settings.secret = secret;
    }

    public static String getSecret() {
        return secret;
    }

    public static String getConsumeKey() {
        return consumer_key;
    }

    public static String getConsumerSecret() {
        return consumer_secret;
    }

    public static String getToken() {
        return token;
    }


    public static int getServerPort() {

        return SERVERPort;
    }

    public static void setStreamer(Streamer streamer) {
        Settings.streamer = streamer;
    }

    public static Streamer getStreamer() {
        return streamer;
    }
}