package com.candeo.app.leaderboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.candeo.app.util.CandeoUtil;

import org.json.JSONObject;

public class LeaderBoardFragment extends Fragment {


    private View root = null;

    //Top 5 last week contents
    private LinearLayout candeoTopContent1 = null;
    private LinearLayout candeoTopContent2 = null;
    private LinearLayout candeoTopContent3 = null;
    private LinearLayout candeoTopContent4 = null;
    private LinearLayout candeoTopContent5 = null;

    //Top 3 Last week creators
    private CardView candeoTopCreator1 = null;
    private CardView candeoTopCreator2 = null;
    private CardView candeoTopCreator3 = null;

    //More contents created till now
    private ListView candeoRestContentView = null;
    private static final String TAG="Candeo - Leaderboard";
    //No Content
    private View noContent  = null;
    private static String getPerformanceMoreListApi = Configuration.BASE_URL+"/api/v1/contents/performances/list/";

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




}
