package com.candeo.app;

import android.app.Application;
import android.os.Environment;
import android.util.DisplayMetrics;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.File;

/**
 * Created by Partho on 7/12/14.
 */
public class CandeoApplication extends Application {
//    public static final String baseUrl="http://192.168.0.105:3000";
    public static final String baseUrl="http://192.168.43.239:3000";
//    public static final String baseUrl="http://10.0.3.116:3000";
    private static CandeoApplication sInstance;
    private RequestQueue appRequestQueue;
    public static DisplayMetrics displayMetrics = null;

    @Override
    public void onCreate() {
        super.onCreate();
        displayMetrics = new DisplayMetrics();
        File candeoDirectory = new File(Environment.getExternalStorageDirectory()+"/candeo");
        candeoDirectory.mkdirs();
        File candeoAudioDirectory = new File(Environment.getExternalStorageDirectory()+"/candeo/audios");
        candeoAudioDirectory.mkdirs();
        File candeoVideoDirectory = new File(Environment.getExternalStorageDirectory()+"/candeo/videos");
        candeoVideoDirectory.mkdirs();
        File candeoImageDirectory = new File(Environment.getExternalStorageDirectory()+"/candeo/images");
        candeoImageDirectory.mkdirs();
        File candeoBookDirectory = new File(Environment.getExternalStorageDirectory()+"/candeo/books");
        candeoBookDirectory.mkdirs();

        appRequestQueue = Volley.newRequestQueue(getApplicationContext());
        if (sInstance == null)
        {
            sInstance = this;
        }


    }


    public synchronized static CandeoApplication getInstance()
    {
        return sInstance;
    }

    public RequestQueue getAppRequestQueue()
    {
        return appRequestQueue;
    }


}

