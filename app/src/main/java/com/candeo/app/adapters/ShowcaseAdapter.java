package com.candeo.app.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.content.ContentActivity;
import com.candeo.app.user.LoginActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Partho on 1/12/14.
 */
public class ShowcaseAdapter extends PagerAdapter {

    private Activity activity;
    private ViewPager pager;
    private List<HashMap<String, String>> showcases;
    private static final String TAG="Candeo- Showcaseadapter";
    private final static String GET_LIMELIGHT_API = Configuration.BASE_URL +"/api/v1/contents/limelight/%s";
    private CircleImageView avatar;
    private ImageView mediaBg;
    private TextView name;
    private TextView title;
    private RelativeLayout showcaseHolder;
    private TextView appreciateCount;

    public ShowcaseAdapter(Activity activity, ViewPager pager, List<HashMap<String, String>> showcases)
    {
        this.activity=activity;
        this.pager = pager;
        this.showcases=showcases;
    }
    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o == view;
    }

    @Override
    public int getCount() {
        Log.e(TAG,"Showcases size "+showcases.size());
        return showcases.size() ;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.e(TAG,"Showcase adapter instantiated");
        View view = activity.getLayoutInflater().inflate(R.layout.showcase_item, container, false);
        showcaseHolder= (RelativeLayout)view.findViewById(R.id.candeo_showcase_holder);
        name=(TextView)view.findViewById(R.id.candeo_user_name);
        title =(TextView)view.findViewById(R.id.candeo_showcase_title);
        appreciateCount=(TextView)view.findViewById(R.id.candeo_appreciate_count);
        TextView copyRightView = (TextView)view.findViewById(R.id.candeo_copyright_icon);
        TextView appreciateIconView = (TextView)view.findViewById(R.id.candeo_appreciate_icon);
        TextView mediaIconView = (TextView)view.findViewById(R.id.candeo_showcase_media_icon);
        Button appreciateButtonView = (Button)view.findViewById(R.id.candeo_showcase_appreciate_button);
        Button skipButtonView = (Button)view.findViewById(R.id.candeo_showcase_skip_button);
        avatar = (CircleImageView)view.findViewById(R.id.candeo_showcase_user_avatar);
        avatar.setImageURI(Uri.parse("android.resource://" + activity.getPackageName() + "/"+ R.raw.default_avatar));
        mediaBg = (ImageView)view.findViewById(R.id.candeo_showcase_media_bg);
        mediaBg.setImageURI(Uri.parse("android.resource://" + activity.getPackageName() + "/"+ R.raw.default_avatar));
        appreciateButtonView.setTypeface(CandeoUtil.loadFont(activity.getAssets(),"fonts/applause.ttf"));
        appreciateButtonView.setText(Configuration.FA_APPRECIATE);
        appreciateButtonView.setOnClickListener(new ShowcaseAppreciateResponseListener(position));
        skipButtonView.setTypeface(CandeoUtil.loadFont(activity.getAssets(), "fonts/fa.ttf"));
        skipButtonView.setText(Configuration.FA_SKIP);
        skipButtonView.setOnClickListener(new ShowcaseSkipResponseListener(position));
        mediaIconView.setTypeface(CandeoUtil.loadFont(activity.getAssets(),"fonts/fa.ttf"));
        mediaIconView.setText(Configuration.FA_AUDIO);
        copyRightView.setTypeface(CandeoUtil.loadFont(activity.getAssets(),"fonts/fa.ttf"));
        copyRightView.setText(Configuration.FA_COPYRIGHT);
        appreciateIconView.setTypeface(CandeoUtil.loadFont(activity.getAssets(),"fonts/applause.ttf"));
        appreciateIconView.setText(Configuration.FA_APPRECIATE);
        container.addView(view);
        Log.e(TAG,"Position "+position);
        Log.e(TAG,"Id at position "+showcases.get(position).get("id"));

        CandeoApplication.getInstance().getAppRequestQueue().add(new FetchLimelight(showcases.get(position).get("id")));
        showcaseHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"Showcase tag is "+showcaseHolder.getTag().toString());
                if(!TextUtils.isEmpty(showcaseHolder.getTag().toString()))
                {
                    Intent contentIntent= new Intent(activity, ContentActivity.class);
                    contentIntent.putExtra("id",showcaseHolder.getTag().toString());
                    contentIntent.putExtra("type",Configuration.SHOWCASE);
                    activity.startActivity(contentIntent);
                }

            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private class ShowcaseAppreciateResponseListener implements View.OnClickListener
    {
        private int position=0;

        public ShowcaseAppreciateResponseListener(int position)
        {
            this.position=position;
        }

        @Override
        public void onClick(View v) {
            if(Preferences.isUserLoggedIn(activity))
            {
                Toast.makeText(activity,"Appreciate pressed",Toast.LENGTH_SHORT).show();
                pager.setCurrentItem(position+1);
            }
            else
            {
                activity.startActivity(new Intent(activity, LoginActivity.class));
            }
        }
    }

    private class ShowcaseSkipResponseListener implements View.OnClickListener
    {
        private int position=0;

        public ShowcaseSkipResponseListener(int position)
        {
            this.position=position;
        }

        @Override
        public void onClick(View v) {
            if(Preferences.isUserLoggedIn(activity))
            {
                Toast.makeText(activity,"Skip pressed",Toast.LENGTH_SHORT).show();
                pager.setCurrentItem(position+1);
            }
            else
            {
                activity.startActivity(new Intent(activity, LoginActivity.class));
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
                image.setImageBitmap(bitmap);
            }
        }
    }

    private class FetchLimelight extends JsonObjectRequest
    {
        public FetchLimelight(final String id)
        {
            super(Method.GET,
                    String.format(GET_LIMELIGHT_API,id),
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.e(TAG,"Fetched limelight");
                            Log.e(TAG,"id fetched is "+id);

                            try {
                                JSONObject limelight = response.getJSONObject("limelight");
                                showcaseHolder.setTag(Integer.toString(limelight.getInt("showcase_id")));
                                name.setText(limelight.getString("name"));
                                title.setText(limelight.getString("title"));
                                appreciateCount.setText(""+limelight.getInt("total_appreciations"));
                                new LoadImageTask(avatar).execute(Configuration.BASE_URL+limelight.getString("user_avatar_url"));
                                new LoadImageTask(mediaBg).execute(Configuration.BASE_URL+limelight.getString("bg_url"));
                                pager.getAdapter().notifyDataSetChanged();
                            }
                            catch (JSONException jse)
                            {
                                jse.printStackTrace();
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            );
        }
    }
}
