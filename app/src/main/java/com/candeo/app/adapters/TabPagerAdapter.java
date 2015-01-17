package com.candeo.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.candeo.app.leaderboard.LeaderBoardFragment;
import com.candeo.app.home.HomeFragment;
import com.candeo.app.user.UserFragment;

/**
 * Created by dholu on 16/11/14.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private static final int TAB_COUNT=3;

    public TabPagerAdapter(FragmentManager fragmentManager)
    {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int index) {

        Fragment fragment=null;
        switch (index)
        {
            case 0:
                fragment = new LeaderBoardFragment();
                break;
            case 1:
                fragment = new HomeFragment();
                break;
            case 2:
                fragment = new UserFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }
}
