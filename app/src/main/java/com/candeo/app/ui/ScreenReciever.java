package com.candeo.app.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by partho on 19/3/15.
 */
public class ScreenReciever extends BroadcastReceiver {

    public static boolean isScreenOn=true;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("reciever", "Reciever called");
        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
        {
            isScreenOn=false;
        }
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON))
        {
            isScreenOn=true;
        }
    }
}
