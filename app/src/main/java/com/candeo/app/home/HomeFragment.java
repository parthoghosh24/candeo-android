package com.candeo.app.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.candeo.app.adapters.FeedAdapter;
import com.candeo.app.content.ContentActivity;
import com.candeo.app.R;
import com.candeo.app.content.PostActivity;
import com.candeo.app.util.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    ListView feedView;
    ViewPager parentHomePager;
    Button inspire;
    Button feed;
    Button user;
    SwipeRefreshLayout refreshView;
    FeedAdapter feedAdapter;
    ArrayList<HashMap<String, String>> feeds;
    private String domain="http://192.168.0.104:3000";
    private String feedsURL = domain+"/api/v1/contents";
    View homeView=null;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {


        if(!isNetworkAvailable())
        {

            homeView= inflater.inflate(R.layout.fragment_no_connectivity, container, false);
        }
        else
        {
            homeView= inflater.inflate(R.layout.fragment_home, container, false);
            parentHomePager=(ViewPager)getActivity().findViewById(R.id.home_pager);
            feedView = (ListView)homeView.findViewById(R.id.feed_list);
            feeds = new ArrayList<HashMap<String, String>>();
            feedAdapter= new FeedAdapter(getActivity(),feeds);
            feedView.setAdapter(feedAdapter);
            inspire = (Button)homeView.findViewById(R.id.candeo_init_post);
            feed=(Button)homeView.findViewById(R.id.candeo_feed);
            user=(Button)homeView.findViewById(R.id.candeo_user);
            refreshView = (SwipeRefreshLayout)homeView.findViewById(R.id.home_list_refresh);
            refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        @Override public void run() {
                            refreshView.setRefreshing(false);
                        }
                    }, 5000);
                }
            });
            inspire.setTypeface(loadFont("fa.ttf"));
            inspire.setText("\uf0d0");
            inspire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postIntent = new Intent(getActivity(),PostActivity.class);
                    startActivity(postIntent);
                }
            });
            feed.setTypeface(loadFont("fa.ttf"));
            feed.setText("\uf09e");
            feed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parentHomePager.setCurrentItem(0);
                }
            });
            user.setTypeface(loadFont("fa.ttf"));
            user.setText("\uf007");
            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parentHomePager.setCurrentItem(2);
                }
            });
            refreshView.setColorSchemeColors(R.color.material_blue_grey_800, R.color.material_blue_grey_900);

            new LoadFeeds().execute(feedsURL);
            feedView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    HashMap<String, String> feed = feeds.get(position);
                    Intent contentIntent = new Intent(getActivity(),ContentActivity.class);
                    System.out.println("PUSHIN ID IS :"+feed.get("id"));
                    contentIntent.putExtra("contentId",feed.get("id"));
                    startActivity(contentIntent);
                }
            });
            feedView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    boolean enable =false;
                    if(feedView != null && feedView.getChildCount() > 0){
                        // check if the first item of the list is visible
                        boolean firstItemVisible = feedView.getFirstVisiblePosition() == 0;
                        // check if the top of the first item is visible
                        boolean topOfFirstItemVisible = feedView.getChildAt(0).getTop() == 0;
                        // enabling or disabling the refresh layout
                        enable = firstItemVisible && topOfFirstItemVisible;
                    }
                    refreshView.setEnabled(enable);

                }
            });

        }
        return homeView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
    }

    private class LoadFeeds extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Fetching Inspiritions...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            return JSONParser.parseGET(urls[0]);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            pDialog.dismiss();
            if(jsonObject!=null)
            {
                try {
                    JSONArray array = jsonObject.getJSONArray("contents");
                    for(int index=0; index< array.length(); ++index)
                    {
                        JSONObject content = array.getJSONObject(index);
                        HashMap<String, String> feedMap = new HashMap<String, String>();
                        feedMap.put("id", content.optString("id"));
                        feedMap.put("desc", content.optString("desc"));
                        feedMap.put("username", content.optString("username"));
                        feedMap.put("timestamp", content.optString("time"));
                        feeds.add(feedMap);


                    }
                    System.out.println("Feeds Size in post exec: "+feeds.size());
                    feedAdapter.notifyDataSetChanged();
                }catch (JSONException je)
                {
                    je.printStackTrace();
                }
            }

        }
    }

    private Typeface loadFont(String fontFile)
    {
        return Typeface.createFromAsset(getActivity().getAssets(),fontFile);
    }




}
