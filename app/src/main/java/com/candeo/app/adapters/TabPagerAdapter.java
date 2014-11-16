package com.candeo.app.adapters;

/**
 * Partho
 *
 * This is swipible TabPagerAdapter
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class TabPagerAdapter extends FragmentPagerAdapter
{
    private static final int TAB_COUNT=3;
    public TabPagerAdapter(FragmentManager fragmentManager)
    {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int i) {
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }
}