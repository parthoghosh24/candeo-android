package com.candeo.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.candeo.app.Configuration;
import com.candeo.app.user.CreatedFragment;
import com.candeo.app.user.SocialFragment;

/**
 * Created by partho on 17/3/15.
 */
public class GeneralUserPagerAdapter extends FragmentStatePagerAdapter{
    private CreatedFragment createdFragment;
    private SocialFragment socialFragment;
    private static final int MAX_COUNT=2;
    public GeneralUserPagerAdapter(FragmentManager fragmentManager, CreatedFragment createdFragment, SocialFragment socialFragment)
    {
        super(fragmentManager);
        this.createdFragment = createdFragment;
        this.socialFragment = socialFragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return createdFragment;
            case 1:
                return socialFragment;
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
        }
        return "";
    }
}
