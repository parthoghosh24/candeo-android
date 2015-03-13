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

import com.android.volley.NetworkResponse;
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

import org.json.JSONArray;
import org.json.JSONException;
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
    private final static String GET_MORE_PERFORMANCES_API=Configuration.BASE_URL+"/api/v1/contents/performances/list/%s";
    private final static String FIRST_MORE_PERFORMANCE_RANK="5";

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

        performancesList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //Here to perform more fetch
                super.onScrolled(recyclerView, dx, dy);
            }
        });


    }

    public void onGetLeaderBoardComplete(JSONObject response)
    {
        if(response!=null)
        {
            if(response.length()>0)
            {
                noContent.setVisibility(View.GONE);
                if(mLeaderboardAdapter == null)
                {
                    mLeaderboardAdapter = new LeaderboardAdapter((HomeActivity)getActivity(),null,morePerformances);
                }
                else
                {
                    mLeaderboardAdapter.notifyDataSetChanged();
                }
                performancesList.setAdapter(mLeaderboardAdapter);
                CandeoApplication.getInstance().getAppRequestQueue().add(new GetMorePerformancesRequest(FIRST_MORE_PERFORMANCE_RANK,false));
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

    private class GetMorePerformancesRequest extends JsonObjectRequest
    {
        public GetMorePerformancesRequest(final String rank, final boolean append)
        {
            super(Method.GET,
                    String.format(GET_MORE_PERFORMANCES_API,rank),
                    null,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            if(response!=null)
                            {
                                try {
                                    JSONArray list = response.getJSONArray("performances");
                                    if(list.length()>0)
                                    {
                                        for(int index=0; index<list.length(); ++index)
                                        {
                                            JSONObject performance = list.getJSONObject(index);
                                        }
                                        //Populate morePerformances
                                        mLeaderboardAdapter.addAllToMorePerformances(morePerformances,append);
                                        mLeaderboardAdapter.notifyDataSetChanged();
                                    }
                                }
                                catch (JSONException jse)
                                {
                                    jse.printStackTrace();
                                }

                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG,"Error occured");
                            NetworkResponse response = error.networkResponse;
                            if(response!=null)
                            {
                                Log.e(TAG,"Server errot response while fetching performances "+new String(response.data));
                            }
                        }
                    });
        }
    }




}
