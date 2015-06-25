package com.candeo.app.shout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.adapters.ShoutPagerAdapter;
import com.candeo.app.ui.FontAwesomeDrawable;
import com.candeo.app.ui.SlidingTabLayout;
import com.candeo.app.user.LoginActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import java.util.ArrayList;

public class ShoutFragment extends Fragment implements ShoutListener{


    private View homeView=null;
    private FloatingActionButton shout;
    private ViewPager shoutPager = null;
    private ShoutPagerAdapter shoutPagerAdapter = null;
    private ShoutListFragment shoutListFragment=null;
    private ShoutNetworkFragment shoutNetworkFragment = null;
    private SlidingTabLayout slidingTabs;
    public static ArrayList<String> networkIdList = new ArrayList<>();
    private static final String TAG="shoutfrag";
    private ShoutListener mListener=null;
    private View notLoggedIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        StringBuilder builer = new StringBuilder();
        homeView = inflater.inflate(R.layout.fragment_shout, container, false);
        notLoggedIn = homeView.findViewById(R.id.candeo_user_not_logged_in);
        ((TextView)notLoggedIn.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        ((TextView)notLoggedIn.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_BULLHORN);
        ((TextView)notLoggedIn.findViewById(R.id.candeo_no_content_text)).setText("Tap to signin/signup to shout to network");
        notLoggedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        if(Preferences.isUserLoggedIn(getActivity()))
        {
            notLoggedIn.setVisibility(View.GONE);
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
            mListener=this;
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

            shout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShoutPostFragment response = new ShoutPostFragment();
                    response.setShoutListener(mListener);
                    Bundle bundle = new Bundle();
                    bundle.putInt("participants", networkIdList.size());
                    bundle.putString("ids",getIdsAsString(networkIdList));
                    networkIdList.toArray();
                    bundle.putStringArrayList("participantsList",networkIdList);
                    response.setArguments(bundle);
                    response.setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Light_Dialog);
                    response.show(getChildFragmentManager().beginTransaction(), "Shout");
                }
            });
        }
        else
        {
            notLoggedIn.setVisibility(View.VISIBLE);
        }


        return homeView;
    }

    private String getIdsAsString(ArrayList<String> ids)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("");
        if(ids.size()>0)
        {
           for(int index=0 ;index<ids.size();++index)
           {
               builder.append(ids.get(index));
               if(index!=ids.size()-1)
               {
                   builder.append(",");
               }
           }
        }
        return builder.toString();
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
    public void onSuccess() {
        shoutPager.setCurrentItem(0,true);
    }

    @Override
    public void onFailure() {
        Toast.makeText(getActivity(),"Failed to shout! Please try again",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}
