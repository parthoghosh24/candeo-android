package com.candeo.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amplitude.api.Amplitude;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.user.UserActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by partho on 30/4/15.
 */
public class ResponseAdapter extends RecyclerView.Adapter<ResponseAdapter.ResponseViewHolder>
{

    private Context mContext;
    private Animation in;
    private JSONArray responses;
    private String type;

    public ResponseAdapter(Context mContext, JSONArray responses, String type)
    {
        this.mContext=mContext;
        this.responses=responses;
        this.type=type;
        if(this.mContext!=null)
        {
            in = AnimationUtils.loadAnimation(mContext.getApplicationContext(), android.R.anim.fade_in);
        }
    }

    @Override
    public ResponseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.response_list_item,parent,false);
        return new ResponseViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ResponseViewHolder holder, int position) {
        try {
            Log.e("responseadpatr","IN HERE");
            final JSONObject responseItem = responses.getJSONObject(position);
            final JSONObject user = responseItem.getJSONObject("user");
            final JSONObject response = responseItem.getJSONObject("response");
            holder.name.setText(user.getString("name"));
            holder.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        Amplitude.getInstance().logEvent("User name clicked from response");
                        Intent userIntent= new Intent(mContext, UserActivity.class);
                        userIntent.putExtra("id",user.getString("id"));
                        mContext.startActivity(userIntent);
                    }
                    catch (JSONException jse)
                    {
                        jse.printStackTrace();
                    }

                }
            });
            holder.userAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Amplitude.getInstance().logEvent("User avatar clicked from response");
                        Intent userIntent= new Intent(mContext, UserActivity.class);
                        userIntent.putExtra("id",user.getString("id"));
                        mContext.startActivity(userIntent);
                    }
                    catch (JSONException jse)
                    {
                        jse.printStackTrace();
                    }

                }
            });
            if("1".equalsIgnoreCase(type))
            {
                JSONObject state = response.getJSONObject("appreciation_response");
                holder.middleText.setText("finds it");
                String ratings = state.getString("rating");
                String feedback = state.getString("feedback");
                if(!TextUtils.isEmpty(feedback))
                {
                    holder.responseBodyHolder.setVisibility(View.VISIBLE);
                    holder.responseText.setText(feedback);
                }
                else
                {
                    holder.responseBodyHolder.setVisibility(View.GONE);
                }
                if(ratings.equalsIgnoreCase("null"))
                {
                    ratings="1";
                }
                holder.response.setText(Configuration.APPRECIATE_LIST[Integer.parseInt(ratings)-1]);

            }
            else
            {
                JSONObject state = response.getJSONObject("inspiration_response");
                holder.middleText.setText("feels");
                String ratings = state.getString("feeling");
                String feedback = state.getString("description");
                if(!TextUtils.isEmpty(feedback))
                {
                    holder.responseBodyHolder.setVisibility(View.VISIBLE);
                    holder.responseText.setText(feedback);
                }
                else
                {
                    holder.responseBodyHolder.setVisibility(View.GONE);
                }
                if(ratings.equalsIgnoreCase("null"))
                {
                    ratings="1";
                }
                holder.response.setText(Configuration.INSPIRE_LIST[Integer.parseInt(ratings)-1]);
                holder.responseText.setText(feedback);
            }

            holder.createdAt.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(response.getString("created_at_timestamp")),System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS));
            new LoadImageTask(holder.userAvatar).execute(user.getString("avatar_path"));
        }
        catch (JSONException jse)
        {
            if(Configuration.DEBUG) Log.e("responseListadptr","AM i here?");
            jse.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return responses.length();
    }

    public static class ResponseViewHolder extends RecyclerView.ViewHolder
    {
        public CircleImageView userAvatar;
        public TextView name, middleText, response, responseText, createdAt;
        public LinearLayout responseBodyHolder;

        public ResponseViewHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            userAvatar = (CircleImageView)itemLayoutView.findViewById(R.id.candeo_response_user_avatar);
            name =(TextView)itemLayoutView.findViewById(R.id.candeo_response_user_name);
            middleText =(TextView)itemLayoutView.findViewById(R.id.candeo_response_middle_text);
            response =(TextView)itemLayoutView.findViewById(R.id.candeo_response_title);
            responseText=(TextView)itemLayoutView.findViewById(R.id.candeo_response_text);
            createdAt = (TextView)itemLayoutView.findViewById(R.id.candeo_response_created_at);
            responseBodyHolder=(LinearLayout)itemLayoutView.findViewById(R.id.candeo_response_list_content_body);
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
