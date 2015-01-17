package com.candeo.app.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.candeo.app.R;
import com.candeo.app.adapters.UserContentAdapter;
import com.candeo.app.home.HomeActivity;
import com.candeo.app.ui.SlidingTabLayout;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.NetworkUtil;

public class UserFragment extends Fragment {


    private ImageView userAvatar;
    private ImageView userAvatarBg;
    private TextView userName;

    //stats
    private TextView appreciateIcon;
    private TextView appreciateCount;

    private TextView inspireIcon;
    private TextView inspireCount;

    private TextView rankIcon;
    private TextView rankCount;
    private SlidingTabLayout slidingTabs;

    private ViewPager userContentPager;
    private UserContentAdapter contentAdapter;

    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        if(!NetworkUtil.isNetworkAvailable(getActivity()))
//        {
//
//            root= inflater.inflate(R.layout.fragment_no_connectivity, container, false);
//        }
//        else
        {
            root=inflater.inflate(R.layout.fragment_user, container, false);
            initWidgets();

        }
        return root;
    }

    private void initWidgets()
    {
        userAvatar = (ImageView)root.findViewById(R.id.candeo_user_avatar);
        userAvatarBg = (ImageView)root.findViewById(R.id.candeo_user_avatar_bg);
        userName = (TextView)root.findViewById(R.id.candeo_user_name_text);
        appreciateIcon = (TextView)root.findViewById(R.id.candeo_user_appreciate_icon);
        appreciateIcon.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(),"fonts/applause.ttf"));
        appreciateIcon.setText("\ue600");
        appreciateCount=(TextView)root.findViewById(R.id.candeo_user_appreciate_count);
        inspireIcon=(TextView)root.findViewById(R.id.candeo_user_inspired_icon);
        inspireIcon.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(),"fonts/response.ttf"));
        inspireIcon.setText("\ue800");
        inspireCount=(TextView)root.findViewById(R.id.candeo_user_inspired_count);
        rankIcon =(TextView)root.findViewById(R.id.candeo_user_highest_rank_icon);
        rankCount=(TextView)root.findViewById(R.id.candeo_user_highest_rank_count);
        slidingTabs = (SlidingTabLayout)root.findViewById(R.id.candeo_user_sliding_tabs);
        userContentPager=(ViewPager)root.findViewById(R.id.candeo_user_content_pager);
        contentAdapter = new UserContentAdapter((HomeActivity)getActivity());
        userContentPager.setAdapter(contentAdapter);
        slidingTabs.setViewPager(userContentPager);


    }

}
