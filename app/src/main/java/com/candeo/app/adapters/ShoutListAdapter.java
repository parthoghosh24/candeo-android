package com.candeo.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.shout.ShoutActivity;
import com.candeo.app.util.CandeoUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by partho on 18/6/15.
 */
public class ShoutListAdapter extends RecyclerView.Adapter<ShoutListAdapter.ShoutListHolder>{


    private List<HashMap<String, Object>> shoutList = new ArrayList<>();
    private Context mContext;
    private static SparseBooleanArray selectedList;

    public ShoutListAdapter(Context mContext,List<HashMap<String, Object>> shoutList)
    {
        this.shoutList = shoutList;
        this.mContext=mContext;
        this.selectedList = new SparseBooleanArray();
    }

    public void refreshList(List<HashMap<String, Object>> shoutList)
    {
        this.shoutList.clear();
        this.shoutList.addAll(shoutList);
    }

    @Override
    public ShoutListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shout_list_item,parent,false);
        return new ShoutListHolder(itemLayoutView,mContext);
    }

    @Override
    public void onBindViewHolder(final ShoutListHolder holder, final int position) {
        holder.name.setText(shoutList.get(position).get("name").toString());
        holder.body.setText(shoutList.get(position).get("body").toString());
        holder.view.setTag(shoutList.get(position).get("id"));
        holder.timestamp.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(shoutList.get(position).get("timestamp").toString()), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
        holder.typeIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(), "fonts/fa.ttf"));
        if(!((boolean) shoutList.get(position).get("is_public")))
        {
            holder.typeIcon.setText(Configuration.FA_LOCK);
            holder.typeIcon.setTextColor(mContext.getResources().getColor(R.color.candeo_private_red));
            holder.typeText.setText("PRIVATE");
            holder.typeText.setTextColor(mContext.getResources().getColor(R.color.candeo_private_red));

        }
        else
        {
            holder.typeIcon.setText(Configuration.FA_UNLOCK);
            holder.typeIcon.setTextColor(mContext.getResources().getColor(R.color.candeo_checked_green));
            holder.typeText.setText("PUBLIC");
            holder.typeText.setTextColor(mContext.getResources().getColor(R.color.candeo_checked_green));

        }
        new LoadImageTask(holder.userAvatar).execute(shoutList.get(position).get("avatar_path").toString());

    }



    @Override
    public int getItemCount() {
        return shoutList.size();
    }

    public static class ShoutListHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public CircleImageView userAvatar;
        public TextView name, body,typeIcon, typeText,timestamp;
        public View view;
        private Context mContext;
        public ShoutListHolder(View itemLayoutView, Context mContext)
        {
            super(itemLayoutView);
            name = (TextView)itemLayoutView.findViewById(R.id.candeo_shout_list_user_name);
            body = (TextView)itemLayoutView.findViewById(R.id.candeo_shout_list_body_text);
            typeIcon =(TextView)itemLayoutView.findViewById(R.id.candeo_shout_list_type);
            typeText=(TextView)itemLayoutView.findViewById(R.id.candeo_shout_list_type_text);
            timestamp=(TextView)itemLayoutView.findViewById(R.id.candeo_shout_list_timestamp);
            userAvatar = (CircleImageView)itemLayoutView.findViewById(R.id.candeo_shout_list_user_avatar);
            view = itemLayoutView;
            this.mContext=mContext;
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
              Intent intent = new Intent(mContext, ShoutActivity.class);
              intent.putExtra("id",view.getTag().toString());
              mContext.startActivity(intent);
        }
    }

    private class LoadImageTask extends AsyncTask<String, String, Bitmap> {

        private ImageView image;

        public LoadImageTask(ImageView image)
        {
            this.image=image;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL imageUrl= new URL(params[0]);
                bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null)
            {
                if(mContext!=null)
                {
                    Animation in = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
                    image.startAnimation(in);
                }
                image.setImageBitmap(bitmap);
            }
        }
    }
}
