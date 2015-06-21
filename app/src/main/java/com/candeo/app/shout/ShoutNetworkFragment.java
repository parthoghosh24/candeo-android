package com.candeo.app.shout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.adapters.ShoutNetworkAdapter;
import com.candeo.app.util.CandeoUtil;


public class ShoutNetworkFragment extends Fragment {

    private RecyclerView shoutNetwork;
    private ShoutNetworkAdapter shoutNetworkAdapter;
    private View noShoutNetworkContent;
    private View loadingContent;
    private View root;
    private LinearLayoutManager shoutNetworkLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root=inflater.inflate(R.layout.fragment_shout_network, container, false);
        initWidgets();
        return root;
    }

    private void initWidgets()
    {
        noShoutNetworkContent = root.findViewById(R.id.candeo_shout_network_no_content);
        loadingContent = root.findViewById(R.id.candeo_loading_content);
        shoutNetwork = (RecyclerView)root.findViewById(R.id.candeo_shout_network);
        shoutNetwork.setAdapter(shoutNetworkAdapter);
        shoutNetworkLayoutManager= new LinearLayoutManager(getActivity());
        shoutNetworkLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        shoutNetwork.setLayoutManager(shoutNetworkLayoutManager);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setText(Configuration.FA_USERS);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_text)).setText("Loading Network...");
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_text)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        CandeoUtil.toggleView(loadingContent, false);
        ((TextView)noShoutNetworkContent.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        ((TextView)noShoutNetworkContent.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_USERS);
        ((TextView)noShoutNetworkContent.findViewById(R.id.candeo_no_content_icon)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        ((TextView)noShoutNetworkContent.findViewById(R.id.candeo_no_content_text)).setText("No Network found yet. Appreciate or Get Appreciated to build one.");
        ((TextView)noShoutNetworkContent.findViewById(R.id.candeo_no_content_text)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        CandeoUtil.toggleView(noShoutNetworkContent, true);

    }


}
