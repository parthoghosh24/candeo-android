package com.candeo.app.user;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.util.CandeoUtil;


public class DiscoveryFragment extends Fragment {

    private View noUserDiscoveredContent;
    private static final String TAG="Candeo-Discover";
    private View mRoot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot=inflater.inflate(R.layout.fragment_discovery, container, false);
        initWidgets();
        return mRoot;
    }

    private void initWidgets()
    {

        noUserDiscoveredContent = mRoot.findViewById(R.id.candeo_user_no_discovered_content);
        noUserDiscoveredContent.setBackgroundColor(getActivity().getResources().getColor(R.color.background_floating_material_light));
        ((TextView) noUserDiscoveredContent.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        ((TextView) noUserDiscoveredContent.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_EYE);
        ((TextView) noUserDiscoveredContent.findViewById(R.id.candeo_no_content_icon)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));

        ((TextView) noUserDiscoveredContent.findViewById(R.id.candeo_no_content_text)).setText("No content discovered yet.");
        ((TextView) noUserDiscoveredContent.findViewById(R.id.candeo_no_content_text)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));


    }




}
