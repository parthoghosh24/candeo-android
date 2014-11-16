package com.candeo.app.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.candeo.app.ContentActivity;
import com.candeo.app.R;
import com.candeo.app.util.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class HomeActivity extends FragmentActivity{


    ActionBar actionBar;
    ViewPager homePager;
    TabPagerAdapter
    Typeface freeBooter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        freeBooter= loadFontFreeBooter();
        actionBar= getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView title = (TextView) findViewById(titleId);
        setTitle("Candeo");

        System.out.println("Action Bar is : "+actionBar);
        actionBar.setIcon(new ColorDrawable(getResources().getColor(R.color.material_blue_500)));
        title.setTypeface(freeBooter, Typeface.BOLD);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Typeface loadFontFreeBooter()
    {
        Typeface fontAwesome = Typeface.createFromAsset(getAssets(),"freebooter.ttf");
        return  fontAwesome;
    }


}
