package com.candeo.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.content.ContentActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Partho on 1/3/15.
 */
public class UserPagerAdapter extends RecyclerView.Adapter<UserPagerAdapter.VariousUserViewHolder> {

    public static final int CREATED=1;
    public static final int FANS=2;
    public static final int PROMOTED=3;
    public static final int APPRECIATIONS=4;
    public static final int INSPIRATIONS=5;
    public int type=CREATED;
    private List<HashMap<String, String>> list;
    private Context mContext;

    public UserPagerAdapter(List<HashMap<String, String>> list, int type, Context mContext)
    {
        this.list=list;
        this.type=type;
        this.mContext=mContext;
    }
    @Override
    public VariousUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(getLayoutForType(type),parent,false);
        return new VariousUserViewHolder(itemLayoutView,mContext,type);
    }

    private int getLayoutForType(int type)
    {
        Log.e("USER ADAPTER","type is "+type);
        switch (type)
        {
            case CREATED:
                return R.layout.content_item;
            case APPRECIATIONS:
            case INSPIRATIONS:
                 return R.layout.condensed_content_item;
            case FANS:
            case PROMOTED:
                 return R.layout.user_item;
        }
        return R.layout.content_item;
    }
    @Override
    public void onBindViewHolder(final VariousUserViewHolder holder, int position) {


        if(type==FANS || type==PROMOTED)
        {
            holder.view.setTag(list.get(position).get("id"));
            holder.userName.setText(list.get(position).get("user_name"));
            new LoadImageTask(holder.userAvatar).execute(list.get(position).get("avatar_path"));
        }

        if(type == CREATED)
        {
            holder.title.setText(list.get(position).get("title"));
            holder.rankValue.setText(list.get(position).get("rank"));
            holder.appreciationValue.setText(list.get(position).get("appreciation_count"));
            holder.appreciateIcon.setText(Configuration.FA_APPRECIATE);
            holder.inspirationValue.setText(list.get(position).get("inspiration_count"));
            holder.inspirationIcon.setText(Configuration.FA_INSPIRE);
            holder.date.setText(list.get(position).get("created_at_text"));
            int mediaType=Integer.parseInt(list.get(position).get("media_type"));
            if(Configuration.AUDIO == mediaType)
            {
                holder.mediaIcon.setText(Configuration.FA_AUDIO);
            }
            else if(Configuration.IMAGE == mediaType)
            {
                holder.mediaIcon.setText(Configuration.FA_IMAGE);
            }
            holder.view.setTag(list.get(position).get("id"));
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( holder.view.getTag()!=null && !TextUtils.isEmpty( holder.view.getTag().toString()) && !"-1".equalsIgnoreCase( holder.view.getTag().toString()) )
                    {
                        Intent contentIntent= new Intent(mContext, ContentActivity.class);
                        contentIntent.putExtra("id", holder.view.getTag().toString());
                        contentIntent.putExtra("type",Configuration.SHOWCASE);
                        mContext.startActivity(contentIntent);
                    }
                }
            });
            new LoadImageTask(holder.bgImage).execute(list.get(position).get("bg_url"));

        }
        if(type == APPRECIATIONS)
        {
            holder.userName.setText(list.get(position).get("user_name"));
            holder.rankValue.setText(list.get(position).get("rank"));
            holder.appreciationValue.setText(list.get(position).get("appreciation_count"));
            holder.appreciateIcon.setText(Configuration.FA_APPRECIATE);
            holder.date.setText(list.get(position).get("created_at_text"));
            int mediaType=Integer.parseInt(list.get(position).get("media_type"));
            if(Configuration.AUDIO == mediaType)
            {
                holder.mediaIcon.setText(Configuration.FA_AUDIO);
            }
            else if(Configuration.IMAGE == mediaType)
            {
                holder.mediaIcon.setText(Configuration.FA_IMAGE);
            }
            holder.view.setTag(list.get(position).get("id"));
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( holder.view.getTag()!=null && !TextUtils.isEmpty( holder.view.getTag().toString()) && !"-1".equalsIgnoreCase( holder.view.getTag().toString()) )
                    {
                        Intent contentIntent= new Intent(mContext, ContentActivity.class);
                        contentIntent.putExtra("id", holder.view.getTag().toString());
                        contentIntent.putExtra("type",Configuration.SHOWCASE);
                        mContext.startActivity(contentIntent);
                    }
                }
            });
            new LoadImageTask(holder.bgImage).execute(list.get(position).get("bg_url"));
            new LoadImageTask(holder.userAvatar).execute(list.get(position).get("avatar_path"));


        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class VariousUserViewHolder extends RecyclerView.ViewHolder
    {

        private Context mContext;
        //Created
        private TextView title;
        private TextView inspirationIcon;
        private TextView inspirationValue;
        //Social
        //Discovery

        public CircleImageView userAvatar;
        public ImageView bgImage;
        public TextView userName;
        public TextView appreciateIcon;
        public TextView rankValue;
        public TextView appreciationValue;
        public TextView date;
        public TextView mediaIcon;
        public View view;
        private int mType;

        public VariousUserViewHolder(View itemLayoutView, Context mContext, int type)
        {
            super(itemLayoutView);
            this.mContext=mContext;
            this.view=itemLayoutView;
            this.mType=type;
            if(mType==UserPagerAdapter.CREATED)
            {
                title=(TextView)itemLayoutView.findViewById(R.id.candeo_showcase_title);


            }
            if(mType==UserPagerAdapter.APPRECIATIONS || mType==UserPagerAdapter.INSPIRATIONS || mType==UserPagerAdapter.FANS|| mType==UserPagerAdapter.PROMOTED)
            {
                userAvatar = (CircleImageView)itemLayoutView.findViewById(R.id.candeo_showcase_user_avatar);
                userAvatar.setImageURI(Uri.parse(Preferences.getUserAvatarPath(mContext)));
                userName = (TextView)itemLayoutView.findViewById(R.id.candeo_user_name);
            }
            if(mType==UserPagerAdapter.CREATED || mType==UserPagerAdapter.APPRECIATIONS)
            {
                appreciateIcon = (TextView)itemLayoutView.findViewById(R.id.candeo_content_appreciate_icon);
                appreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
            }
            if(mType==UserPagerAdapter.CREATED || mType == UserPagerAdapter.INSPIRATIONS)
            {
                inspirationIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_content_inspire_icon);
                inspirationIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/response.ttf"));
                inspirationValue=(TextView)itemLayoutView.findViewById(R.id.candeo_content_inspire_count);

            }
            if(mType==UserPagerAdapter.APPRECIATIONS || mType==UserPagerAdapter.INSPIRATIONS || mType==UserPagerAdapter.CREATED )
            {
                mediaIcon = (TextView)itemLayoutView.findViewById(R.id.candeo_showcase_media_icon);
                mediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/fa.ttf"));
                bgImage = (ImageView)itemLayoutView.findViewById(R.id.candeo_content_bg);
                bgImage.setImageURI(Uri.parse(Preferences.getUserAvatarPath(mContext)));
                date= (TextView)itemLayoutView.findViewById(R.id.candeo_showcase_date);
                appreciationValue = (TextView)itemLayoutView.findViewById(R.id.candeo_content_appreciate_count);
                rankValue = (TextView)itemLayoutView.findViewById(R.id.candeo_content_rank_value);

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

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null)
            {
                Animation in = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
                image.startAnimation(in);
                image.setImageBitmap(bitmap);
            }
        }
    }

}
