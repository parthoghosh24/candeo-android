package com.candeo.app.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.candeo.app.ContentActivity;
import com.candeo.app.R;
import com.candeo.app.util.JSONParser;
import com.melnykov.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeFragment extends Fragment {

    ListView feedView;
    SwipeRefreshLayout refreshView;
    FeedAdapter feedAdapter;
    ArrayList<HashMap<String, String>> feeds;
    private String domain="http://192.168.0.104:3000";
    private String feedsURL = domain+"/api/v1/contents";
    FloatingActionButton fab;
    View homeView=null;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(!isNetworkAvailable())
        {

            homeView= inflater.inflate(R.layout.fragment_no_connectivity, container, false);
        }
        else
        {
            homeView= inflater.inflate(R.layout.fragment_home, container, false);
            feedView = (ListView)homeView.findViewById(R.id.feed_list);
            fab=(FloatingActionButton)homeView.findViewById(R.id.fab);
            fab.setColorNormal(getResources().getColor(R.color.material_blue_500));
            fab.setColorPressed(getResources().getColor(R.color.material_blue_500));
            fab.attachToListView(feedView);
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
            refreshView.setColorSchemeColors(R.color.material_blue_600, R.color.material_blue_500);
            feeds = new ArrayList<HashMap<String, String>>();
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

        }
        return homeView;
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
                        feedAdapter= new FeedAdapter(getActivity());
                        feedView.setAdapter(feedAdapter);
                    }
                    System.out.println("Feeds Size in post exec: "+feeds.size());
                }catch (JSONException je)
                {
                    je.printStackTrace();
                }
            }

        }
    }




    class FeedAdapter extends BaseAdapter
    {

        Activity activity;

        FeedAdapter(Activity activity)
        {
            this.activity=activity;
        }

        @Override
        public int getCount() {
            System.out.println("Feeds Size: "+feeds.size());
            return feeds.size();
        }

        class ViewHolder
        {
            TextView description;
            TextView username;
            TextView time;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            HashMap<String, String> feed = feeds.get(position);
            if(convertView == null)
            {
                convertView= LayoutInflater.from(activity).inflate(R.layout.feed_item, parent, false);
                holder = new ViewHolder();
                holder.description = (TextView)convertView.findViewById(R.id.content_description);
                holder.username = (TextView)convertView.findViewById(R.id.username);
                holder.time = (TextView)convertView.findViewById(R.id.timestamp);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.description.setText(feed.get("desc"));
            holder.username.setText(feed.get("username"));
            holder.time.setText(feed.get("timestamp"));
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return feeds.get(position);
        }
    }



}
