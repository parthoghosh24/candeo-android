package com.candeo.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.candeo.app.R;

/**
 * Created by partho on 20/1/15.
 */
public class Preferences {
    private static final String CANDEO_PREF="Candeo";
    private static final String CANDEO_PREF_FIRST_RUN="CANDEO_PREF_FIRST_RUN";
    private static final String CANDEO_PREF_USER_NAME="CANDEO_PREF_USER_NAME";
    private static final String CANDEO_PREF_USER_EMAIL="CANDEO_PREF_USER_EMAIL";
    private static final String CANDEO_PREF_USER_USERNAME="CANDEO_PREF_USER_USERNAME";
    private static final String CANDEO_PREF_USER_IS_LOGGED_IN="CANDEO_PREF_USER_IS_LOGGED_IN";
    private static final String CANDEO_PREF_USER_AVATAR_PATH="CANDEO_PREF_USER_AVATAR_PATH";
    private static final String CANDEO_PREF_USER_ABOUT="CANDEO_PREF_USER_ABOUT";
    private static final String CANDEO_PREF_USER_ROW_ID="CANDEO_PREF_USER_ROW_ID";
    private static final String CANDEO_PREF_USER_API_KEY="CANDEO_PREF_USER_API_KEY";
    private static final String CANDEO_PREF_USER_GCM_ID="CANDEO_PREF_USER_GCM_ID";


    private static SharedPreferences getSharedPref(Context ctx)
    {
        return ctx.getSharedPreferences(CANDEO_PREF, Context.MODE_PRIVATE);
    }
    public static boolean isFirstRun(Context context)
    {
        return getSharedPref(context).getBoolean(CANDEO_PREF_FIRST_RUN, true);
    }

    public static void setFirstRun(Context context, boolean state)
    {
        Editor editor = getSharedPref(context).edit();
        editor.putBoolean(CANDEO_PREF_FIRST_RUN,state);
        editor.commit();
    }

    public static String getUserName(Context context)
    {
        return getSharedPref(context).getString(CANDEO_PREF_USER_NAME,"");
    }

    public static void setUserName(Context context, String name)
    {
        Editor editor = getSharedPref(context).edit();
        editor.putString(CANDEO_PREF_USER_NAME, name);
        editor.commit();
    }

    public static String getUserEmail(Context context)
    {
        return getSharedPref(context).getString(CANDEO_PREF_USER_EMAIL,"");
    }

    public static void setUserEmail(Context context, String email)
    {
        Editor editor = getSharedPref(context).edit();
        editor.putString(CANDEO_PREF_USER_EMAIL,email);
        editor.commit();
    }

    public static String getUserUsername(Context context)
    {
        return getSharedPref(context).getString(CANDEO_PREF_USER_USERNAME,"");
    }

    public static void setUserUsername(Context context, String username)
    {
        Editor editor = getSharedPref(context).edit();
        editor.putString(CANDEO_PREF_USER_USERNAME,username);
        editor.commit();
    }

    public static String getUserAbout(Context context)
    {
        return getSharedPref(context).getString(CANDEO_PREF_USER_ABOUT,"");
    }

    public static void setUserAbout(Context context, String about)
    {
        Editor editor = getSharedPref(context).edit();
        editor.putString(CANDEO_PREF_USER_ABOUT,about);
        editor.commit();
    }

    public static boolean isUserLoggedIn(Context context)
    {
        return getSharedPref(context).getBoolean(CANDEO_PREF_USER_IS_LOGGED_IN,false);
    }

    public static void setUserLoggedIn(Context context, boolean state)
    {
        Editor editor = getSharedPref(context).edit();
        editor.putBoolean(CANDEO_PREF_USER_IS_LOGGED_IN,state);
        editor.commit();
    }

    public static String getUserAvatarPath(Context context)
    {
        return getSharedPref(context).getString(CANDEO_PREF_USER_AVATAR_PATH,"android.resource://" + context.getPackageName() + "/"
                + R.raw.default_avatar);
    }

    public static void setUserAvatarPath(Context context, String avatarPath)
    {
        Editor editor = getSharedPref(context).edit();
        editor.putString(CANDEO_PREF_USER_AVATAR_PATH,avatarPath);
        editor.commit();
    }

    public static String getUserRowId(Context context)
    {
        return getSharedPref(context).getString(CANDEO_PREF_USER_ROW_ID,"");
    }

    public static void setUserRowId(Context context, String id)
    {
        Editor editor = getSharedPref(context).edit();
        editor.putString(CANDEO_PREF_USER_ROW_ID,id);
        editor.commit();
    }

    public static String getUserApiKey(Context context)
    {
        return getSharedPref(context).getString(CANDEO_PREF_USER_API_KEY,"");
    }

    public static void setUserApiKey(Context context, String apiKey)
    {
        Editor editor = getSharedPref(context).edit();
        editor.putString(CANDEO_PREF_USER_API_KEY,apiKey);
        editor.commit();
    }

    public static void setUserGcmId(Context context, String gcmId)
    {
        Editor editor = getSharedPref(context).edit();
        editor.putString(CANDEO_PREF_USER_GCM_ID,gcmId);
        editor.commit();
    }

    public static String getUserGcmId(Context context)
    {
        return getSharedPref(context).getString(CANDEO_PREF_USER_GCM_ID,"");
    }






}
