package com.candeo.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.shout.ShoutFragment;
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
public class ShoutNetworkAdapter extends RecyclerView.Adapter<ShoutNetworkAdapter.ShoutNetworkHolder>{

    private List<HashMap<String, String>> networkList = new ArrayList<>();
    private Context mContext;
    private static SparseBooleanArray selectedList;
    private static ShoutNetworkAdapter adapterInstance;

    public ShoutNetworkAdapter(Context mContext,List<HashMap<String, String>> networkList)
    {
        this.networkList=networkList;
        this.mContext=mContext;
        this.selectedList = new SparseBooleanArray();
        adapterInstance=this;
    }

    @Override
    public ShoutNetworkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shout_network_item,parent,false);
        return new ShoutNetworkHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(final ShoutNetworkHolder holder, final int position) {
        holder.name.setText(networkList.get(position).get("name"));
        holder.bio.setText(networkList.get(position).get("bio"));
        holder.view.setTag(networkList.get(position).get("id"));
        holder.selected.setTypeface(CandeoUtil.loadFont(mContext.getAssets(), "fonts/fa.ttf"));
        holder.selected.setText(Configuration.FA_CHECK_CIRCLE);
        new LoadImageTask(holder.userAvatar).execute(networkList.get(position).get("avatar_path"));
        if(selectedList.get(position))
        {
            holder.selected.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.selected.setVisibility(View.GONE);
        }
    }



    @Override
    public int getItemCount() {
        return networkList.size();
    }

    public static class ShoutNetworkHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public CircleImageView userAvatar;
        public TextView name, bio, selected;
        public View view;
        public ShoutNetworkHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            name = (TextView)itemLayoutView.findViewById(R.id.candeo_shout_network_user_name);
            bio = (TextView)itemLayoutView.findViewById(R.id.candeo_shout_network_user_bio);
            selected = (TextView)itemLayoutView.findViewById(R.id.candeo_shout_network_user_selector);
            userAvatar = (CircleImageView)itemLayoutView.findViewById(R.id.candeo_shout_network_user_avatar);
            view = itemLayoutView;
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getPosition();
            Log.e("shoutntwrkadptr","POS IS :"+position);
            if(!selectedList.get(position))
            {
                selectedList.put(position,true);
                ShoutFragment.networkIdList.add(view.getTag().toString());
            }
            else
            {
                selectedList.put(position,false);
                ShoutFragment.networkIdList.remove(view.getTag().toString());
            }
            if(selectedList.size()>0)
            {
                adapterInstance.notifyItemChanged(position);
            }

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
