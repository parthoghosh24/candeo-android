package com.candeo.app.adapters;


import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.candeo.app.home.HomeActivity;
import com.candeo.app.home.LimelightFragment;
import com.candeo.app.response.ResponseListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Partho on 9/2/15.
 */
public class LimelightAdapter extends FragmentStatePagerAdapter implements ResponseListener, Parcelable{


    private static final String TAG="Candeo- limelight";
    private ArrayList<LimelightFragment> showcases = new ArrayList<>();
    private ViewPager limelightPager=null;

    public LimelightAdapter(ViewPager limelightPager, FragmentManager fragmentManager, ArrayList<HashMap<String, String>> showcaseIds)
    {
        super(fragmentManager);
        Log.e(TAG,"Adapter instantiated");
        this.limelightPager=limelightPager;
        for(int index=0; index<showcaseIds.size();++index)
        {
            LimelightFragment limelightFragment = new LimelightFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id",showcaseIds.get(index).get("id"));
            bundle.putInt("position", index);
            bundle.putParcelable("adapter",this);
            limelightFragment.setArguments(bundle);
            showcases.add(limelightFragment);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return showcases.get(position);
    }

    @Override
    public int getCount() {
        return showcases.size();
    }

    @Override
    public void onResponseClick(int position) {
        limelightPager.setCurrentItem(position+1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}

