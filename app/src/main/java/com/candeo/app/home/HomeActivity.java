package com.candeo.app.home;


import android.content.Intent;
import android.graphics.Typeface;
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

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.SplashActivity;
import com.candeo.app.adapters.TabPagerAdapter;
import com.candeo.app.leaderboard.LeaderBoardFragment;
import com.candeo.app.user.LoginActivity;
import com.candeo.app.user.UserFragment;
import com.candeo.app.util.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class HomeActivity extends ActionBarActivity{


    private Toolbar toolbar;
    private ViewPager homePager;
    private TabPagerAdapter tabPagerAdapter;
    private HomeFragment homeFragment;
    private LeaderBoardFragment leaderBoardFragment;
    private UserFragment userFragment;
    private static final String TAG="Candeo - Home";
    private final static String GET_USER_API = Configuration.BASE_URL+"/api/v1/users/%s";
    private final static String GET_LIMELIGHT_LIST_API = Configuration.BASE_URL +"/api/v1/contents/limelights/list/%s";
    private final static String GET_PERFORMANCES_API = Configuration.BASE_URL+"/api/v1/contents/performances/show";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Preferences.isFirstRun(getApplicationContext()))
        {
            finish();
            startActivity(new Intent(getApplicationContext(), SplashActivity.class));
        }
        else
        {

            setContentView(R.layout.activity_home);
            homeFragment = new HomeFragment();
            leaderBoardFragment = new LeaderBoardFragment();
            userFragment = new UserFragment();
            tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), homeFragment, leaderBoardFragment, userFragment);
            homePager = (ViewPager)findViewById(R.id.home_pager);
            toolbar = (Toolbar)findViewById(R.id.candeo_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            homePager.setAdapter(tabPagerAdapter);
            homePager.setOffscreenPageLimit(2);
            String fromVerify = getIntent().getStringExtra("fromVerify");
            if(!TextUtils.isEmpty(fromVerify) && "verified".equalsIgnoreCase(fromVerify))
            {
                homePager.setCurrentItem(2);
                GetUserRequest userRequest = new GetUserRequest(Preferences.getUserRowId(getApplicationContext()));
                userRequest.setShouldCache(false);
                CandeoApplication.getInstance().getAppRequestQueue().add(userRequest);
            }
            else
            {
                homePager.setCurrentItem(1);
                String id = TextUtils.isEmpty(Preferences.getUserRowId(getApplicationContext())) ? "0":Preferences.getUserRowId(getApplicationContext());
                FetchLimelightList fetchLimelightListRequest = new FetchLimelightList(id);
                fetchLimelightListRequest.setShouldCache(false);
                CandeoApplication.getInstance().getAppRequestQueue().add(fetchLimelightListRequest);
            }

            getSupportActionBar().hide();
            homePager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    switch (position)
                    {
                        case 0:
                            getSupportActionBar().show();
                            getSupportActionBar().setTitle("Performances");
                            GetPerformanceRequest performanceRequest = new GetPerformanceRequest();
                            performanceRequest.setShouldCache(false);
                            CandeoApplication.getInstance().getAppRequestQueue().add(performanceRequest);
                            break;
                        case 1:
                            getSupportActionBar().hide();
                            if(Configuration.DEBUG)Log.e(TAG,"Limelight request fetched");
                            String id = TextUtils.isEmpty(Preferences.getUserRowId(getApplicationContext())) ? "0":Preferences.getUserRowId(getApplicationContext());
                            FetchLimelightList fetchLimelightListRequest = new FetchLimelightList(id);
                            fetchLimelightListRequest.setShouldCache(false);
                            CandeoApplication.getInstance().getAppRequestQueue().add(fetchLimelightListRequest);
                            break;
                        case 2:
                            getSupportActionBar().show();
                            getSupportActionBar().setTitle("My Profile");
                            GetUserRequest userRequest = new GetUserRequest(Preferences.getUserRowId(getApplicationContext()));
                            userRequest.setShouldCache(false);
                            CandeoApplication.getInstance().getAppRequestQueue().add(userRequest);
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

    }

    @Override
    public void onBackPressed() {
        if(homePager.getCurrentItem() == 1)
        {
            super.onBackPressed();
        }
        else
        {
            homePager.setCurrentItem(1,true);

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
        switch (id)
        {
            case android.R.id.home:
                homePager.setCurrentItem(1);
                break;
        }
        return true;
    }


    private class GetUserRequest extends JsonObjectRequest
    {
        public GetUserRequest(String id)
        {
            super(Method.GET,
                    String.format(GET_USER_API,id),
                    null,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            userFragment.onGetUserComplete(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "error is " + error.getLocalizedMessage());
                        }
                    });
        }
    }

    private class FetchLimelightList extends JsonObjectRequest
    {
        public FetchLimelightList(String id)
        {
            super(Method.GET,
                    String.format(GET_LIMELIGHT_LIST_API,id),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            homeFragment.onGetLimelightComplete(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e(TAG,"Error occured");
                            Log.e(TAG, "localized error while fetching is limelight " + error.getLocalizedMessage());
                            NetworkResponse response = error.networkResponse;
                            if(response!=null)
                            {
                                Log.e(TAG,"Actual error while fetching limelight is "+new String(response.data));
                            }

                        }
                    }
            );
        }
    }

    private class GetPerformanceRequest extends JsonObjectRequest
    {
        public GetPerformanceRequest()
        {
            super(Method.GET,
                    GET_PERFORMANCES_API,
                    null,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            leaderBoardFragment.onGetLeaderBoardComplete(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG,"Error occured");
                            Log.e(TAG, "localized error while fetching is leaderboard " + error.getLocalizedMessage());
                            NetworkResponse response = error.networkResponse;
                            if(response!=null)
                            {
                                Log.e(TAG,"Actual error while fetching leaderboard is "+new String(response.data));
                            }
                        }
                    });
        }
    }


}
