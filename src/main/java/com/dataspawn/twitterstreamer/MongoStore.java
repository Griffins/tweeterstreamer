package com.dataspawn.twitterstreamer;

import com.mongodb.*;
import com.mongodb.util.JSON;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by griffins on 7/14/15.
 */
public class MongoStore implements Store {


    private MongoClient mongoClient;
    DB db;
    DBCollection tweets;

    public MongoStore(List<ServerAddress> servers) throws UnknownHostException {
        mongoClient = new MongoClient(servers);
        db = mongoClient.getDB("tweets_store");
        tweets = db.getCollection("tweets");
    }

    public void add(String tweet) throws IOException {
        DBObject dbtweet = (DBObject) JSON.parse(tweet);
        tweets.insert(dbtweet);
    }
}
