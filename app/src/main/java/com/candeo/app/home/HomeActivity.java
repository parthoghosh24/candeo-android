package com.candeo.app.home;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.candeo.app.R;
import com.candeo.app.SplashActivity;
import com.candeo.app.adapters.TabPagerAdapter;
import com.candeo.app.leaderboard.LeaderBoardFragment;
import com.candeo.app.user.LoginActivity;
import com.candeo.app.user.UserFragment;
import com.candeo.app.util.Preferences;


public class HomeActivity extends ActionBarActivity{


    private Toolbar toolbar;
    private ViewPager homePager;
    private TabPagerAdapter tabPagerAdapter;
    private HomeFragment homeFragment;
    private LeaderBoardFragment leaderBoardFragment;
    private UserFragment userFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(Preferences.isFirstRun(getApplicationContext()))
        {
            finish();
            startActivity(new Intent(getApplicationContext(), SplashActivity.class));
        }

        super.onCreate(savedInstanceState);
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
        homePager.setOffscreenPageLimit(3);
        String fromVerify = getIntent().getStringExtra("fromVerify");
        if(!TextUtils.isEmpty(fromVerify) && "verified".equalsIgnoreCase(fromVerify))
        {
            homePager.setCurrentItem(2);
        }
        else
        {
            homePager.setCurrentItem(1);
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
                        break;
                    case 1:
                        getSupportActionBar().hide();
                        break;
                    case 2:
                         getSupportActionBar().show();
                         getSupportActionBar().setTitle("My Profile");


                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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


}
