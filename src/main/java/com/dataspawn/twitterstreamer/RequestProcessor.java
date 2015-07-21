package com.dataspawn.twitterstreamer;

/**
 * Created by griffins on 6/20/15.
 */

import com.mongodb.ServerAddress;

import java.util.*;

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
        if (Settings.getStreamer() == null) {


            PausableTask task = new PausableTask() {
                @Override
                public boolean task() {
                    return Settings.getStreamer().run();
                }
            };

            try {
                //Store store = new FileStore("/home/griffins/apps/spark/tweets2.txt");
                ArrayList<ServerAddress> servers = new ArrayList<ServerAddress>();
                servers.add(new ServerAddress("localhost", 27017));

                Store store = new MongoStore(servers);
                //Store store = new HBaseStore();
                Streamer s = new Streamer();
                s.init(Settings.getConsumeKey(), Settings.getConsumerSecret(), Settings.getToken(), Settings.getSecret(), store);
                Settings.setStreamer(s);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String tag = ServiceManager.setTask(task);
            Map<String, String> result = new HashMap<String, String>();
            result.put("tag", tag);
            result.put("status", "true");
            return result;
        }else{
            Map<String, String> result = new HashMap<String, String>();
            result.put("tag", "");
            result.put("status", "true");
            return result;
        }
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

        List aTerms = new ArrayList();
        List aLocs = new ArrayList();


        for (String term : Arrays.asList(params.get("terms").split(" "))) {
            if (term != null) {
                if (!term.trim().contentEquals("")) {
                    aTerms.add(term);
                }
            }
        }
           /*for (int x = 0; x < jsLocs.length(); x++) {
                aLocs.add(jsLocs.get(x));
            }
            */
        final List<String> terms = aTerms, locations = aLocs;
        Settings.getStreamer().addTerms(terms, locations);
        boolean status = ServiceManager.start();
        result.put("status", String.valueOf(status));
        return result;
    }

    private Map stopStreamingService(Map<String, String> params) {
        Map<String, String> result = new HashMap<String, String>();
        boolean status = ServiceManager.stop();
        result.put("status", String.valueOf(status));
        Settings.setStreamer(null);
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