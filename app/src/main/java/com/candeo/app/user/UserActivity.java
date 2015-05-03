package com.candeo.app.user;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.amplitude.api.Amplitude;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.algorithms.Security;
import com.candeo.app.util.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends ActionBarActivity {

    private final static String GET_USER_RELATIVE_API = "/users/%s";
    private final static String GET_USER_API = Configuration.BASE_URL + "/api/v1" + GET_USER_RELATIVE_API;
    private static final String TAG = "Candeo - User";
    private Toolbar toolbar;

    UserFragment userFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        toolbar = (Toolbar)findViewById(R.id.candeo_content_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userFragment = new UserFragment();
        String id = getIntent().getStringExtra("id");
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        userFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.candeo_user_container, userFragment).commit();
        userFragment.requestRefresh(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
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

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

}
