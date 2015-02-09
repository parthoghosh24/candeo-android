package com.candeo.app.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.adapters.LimelightAdapter;
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
    private ArrayList<HashMap<String, String>> showcases = new ArrayList<>();
//    private ShowcaseAdapter pagerAdapter;
    private LimelightAdapter pagerAdapter;
    View homeView=null;
    private static final String TAG="Candeo - HomeFrag";

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

            homeView= inflater.inflate(R.layout.fragment_home, container, false);
            parentHomePager=(ViewPager)getActivity().findViewById(R.id.home_pager);
            showcasePager = (NonSwipeablePager)homeView.findViewById(R.id.candeo_showcase_pager);
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
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onGetLimelightComplete(JSONObject response)
    {
        if(Configuration.DEBUG)
        {
            Log.e(TAG,"Getting limelight list");
        }
        try {
            JSONArray list = response.getJSONArray("limelights");
            showcases.clear();
            for(int index=0; index<list.length();++index)
            {
                JSONObject limelight = list.getJSONObject(index);
                HashMap<String, String> limelightMap = new HashMap<>();
                limelightMap.put("id",Integer.toString(limelight.getInt("id")));
                showcases.add(limelightMap);
            }
            HashMap<String, String> limelightMap = new HashMap<>();
            limelightMap.put("id","-1"); //End marker
            showcases.add(limelightMap);
            if(showcases!=null && showcases.size()>0)
            {
               pagerAdapter = new LimelightAdapter(showcasePager,getActivity().getSupportFragmentManager(),showcases);
                showcasePager.setAdapter(pagerAdapter);
//               pagerAdapter.notifyDataSetChanged();
            }
        }
        catch (JSONException jse)
        {
            jse.printStackTrace();
        }




    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }








}
