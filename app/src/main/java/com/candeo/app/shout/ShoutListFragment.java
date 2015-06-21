package com.candeo.app.shout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.adapters.ShoutListAdapter;
import com.candeo.app.util.CandeoUtil;


public class ShoutListFragment extends Fragment {

    private RecyclerView shoutList;
    private ShoutListAdapter shoutListAdapter;
    private LinearLayoutManager shoutListLayoutManager;
    private View noShoutListContent;
    private View loadingContent;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root= inflater.inflate(R.layout.fragment_shout_list, container, false);
        initWidgets();
        return root;
    }

    private void initWidgets()
    {
        noShoutListContent = root.findViewById(R.id.candeo_shout_list_no_content);
        loadingContent = root.findViewById(R.id.candeo_loading_content);
        shoutList = (RecyclerView)root.findViewById(R.id.candeo_shout_list);
        shoutList.setAdapter(shoutListAdapter);
        shoutListLayoutManager= new LinearLayoutManager(getActivity());
        shoutListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        shoutList.setLayoutManager(shoutListLayoutManager);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setText(Configuration.FA_BULLHORN);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_text)).setText("Loading Shouts...");
        ((TextView) loadingContent.findViewById(R.id.candeo_progress_text)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        CandeoUtil.toggleView(loadingContent, false);
        ((TextView)noShoutListContent.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        ((TextView)noShoutListContent.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_BULLHORN);
        ((TextView)noShoutListContent.findViewById(R.id.candeo_no_content_icon)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        ((TextView)noShoutListContent.findViewById(R.id.candeo_no_content_text)).setText("No Shouts yet.");
        ((TextView)noShoutListContent.findViewById(R.id.candeo_no_content_text)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        CandeoUtil.toggleView(noShoutListContent, true);
    }


}
