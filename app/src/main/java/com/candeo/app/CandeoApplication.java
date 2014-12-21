package com.candeo.app;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.candeo.app.db.CandeoDatabase;
import com.candeo.app.db.CandeoDbHelper;

import java.io.File;

/**
 * Created by Partho on 7/12/14.
 */
public class CandeoApplication extends Application {
    public static final String baseUrl="http://192.168.0.105:3000";
    private static CandeoApplication sInstance;
    private RequestQueue appRequestQueue;
    private CandeoDbHelper appDatabase;
    private static final String TABLE_NAME="users";
    private static final String USER_ID ="_id";
    private static final String USER_NAME="name";
    private static final String USER_EMAIL="email";
    private static final String USER_USERNAME="username";
    private static final String USER_AUTH_TOKEN="auth_token";
    private static final String USER_UUID="uuid";
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
        if(appDatabase == null)
        {
            appDatabase = new CandeoDbHelper(getApplicationContext());
            initWithAnonymous(appDatabase.getWritableDatabase());
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

    public synchronized CandeoDbHelper getDatabase()
    {
        return appDatabase;
    }

    private void initWithAnonymous(SQLiteDatabase appDatabase)
    {

        ContentValues anonymousValues = new ContentValues();
        anonymousValues.put(USER_NAME,"Anonymous");
        anonymousValues.put(USER_EMAIL,"anonymous@candeoapp.com");
        anonymousValues.put(USER_USERNAME,"anonymous");
        anonymousValues.put(USER_AUTH_TOKEN,"sqxCMW3ozXEsyRvqdp5LXzHlKMor");
        anonymousValues.put(USER_UUID,"468d897cb5971e958c1b6e5cdd31c3fe6fe8c43a");
        appDatabase.insert(TABLE_NAME,null,anonymousValues);
    }
}

