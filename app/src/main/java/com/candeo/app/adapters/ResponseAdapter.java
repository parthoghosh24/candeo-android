package com.candeo.app.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.candeo.app.R;

import org.json.JSONArray;

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

    public ResponseAdapter(Context mContext, JSONArray responses)
    {
        this.mContext=mContext;
        this.responses=responses;
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

    }

    @Override
    public int getItemCount() {
        return responses.length();
    }

    public static class ResponseViewHolder extends RecyclerView.ViewHolder
    {
        public CircleImageView userAvatar;
        public TextView name, middleText, response, leftQuote, responseText, rightQuote, createdAt;

        public ResponseViewHolder(View itemLayoutView)
        {
            super(itemLayoutView);
            userAvatar = (CircleImageView)itemLayoutView.findViewById(R.id.candeo_response_user_avatar);
            name =(TextView)itemLayoutView.findViewById(R.id.candeo_response_user_name);
            middleText =(TextView)itemLayoutView.findViewById(R.id.candeo_response_middle_text);
            response =(TextView)itemLayoutView.findViewById(R.id.candeo_response_title);
            leftQuote = (TextView)itemLayoutView.findViewById(R.id.candeo_response_text_left_quote);
            responseText=(TextView)itemLayoutView.findViewById(R.id.candeo_response_text);
            rightQuote = (TextView)itemLayoutView.findViewById(R.id.candeo_response_text_right_quote);
            createdAt = (TextView)itemLayoutView.findViewById(R.id.candeo_response_created_at);
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
