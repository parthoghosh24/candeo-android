package com.candeo.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
        String messageType = gcm.getMessageType(intent);
        try
        {
            JSONObject json = new JSONObject(intent.getStringExtra("message"));
            HashMap<String,String> map = new HashMap<>();
            map.put("title",json.getString("name"));
            map.put("body", json.getString("body"));
            CandeoUtil.showNotification(context,map, HomeActivity.class);
        }
        catch (JSONException jse)
        {
            jse.printStackTrace();
        }
    }
}
