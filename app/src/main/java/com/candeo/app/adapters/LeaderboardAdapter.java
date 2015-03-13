package com.candeo.app.adapters;

import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.home.HomeActivity;
import com.candeo.app.util.CandeoUtil;

import org.json.JSONObject;

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

//        switch (getType(position)) //Top Content
//        {
//            case TOP_PERFORMANCES:
//                holder.candeoTopContentImage1.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
//                holder.candeoTopContentImage2.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
//                holder.candeoTopContentImage3.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
//                holder.candeoTopContentImage4.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
//                holder.candeoTopContentImage5.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
//                break;
//            case TOP_USERS:
//                holder.candeoTopCreatorImg1.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
//                holder.candeoTopCreatorImg2.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
//                holder.candeoTopCreatorImg3.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
//                break;
//            case MORE_PERFORMANCES:
//                break;
//
//        }
        if(position == 0)
        {
            holder.candeoTopContentImage1.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
            holder.candeoTopContent1UserAvatar.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
            holder.candeoTopContent1AppreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
            holder.candeoTopContent1AppreciateIcon.setText(Configuration.FA_APPRECIATE);
            holder.candeoTopContent1MediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/fa.ttf"));
            holder.candeoTopContent1MediaIcon.setText(Configuration.FA_AUDIO);

            holder.candeoTopContentImage2.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
            holder.candeoTopContent2UserAvatar.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
            holder.candeoTopContent2AppreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
            holder.candeoTopContent2AppreciateIcon.setText(Configuration.FA_APPRECIATE);
            holder.candeoTopContent2MediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/fa.ttf"));
            holder.candeoTopContent2MediaIcon.setText(Configuration.FA_AUDIO);

            holder.candeoTopContentImage3.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
            holder.candeoTopContent3UserAvatar.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
            holder.candeoTopContent3AppreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
            holder.candeoTopContent3AppreciateIcon.setText(Configuration.FA_APPRECIATE);
            holder.candeoTopContent3MediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/fa.ttf"));
            holder.candeoTopContent3MediaIcon.setText(Configuration.FA_AUDIO);

            holder.candeoTopContentImage4.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
            holder.candeoTopContent4UserAvatar.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
            holder.candeoTopContent4AppreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
            holder.candeoTopContent4AppreciateIcon.setText(Configuration.FA_APPRECIATE);
            holder.candeoTopContent4MediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/fa.ttf"));
            holder.candeoTopContent4MediaIcon.setText(Configuration.FA_AUDIO);

            holder.candeoTopContentImage5.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
            holder.candeoTopContent5UserAvatar.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
            holder.candeoTopContent5AppreciateIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
            holder.candeoTopContent5AppreciateIcon.setText(Configuration.FA_APPRECIATE);
            holder.candeoTopContent5MediaIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/fa.ttf"));
            holder.candeoTopContent5MediaIcon.setText(Configuration.FA_AUDIO);
        }
        else if(position == 1)
        {
            holder.candeoTopCreatorImg1.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
            holder.candeoTopCreatorImg2.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));
            holder.candeoTopCreatorImg3.setImageURI(Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.default_avatar));

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
        public TextView candeoTopContent1MediaIcon=null;
        public ImageView candeoTopContent1UserAvatar=null;
        public TextView candeoTopContent1UserName=null;

        public ImageView candeoTopContentImage2 = null;
        public TextView candeoTopContent2AppreciateIcon=null;
        public TextView candeoTopContent2MediaIcon=null;
        public ImageView candeoTopContent2UserAvatar=null;
        public TextView candeoTopContent2UserName=null;

        public ImageView candeoTopContentImage3 = null;
        public TextView candeoTopContent3AppreciateIcon=null;
        public TextView candeoTopContent3MediaIcon=null;
        public ImageView candeoTopContent3UserAvatar=null;

        public ImageView candeoTopContentImage4 = null;
        public TextView candeoTopContent4AppreciateIcon=null;
        public TextView candeoTopContent4MediaIcon=null;
        public ImageView candeoTopContent4UserAvatar=null;

        public ImageView candeoTopContentImage5 = null;
        public TextView candeoTopContent5AppreciateIcon=null;
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
                candeoTopContent1MediaIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_1_media_icon);
                candeoTopContent1UserAvatar=(ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_1_user_avatar);
                candeoTopContent1UserName=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_1_user_name);

                candeoTopContentImage2 = (ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_bg);
                candeoTopContent2AppreciateIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_appreciate_icon);
                candeoTopContent2MediaIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_media_icon);
                candeoTopContent2UserAvatar=(ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_user_avatar);
                candeoTopContent2UserName=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_2_user_name);

                candeoTopContentImage3 = (ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_3_bg);
                candeoTopContent3AppreciateIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_3_appreciate_icon);
                candeoTopContent3MediaIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_3_media_icon);
                candeoTopContent3UserAvatar=(ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_3_user_avatar);

                candeoTopContentImage4 = (ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_4_bg);
                candeoTopContent4AppreciateIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_4_appreciate_icon);
                candeoTopContent4MediaIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_4_media_icon);
                candeoTopContent4UserAvatar=(ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_4_user_avatar);

                candeoTopContentImage5 = (ImageView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_5_bg);
                candeoTopContent5AppreciateIcon=(TextView)itemLayoutView.findViewById(R.id.candeo_performance_top_content_5_appreciate_icon);
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
}
