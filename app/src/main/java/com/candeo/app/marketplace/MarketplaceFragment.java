package com.candeo.app.marketplace;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.util.CandeoUtil;


public class MarketplaceFragment extends Fragment {


    private View homeView = null;
    private View comingSoon = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.fragment_marketplace, container, false);
        comingSoon = homeView.findViewById(R.id.candeo_marketplace_comingsoon);
        ((TextView)comingSoon.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        ((TextView)comingSoon.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_SHOPPING_CART);
        ((TextView)comingSoon.findViewById(R.id.candeo_no_content_text)).setText("Marketplace coming soon, so that you can make your talent just more useful :) \nMeanwhile, try to perform your best.");
        return homeView;
    }



}
