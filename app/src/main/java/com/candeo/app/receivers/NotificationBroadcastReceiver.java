package com.candeo.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.candeo.app.home.HomeActivity;
import com.candeo.app.util.CandeoUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by partho on 16/5/15.
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //This receiver is called when A GCM is received for notification and Notification framework is executed
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        try
        {
            JSONObject json = new JSONObject(intent.getStringExtra("message"));
            HashMap<String,String> map = new HashMap<>();
            map.put("title",json.getString("title"));
            map.put("body", json.getString("body"));
            map.put("imageUrl", json.getString("imageUrl"));
            map.put("bigImageUrl", json.getString("bigImageUrl"));
            map.put("type", json.getString("type"));
            map.put("id", json.getString("id"));
            new LoadNotification(map,context).execute();


        }
        catch (JSONException jse)
        {
            jse.printStackTrace();
        }
    }

    class LoadNotification extends AsyncTask<Void, Void, Void>
    {
        private HashMap<String, String> map;
        private Context context;

        public LoadNotification(HashMap<String, String> map, Context context)
        {
            this.map=map;
            this.context=context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            CandeoUtil.showNotification(context, map);
            return null;
        }

    }
}
