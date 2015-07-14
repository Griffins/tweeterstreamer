package com.dataspawn.twitterstreamer;

/**
 * Created by griffins on 6/20/15.
 */

import twitter4j.JSONArray;
import twitter4j.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestProcessor {

    NanoHTTPD.IHTTPSession mSession;

    public RequestProcessor(NanoHTTPD.IHTTPSession session) {
        mSession = session;
    }

    public Map process() {
        int type = 0;

        Map<String, String> params = mSession.getParms();
        String command_type = params.get("command_type");
        type = Integer.parseInt(command_type);

        /* Type 1. restart job
         * Type 2. stop job
         * Type 3. start job
         * Type 4. pause job
         * Type 5. resume job
         * Type 6. setTask track terms
         */

        switch (type) {
            case 1:
                return restartStreamingService(params);
            case 2:
                return stopStreamingService(params);
            case 3:
                return startStreamingService(params);
            case 4:
                return pauseStreamingService(params);
            case 5:
                return resumeStreamingService(params);
            case 6:
                return addTrackTerms(params);
            default:
                Map<String, String> result = new HashMap<String, String>();
                result.put("error", "Unknown Api command");
                return result;

        }
    }

    private Map startStreamingService(Map<String, String> params) {

        PausableTask task = new PausableTask() {
            @Override
            public boolean task() {
                return Streamer.run();
            }
        };

        try {
            Store store = new FileStore("/home/griffins/apps/spark/tweets2.txt");
            Streamer.init(Settings.getConsumeKey(), Settings.getConsumerSecret(), Settings.getToken(), Settings.getSecret(), store);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String tag = ServiceManager.setTask(task);
        boolean status = ServiceManager.start();
        Map<String, String> result = new HashMap<String, String>();
        result.put("tag", tag);
        result.put("status", String.valueOf(status));
        return result;
    }

    private Map resumeStreamingService(Map<String, String> params) {
        Map<String, String> result = new HashMap<String, String>();
        boolean status = ServiceManager.resume();
        result.put("status", String.valueOf(status));
        return result;

    }

    private Map pauseStreamingService(Map<String, String> params) {
        Map<String, String> result = new HashMap<String, String>();
        String tag = params.get("tag");
        boolean status = ServiceManager.pause();
        result.put("status", String.valueOf(status));
        return result;
    }

    private Map addTrackTerms(Map<String, String> params) {

        //Lists.newArrayList("nairobi", "nairobitraffic", "mombasa", "ma3route", "citizentvkenya", "ntvkenya", "dailynation");

        Map<String, String> result = new HashMap<String, String>();
        JSONArray jsTerms;
        JSONArray jsLocs;
        List aTerms = new ArrayList();
        List aLocs = new ArrayList();

        try {
            jsTerms = new JSONArray(params.get("terms"));
            jsLocs = new JSONArray(params.get("location"));
            for (int x = 0; x < jsTerms.length(); x++) {
                aTerms.add(jsTerms.getJSONObject(x).getString("term"));
            }

            for (int x = 0; x < jsLocs.length(); x++) {
                aLocs.add(jsLocs.get(x));
            }
        } catch (JSONException e) {
            result.put("errror", "Invalid terms or locations");
            return result;
        }
        final List<String> terms = aTerms, locations = aLocs;
        Streamer.addTerms(terms, locations);
        return result;
    }

    private Map stopStreamingService(Map<String, String> params) {
        Map<String, String> result = new HashMap<String, String>();
        String tag = params.get("tag");
        boolean status = ServiceManager.stop();
        result.put("status", String.valueOf(status));
        return result;
    }

    private Map restartStreamingService(Map<String, String> params) {
        Map<String, String> result = new HashMap<String, String>();
        boolean status = ServiceManager.stop();

        if (!status) {
            result.put("status", String.valueOf(status));
            result.put("message", "Service failed to stop");
            return result;
        }

        return addTrackTerms(params);
    }
}