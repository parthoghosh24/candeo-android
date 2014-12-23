package com.candeo.app;

import android.os.Environment;

import java.io.File;

/**
 * Created by Partho on 21/12/14.
 */
public class Configuration {
    public static final String BOOKSROOT= Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"Books";
    public static final String CANDEO_BOOKSROOT= Environment.getExternalStorageDirectory()+"/candeo/books";
    public static final String INTENTBOOK="book";
    public static final String INTENTCHAPTERLIST="chapterList";
    public static final String INTENTBASEURL="baseUrl";
    public static final String CONTAINERFOLDER="/META-INF/container.xml";
}
