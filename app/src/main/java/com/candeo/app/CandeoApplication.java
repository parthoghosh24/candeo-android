package com.candeo.app;

import android.app.Application;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.amplitude.api.Amplitude;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.candeo.app.network.Hurlstack;
import com.candeo.app.util.CandeoUtil;

import java.io.File;

/**
 * Created by Partho on 7/12/14.
 */
public class CandeoApplication extends Application {

    private static CandeoApplication sInstance;
    private RequestQueue appRequestQueue;
    public static DisplayMetrics displayMetrics = null;

    @Override
    public void onCreate() {
        super.onCreate();
        CandeoUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/caviar.ttf");
        displayMetrics = new DisplayMetrics();
        File candeoDirectory = new File(Environment.getExternalStorageDirectory()+"/candeo");
        candeoDirectory.mkdirs();
        File candeoBinaryDirectory = new File(Environment.getExternalStorageDirectory()+"/candeo/bin");
        candeoBinaryDirectory.mkdirs();
        File candeoAudioDirectory = new File(Environment.getExternalStorageDirectory()+"/candeo/audios");
        candeoAudioDirectory.mkdirs();
        File candeoVideoDirectory = new File(Environment.getExternalStorageDirectory()+"/candeo/videos");
        candeoVideoDirectory.mkdirs();
        File candeoImageDirectory = new File(Environment.getExternalStorageDirectory()+"/candeo/images");
        candeoImageDirectory.mkdirs();
        File candeoBookDirectory = new File(Environment.getExternalStorageDirectory()+"/candeo/books");
        candeoBookDirectory.mkdirs();

        appRequestQueue = Configuration.DEBUG?Volley.newRequestQueue(getApplicationContext()):Volley.newRequestQueue(getApplicationContext(), new Hurlstack());

        if (sInstance == null)
        {
            sInstance = this;
        }

        if(!Configuration.DEBUG)
        {
            if(Configuration.BASE_URL.contains("stage"))
            {
                Amplitude.getInstance().initialize(this,"3e65f9966c5feeef7f1d77af0ec0f741");
            }
            else
            {
                Amplitude.getInstance().initialize(this,"a2e3be092e1ae98ede5594945451d93e");
            }

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

