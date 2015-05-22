package com.candeo.app.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.algorithms.Security;
import com.candeo.app.home.HomeActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserVerifyActivity extends ActionBarActivity {


    private static final String API_USER_VERIFY_RELATIVE_URL="/users/verify";
    private static final String API_USER_VERIFY_URL= Configuration.BASE_URL +"/api/v1"+API_USER_VERIFY_RELATIVE_URL;
    private static final String API_USER_UPDATE_GCM_RELATIVE_URL="/users/gcm";
    private static final String API_USER_UPDATE_GCM_URL= Configuration.BASE_URL +"/api/v1"+API_USER_UPDATE_GCM_RELATIVE_URL;
    private static final String TAG="Candeo - User Verify";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Amplitude.getInstance().logEvent("Verify activity loaded");
        setContentView(R.layout.activity_user_verify);
        String url = getIntent().getDataString();
//        Toast.makeText(getApplicationContext(),"Url is "+url,Toast.LENGTH_LONG).show();
        int code =Integer.parseInt(CandeoUtil.getCodeFromUrl(url));
        if(Configuration.DEBUG)Log.e(TAG,"code received is "+code);
        Map<String ,Integer> payload = new HashMap<>();
        payload.put("token",code);
        UserVerifyRequest verifyRequest = new UserVerifyRequest(payload);
        verifyRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*10, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        CandeoApplication.getInstance().getAppRequestQueue().add(verifyRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Amplitude.getInstance().startSession();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Amplitude.getInstance().endSession();
    }

    class UserVerifyRequest extends JsonObjectRequest
    {
        public UserVerifyRequest(Map<String,Integer> payload)
        {

            super(Method.POST,
                    API_USER_VERIFY_URL,
                    new JSONObject(payload),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                           if(response!=null)
                           {
                               try {
                                   JSONObject user =response.getJSONObject("user");

                                   Preferences.setUserRowId(getApplicationContext(),Integer.toString(user.getInt("id")));
                                   Preferences.setUserUsername(getApplicationContext(), user.getString("username"));
                                   Preferences.setUserEmail(getApplicationContext(), user.getString("email"));
                                   Preferences.setUserName(getApplicationContext(), user.getString("name"));
                                   Preferences.setUserAbout(getApplicationContext(),user.getString("about"));
                                   Preferences.setUserApiKey(getApplicationContext(), user.getString("auth_token"));
                                   Preferences.setUserAvatarPath(getApplicationContext(), Configuration.BASE_URL+user.getString("avatar_path"));
                                   Preferences.setUserLoggedIn(getApplicationContext(),true);
                                   if(checkPlayServices())
                                   {
                                       GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                                       String gcmId = Preferences.getUserGcmId(getApplicationContext());
                                       if(TextUtils.isEmpty(gcmId))
                                       {
                                           registerInBackground(gcm);
                                       }
                                       else
                                       {
                                           if(Configuration.DEBUG)Log.e(TAG,"GCM ID IS "+gcmId);
                                       }
                                   }
                                   finish();
                                   Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                   intent.putExtra("fromVerify","verified");
                                   startActivity(intent);

                                   if(Configuration.DEBUG)Log.e(TAG,"Response is "+response.toString());
                               }
                               catch (JSONException je)
                               {

                                   je.printStackTrace();
                               }

                           }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            String secret=Configuration.CANDEO_DEFAULT_SECRET;
            params.put("email", "");
            String message = API_USER_VERIFY_RELATIVE_URL;
            params.put("message", message);
            if(Configuration.DEBUG)Log.e(TAG,"secret->"+secret);
            String hash = Security.generateHmac(secret, message);
            if(Configuration.DEBUG)Log.e(TAG,"hash->"+hash);
            params.put("Authorization", "Token token=" + hash);
            return params;
        }
    }

    private void registerInBackground(final GoogleCloudMessaging gcm)
    {
        new AsyncTask<Void, String, String>()
        {
            @Override
            protected String doInBackground(Void... params) {
                String message ="";
                try {
                    String regId = gcm.register(Configuration.GCM_SENDER_ID);
                    message  = "Device registered, registration ID=" + regId;
                    Preferences.setUserGcmId(getApplicationContext(),regId);
                    HashMap<String, String> payload = new HashMap<>();
                    payload.put("id", Preferences.getUserRowId(getApplicationContext()));
                    payload.put("gcm_id", regId);
                    UpdateGCMIDRequest updateGCMIDRequest = new UpdateGCMIDRequest(payload);
                    updateGCMIDRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*10, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    CandeoApplication.getInstance().getAppRequestQueue().add(updateGCMIDRequest);
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                    message="Error: "+ioe.getMessage();
                }
                return message;
            }

            @Override
            protected void onPostExecute(String s) {
                if(Configuration.DEBUG)Log.e(TAG,"GCM response");
            }
        }.execute(null, null, null);
    }

    class UpdateGCMIDRequest extends JsonObjectRequest
    {
        public UpdateGCMIDRequest(Map<String,String> payload)
        {
            super(Method.POST,
                    API_USER_UPDATE_GCM_URL,
                    new JSONObject(payload),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //GCM successfully registered
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Something wrong happened during registration
                        }
                    });
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            String secret="";
            if ((Preferences.isUserLoggedIn(getApplicationContext()) && !TextUtils.isEmpty(Preferences.getUserEmail(getApplicationContext())))) {
                params.put("email", Preferences.getUserEmail(getApplicationContext()));
                secret=Preferences.getUserApiKey(getApplicationContext());

            } else {
                params.put("email", "");
                secret=Configuration.CANDEO_DEFAULT_SECRET;
            }
            String message = API_USER_UPDATE_GCM_RELATIVE_URL;
            params.put("message", message);
            if(Configuration.DEBUG)Log.e(TAG,"secret->"+secret);
            String hash = Security.generateHmac(secret, message);
            if(Configuration.DEBUG)Log.e(TAG,"hash->"+hash);
            params.put("Authorization", "Token token=" + hash);
            return params;
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        Configuration.PLAY_SERVICES_RESOLUTION_REQUEST ).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
