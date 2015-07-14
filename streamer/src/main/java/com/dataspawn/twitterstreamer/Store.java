package com.dataspawn.twitterstreamer;

import java.io.IOException;

/**
 * Created by griffins on 7/14/15.
 */
public interface Store {
    public void add(String tweet) throws IOException;
}
