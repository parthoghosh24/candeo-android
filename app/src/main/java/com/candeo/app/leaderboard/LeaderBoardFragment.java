package com.candeo.app.leaderboard;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.adapters.LeaderboardAdapter;
import com.candeo.app.home.HomeActivity;
import com.candeo.app.util.CandeoUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderBoardFragment extends Fragment {


    private View root = null;


    //More contents created till now
    private ListView candeoRestContentView = null;
    private static final String TAG="Candeo - Leaderboard";
    //No Content
    private View noContent  = null;
    private View loadingContent = null;
    private RecyclerView performancesList;
    private LeaderboardAdapter mLeaderboardAdapter;
    private LinearLayoutManager performanceListLayoutManager;
    private List<HashMap<String,String>> morePerformances = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_leader_board, container, false);
        initWidgets();
        return root;
    }

    private void initWidgets()
    {
        noContent = root.findViewById(R.id.candeo_leaderboard_no_content);
        ((TextView)noContent.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        ((TextView)noContent.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_STATS);
        ((TextView)noContent.findViewById(R.id.candeo_no_content_text)).setText("Sorry! No Performances yet...");

        loadingContent = root.findViewById(R.id.candeo_data_loading);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(),"fonts/fa.ttf"));
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setText(Configuration.FA_STATS);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_text)).setText("Loading Performances...");

        toggleView(loadingContent,true);
        toggleView(noContent,false);


        performancesList= (RecyclerView)root.findViewById(R.id.candeo_performance_list);
        performanceListLayoutManager = new LinearLayoutManager(getActivity());
        performanceListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        performancesList.setLayoutManager(performanceListLayoutManager);
        if(mLeaderboardAdapter == null)
        {
              for(int i=0; i<5;i++) //testing
              {
                  morePerformances.add(new HashMap<String, String>());
              }
              mLeaderboardAdapter = new LeaderboardAdapter((HomeActivity)getActivity(),null,morePerformances);
        }
        else
        {
               mLeaderboardAdapter.notifyDataSetChanged();
        }
        performancesList.setAdapter(mLeaderboardAdapter);


    }

    public void onGetLeaderBoardComplete(JSONObject response)
    {
        if(response!=null)
        {
            if(response.length()>0)
            {
                noContent.setVisibility(View.GONE);
            }
            else
            {
                noContent.setVisibility(View.VISIBLE);
            }
        }

    }

    private void toggleView(View view, boolean show)
    {
        view.setVisibility(show?View.VISIBLE:View.GONE);
    }




}
