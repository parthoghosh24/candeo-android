package com.candeo.app.adapters;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.home.HomeActivity;
import com.candeo.app.util.CandeoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Partho on 4/3/15.
 */
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {


    private HomeActivity mContext;
    private JSONObject topContentAndUser;
    private List<HashMap<String,String>> morePerformances;
    private static final int TOP_PERFORMANCES=100;
    private static final int TOP_USERS=200;
    private static final int MORE_PERFORMANCES=300;
    private static final String TAG="Candeo-lbadaptr";
//    private int type=TOP_PERFORMANCES;

    public LeaderboardAdapter(HomeActivity mContext,JSONObject topContentAndUser, List<HashMap<String,String>> morePerformances)
    {
        this.mContext=mContext;
        this.topContentAndUser=topContentAndUser;
        this.morePerformances=morePerformances;
    }
    @Override
    public LeaderboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(getLayoutForType(viewType),parent,false);
        Log.e("Candeo Leaderboard","Type is "+viewType);
        return new LeaderboardViewHolder(itemLayoutView,viewType);
    }

    public void addAllToMorePerformances(List<HashMap<String,String>> morePerformances, boolean append)
    {
        if(!append)
        {
            this.morePerformances.clear();
        }
        this.morePerformances.addAll(morePerformances);
    }

    @Override
    public int getItemViewType(int position) {
        switch (position)
        {
            case 0:
                return TOP_PERFORMANCES;
            case 1:
                return TOP_USERS;
            default:
                return MORE_PERFORMANCES;
        }
    }

    private int getLayoutForType(int type)
    {
        switch (type)
        {
            case TOP_PERFORMANCES:
                return R.layout.top_content;
            case TOP_USERS:
                return R.layout.top_users;
            case MORE_PERFORMANCES:
                return R.layout.content_item;
        }
        return R.layout.content_item;
    }



    @Override
    public void onBindViewHolder(LeaderboardViewHolder holder, int position) {

        if(position == 0)
        {

            try {
                JSONObject candeoContent1 = topContentAndUser.getJSONObject("performance").getJSONObject("candeoTopContent1");
                Log.e(TAG,"top1 "+Configuration.BASE_URL+candeoContent1.getString("bg_url"));
                if(TextUtils.isEmpty(candeoContent1.getString("bg_url")))
                {
                    new LoadImageTask(Configuration.BASE_URL+candeoContent1.getString("media_url"),holder.candeoTopContentImage1).execute();
                }
                else
                {
                    new LoadImageTask(Configuration.BASE_URL+candeoContent1.getString("bg_url"),holder.candeoTopContentImage1).execute();
                }

                new LoadImageTask(Configuration.BASE_URL+candeoContent1.getString("user_avatar_url"),holder.candeoTopContent1UserAvatar).execute();

                holder.candeoTopContent1AppreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
                holder.candeoTopContent1AppreciateIcon.setText(Configuration.FA_APPRECIATE);
                holder.candeoTopContent1MediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/fa.ttf"));
                holder.candeoTopContent1MediaIcon.setText(Configuration.FA_AUDIO);
                holder.candeoTopContent1UserName.setText(candeoContent1.getString("name"));
                holder.candeoTopContent1AppreciateCount.setText(candeoContent1.getString("showcase_total_appreciations"));


                JSONObject candeoContent2 = topContentAndUser.getJSONObject("performance").getJSONObject("candeoTopContent2");
                Log.e(TAG,"top2 "+Configuration.BASE_URL+candeoContent2.getString("bg_url"));
                if(TextUtils.isEmpty(candeoContent2.getString("bg_url")))
                {
                    new LoadImageTask(Configuration.BASE_URL+candeoContent2.getString("media_url"),holder.candeoTopContentImage2).execute();
                }
                else
                {
                    new LoadImageTask(Configuration.BASE_URL+candeoContent2.getString("bg_url"),holder.candeoTopContentImage2).execute();
                }
                new LoadImageTask(Configuration.BASE_URL+candeoContent2.getString("user_avatar_url"),holder.candeoTopContent2UserAvatar).execute();

                holder.candeoTopContent2AppreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
                holder.candeoTopContent2AppreciateIcon.setText(Configuration.FA_APPRECIATE);
                holder.candeoTopContent2MediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/fa.ttf"));
                holder.candeoTopContent2MediaIcon.setText(Configuration.FA_AUDIO);
                holder.candeoTopContent2UserName.setText(candeoContent2.getString("name"));
                holder.candeoTopContent2AppreciateCount.setText(candeoContent2.getString("showcase_total_appreciations"));

                JSONObject candeoContent3 = topContentAndUser.getJSONObject("performance").getJSONObject("candeoTopContent3");
                Log.e(TAG,"top3 "+Configuration.BASE_URL+candeoContent3.getString("bg_url"));
                if(TextUtils.isEmpty(candeoContent3.getString("bg_url")))
                {
                    new LoadImageTask(Configuration.BASE_URL+candeoContent3.getString("media_url"),holder.candeoTopContentImage3).execute();
                }
                else
                {
                    new LoadImageTask(Configuration.BASE_URL+candeoContent3.getString("bg_url"),holder.candeoTopContentImage3).execute();
                }
                new LoadImageTask(Configuration.BASE_URL+candeoContent3.getString("user_avatar_url"),holder.candeoTopContent3UserAvatar).execute();

                holder.candeoTopContent3AppreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
                holder.candeoTopContent3AppreciateIcon.setText(Configuration.FA_APPRECIATE);
                holder.candeoTopContent3MediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/fa.ttf"));
                holder.candeoTopContent3MediaIcon.setText(Configuration.FA_AUDIO);
                holder.candeoTopContent3AppreciateCount.setText(candeoContent3.getString("showcase_total_appreciations"));


                JSONObject candeoContent4 = topContentAndUser.getJSONObject("performance").getJSONObject("candeoTopContent4");
                Log.e(TAG,"top4 "+Configuration.BASE_URL+candeoContent4.getString("bg_url"));
                if(TextUtils.isEmpty(candeoContent4.getString("bg_url")))
                {
                    new LoadImageTask(Configuration.BASE_URL+candeoContent4.getString("media_url"),holder.candeoTopContentImage4).execute();
                }
                else
                {
                    new LoadImageTask(Configuration.BASE_URL+candeoContent4.getString("bg_url"),holder.candeoTopContentImage4).execute();
                }
                new LoadImageTask(Configuration.BASE_URL+candeoContent4.getString("user_avatar_url"),holder.candeoTopContent4UserAvatar).execute();

                holder.candeoTopContent4AppreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
                holder.candeoTopContent4AppreciateIcon.setText(Configuration.FA_APPRECIATE);
                holder.candeoTopContent4MediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/fa.ttf"));
                holder.candeoTopContent4MediaIcon.setText(Configuration.FA_AUDIO);
                holder.candeoTopContent4AppreciateCount.setText(candeoContent4.getString("showcase_total_appreciations"));

                JSONObject candeoContent5 = topContentAndUser.getJSONObject("performance").getJSONObject("candeoTopContent5");
                Log.e(TAG,"top5 "+Configuration.BASE_URL+candeoContent5.getString("bg_url"));
                if(TextUtils.isEmpty(candeoContent5.getString("bg_url")))
                {
                    new LoadImageTask(Configuration.BASE_URL+candeoContent5.getString("media_url"),holder.candeoTopContentImage5).execute();
                }
                else
                {
                    new LoadImageTask(Configuration.BASE_URL+candeoContent5.getString("bg_url"),holder.candeoTopContentImage5).execute();
                }
                new LoadImageTask(Configuration.BASE_URL+candeoContent5.getString("user_avatar_url"),holder.candeoTopContent5UserAvatar).execute();

                holder.candeoTopContent5AppreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
                holder.candeoTopContent5AppreciateIcon.setText(Configuration.FA_APPRECIATE);
                holder.candeoTopContent5MediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/fa.ttf"));
                holder.candeoTopContent5MediaIcon.setText(Configuration.FA_AUDIO);
                holder.candeoTopContent5AppreciateCount.setText(candeoContent5.getString("showcase_total_appreciations"));

            }
            catch (JSONException jse)
            {
                jse.printStackTrace();
            }

        }
        else if(position == 1)
        {
            try
            {
                JSONObject candeoTopUser1 = topContentAndUser.getJSONObject("performance").getJSONObject("candeoTopCreator1");
                new LoadImageTask(Configuration.BASE_URL+candeoTopUser1.getString("user_avatar_url"),holder.candeoTopCreatorImg1).execute();
                Log.e(TAG,"candeoTopUser1 "+candeoTopUser1.getString("name"));
                holder.candeoTopCreator1Name.setText(candeoTopUser1.getString("name"));

                JSONObject candeoTopUser2 = topContentAndUser.getJSONObject("performance").getJSONObject("candeoTopCreator2");
                new LoadImageTask(Configuration.BASE_URL+candeoTopUser2.getString("user_avatar_url"),holder.candeoTopCreatorImg2).execute();
                holder.candeoTopCreator2Name.setText(candeoTopUser2.getString("name"));

                JSONObject candeoTopUser3 = topContentAndUser.getJSONObject("performance").getJSONObject("candeoTopCreator3");
                new LoadImageTask(Configuration.BASE_URL+candeoTopUser3.getString("user_avatar_url"),holder.candeoTopCreatorImg3).execute();
                holder.candeoTopCreator3Name.setText(candeoTopUser3.getString("name"));
            }
            catch (JSONException jse)
            {
                Log.e(TAG,"IN HRREREEE");
                jse.printStackTrace();
            }

        }
        else
        {
            //Regular
        }


    }

    @Override
    public int getItemCount() {

        Log.e("Leaderboard","count "+(morePerformances.size()+2));
        return morePerformances.size()+2;
    }

    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder
    {

        //Top 5 last week contents
        public ImageView candeoTopContentImage1 = null;
        public TextView candeoTopContent1AppreciateIcon=null;
        public TextView candeoTopContent1AppreciateCount = null;
        public TextView candeoTopContent1MediaIcon=null;
        public ImageView candeoTopContent1UserAvatar=null;
        public TextView candeoTopContent1UserName=null;

        public ImageView candeoTopContentImage2 = null;
        public TextView candeoTopContent2AppreciateIcon=null;
        public TextView candeoTopContent2AppreciateCount = null;
        public TextView candeoTopContent2MediaIcon=null;
        public ImageView candeoTopContent2UserAvatar=null;
        public TextView candeoTopContent2UserName=null;

        public ImageView candeoTopContentImage3 = null;
        public TextView candeoTopContent3AppreciateIcon=null;
        public TextView candeoTopContent3AppreciateCount = null;
        public TextView candeoTopContent3MediaIcon=null;
        public ImageView candeoTopContent3UserAvatar=null;

        public ImageView candeoTopContentImage4 = null;
        public TextView candeoTopContent4AppreciateIcon=null;
        public TextView candeoTopContent4AppreciateCount = null;
        public TextView candeoTopContent4MediaIcon=null;
        public ImageView candeoTopContent4UserAvatar=null;

        public ImageView candeoTopContentImage5 = null;
        public TextView candeoTopContent5AppreciateIcon=null;
        public TextView candeoTopContent5AppreciateCount = null;
        public TextView candeoTopContent5MediaIcon=null;
        public ImageView candeoTopContent5UserAvatar=null;

        //Top 3 All time creators
        public CircleImageView candeoTopCreatorImg1 = null;
        public TextView candeoTopCreator1Name = null;
        public CircleImageView candeoTopCreatorImg2 = null;
        public TextView candeoTopCreator2Name = null;
        public CircleImageView candeoTopCreatorImg3 = null;
        public TextView candeoTopCreator3Name = null;


        private int type;

        public LeaderboardViewHolder(View itemLayoutView,int type)
        {
            super(itemLayoutView);
            this.type=type;

            if(TOP_PERFORMANCES == type)
            {
                candeoTopContentImage1 = (ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_1_bg);
                candeoTopContent1AppreciateIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_1_appreciate_icon);
                candeoTopContent1AppreciateCount=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_1_appreciate_count);
                candeoTopContent1MediaIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_1_media_icon);
                candeoTopContent1UserAvatar=(ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_1_user_avatar);
                candeoTopContent1UserName=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_1_user_name);

                candeoTopContentImage2 = (ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_bg);
                candeoTopContent2AppreciateIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_appreciate_icon);
                candeoTopContent2AppreciateCount=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_appreciate_count);
                candeoTopContent2MediaIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_media_icon);
                candeoTopContent2UserAvatar=(ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_user_avatar);
                candeoTopContent2UserName=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_user_name);

                candeoTopContentImage3 = (ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_3_bg);
                candeoTopContent3AppreciateIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_3_appreciate_icon);
                candeoTopContent3AppreciateCount=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_3_appreciate_count);
                candeoTopContent3MediaIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_3_media_icon);
                candeoTopContent3UserAvatar=(ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_3_user_avatar);

                candeoTopContentImage4 = (ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_4_bg);
                candeoTopContent4AppreciateIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_4_appreciate_icon);
                candeoTopContent4AppreciateCount=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_4_appreciate_count);
                candeoTopContent4MediaIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_4_media_icon);
                candeoTopContent4UserAvatar=(ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_4_user_avatar);

                candeoTopContentImage5 = (ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_5_bg);
                candeoTopContent5AppreciateIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_5_appreciate_icon);
                candeoTopContent5AppreciateCount=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_5_appreciate_count);
                candeoTopContent5MediaIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_5_media_icon);
                candeoTopContent5UserAvatar=(ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_5_user_avatar);

            }
            if(TOP_USERS==type)
            {
                candeoTopCreatorImg1 = (CircleImageView)itemLayoutView.findViewById(R.id.candeo_top_user_1_avatar);
                candeoTopCreator1Name = (TextView)itemLayoutView.findViewById(R.id.candeo_top_user_1_name);
                candeoTopCreatorImg2 = (CircleImageView)itemLayoutView.findViewById(R.id.candeo_top_user_2_avatar);
                candeoTopCreator2Name = (TextView)itemLayoutView.findViewById(R.id.candeo_top_user_2_name);
                candeoTopCreatorImg3 = (CircleImageView)itemLayoutView.findViewById(R.id.candeo_top_user_3_avatar);
                candeoTopCreator3Name = (TextView)itemLayoutView.findViewById(R.id.candeo_top_user_3_name);

            }
            if(MORE_PERFORMANCES ==type)
            {

            }



        }
    }

    private class LoadImageTask extends AsyncTask<String, String, Bitmap>
    {
        private String url="";
        private ImageView imageView;

        LoadImageTask(String url, ImageView imageView)
        {
            this.url=url;
            this.imageView=imageView;
        }



        @Override
        protected Bitmap doInBackground(String... params) {
            Log.e(TAG,"IN HERE");
            Bitmap bitmap=null;
            try
            {
                URL imageUrl = new URL(url);
                bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            }
            catch (IOException ioe)
            {

                ioe.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            System.out.println("BITMAP IS "+bitmap);
            if(bitmap!=null)
            {
                int height=bitmap.getHeight();
                int width=bitmap.getWidth();
                Log.e(TAG,"Bitmap width is "+ width+" and height is "+height);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
                mContext.getWindowManager().getDefaultDisplay().getMetrics(CandeoApplication.displayMetrics);
                int screenWidth=CandeoApplication.displayMetrics.widthPixels;
                Log.e(TAG,"Screen width is "+ screenWidth);
                double scaleFactor = (width*1.0)/(screenWidth*1.0);
                Log.e(TAG,"Scale Factor is "+scaleFactor);
                int calculatedHeight=height;
                if(scaleFactor>0)
                {
                    calculatedHeight = (int)(height/scaleFactor);
                }

                Log.e(TAG,"Calculated width is "+ screenWidth+" and height is "+calculatedHeight);
                Animation in = AnimationUtils.loadAnimation(mContext.getApplicationContext(), android.R.anim.fade_in);
                imageView.startAnimation(in);
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,screenWidth,calculatedHeight,false));
            }
        }
    }
}
