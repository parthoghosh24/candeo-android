package com.candeo.app.home;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.candeo.app.R;
import com.candeo.app.adapters.TabPagerAdapter;



public class HomeActivity extends ActionBarActivity{


    Toolbar toolbar;
//    android.support.v7.app.ActionBar actionBar;
    ViewPager homePager;
    TabPagerAdapter tabPagerAdapter;
    Typeface freeBooter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        homePager = (ViewPager)findViewById(R.id.home_pager);
        toolbar = (Toolbar)findViewById(R.id.candeo_toolbar);
        setSupportActionBar(toolbar);
        homePager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
//                actionBar=getSupportActionBar();
//                actionBar.setSelectedNavigationItem(position);
            }
        });
        homePager.setAdapter(tabPagerAdapter);
        freeBooter= loadFont("freebooter.ttf");
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView title = (TextView) findViewById(titleId);
        setTitle("Candeo");
//        actionBar= getActionBar();
//        actionBar.setIcon(android.R.color.transparent);
//        title.setTypeface(freeBooter, Typeface.BOLD);
//        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.TabListener tabListener = new ActionBar.TabListener(){
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                homePager.setCurrentItem(tab.getPosition());
                TextView view=(TextView)tab.getCustomView();
                view.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                TextView view=(TextView)tab.getCustomView();
                view.setTextColor(getResources().getColor(android.R.color.white));
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };
        Typeface fa= loadFont("fa.ttf");
        final TextView feedView = new TextView(this);
        feedView.setTypeface(fa);
        feedView.setText("\uf09e");
        feedView.setTextColor(getResources().getColor(android.R.color.white));
        feedView.setTextSize(25);
        feedView.setPadding(0, 10, 0, 0);

        final TextView homeView = new TextView(this);
        homeView.setTypeface(fa);
        homeView.setText("\uf015");
        homeView.setTextColor(getResources().getColor(android.R.color.white));
        homeView.setTextSize(25);
        homeView.setPadding(0, 10, 0, 0);

        final TextView userView = new TextView(this);
        userView.setTypeface(fa);
        userView.setText("\uf007");
        userView.setTextColor(getResources().getColor(android.R.color.white));
        userView.setTextSize(25);
        userView.setPadding(0, 10, 0, 0);


//        actionBar.addTab(actionBar.newTab().setCustomView(feedView).setTabListener(tabListener));
//        actionBar.addTab(actionBar.newTab().setCustomView(homeView).setTabListener(tabListener));
//        actionBar.addTab(actionBar.newTab().setCustomView(userView).setTabListener(tabListener));

        homePager.setCurrentItem(1);
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

    private Typeface loadFont(String name)
    {
        return  Typeface.createFromAsset(getAssets(),name);
    }


}
