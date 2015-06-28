package com.candeo.app;

import android.os.Environment;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Partho on 21/12/14.
 */
public class Configuration {

//    public static final boolean DEBUG=false;
    public static final boolean DEBUG=true;
    //Base url
//    public static final String BASE_URL ="http://192.168.0.104:3000";
//      public static final String BASE_URL ="http://192.168.0.103:3000";
      public static final String BASE_URL ="http://192.168.0.102:3000";
//    public static final String BASE_URL ="http://192.168.0.101:3000";
//        public static final String BASE_URL ="http://192.168.0.104:3000";
//    public static final String BASE_URL="http://192.168.43.239:3000";
    //    public static final String BASE_URL="http://10.0.3.116:3000";
//    public static final String BASE_URL ="http://stage.candeoapp.com";
//    public static final String BASE_URL ="http://www.candeoapp.com";

    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public final static String GCM_SENDER_ID="1026062841670";
    public static final String CANDEO_DEFAULT_SECRET="candeosecret2015";
    public static final String CANDEO_DEFAULT_BIO="Hello World";
    public static final String BOOKSROOT= Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"Books";
    public static final String CANDEO_BOOKSROOT= Environment.getExternalStorageDirectory()+"/candeo/books";
    public static final String CANDEO_AUDIOSROOT= Environment.getExternalStorageDirectory()+"/candeo/audios";
    public static final String CANDEO_VIDEOSROOT= Environment.getExternalStorageDirectory()+"/candeo/videos";
    public static final String CANDEO_IMAGESROOT= Environment.getExternalStorageDirectory()+"/candeo/images";
    public static final String CANDEO_BINROOT= Environment.getExternalStorageDirectory()+"/candeo/bin";
    public static final String INTENTBOOK="book";
    public static final String INTENTCHAPTERLIST="chapterList";
    public static final String INTENTBASEURL="BASE_URL";
    public static final String CONTAINERFOLDER="/META-INF/container.xml";



    //Font icons
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
    public static final String FA_PLAY_ROUND="\uf01d";
    public static final String FA_PAUSE="\uf04c";
    public static final String FA_CLOCK="\uf017";
    public static final String FA_MAIL="\uf0e0";
    public static final String FA_EYE="\uf06e";
    public static final String FA_CIRCLE="\uf111";
    public static final String FA_CIRCLE_O="\uf10c";
    public static final String FA_PENCIL="\uf14b";
    public static final String FA_TEXT="\uf040";
    public static final String FA_LEFT_QUOTE="\uf10d";
    public static final String FA_RIGHT_QUOTE="\uf10e";
    public static final String FA_SHARE_ALT="\uf1e0";
    public static final String FA_BULLHORN="\uf0a1";
    public static final String FA_HOME ="\uf015";
    public static final String FA_SHOPPING_CART ="\uf07a";
    public static final String FA_CHECK="\uf00c";
    public static final String FA_CHECK_CIRCLE="\uf058";
    public static final String FA_UNLOCK ="\uf13e";
    public static final String FA_LOCK="\uf023";
    public static final String FA_PAPERPLANE="\uf1d8";


    //Media types
    public static final int TEXT=0;
    public static final int AUDIO=1;
    public static final int VIDEO=2;
    public static final int IMAGE=3;
    public static final int BOOK=4;

    //Types of Post
    public static final int SHOWCASE=1;
    public static final int INSPIRATION=2;

    //Common API Urls
    public static final String MEDIA_UPLOAD_RELATIVE_URL="/media/create";
    public static final String MEDIA_UPLOAD_URL=BASE_URL+"/api/v1"+MEDIA_UPLOAD_RELATIVE_URL;


    //Response Lists
    public static final String[] SKIP_LIST ={"Didn't Like", "Repeated", "Offensive", "Plagiarized"};
    public static final String[] APPRECIATE_LIST={"Good", "Wow", "Superb", "Excellent", "Mesmerizing"};
    public static final String[] INSPIRE_LIST={"Motivated", "Spirited", "Enlightened", "Happy", "Cheered", "Loved", "Blessed", "Funny", "Strong"};

    //Response States
    public static final int APPRECIATE=1;
    public static final int SKIP=2;
    public static final int INSPIRE=3;


}
