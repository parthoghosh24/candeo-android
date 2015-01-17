package com.candeo.app.adapters;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.candeo.app.home.HomeActivity;
import com.candeo.app.user.AppreciatedFragment;
import com.candeo.app.user.FeedFragment;
import com.candeo.app.user.InspirationsFragment;
import com.candeo.app.user.ShowcasesFragment;
import com.candeo.app.user.SocialFragment;

/**
 * Created by partho on 16/1/15.
 */
public class UserContentAdapter extends FragmentStatePagerAdapter{
    private HomeActivity activity;

    public UserContentAdapter(HomeActivity activity)
    {
      super(activity.getSupportFragmentManager());
      this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                  return new FeedFragment();
            case 1:
                  return new ShowcasesFragment();
            case 2:
                  return new InspirationsFragment();
            case 3:
                  return new SocialFragment();
            case 4:
                  return new AppreciatedFragment();
        }
        return new Fragment();
    }


    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                  return "Feed";
            case 1:
                  return "Showcases";
            case 2:
                  return "Inspirations";
            case 3:
                  return "Social";
            case 4:
                  return "Appreciated";
        }
        return "";
    }
}
