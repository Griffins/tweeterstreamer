/**
 * Copyright 2013 Twitter, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package com.dataspawn.twitterstreamer;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.*;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;

import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import twitter4j.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Streamer {

    BasicClient client;
    private Store mStore;
    String consumerKey, consumerSecret, token, secret;
    private List<String> terms = new ArrayList<String>();
    private StatusesFilterEndpoint endpoint;

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public void setStore(Store mStore) {
        this.mStore = mStore;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setTerms(List<String> terms) {
        this.terms = terms;
    }


    public void init(String cKey, String cSecret, String mtoken, String msecret, Store store) throws InterruptedException {

        //init system
        consumerKey = cKey;
        consumerSecret = cSecret;
        token = mtoken;
        secret = msecret;
        mStore = store;

    }

    public boolean run() {
        System.out.println("Thread is interrupted:" + Thread.currentThread().isInterrupted());
        Authentication auth;

        BlockingQueue<String> queue;

        queue = new LinkedBlockingQueue<String>(10000);
        endpoint = new StatusesFilterEndpoint();

        endpoint.trackTerms(terms);

        //endpoint.locations(locations);


        //Authentication auth = new com.twitter.hbc.httpclient.auth.BasicAuth(username, password);
        auth = new OAuth1(consumerKey, consumerSecret, token, secret);

        client = new ClientBuilder()
                .name("Streamer Client")
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        client.connect();
        try {
            ArrayList<String> tweets = new ArrayList<String>();
            Long msgRead = 0L;
            for (msgRead = 0L; msgRead < 1000L; msgRead++) {
                if (client.isDone()) {
                    System.out.println("Client connection closed unexpectedly: " + client.getExitEvent().getMessage());
                    break;
                }

                String msg = queue.poll(5, TimeUnit.SECONDS);
                if (msg == null) {
                    System.out.println("Did not receive a message in 5 seconds");
                } else {
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                    try {
                        JSONObject tweet = new JSONObject(msg);
                        mStore.add(tweet.toString(4));
                        System.out.println(msg);
                    } catch (IOException e) {
                        System.out.println("Tweet Store failed: " + e.getMessage());
                        return false;
                    }

                }
            }

            System.out.println("Streamer read: " + msgRead);
            client.stop();

        } catch (Exception e) {
            System.out.print("Caught exception when reading tweets");
            // Print some stats
            System.out.printf("The client read %d messages!\n", client.getStatsTracker().getNumMessages());
            return false;
        }
        // Print some stats
        System.out.printf("The client read %d messages!\n", client.getStatsTracker().getNumMessages());
        return true;
    }

    private boolean hasTerm(String term) {

        for (String mTerm : terms) {

            if (term.equalsIgnoreCase(mTerm)) {
                return true;
            }

        }
        return false;
    }

    public void addTerms(List<String> new_terms, List new_locations) {

        for (String term : new_terms) {

            if (!hasTerm(term)) {
                terms.add(term);
            }
        }

        // Create a new BasicClient. By default gzip is enabled.
        if (client != null) {
            client.stop();
        }
    }

    public void stop() {
        client.stop();
    }
}
