package com.candeo.app.user;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserVerifyActivity extends ActionBarActivity {


    private static final String API_USER_VERIFY_RELATIVE_URL="/users/verify";
    private static final String API_USER_VERIFY_URL= Configuration.BASE_URL +"/api/v1"+API_USER_VERIFY_RELATIVE_URL;
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

}
