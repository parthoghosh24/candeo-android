package com.candeo.app.home;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.SplashActivity;
import com.candeo.app.adapters.TabPagerAdapter;
import com.candeo.app.algorithms.FFmpegOperations;
import com.candeo.app.algorithms.Security;
import com.candeo.app.leaderboard.LeaderBoardFragment;
import com.candeo.app.user.LoginActivity;
import com.candeo.app.user.UserFragment;
import com.candeo.app.util.Preferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class HomeActivity extends ActionBarActivity {


    private Toolbar toolbar;
    private ViewPager homePager;
    private TabPagerAdapter tabPagerAdapter;
    private HomeFragment homeFragment;
    private LeaderBoardFragment leaderBoardFragment;
    private UserFragment userFragment;
    private static final String TAG = "Candeo - Home";
    private static final String API_USER_UPDATE_GCM_RELATIVE_URL="/users/gcm";
    private static final String API_USER_UPDATE_GCM_URL= Configuration.BASE_URL +"/api/v1"+API_USER_UPDATE_GCM_RELATIVE_URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Preferences.isFirstRun(getApplicationContext())) {
            finish();
            startActivity(new Intent(getApplicationContext(), SplashActivity.class));
        } else {

            setContentView(R.layout.activity_home);
            Amplitude.getInstance().logEvent("Home Activity loaded");
            toolbar = (Toolbar) findViewById(R.id.candeo_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if(!TextUtils.isEmpty(Preferences.getUserRowId(this)) && checkPlayServices())
            {
                if(Configuration.DEBUG)Log.e(TAG,"GCM Registration");
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

            homePager = (ViewPager) findViewById(R.id.home_pager);
            homeFragment = new HomeFragment();
            leaderBoardFragment = new LeaderBoardFragment();
            userFragment = new UserFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id",Preferences.getUserRowId(this));
            userFragment.setArguments(bundle);
            tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), homeFragment, leaderBoardFragment, userFragment);
            homePager.setAdapter(tabPagerAdapter);
            homePager.setOffscreenPageLimit(2);
            String fromVerify = getIntent().getStringExtra("fromVerify");
            String performance = getIntent().getStringExtra("performance");
            if(!TextUtils.isEmpty(performance) && "performance".equalsIgnoreCase(performance))
            {
                getSupportActionBar().show();
                getSupportActionBar().setTitle("Performances");
                homePager.setCurrentItem(0);
            }
            else if (!TextUtils.isEmpty(fromVerify) && "verified".equalsIgnoreCase(fromVerify)) {
                getSupportActionBar().show();
                getSupportActionBar().setTitle("My Profile");
                homePager.setCurrentItem(2);
            } else {
                homePager.setCurrentItem(1);
                getSupportActionBar().hide();
            }
            homePager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    getSupportActionBar().hide();

                    switch (position) {
                        case 0:
                            Amplitude.getInstance().logEvent("LeaderBoard swiped");
                            getSupportActionBar().show();
                            getSupportActionBar().setTitle("Performances");
                            leaderBoardFragment.requestRefresh(HomeActivity.this);
                            break;

                        case 1:
                            Amplitude.getInstance().logEvent("Limelight swiped");
                            getSupportActionBar().hide();
                            if (Configuration.DEBUG) Log.e(TAG, "Limelight request fetched");
                            homeFragment.requestRefresh(HomeActivity.this);
                            break;

                        case 2:
                            Amplitude.getInstance().logEvent("Profile swiped");
                            getSupportActionBar().show();
                            getSupportActionBar().setTitle("My Profile");
                            userFragment.requestRefresh(HomeActivity.this);
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        switch (homePager.getCurrentItem())
        {
            case 0: //Performances
                leaderBoardFragment.requestRefresh(this);
                break;
            case 1: //Limelight
                homeFragment.requestRefresh(this);
                break;
            case 2: //User
                userFragment.requestRefresh(this);
                break;
        }

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

    @Override
    public void onBackPressed() {
        if (homePager.getCurrentItem() == 1) {
            super.onBackPressed();
        } else {
            homePager.setCurrentItem(1, true);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem settings = menu.getItem(0);
        settings.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                homePager.setCurrentItem(1);
                break;
        }
        return true;
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

