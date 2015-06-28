package com.candeo.app.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.candeo.app.R;
import com.candeo.app.util.Preferences;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by partho on 27/6/15.
 */
public class ShoutDiscussionsAdapter extends RecyclerView.Adapter<ShoutDiscussionsAdapter.ShoutContentHolder>{

    private List<HashMap<String, String>> discussions = new ArrayList<>();
    private Context mContext;

    public ShoutDiscussionsAdapter(Context mContext)
    {
        this.mContext=mContext;
    }

    @Override
    public ShoutContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shout_content_disscussion_item,parent,false);
        return new ShoutContentHolder(itemLayoutView);
    }

    @Override
    public int getItemCount() {
        return discussions.size();
    }

    public void addAllToList(List<HashMap<String, String>> items, boolean append)
    {
        if(!append)
        {
            discussions.clear();
            discussions.addAll(items);
            notifyDataSetChanged();
        }
        else
        {

            int lastPosition = discussions.size();
            discussions.addAll(items);
            notifyItemRangeInserted(lastPosition,items.size());
        }

    }

    public int updateList(HashMap<String,String> discussion)
    {
        discussions.add(discussion);
        notifyItemInserted(discussions.size());
        return discussions.size();
    }



    @Override
    public void onBindViewHolder(ShoutDiscussionsAdapter.ShoutContentHolder holder, int position) {

        holder.discussionText.setText(discussions.get(position).get("discussion"));
        holder.discussionUserName.setText(discussions.get(position).get("name"));
        if(Preferences.getUserRowId(mContext).equalsIgnoreCase(discussions.get(position).get("user_id")))
        {
            holder.dummyCard.setVisibility(View.GONE);
            holder.discussionCard.setBackgroundColor(mContext.getResources().getColor(R.color.candeo_light_btn_blue));
            holder.discussionText.setTextColor(mContext.getResources().getColor(R.color.candeo_white));
            holder.discussionUserName.setTextColor(mContext.getResources().getColor(R.color.candeo_accent_light));
        }
        else
        {
            holder.dummyCard.setVisibility(View.VISIBLE);
            holder.discussionCard.setBackgroundColor(mContext.getResources().getColor(R.color.candeo_white));
            holder.discussionText.setTextColor(mContext.getResources().getColor(R.color.candeo_light_btn_blue));
            holder.discussionUserName.setTextColor(mContext.getResources().getColor(R.color.candeo_light_btn_blue));
        }


    }


    public static class ShoutContentHolder extends RecyclerView.ViewHolder
    {

        //Discussion item
        public TextView discussionUserName, discussionText;
        public CardView dummyCard, discussionCard;

        public ShoutContentHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            discussionUserName=(TextView)itemLayoutView.findViewById(R.id.candeo_shout_discussion_name);
            discussionText=(TextView)itemLayoutView.findViewById(R.id.candeo_shout_discussion_message);
            dummyCard=(CardView)itemLayoutView.findViewById(R.id.candeo_shout_discussion_dummy_card);
            discussionCard=(CardView)itemLayoutView.findViewById(R.id.candeo_shout_discussion_card);
        }
    }
}
