package com.candeo.app.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.content.ContentActivity;
import com.candeo.app.home.HomeActivity;
import com.candeo.app.ui.BitmapLruCache;
import com.candeo.app.user.UserActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Partho on 4/3/15.
 */
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {


    private HomeActivity mContext;
    private JSONObject topContentAndUser;
    public ArrayList<HashMap<String,String>> morePerformances = new ArrayList<>();
    private static final int TOP_PERFORMANCES=100;
    private static final int TOP_USERS=200;
    private static final int MORE_PERFORMANCES=300;
    private static final String TAG="Candeo-lbadaptr";
    private ImageLoader imageLoader;
    private BitmapLruCache imageCache;
    private Animation in;
//    private int type=TOP_PERFORMANCES;

    public LeaderboardAdapter(HomeActivity mContext,JSONObject topContentAndUser)
    {
        in = AnimationUtils.loadAnimation(mContext.getApplicationContext(), android.R.anim.fade_in);
        this.mContext=mContext;
        this.topContentAndUser=topContentAndUser;
        this.imageCache=new BitmapLruCache();
        this.imageLoader=new ImageLoader(CandeoApplication.getInstance().getAppRequestQueue(),imageCache);
    }
    @Override
    public LeaderboardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(getLayoutForType(viewType),parent,false);
        if(Configuration.DEBUG)Log.e("Candeo Leaderboard","Type is "+viewType);
        return new LeaderboardViewHolder(itemLayoutView,viewType,mContext);
    }

    public void addAllToMorePerformances(ArrayList<HashMap<String,String>> performances, boolean append)
    {
        if(Configuration.DEBUG)Log.e(TAG,"Incoming list size "+performances.size());
        if(Configuration.DEBUG)Log.e(TAG,"Adding to performances");
        if(!append)
        {
            morePerformances.clear();
        }
        morePerformances.addAll(performances);
        if(Configuration.DEBUG)Log.e(TAG,"Total size is "+morePerformances.size());
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
    public void onBindViewHolder(final LeaderboardViewHolder holder, int position) {

        if(position == 0)
        {

            try {
                JSONObject candeoContent1 = topContentAndUser.getJSONObject("performance").getJSONObject("candeoTopContent1");
                if(candeoContent1.length()>0)
                {
                    if(Configuration.DEBUG)Log.e(TAG,"top1 "+candeoContent1.getString("bg_url"));
                    holder.candeoTopContentImage1.startAnimation(in);
                    if(TextUtils.isEmpty(candeoContent1.getString("bg_url")) || "null".equalsIgnoreCase(candeoContent1.getString("bg_url")))
                    {
                        holder.candeoTopContentImage1.setImageUrl(candeoContent1.getString("media_url"),imageLoader);
                    }
                    else
                    {
                        holder.candeoTopContentImage1.setImageUrl(candeoContent1.getString("bg_url"),imageLoader);
                    }

                    new LoadImageTask(candeoContent1.getString("user_avatar_url"),holder.candeoTopContent1UserAvatar).execute();

                    holder.candeoTopContent1AppreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
                    holder.candeoTopContent1AppreciateIcon.setText(Configuration.FA_APPRECIATE);
                    holder.candeoTopContent1MediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(), "fonts/fa.ttf"));
                    if(Configuration.TEXT == Integer.parseInt(candeoContent1.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent1MediaIcon.setText(Configuration.FA_TEXT);
                    }
                    if(Configuration.AUDIO == Integer.parseInt(candeoContent1.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent1MediaIcon.setText(Configuration.FA_AUDIO);
                    }
                    else if(Configuration.IMAGE == Integer.parseInt(candeoContent1.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent1MediaIcon.setText(Configuration.FA_IMAGE);
                    }
                    else if(Configuration.VIDEO == Integer.parseInt(candeoContent1.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent1MediaIcon.setText(Configuration.FA_VIDEO);
                    }
                    else if(Configuration.BOOK == Integer.parseInt(candeoContent1.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent1MediaIcon.setText(Configuration.FA_BOOK);
                    }
                    holder.candeoTopContent1Title.setText(candeoContent1.getString("showcase_title"));
                    holder.candeoTopContent1AppreciateCount.setText(candeoContent1.getString("showcase_total_appreciations"));
                    holder.candeoTopContent1.setTag(candeoContent1.getString("showcase_id"));
                    holder.candeoTopContent1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(holder.candeoTopContent1.getTag()!=null && !TextUtils.isEmpty(holder.candeoTopContent1.getTag().toString()) && !"-1".equalsIgnoreCase(holder.candeoTopContent1.getTag().toString()) )
                            {
                                Intent contentIntent= new Intent(mContext, ContentActivity.class);
                                contentIntent.putExtra("id",holder.candeoTopContent1.getTag().toString());
                                contentIntent.putExtra("type",Configuration.SHOWCASE);
                                mContext.startActivity(contentIntent);
                            }

                        }
                    });

                }
                else
                {
                    CandeoUtil.toggleView(holder.candeoTopContent1,false);
                }



                JSONObject candeoContent2 = topContentAndUser.getJSONObject("performance").getJSONObject("candeoTopContent2");
                if(candeoContent2.length()>0)
                {
                    if(Configuration.DEBUG)Log.e(TAG,"top2 "+candeoContent2.getString("bg_url"));
                    holder.candeoTopContentImage2.startAnimation(in);
                    if(TextUtils.isEmpty(candeoContent2.getString("bg_url")) || "null".equalsIgnoreCase(candeoContent2.getString("bg_url")))
                    {
                        holder.candeoTopContentImage2.setImageUrl(candeoContent2.getString("media_url"),imageLoader);
                    }
                    else
                    {
                        holder.candeoTopContentImage2.setImageUrl(candeoContent2.getString("bg_url"),imageLoader);
                    }
                    new LoadImageTask(candeoContent2.getString("user_avatar_url"),holder.candeoTopContent2UserAvatar).execute();

                    holder.candeoTopContent2AppreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
                    holder.candeoTopContent2AppreciateIcon.setText(Configuration.FA_APPRECIATE);
                    holder.candeoTopContent2MediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/fa.ttf"));
                    if(Configuration.TEXT == Integer.parseInt(candeoContent2.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent2MediaIcon.setText(Configuration.FA_TEXT);
                    }
                    if(Configuration.AUDIO == Integer.parseInt(candeoContent2.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent2MediaIcon.setText(Configuration.FA_AUDIO);
                    }
                    else if(Configuration.IMAGE == Integer.parseInt(candeoContent2.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent2MediaIcon.setText(Configuration.FA_IMAGE);
                    }
                    else if(Configuration.VIDEO == Integer.parseInt(candeoContent2.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent2MediaIcon.setText(Configuration.FA_VIDEO);
                    }
                    else if(Configuration.BOOK == Integer.parseInt(candeoContent2.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent2MediaIcon.setText(Configuration.FA_BOOK);
                    }
                    holder.candeoTopContent2Title.setText(candeoContent2.getString("showcase_title"));
                    holder.candeoTopContent2AppreciateCount.setText(candeoContent2.getString("showcase_total_appreciations"));
                    holder.candeoTopContent2.setTag(candeoContent2.getString("showcase_id"));
                    holder.candeoTopContent2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(holder.candeoTopContent2.getTag()!=null && !TextUtils.isEmpty(holder.candeoTopContent2.getTag().toString()) && !"-1".equalsIgnoreCase(holder.candeoTopContent2.getTag().toString()) )
                            {
                                Intent contentIntent= new Intent(mContext, ContentActivity.class);
                                contentIntent.putExtra("id",holder.candeoTopContent2.getTag().toString());
                                contentIntent.putExtra("type",Configuration.SHOWCASE);
                                mContext.startActivity(contentIntent);
                            }

                        }
                    });

                }
                else
                {
                    CandeoUtil.toggleView(holder.candeoTopContent2,false);
                }


                JSONObject candeoContent3 = topContentAndUser.getJSONObject("performance").getJSONObject("candeoTopContent3");
                if(candeoContent3.length()>0)
                {
                    if(Configuration.DEBUG)Log.e(TAG,"top3 "+candeoContent3.getString("bg_url"));
                    holder.candeoTopContentImage3.startAnimation(in);
                    if(TextUtils.isEmpty(candeoContent3.getString("bg_url")) || "null".equalsIgnoreCase(candeoContent3.getString("bg_url")))
                    {
                        holder.candeoTopContentImage3.setImageUrl(candeoContent3.getString("media_url"),imageLoader);
                    }
                    else
                    {
                        holder.candeoTopContentImage3.setImageUrl(candeoContent3.getString("bg_url"),imageLoader);
                    }

                    holder.candeoTopContent3AppreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
                    holder.candeoTopContent3AppreciateIcon.setText(Configuration.FA_APPRECIATE);
                    holder.candeoTopContent3MediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/fa.ttf"));
                    if(Configuration.TEXT == Integer.parseInt(candeoContent3.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent3MediaIcon.setText(Configuration.FA_TEXT);
                    }
                    if(Configuration.AUDIO == Integer.parseInt(candeoContent3.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent3MediaIcon.setText(Configuration.FA_AUDIO);
                    }
                    else if(Configuration.IMAGE == Integer.parseInt(candeoContent3.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent3MediaIcon.setText(Configuration.FA_IMAGE);
                    }
                    else if(Configuration.VIDEO == Integer.parseInt(candeoContent3.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent3MediaIcon.setText(Configuration.FA_VIDEO);
                    }
                    else if(Configuration.BOOK == Integer.parseInt(candeoContent3.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent3MediaIcon.setText(Configuration.FA_BOOK);
                    }
                    holder.candeoTopContent3AppreciateCount.setText(candeoContent3.getString("showcase_total_appreciations"));
                    holder.candeoTopContent3Title.setText(candeoContent3.getString("showcase_title"));
                    holder.candeoTopContent3.setTag(candeoContent3.getString("showcase_id"));
                    holder.candeoTopContent3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (holder.candeoTopContent3.getTag() != null && !TextUtils.isEmpty(holder.candeoTopContent3.getTag().toString()) && !"-1".equalsIgnoreCase(holder.candeoTopContent3.getTag().toString())) {
                                Intent contentIntent = new Intent(mContext, ContentActivity.class);
                                contentIntent.putExtra("id", holder.candeoTopContent3.getTag().toString());
                                contentIntent.putExtra("type", Configuration.SHOWCASE);
                                mContext.startActivity(contentIntent);
                            }

                        }
                    });

                }
                else
                {
                    CandeoUtil.toggleView(holder.candeoTopContent3,false);
                }


                JSONObject candeoContent4 = topContentAndUser.getJSONObject("performance").getJSONObject("candeoTopContent4");
                if(candeoContent4.length()>0)
                {
                    if(Configuration.DEBUG)Log.e(TAG,"top4 "+candeoContent4.getString("bg_url"));
                    holder.candeoTopContentImage4.startAnimation(in);
                    if(TextUtils.isEmpty(candeoContent4.getString("bg_url")) || "null".equalsIgnoreCase(candeoContent4.getString("bg_url")))
                    {
                        holder.candeoTopContentImage4.setImageUrl(candeoContent4.getString("media_url"),imageLoader);
                    }
                    else
                    {
                        holder.candeoTopContentImage4.setImageUrl(candeoContent4.getString("bg_url"),imageLoader);
                    }

                    holder.candeoTopContent4AppreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(), "fonts/applause.ttf"));
                    holder.candeoTopContent4AppreciateIcon.setText(Configuration.FA_APPRECIATE);
                    holder.candeoTopContent4MediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(), "fonts/fa.ttf"));
                    if(Configuration.TEXT == Integer.parseInt(candeoContent4.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent4MediaIcon.setText(Configuration.FA_TEXT);
                    }
                    if(Configuration.AUDIO == Integer.parseInt(candeoContent4.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent4MediaIcon.setText(Configuration.FA_AUDIO);
                    }
                    else if(Configuration.IMAGE == Integer.parseInt(candeoContent4.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent4MediaIcon.setText(Configuration.FA_IMAGE);
                    }
                    else if(Configuration.VIDEO == Integer.parseInt(candeoContent4.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent4MediaIcon.setText(Configuration.FA_VIDEO);
                    }
                    else if(Configuration.BOOK == Integer.parseInt(candeoContent4.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent4MediaIcon.setText(Configuration.FA_BOOK);
                    }
                    holder.candeoTopContent4AppreciateCount.setText(candeoContent4.getString("showcase_total_appreciations"));
                    holder.candeoTopContent4Title.setText(candeoContent4.getString("showcase_title"));
                    holder.candeoTopContent4.setTag(candeoContent4.getString("showcase_id"));
                    holder.candeoTopContent4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (holder.candeoTopContent4.getTag() != null && !TextUtils.isEmpty(holder.candeoTopContent4.getTag().toString()) && !"-1".equalsIgnoreCase(holder.candeoTopContent4.getTag().toString())) {
                                Intent contentIntent = new Intent(mContext, ContentActivity.class);
                                contentIntent.putExtra("id", holder.candeoTopContent4.getTag().toString());
                                contentIntent.putExtra("type", Configuration.SHOWCASE);
                                mContext.startActivity(contentIntent);
                            }

                        }
                    });

                }
                else
                {
                    CandeoUtil.toggleView(holder.candeoTopContent4,false);
                }


                JSONObject candeoContent5 = topContentAndUser.getJSONObject("performance").getJSONObject("candeoTopContent5");
                if(candeoContent5.length()>0)
                {
                    if(Configuration.DEBUG)Log.e(TAG,"top5 "+candeoContent5.getString("bg_url"));
                    holder.candeoTopContentImage5.startAnimation(in);
                    if(TextUtils.isEmpty(candeoContent5.getString("bg_url")) || "null".equalsIgnoreCase(candeoContent5.getString("bg_url")))
                    {
                        holder.candeoTopContentImage5.setImageUrl(candeoContent5.getString("media_url"),imageLoader);
                    }
                    else
                    {
                        holder.candeoTopContentImage5.setImageUrl(candeoContent5.getString("bg_url"),imageLoader);
                    }

                    holder.candeoTopContent5AppreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(), "fonts/applause.ttf"));
                    holder.candeoTopContent5AppreciateIcon.setText(Configuration.FA_APPRECIATE);
                    holder.candeoTopContent5MediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(), "fonts/fa.ttf"));
                    if(Configuration.TEXT == Integer.parseInt(candeoContent5.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent5MediaIcon.setText(Configuration.FA_TEXT);
                    }
                    if(Configuration.AUDIO == Integer.parseInt(candeoContent5.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent5MediaIcon.setText(Configuration.FA_AUDIO);
                    }
                    else if(Configuration.IMAGE == Integer.parseInt(candeoContent5.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent5MediaIcon.setText(Configuration.FA_IMAGE);
                    }
                    else if(Configuration.VIDEO == Integer.parseInt(candeoContent5.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent5MediaIcon.setText(Configuration.FA_VIDEO);
                    }
                    else if(Configuration.BOOK == Integer.parseInt(candeoContent5.getString("showcase_media_type")))
                    {
                        holder.candeoTopContent5MediaIcon.setText(Configuration.FA_BOOK);
                    }
                    holder.candeoTopContent5AppreciateCount.setText(candeoContent5.getString("showcase_total_appreciations"));
                    holder.candeoTopContent5Title.setText(candeoContent5.getString("showcase_title"));
                    holder.candeoTopContent5.setTag(candeoContent5.getString("showcase_id"));
                    holder.candeoTopContent5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(holder.candeoTopContent5.getTag()!=null && !TextUtils.isEmpty(holder.candeoTopContent5.getTag().toString()) && !"-1".equalsIgnoreCase(holder.candeoTopContent5.getTag().toString()) )
                            {
                                Intent contentIntent= new Intent(mContext, ContentActivity.class);
                                contentIntent.putExtra("id",holder.candeoTopContent5.getTag().toString());
                                contentIntent.putExtra("type",Configuration.SHOWCASE);
                                mContext.startActivity(contentIntent);
                            }

                        }
                    });

                }
                else
                {
                    CandeoUtil.toggleView(holder.candeoTopContent5,false);
                }


                if(Configuration.DEBUG)Log.e(TAG,"No top content: "+(candeoContent1.length() == 0 && candeoContent2.length() == 0 && candeoContent3.length() == 0 && candeoContent4.length() == 0 && candeoContent5.length() == 0));
                if(candeoContent1.length() == 0 && candeoContent2.length() == 0 && candeoContent3.length() == 0 && candeoContent4.length() == 0 && candeoContent5.length() == 0)
                {
                    CandeoUtil.toggleView(holder.noTopContent,true);
                    CandeoUtil.toggleView(holder.topContentHolder,false);
                    ((TextView)holder.noTopContent.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/fa.ttf"));
                    ((TextView)holder.noTopContent.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_MAGIC);
                    ((TextView)holder.noTopContent.findViewById(R.id.candeo_no_content_text)).setText("No Performances last week!");
                }

            }
            catch (JSONException jse)
            {
                jse.printStackTrace();
                if(Configuration.DEBUG)Log.e(TAG,"Top content error is "+jse.getLocalizedMessage());
            }

        }
        else if(position == 1)
        {
            try
            {
                JSONObject candeoTopUser1 = topContentAndUser.getJSONObject("performance").getJSONObject("candeoTopCreator1");
                holder.candeoTopCreatorImg1.startAnimation(in);
                holder.candeoTopCreatorImg1.setImageUrl(candeoTopUser1.getString("user_avatar_url"), imageLoader);
                if(Configuration.DEBUG)Log.e(TAG, "candeoTopUser1 " + candeoTopUser1.getString("name"));
                if(Configuration.DEBUG)Log.e(TAG, "candeoTopUser1 id " + candeoTopUser1.getString("id"));
                holder.candeoTopCreator1Name.setText(candeoTopUser1.getString("name"));
                holder.candeoTopCreator1.setTag(candeoTopUser1.getString("id"));
                holder.candeoTopCreator1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(holder.candeoTopCreator1.getTag()!=null && !TextUtils.isEmpty(holder.candeoTopCreator1.getTag().toString()) && !"-1".equalsIgnoreCase(holder.candeoTopCreator1.getTag().toString()) )
                        {
                            Intent contentIntent= new Intent(mContext, UserActivity.class);
                            contentIntent.putExtra("id",holder.candeoTopCreator1.getTag().toString());
                            mContext.startActivity(contentIntent);
                        }

                    }
                });

                JSONObject candeoTopUser2 = topContentAndUser.getJSONObject("performance").getJSONObject("candeoTopCreator2");
                holder.candeoTopCreatorImg2.startAnimation(in);
                holder.candeoTopCreatorImg2.setImageUrl(candeoTopUser2.getString("user_avatar_url"), imageLoader);
                holder.candeoTopCreator2Name.setText(candeoTopUser2.getString("name"));
                holder.candeoTopCreator2.setTag(candeoTopUser2.getString("id"));
                holder.candeoTopCreator2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(holder.candeoTopCreator2.getTag()!=null && !TextUtils.isEmpty(holder.candeoTopCreator2.getTag().toString()) && !"-1".equalsIgnoreCase(holder.candeoTopCreator2.getTag().toString()) )
                        {
                            Intent contentIntent= new Intent(mContext, UserActivity.class);
                            contentIntent.putExtra("id",holder.candeoTopCreator2.getTag().toString());
                            mContext.startActivity(contentIntent);
                        }

                    }
                });

                JSONObject candeoTopUser3 = topContentAndUser.getJSONObject("performance").getJSONObject("candeoTopCreator3");
                holder.candeoTopCreatorImg3.startAnimation(in);
                holder.candeoTopCreatorImg3.setImageUrl(candeoTopUser3.getString("user_avatar_url"), imageLoader);
                holder.candeoTopCreator3Name.setText(candeoTopUser3.getString("name"));
                holder.candeoTopCreator3.setTag(candeoTopUser3.getString("id"));
                holder.candeoTopCreator3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(holder.candeoTopCreator3.getTag()!=null && !TextUtils.isEmpty(holder.candeoTopCreator3.getTag().toString()) && !"-1".equalsIgnoreCase(holder.candeoTopCreator3.getTag().toString()) )
                        {
                            Intent contentIntent= new Intent(mContext, UserActivity.class);
                            contentIntent.putExtra("id",holder.candeoTopCreator3.getTag().toString());
                            mContext.startActivity(contentIntent);
                        }

                    }
                });
            }
            catch (JSONException jse)
            {
                if(Configuration.DEBUG)Log.e(TAG,"IN HRREREEE");
                jse.printStackTrace();
            }

        }
        else
        {
            //More
            HashMap<String, String> morePerformance = morePerformances.get(position-2);
            holder.bgImage.startAnimation(in);
            if(Configuration.DEBUG)Log.e(TAG,"bg url is "+(TextUtils.isEmpty(morePerformance.get("showcase_bg_url")) || "null".equalsIgnoreCase(morePerformance.get("showcase_bg_url"))));
            if(TextUtils.isEmpty(morePerformance.get("showcase_bg_url")) || "null".equalsIgnoreCase(morePerformance.get("showcase_bg_url")))
            {
                holder.bgImage.setImageUrl(morePerformance.get("showcase_media_url"), imageLoader);

            }
            else
            {
                holder.bgImage.setImageUrl(morePerformance.get("showcase_bg_url"), imageLoader);
            }

            holder.title.setText(morePerformance.get("showcase_title"));
            new LoadImageTask(morePerformance.get("showcase_user_avatar_url"),holder.userAvatar).execute();
            holder.appreciateIcon.setText(Configuration.FA_APPRECIATE);
            holder.appreciationValue.setText(morePerformance.get("showcase_total_appreciations"));
            holder.rankValue.setText(morePerformance.get("showcase_rank"));
            holder.date.setText(morePerformance.get("showcase_user_name"));
            if(Configuration.TEXT == Integer.parseInt(morePerformance.get("showcase_media_type")))
            {
                holder.mediaIcon.setText(Configuration.FA_TEXT);
            }
            if(Configuration.AUDIO == Integer.parseInt(morePerformance.get("showcase_media_type")))
            {
                holder.mediaIcon.setText(Configuration.FA_AUDIO);
            }
            else if(Configuration.IMAGE == Integer.parseInt(morePerformance.get("showcase_media_type")))
            {
                holder.mediaIcon.setText(Configuration.FA_IMAGE);
            }
            else if(Configuration.VIDEO == Integer.parseInt(morePerformance.get("showcase_media_type")))
            {
                holder.mediaIcon.setText(Configuration.FA_VIDEO);
            }
            else if(Configuration.BOOK == Integer.parseInt(morePerformance.get("showcase_media_type")))
            {
                holder.mediaIcon.setText(Configuration.FA_BOOK);
            }
            holder.contentHolder.setTag(morePerformance.get("showcase_id"));
            holder.contentHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.contentHolder.getTag()!=null && !TextUtils.isEmpty(holder.contentHolder.getTag().toString()) && !"-1".equalsIgnoreCase(holder.contentHolder.getTag().toString()) )
                    {
                        Intent contentIntent= new Intent(mContext, ContentActivity.class);
                        contentIntent.putExtra("id",holder.contentHolder.getTag().toString());
                        contentIntent.putExtra("type",Configuration.SHOWCASE);
                        mContext.startActivity(contentIntent);
                    }
                }
            });

        }


    }

    @Override
    public int getItemCount() {

        if(Configuration.DEBUG)Log.e(TAG,"count "+(morePerformances.size()+2));
        return morePerformances.size()+2;
    }

    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder
    {

        //Top 5 last week contents
        public View noTopContent = null;
        public LinearLayout topContentHolder = null;
        public NetworkImageView candeoTopContentImage1 = null;
        public TextView candeoTopContent1AppreciateIcon=null;
        public TextView candeoTopContent1AppreciateCount = null;
        public TextView candeoTopContent1MediaIcon=null;
        public ImageView candeoTopContent1UserAvatar=null;
        public TextView candeoTopContent1Title=null;
        public CardView candeoTopContent1=null;

        public NetworkImageView candeoTopContentImage2 = null;
        public TextView candeoTopContent2AppreciateIcon=null;
        public TextView candeoTopContent2AppreciateCount = null;
        public TextView candeoTopContent2MediaIcon=null;
        public ImageView candeoTopContent2UserAvatar=null;
        public TextView candeoTopContent2Title=null;
        public CardView candeoTopContent2=null;

        public NetworkImageView candeoTopContentImage3 = null;
        public TextView candeoTopContent3AppreciateIcon=null;
        public TextView candeoTopContent3AppreciateCount = null;
        public TextView candeoTopContent3MediaIcon=null;
        public TextView candeoTopContent3Title=null;
        public CardView candeoTopContent3=null;

        public NetworkImageView candeoTopContentImage4 = null;
        public TextView candeoTopContent4AppreciateIcon=null;
        public TextView candeoTopContent4AppreciateCount = null;
        public TextView candeoTopContent4MediaIcon=null;
        public TextView candeoTopContent4Title=null;
        public CardView candeoTopContent4=null;

        public NetworkImageView candeoTopContentImage5 = null;
        public TextView candeoTopContent5AppreciateIcon=null;
        public TextView candeoTopContent5AppreciateCount = null;
        public TextView candeoTopContent5MediaIcon=null;
        public TextView candeoTopContent5Title=null;
        public CardView candeoTopContent5=null;

        //Top 3 All time creators
        public CardView candeoTopCreator1=null;
        public NetworkImageView candeoTopCreatorImg1 = null;
        public TextView candeoTopCreator1Name = null;
        public CardView candeoTopCreator2=null;
        public NetworkImageView candeoTopCreatorImg2 = null;
        public TextView candeoTopCreator2Name = null;
        public CardView candeoTopCreator3=null;
        public NetworkImageView candeoTopCreatorImg3 = null;
        public TextView candeoTopCreator3Name = null;

        //More Performances
        private TextView title;
        public CircleImageView userAvatar;
        public NetworkImageView bgImage;
        public TextView userName;
        public TextView appreciateIcon;
        public TextView rankValue;
        public TextView appreciationValue;
        public TextView date;
        public TextView mediaIcon;
        public LinearLayout inspirationHolder;
        public View view;
        public CardView contentHolder;

        private int type;
        private Context mContext;

        public LeaderboardViewHolder(View itemLayoutView,int type, Context mContext)
        {
            super(itemLayoutView);
            this.type=type;
            this.mContext=mContext;

            if(TOP_PERFORMANCES == type)
            {
                noTopContent = itemLayoutView.findViewById(R.id.candeo_no_content);
                CandeoUtil.toggleView(noTopContent,false);
                topContentHolder = (LinearLayout)itemLayoutView.findViewById(R.id.candeo_performance_top_content_holder);
                candeoTopContentImage1 = (NetworkImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_1_bg);
                candeoTopContent1AppreciateIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_1_appreciate_icon);
                candeoTopContent1AppreciateCount=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_1_appreciate_count);
                candeoTopContent1MediaIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_1_media_icon);
                candeoTopContent1UserAvatar=(ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_1_user_avatar);
                candeoTopContent1Title=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_1_title);
                candeoTopContent1=(CardView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_1);

                candeoTopContentImage2 = (NetworkImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_bg);
                candeoTopContent2AppreciateIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_appreciate_icon);
                candeoTopContent2AppreciateCount=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_appreciate_count);
                candeoTopContent2MediaIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_media_icon);
                candeoTopContent2UserAvatar=(ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_user_avatar);
                candeoTopContent2Title=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_title);
                candeoTopContent2=(CardView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2);

                candeoTopContentImage3 = (NetworkImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_3_bg);
                candeoTopContent3AppreciateIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_3_appreciate_icon);
                candeoTopContent3AppreciateCount=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_3_appreciate_count);
                candeoTopContent3MediaIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_3_media_icon);
                candeoTopContent3Title=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_3_title);
                candeoTopContent3=(CardView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_3);

                candeoTopContentImage4 = (NetworkImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_4_bg);
                candeoTopContent4AppreciateIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_4_appreciate_icon);
                candeoTopContent4AppreciateCount=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_4_appreciate_count);
                candeoTopContent4MediaIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_4_media_icon);
                candeoTopContent4Title=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_4_title);
                candeoTopContent4=(CardView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_4);

                candeoTopContentImage5 = (NetworkImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_5_bg);
                candeoTopContent5AppreciateIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_5_appreciate_icon);
                candeoTopContent5AppreciateCount=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_5_appreciate_count);
                candeoTopContent5MediaIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_5_media_icon);
                candeoTopContent5Title=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_5_title);
                candeoTopContent5=(CardView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_5);

            }
            if(TOP_USERS==type)
            {
                candeoTopCreatorImg1 = (NetworkImageView)itemLayoutView.findViewById(R.id.candeo_top_user_1_avatar);
                candeoTopCreator1Name = (TextView)itemLayoutView.findViewById(R.id.candeo_top_user_1_name);
                candeoTopCreator1=(CardView)itemLayoutView.findViewById(R.id.candeo_top_user_1);
                candeoTopCreatorImg2 = (NetworkImageView)itemLayoutView.findViewById(R.id.candeo_top_user_2_avatar);
                candeoTopCreator2Name = (TextView)itemLayoutView.findViewById(R.id.candeo_top_user_2_name);
                candeoTopCreator2=(CardView)itemLayoutView.findViewById(R.id.candeo_top_user_2);
                candeoTopCreatorImg3 = (NetworkImageView)itemLayoutView.findViewById(R.id.candeo_top_user_3_avatar);
                candeoTopCreator3Name = (TextView)itemLayoutView.findViewById(R.id.candeo_top_user_3_name);
                candeoTopCreator3=(CardView)itemLayoutView.findViewById(R.id.candeo_top_user_3);

            }
            if(MORE_PERFORMANCES ==type)
            {
                inspirationHolder=(LinearLayout)itemLayoutView.findViewById(R.id.candeo_content_inspire_holder);
                inspirationHolder.setVisibility(View.GONE);
                title=(TextView)itemLayoutView.findViewById(R.id.candeo_showcase_title);
                userAvatar=(CircleImageView)itemLayoutView.findViewById(R.id.candeo_content_user_avatar);
                userAvatar.setVisibility(View.VISIBLE);
                mediaIcon = (TextView)itemLayoutView.findViewById(R.id.candeo_showcase_media_icon);
                mediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/fa.ttf"));
                bgImage = (NetworkImageView)itemLayoutView.findViewById(R.id.candeo_content_bg);
                bgImage.setImageURI(Uri.parse(Preferences.getUserAvatarPath(mContext)));
                date= (TextView)itemLayoutView.findViewById(R.id.candeo_showcase_date);
                appreciationValue = (TextView)itemLayoutView.findViewById(R.id.candeo_content_appreciate_count);
                rankValue = (TextView)itemLayoutView.findViewById(R.id.candeo_content_rank_value);
                appreciateIcon = (TextView)itemLayoutView.findViewById(R.id.candeo_content_appreciate_icon);
                appreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
                contentHolder=(CardView)itemLayoutView.findViewById(R.id.candeo_content_holder);


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
            if(Configuration.DEBUG)Log.e(TAG,"IN HERE");
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
                if(Configuration.DEBUG)Log.e(TAG,"Bitmap width is "+ width+" and height is "+height);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
                mContext.getWindowManager().getDefaultDisplay().getMetrics(CandeoApplication.displayMetrics);
                int screenWidth=CandeoApplication.displayMetrics.widthPixels;
                if(Configuration.DEBUG)Log.e(TAG,"Screen width is "+ screenWidth);
                double scaleFactor = (width*1.0)/(screenWidth*1.0);
                if(Configuration.DEBUG)Log.e(TAG,"Scale Factor is "+scaleFactor);
                int calculatedHeight=height;
                if(scaleFactor>0)
                {
                    calculatedHeight = (int)(height/scaleFactor);
                }

                if(Configuration.DEBUG)Log.e(TAG,"Calculated width is "+ screenWidth+" and height is "+calculatedHeight);
                Animation in = AnimationUtils.loadAnimation(mContext.getApplicationContext(), android.R.anim.fade_in);
                imageView.startAnimation(in);
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,screenWidth,calculatedHeight,false));
            }
        }
    }
}
