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

package com.imagine.twitterstreamer;

import com.google.common.collect.Lists;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.*;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import twitter4j.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Streamer {

    public static void run(String consumerKey, String consumerSecret, String token, String secret) throws InterruptedException {
        // Create an appropriately sized blocking queue
        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);

        // Define our endpoint: By default, delimited=length is set (we need this for our processor)
        // and stall warnings are on.
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        endpoint.trackTerms(Lists.newArrayList("nairobi", "nairobitraffic", "mombasa", "ma3route", "citizentvkenya", "ntvkenya", "dailynation"));

        endpoint.locations(Lists.newArrayList(new Location(new Location.Coordinate(34, -3), new Location.Coordinate(45, 3))));
        endpoint.stallWarnings(false);


        Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secret);
        //Authentication auth = new com.twitter.hbc.httpclient.auth.BasicAuth(username, password);

        // Create a new BasicClient. By default gzip is enabled.
        BasicClient client = new ClientBuilder()
                .name("Streamer Client")
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        // Establish a connection
        client.connect();
        try {
            File tmp = new File("/home/griffins/apps/spark/tweets.txt");
            FileOutputStream out = new FileOutputStream(tmp, true);
            ArrayList<String> tweets = new ArrayList<String>();
            for (int msgRead = 0; msgRead < 100000; msgRead++) {
                if (client.isDone()) {
                    System.out.println("Client connection closed unexpectedly: " + client.getExitEvent().getMessage());
                    break;
                }

                String msg = queue.poll(5, TimeUnit.SECONDS);
                if (msg == null) {
                    System.out.println("Did not receive a message in 5 seconds");
                } else {
                    //System.out.print(msg);
                    try {
                        JSONObject tweet = new JSONObject(msg);
                        String text = tweet.getString("text");
                        // tweets.add(text);
                        out.write(msg.concat("\n").getBytes());
                        System.out.print(text);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }


            client.stop();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.print("Caught exception when processing tweets");
        }
        // Print some stats
        System.out.printf("The client read %d messages!\n", client.getStatsTracker().

                        getNumMessages()

        );
    }


}
