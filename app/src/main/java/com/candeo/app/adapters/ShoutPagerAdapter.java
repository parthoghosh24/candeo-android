package com.candeo.app.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.candeo.app.shout.ShoutListFragment;
import com.candeo.app.shout.ShoutNetworkFragment;

/**
 * Created by partho on 18/6/15.
 */
public class ShoutPagerAdapter extends FragmentStatePagerAdapter {


    private ShoutListFragment shoutListFragment;
    private ShoutNetworkFragment shoutNetworkFragment;

    private static final int MAX_COUNT=2;

    public ShoutPagerAdapter(FragmentManager fragmentManager, ShoutListFragment shoutListFragment, ShoutNetworkFragment shoutNetworkFragment)
    {
        super(fragmentManager);
        this.shoutListFragment = shoutListFragment;
        this.shoutNetworkFragment = shoutNetworkFragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return shoutListFragment;
            case 1:
                return shoutNetworkFragment;
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
                return "MY SHOUTS";
            case 1:
                return "MY NETWORK";
        }
        return "";
    }
}
