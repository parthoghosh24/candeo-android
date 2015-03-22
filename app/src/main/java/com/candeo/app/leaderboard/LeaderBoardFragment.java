package com.candeo.app.leaderboard;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.adapters.LeaderboardAdapter;
import com.candeo.app.algorithms.Security;
import com.candeo.app.home.HomeActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderBoardFragment extends Fragment {


    private View root = null;


    private static final String TAG="Candeo - Leaderboard";
    //No Content
    private View noContent  = null;
    private View loadingContent = null;
    private RecyclerView performancesList;
    private LeaderboardAdapter mLeaderboardAdapter;
    private LinearLayoutManager performanceListLayoutManager;
    private ArrayList<HashMap<String,String>> morePerformances = new ArrayList<>();
    private final static String GET_MORE_PERFORMANCES_RELATIVE_API="/contents/performances/list/%s";
    private final static String GET_MORE_PERFORMANCES_API=Configuration.BASE_URL+"/api/v1"+GET_MORE_PERFORMANCES_RELATIVE_API;
    private final static String FIRST_MORE_PERFORMANCE_RANK="5";
    private String lastRank="";
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_leader_board, container, false);
        pastVisiblesItems=visibleItemCount=totalItemCount=0;
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
                visibleItemCount = performanceListLayoutManager.getChildCount();
                totalItemCount = performanceListLayoutManager.getItemCount();
                pastVisiblesItems = performanceListLayoutManager.findFirstVisibleItemPosition();
                //TODO Not working, need to see
                if (loading) {
                    Log.e(TAG,"in loading");
                    if ( (visibleItemCount+pastVisiblesItems) >= totalItemCount) {
                        loading = false;
                        loadMore();
                    }
                }
            }
        });


    }

    public void onGetLeaderBoardComplete(JSONObject response)
    {
        if(response!=null)
        {
            if(response.length()>0)
            {
                toggleView(loadingContent,false);
                toggleView(noContent,false);
                noContent.setVisibility(View.GONE);
                if(mLeaderboardAdapter==null)
                {
                    mLeaderboardAdapter = new LeaderboardAdapter((HomeActivity)getActivity(),response);
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
                toggleView(loadingContent,false);
                toggleView(noContent, true);
            }
        }

    }

    private void loadMore()
    {
        Log.e(TAG,"last rank "+lastRank);
        CandeoApplication.getInstance().getAppRequestQueue().add(new GetMorePerformancesRequest(lastRank,true));
    }

    private void toggleView(View view, boolean show)
    {
        view.setVisibility(show?View.VISIBLE:View.GONE);
    }

    private class GetMorePerformancesRequest extends JsonObjectRequest
    {
        private String rank;
        public GetMorePerformancesRequest(final String rank, final boolean append)
        {
            super(Method.GET,
                    String.format(GET_MORE_PERFORMANCES_API,rank),
                    new JSONObject(),
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            if(response!=null)
                            {
                                toggleView(loadingContent,false);
                                toggleView(noContent,false);
                                try {
                                    JSONArray list = response.getJSONArray("performances");
//                                    if(!append)
//                                    {
                                        morePerformances.clear();
//                                    }
                                    if(list.length()>0)
                                    {
                                        for(int index=0; index<list.length(); ++index)
                                        {
                                            JSONObject performance = list.getJSONObject(index);
                                            HashMap<String,String> performanceMap = new HashMap<>();
                                            performanceMap.put("showcase_id",performance.getString("showcase_id"));
                                            performanceMap.put("showcase_title",performance.getString("showcase_title"));
                                            performanceMap.put("showcase_media_type",performance.getString("showcase_media_type"));
                                            performanceMap.put("showcase_total_appreciations",performance.getString("showcase_total_appreciations"));
                                            performanceMap.put("showcase_rank",performance.getString("showcase_rank"));
                                            performanceMap.put("showcase_bg_url",performance.getString("bg_url"));
                                            performanceMap.put("showcase_media_url",performance.getString("media_url"));
                                            performanceMap.put("showcase_user_name",performance.getString("name"));
                                            performanceMap.put("showcase_user_avatar_url",performance.getString("user_avatar_url"));
                                            if(index == list.length()-1)
                                            {
                                                lastRank=performance.getString("showcase_rank");
                                            }
                                            morePerformances.add(performanceMap);
//                                            Log.e(TAG, "More Performances len "+morePerformances.size());
                                        }
                                        //Populate morePerformances
                                        Log.e(TAG,"List size is b4 add "+morePerformances.size());
                                        Log.e(TAG,"append is "+append);
                                        mLeaderboardAdapter.addAllToMorePerformances(morePerformances,append);
                                        Log.e(TAG,"List size is "+mLeaderboardAdapter.morePerformances.size());
                                        mLeaderboardAdapter.notifyDataSetChanged();
//                                        performancesList.setAdapter(mLeaderboardAdapter);
                                    }
                                }
                                catch (JSONException jse)
                                {
                                    jse.printStackTrace();
                                    Log.e(TAG, "error is "+jse.getLocalizedMessage());
                                    toggleView(loadingContent,false);
                                    toggleView(noContent,true);
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
                                Log.e(TAG,"Server error response while fetching performances "+new String(response.data));
                            }
                            toggleView(loadingContent,false);
                            if(morePerformances.size()==0)
                            {
                                toggleView(noContent,true);
                            }

                        }
                    });
            this.rank=rank;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            String secret="";
            if (Preferences.isUserLoggedIn(getActivity()) && !TextUtils.isEmpty(Preferences.getUserEmail(getActivity()))) {
                params.put("email", Preferences.getUserEmail(getActivity()));
                secret=Preferences.getUserApiKey(getActivity());

            } else {
                params.put("email", "");
                secret=Configuration.CANDEO_DEFAULT_SECRET;
            }
            String message = String.format(GET_MORE_PERFORMANCES_RELATIVE_API,rank);
            params.put("message", message);
            Log.e(TAG,"secret->"+secret);
            String hash = Security.generateHmac(secret, message);
            Log.e(TAG,"hash->"+hash);
            params.put("Authorization", "Token token=" + hash);
            return params;
        }
    }




}
