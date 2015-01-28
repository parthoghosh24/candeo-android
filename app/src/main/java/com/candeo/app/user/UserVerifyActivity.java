package com.candeo.app.user;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.R;
import com.candeo.app.home.HomeActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserVerifyActivity extends ActionBarActivity {


    private static final String API_USER_VERIFY_URL= CandeoApplication.BASE_URL +"/api/v1/users/verify";
    private static final String TAG="Candeo - User Verify Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_verify);
        String url = getIntent().getDataString();
        Toast.makeText(getApplicationContext(),"Url is "+url,Toast.LENGTH_LONG).show();
        int code =Integer.parseInt(CandeoUtil.getCodeFromUrl(url));
        Log.e(TAG,"code received is "+code);
        Map<String ,Integer> payload = new HashMap<>();
        payload.put("token",code);
        UserVerifyRequest verifyRequest = new UserVerifyRequest(payload);
        CandeoApplication.getInstance().getAppRequestQueue().add(verifyRequest);
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
                                   Preferences.setUserLoggedIn(getApplicationContext(),true);
                                   finish();
                                   Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                   intent.putExtra("fromVerify","verified");
                                   startActivity(intent);

                                   Log.e(TAG,"Response is "+response.toString());
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
    }

}
