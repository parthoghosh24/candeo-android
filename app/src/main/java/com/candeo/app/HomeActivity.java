package com.candeo.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.candeo.app.util.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class HomeActivity extends ActionBarActivity {

    ListView feedView;
    FeedAdapter feedAdapter;
    Typeface freeBooter;
    ArrayList<HashMap<String, String>> feeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        freeBooter= loadFontFreeBooter();
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView title = (TextView) findViewById(titleId);
        setTitle("Candeo");
        getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        title.setTypeface(freeBooter, Typeface.BOLD);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        feedView = (ListView)findViewById(R.id.feed_list);
        feeds = new ArrayList<HashMap<String, String>>();
        new LoadFeeds().execute("http://192.168.0.104:3000/api/v1/contents");
    }

    private class LoadFeeds extends AsyncTask<String, String, JSONObject>{

        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeActivity.this);
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
            try {
                JSONArray array = jsonObject.getJSONArray("contents");
                for(int index=0; index< array.length(); ++index)
                {
                  JSONObject content = array.getJSONObject(index);
                  HashMap<String, String> feedMap = new HashMap<String, String>();
                  feedMap.put("desc", content.optString("desc"));
                  feedMap.put("username", content.optString("username"));
                  feedMap.put("timestamp", content.optString("time"));
                  feeds.add(feedMap);
                  feedAdapter= new FeedAdapter(HomeActivity.this);
                  feedView.setAdapter(feedAdapter);
                }
                System.out.println("Feeds Size in post exec: "+feeds.size());
            }catch (JSONException je)
            {
                je.printStackTrace();
            }



        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Typeface loadFontFreeBooter()
    {
        Typeface fontAwesome = Typeface.createFromAsset(getAssets(),"freebooter.ttf");
        return  fontAwesome;
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
