package com.candeo.app.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.adapters.ShowcaseAdapter;
import com.candeo.app.R;
import com.candeo.app.content.PostActivity;
import com.candeo.app.transformers.ShowcaseTransformer;
import com.candeo.app.ui.NonSwipeablePager;
import com.candeo.app.user.LoginActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.JSONParser;
import com.candeo.app.util.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    private ViewPager parentHomePager;
    private NonSwipeablePager showcasePager;
    private Button inspire;
    private Button feed;
    private Button user;
    private String limelightUrl = Configuration.BASE_URL +"/api/v1/contents/limelight/";
    private String lastTimestamp =null;
    private ArrayList<LimelightFragment> showcases = new ArrayList<>();
    View homeView=null;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

            homeView= inflater.inflate(R.layout.fragment_home, container, false);
            parentHomePager=(ViewPager)getActivity().findViewById(R.id.home_pager);
            showcasePager = (NonSwipeablePager)homeView.findViewById(R.id.candeo_showcase_pager);
            showcasePager.setAdapter(new ShowcaseAdapter(getActivity(),showcasePager));
            showcasePager.setPageTransformer(true, new ShowcaseTransformer());
            inspire = (Button)homeView.findViewById(R.id.candeo_init_post);
            feed=(Button)homeView.findViewById(R.id.candeo_feed);
            user=(Button)homeView.findViewById(R.id.candeo_user);
            inspire.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
            inspire.setText(Configuration.FA_MAGIC);
            inspire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postIntent=null;
                    if(Preferences.isUserLoggedIn(getActivity()))
                    {
                        postIntent = new Intent(getActivity(),PostActivity.class);
                        postIntent.putExtra("type","showcase");

                    }
                    else
                    {
                        postIntent = new Intent(getActivity(),LoginActivity.class);
                    }
                    startActivity(postIntent);

                }
            });
            feed.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
            feed.setText(Configuration.FA_STATS);
            feed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parentHomePager.setCurrentItem(0);
                }
            });
            user.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
            user.setText(Configuration.FA_USER);
            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Preferences.isUserLoggedIn(getActivity()))
                    {
                        parentHomePager.setCurrentItem(2);
                    }
                    else
                    {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }

                }
            });

        return homeView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private class FetchLimelight extends JsonObjectRequest
    {
        public FetchLimelight(String url)
        {
            super(
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            lastTimestamp = response.getString("last_timestamp");
                            JSONArray list =response.getJSONArray("list");


                        }
                        catch (JSONException jse)
                        {
                            jse.printStackTrace();
                        }

                    }
                },
               new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {

                   }
               }
            );
        }
    }






}
