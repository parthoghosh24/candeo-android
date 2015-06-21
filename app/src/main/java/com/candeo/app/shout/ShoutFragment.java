package com.candeo.app.shout;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.candeo.app.R;
import com.candeo.app.adapters.ShoutPagerAdapter;
import com.candeo.app.ui.FontAwesomeDrawable;
import com.candeo.app.ui.SlidingTabLayout;

public class ShoutFragment extends Fragment {


    private View homeView=null;
    private FloatingActionButton shout;
    private ViewPager shoutPager = null;
    private ShoutPagerAdapter shoutPagerAdapter = null;
    private ShoutListFragment shoutListFragment=null;
    private ShoutNetworkFragment shoutNetworkFragment = null;
    private SlidingTabLayout slidingTabs;
    private static final String TAG="shoutfrag";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.fragment_shout, container, false);
        shout = (FloatingActionButton)homeView.findViewById(R.id.candeo_create_shout);
        FontAwesomeDrawable.FontAwesomeDrawableBuilder builder = new FontAwesomeDrawable.FontAwesomeDrawableBuilder(getActivity(),R.string.fa_bullhorn);
        builder.setColor(getActivity().getResources().getColor(R.color.candeo_white));
        builder.setSize(20);
        shout.setImageDrawable(builder.build());
        slidingTabs = (SlidingTabLayout)homeView.findViewById(R.id.candeo_shout_sliding_tabs);
        shoutPager = (ViewPager)homeView.findViewById(R.id.candeo_shouts_pager);
        shoutListFragment = new ShoutListFragment();
        shoutNetworkFragment = new ShoutNetworkFragment();
        shoutPagerAdapter = new ShoutPagerAdapter(getChildFragmentManager(),shoutListFragment,shoutNetworkFragment);


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,"I am called");
                shoutPager.setAdapter(shoutPagerAdapter);
                slidingTabs.setViewPager(shoutPager);
                slidingTabs.setDistributeEvenly(true);
                slidingTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                    @Override
                    public int getIndicatorColor(int position) {
                        return getResources().getColor(R.color.candeo_light_btn_blue);
                    }

                    @Override
                    public int getDividerColor(int position) {
                        return 0;
                    }
                });
            }
        });
        return homeView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}
