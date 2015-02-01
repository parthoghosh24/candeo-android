package com.candeo.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.candeo.app.leaderboard.LeaderBoardFragment;
import com.candeo.app.home.HomeFragment;
import com.candeo.app.user.UserFragment;

/**
 * Created by Partho on 16/11/14.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private static final int TAB_COUNT=3;
    private HomeFragment homeFragment;
    private LeaderBoardFragment leaderBoardFragment;
    private UserFragment userFragment;

    public TabPagerAdapter(FragmentManager fragmentManager, HomeFragment homeFragment, LeaderBoardFragment leaderBoardFragment, UserFragment userFragment)
    {
        super(fragmentManager);
        this.homeFragment = homeFragment;
        this.leaderBoardFragment = leaderBoardFragment;
        this.userFragment = userFragment;
    }

    @Override
    public Fragment getItem(int index) {

        switch (index)
        {
            case 0:
                return leaderBoardFragment;
            case 1:
                return homeFragment;
            case 2:
                return userFragment;
        }
        return  new Fragment();
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }
}
