package com.candeo.app.home;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.candeo.app.R;
import com.candeo.app.adapters.TabPagerAdapter;



public class HomeActivity extends ActionBarActivity{


    Toolbar toolbar;
    ViewPager homePager;
    TabPagerAdapter tabPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());
        homePager = (ViewPager)findViewById(R.id.home_pager);
        toolbar = (Toolbar)findViewById(R.id.candeo_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        homePager.setAdapter(tabPagerAdapter);
        homePager.setCurrentItem(1);
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
                        getSupportActionBar().setTitle("Leaderboard");
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
            finish();
        }
        else
        {
            homePager.setCurrentItem(1);

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
