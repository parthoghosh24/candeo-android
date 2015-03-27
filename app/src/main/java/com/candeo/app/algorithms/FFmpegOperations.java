package com.candeo.app.algorithms;

/**
 * Created by partho on 27/3/15.
 */
public class FFmpegOperations {

    static
    {
        System.loadLibrary("");
    }

    private native void convertToMp3(String fromFile, String toFile);
}
