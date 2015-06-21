package com.candeo.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.candeo.app.leaderboard.LeaderBoardFragment;
import com.candeo.app.home.HomeFragment;
import com.candeo.app.marketplace.MarketplaceFragment;
import com.candeo.app.shout.ShoutFragment;
import com.candeo.app.user.UserFragment;

/**
 * Created by Partho on 16/11/14.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {

    private static final int TAB_COUNT=5;
    private HomeFragment homeFragment;
    private LeaderBoardFragment leaderBoardFragment;
    private UserFragment userFragment;
    private ShoutFragment shoutFragment;
    private MarketplaceFragment marketplaceFragment;

    public TabPagerAdapter(FragmentManager fragmentManager, HomeFragment homeFragment, LeaderBoardFragment leaderBoardFragment, UserFragment userFragment, ShoutFragment shoutFragment, MarketplaceFragment marketplaceFragment)
    {
        super(fragmentManager);
        this.homeFragment = homeFragment;
        this.leaderBoardFragment = leaderBoardFragment;
        this.userFragment = userFragment;
        this.shoutFragment=shoutFragment;
        this.marketplaceFragment=marketplaceFragment;
    }

    @Override
    public Fragment getItem(int index) {

        switch (index)
        {
            case 0:
                return leaderBoardFragment;
            case 1:
                return shoutFragment;
            case 2:
                return homeFragment;
            case 3:
                return userFragment;
            case 4:
                return marketplaceFragment;
        }
        return  new Fragment();
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }
}
