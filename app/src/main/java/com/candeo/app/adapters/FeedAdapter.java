package com.candeo.app.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.candeo.app.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Partho on 23/11/14.
 */
public class FeedAdapter extends BaseAdapter
{

    Activity activity;
    ArrayList<HashMap<String, String>> feeds;

    public FeedAdapter(Activity activity, ArrayList<HashMap<String, String>> feeds)
    {

        this.activity=activity;
        this.feeds=feeds;
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
