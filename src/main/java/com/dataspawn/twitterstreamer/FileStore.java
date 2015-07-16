package com.dataspawn.twitterstreamer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by griffins on 7/14/15.
 */
public class FileStore implements Store {
    File mFile = null;
    FileOutputStream outputStream = null;

    public FileStore(String filename) throws FileNotFoundException {
        mFile = new File(filename);
        outputStream = new FileOutputStream(mFile, true);
    }

    @Override
    public void add(String tweet) throws IOException {
        outputStream.write(tweet.concat("\n").getBytes());
    }
}
