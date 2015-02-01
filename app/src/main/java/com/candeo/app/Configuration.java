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
    public static final String INTENTBASEURL="BASE_URL";
    public static final String CONTAINERFOLDER="/META-INF/container.xml";
//    public static final boolean DEBUG=true;
    public static final boolean DEBUG=false;


    public static final String FA_VIDEO="\uf008";
    public static final String FA_AUDIO="\uf001";
    public static final String FA_IMAGE="\uf030";
    public static final String FA_BOOK="\uf02d";
    public static final String FA_MAGIC="\uf0d0";
    public static final String FA_USER="\uf007";
    public static final String FA_USERS="\uf0c0";
    public static final String FA_STATS="\uf0ac";
    public static final String FA_INSPIRE="\ue800";
    public static final String FA_APPRECIATE="\ue600";
    public static final String FA_SKIP="\uf088";
    public static final String FA_COPYRIGHT="\uf1f9";
    public static final String FA_CHEVRON="\uf054";
    public static final String FA_PLAY="\uf04b";
    public static final String FA_PAUSE="\uf04c";

}
