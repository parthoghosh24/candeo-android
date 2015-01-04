package com.candeo.app.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.candeo.app.R;
import com.candeo.app.util.CandeoUtil;

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
        TextView name;
        TextView time;
        TextView mediaIcon;
        TextView inspirationIcon;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        HashMap<String, String> feed = feeds.get(position);
        if(convertView == null)
        {
            convertView= LayoutInflater.from(activity).inflate(R.layout.feed_item, parent, false);
            holder = new ViewHolder();
            holder.description = (TextView)convertView.findViewById(R.id.candeo_content_description);
            holder.name= (TextView)convertView.findViewById(R.id.name);
            holder.time = (TextView)convertView.findViewById(R.id.timestamp);
            holder.mediaIcon =(TextView)convertView.findViewById(R.id.candeo_media_icon);
            holder.mediaIcon.setTypeface(CandeoUtil.loadFont(activity.getAssets(),"fonts/fa.ttf"));
            holder.inspirationIcon = (TextView)convertView.findViewById(R.id.candeo_inspire_icon);
            holder.inspirationIcon.setTypeface(CandeoUtil.loadFont(activity.getAssets(),"fonts/response.ttf"));
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.description.setText(feed.get("desc"));
        holder.name.setText(feed.get("name"));
        holder.time.setText(feed.get("timestamp"));
        switch (Integer.parseInt(feed.get("media_type")))
        {
            case 0: //No Media
                holder.mediaIcon.setText("");
                break;
            case 1://Audio
                holder.mediaIcon.setText("\uf001");
                break;
            case 2://Video
                holder.mediaIcon.setText("\uf008");
                break;
            case 3: //Image
                holder.mediaIcon.setText("\uf030");
                break;
            case 4: //Book
                holder.mediaIcon.setText("\uf02d");
                break;
        }
        holder.inspirationIcon.setText("\ue800");
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
