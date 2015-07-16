package com.dataspawn.twitterstreamer;

import twitter4j.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by griffins on 6/19/15.
 */

public class InterfaceServer extends NanoHTTPD {

    public InterfaceServer(int port) {
        super(port);
    }

    public InterfaceServer(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public Response serve(IHTTPSession session) {

        Map<String, List<String>> decodedQueryParameters = decodeParameters(session.getQueryParameterString());
        Map<String, String> params = session.getParms();

        String command_type = params.get("command_type");
        
        StringBuilder sb = new StringBuilder();
        JSONObject response = new JSONObject();
        try {
            if (command_type == null) {
                //fail fast
                JSONObject error = new JSONObject();
                error.put("type", "APIException");
                error.put("message", "Invalid command type type");
                error.put("code", 300);
                response.put("error", error);
                sb.append(response.toString(4));

            } else {
                RequestProcessor processor = new RequestProcessor(session);
                Map<String, String> map = processor.process();
                if (map != null) {
                    JSONObject data = new JSONObject();
                    Iterator i = map.keySet().iterator();
                    while (i.hasNext()) {
                        String key = i.next().toString();
                        data.put(key, map.get(key));
                    }

                    data.put("code", 200);
                    response.put("data", data);
                    sb.append(response.toString(4));
                } else {
                    JSONObject error = new JSONObject();
                    error.put("type", "APIException");
                    error.put("message", "Unknown error occured. Try again later");
                    error.put("code", 300);
                    response.put("error", error);
                    sb.append(response.toString(4));
                }

            }

            Response mResponse = this.newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, sb.toString());

            return mResponse;
        } catch (Exception e) {
            e.printStackTrace();
            Response mResponse = this.newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, sb.toString());

            return mResponse;
        }

    }

}
