package com.candeo.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.candeo.app.Configuration;
import com.candeo.app.home.HomeActivity;
import com.candeo.app.user.AppreciatedFragment;
import com.candeo.app.user.DiscoveryFragment;
import com.candeo.app.user.FeedFragment;
import com.candeo.app.user.InspirationsFragment;
import com.candeo.app.user.CreatedFragment;
import com.candeo.app.user.SocialFragment;

/**
 * Created by partho on 16/1/15.
 */
public class UserContentAdapter extends FragmentStatePagerAdapter{
    private HomeActivity activity;
    private CreatedFragment createdFragment;
    private SocialFragment socialFragment;
    private InspirationsFragment inspirationsFragment;
    private DiscoveryFragment discoveryFragment;
    private static final int MAX_COUNT=3;


    public UserContentAdapter(HomeActivity activity, CreatedFragment createdFragment, SocialFragment socialFragment, InspirationsFragment inspirationsFragment, DiscoveryFragment discoveryFragment)
    {
      super(activity.getSupportFragmentManager());
      this.activity = activity;
      this.createdFragment = createdFragment;
      this.socialFragment = socialFragment;
      this.inspirationsFragment = inspirationsFragment;
      this.discoveryFragment = discoveryFragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                  return createdFragment;
            case 1:
                  return socialFragment;
            case 2:
                  return discoveryFragment;
        }
        return new Fragment();
    }


    @Override
    public int getCount() {
        return MAX_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                  return Configuration.FA_MAGIC+" Created";
            case 1:
                  return Configuration.FA_USERS+" Social";
            case 2:
                  return Configuration.FA_EYE+" Discoveries";
        }
        return "";
    }
}
