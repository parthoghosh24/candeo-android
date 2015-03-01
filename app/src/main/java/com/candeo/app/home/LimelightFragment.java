package com.candeo.app.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.content.ContentActivity;
import com.candeo.app.response.ResponseListener;
import com.candeo.app.ui.ResponseFragment;
import com.candeo.app.user.LoginActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;


public class LimelightFragment extends Fragment{

    private String id="";
    private static final String TAG="Candeo- Limelight";
    private final static String GET_LIMELIGHT_API = Configuration.BASE_URL +"/api/v1/contents/limelight/%s";
    private CircleImageView avatar;
    private ImageView mediaBg;
    private TextView name;
    private TextView title;
    private RelativeLayout showcaseHolder;
    private TextView appreciateCount;
    private TextView copyRightView;
    private TextView appreciateIconView;
    private TextView mediaIconView;
    private Button appreciateButtonView;
    private Button skipButtonView;
    private View root;
    private View loadingContent = null;
    private View noContent =null;
    private ResponseListener responseListener=null;
    private int position=0;

    public LimelightFragment()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_limelight, container, false);
        Log.e(TAG,"IN Lime light fragment");
        initWidgets();
        return root;
    }


    private void initWidgets()
    {
        loadingContent = root.findViewById(R.id.candeo_data_loading);
        noContent = root.findViewById(R.id.candeo_no_content);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(),"fonts/applause.ttf"));
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setText(Configuration.FA_APPRECIATE);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_text)).setText("Fetching Showcases...");
        ((TextView)noContent.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/applause.ttf"));
        ((TextView)noContent.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_APPRECIATE);
        ((TextView)noContent.findViewById(R.id.candeo_no_content_text)).setText("No More Showcases to fetch right now");
        toggleLoading(true);
        toggleNoContent(false);
        showcaseHolder= (RelativeLayout)root.findViewById(R.id.candeo_showcase_holder);
        name=(TextView)root.findViewById(R.id.candeo_user_name);
        title =(TextView)root.findViewById(R.id.candeo_showcase_title);
        appreciateCount=(TextView)root.findViewById(R.id.candeo_appreciate_count);
        copyRightView = (TextView)root.findViewById(R.id.candeo_copyright_icon);
        appreciateIconView = (TextView)root.findViewById(R.id.candeo_appreciate_icon);
        mediaIconView = (TextView)root.findViewById(R.id.candeo_showcase_media_icon);
        appreciateButtonView = (Button)root.findViewById(R.id.candeo_showcase_appreciate_button);
        skipButtonView = (Button)root.findViewById(R.id.candeo_showcase_skip_button);
        avatar = (CircleImageView)root.findViewById(R.id.candeo_showcase_user_avatar);
        Log.e(TAG,"Avatar is "+avatar);
        avatar.setImageURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.default_avatar));
        mediaBg = (ImageView)root.findViewById(R.id.candeo_showcase_media_bg);
        mediaBg.setImageURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/"+ R.raw.default_avatar));
        appreciateButtonView.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/applause.ttf"));

        appreciateButtonView.setText(Configuration.FA_APPRECIATE);
        appreciateButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!Preferences.isUserLoggedIn(getActivity()))
                {


                    startActivity(new Intent(getActivity(),LoginActivity.class));
                }
                else
                {

                    ResponseFragment response = new ResponseFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("introText", "I find this");
                    String[] choices = new String[]{"Good", "Wow", "Superb", "Excellent", "Mesmerizing"};
                    bundle.putStringArray("choices", choices);
                    bundle.putString("title", "Appreciate Showcase");
                    bundle.putString("positiveText", "Appreciate");
                    bundle.putString("showcaseId",showcaseHolder.getTag().toString());
                    bundle.putInt("position",position);
                    bundle.putParcelable("adapter",getArguments().getParcelable("adapter"));
                    response.setArguments(bundle);
                    response.show(getActivity().getSupportFragmentManager(), "Appreciate");
                }


            }
        });
        skipButtonView.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        skipButtonView.setText(Configuration.FA_SKIP);
        skipButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Preferences.isUserLoggedIn(getActivity()))
                {


                    startActivity(new Intent(getActivity(),LoginActivity.class));
                }
                else
                {

                    ResponseFragment response = new ResponseFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("introText", "");
                    String[] choices = new String[]{"Didn't Like", "Offensive", "Plagiarized"};
                    bundle.putStringArray("choices", choices);
                    bundle.putString("title", "Skip Showcase");
                    bundle.putString("positiveText", "Skip");
                    bundle.putString("showcaseId",showcaseHolder.getTag().toString());
                    bundle.putInt("position",position);
                    bundle.putParcelable("adapter",getArguments().getParcelable("adapter"));
                    response.setArguments(bundle);
                    response.show(getActivity().getSupportFragmentManager(), "Skip");
                }
            }
        });
        mediaIconView.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        mediaIconView.setText(Configuration.FA_AUDIO);
        copyRightView.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        copyRightView.setText(Configuration.FA_COPYRIGHT);
        appreciateIconView.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/applause.ttf"));
        appreciateIconView.setText(Configuration.FA_APPRECIATE);
        id=getArguments().getString("id");
        responseListener = (ResponseListener)getArguments().getParcelable("adapter");
        position=getArguments().getInt("position");
        Log.e(TAG,"id is "+id);
        if(!TextUtils.isEmpty(id))
        {
            CandeoApplication.getInstance().getAppRequestQueue().add(new FetchLimelight(id));
        }
        showcaseHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showcaseHolder.getTag()!=null && !TextUtils.isEmpty(showcaseHolder.getTag().toString()) && !"-1".equalsIgnoreCase(showcaseHolder.getTag().toString()) )
                {
                    Intent contentIntent= new Intent(getActivity(), ContentActivity.class);
                    contentIntent.putExtra("id",showcaseHolder.getTag().toString());
                    contentIntent.putExtra("type",Configuration.SHOWCASE);
                    getActivity().startActivity(contentIntent);
                }

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

                            Log.e(TAG, "Fetched limelight");
                            Log.e(TAG,"id fetched is "+id);

                            try {
                                JSONObject limelight = response.getJSONObject("limelight");
                                showcaseHolder.setTag(Integer.toString(limelight.getInt("showcase_id")));
                                name.setText(limelight.getString("name"));
                                title.setText(limelight.getString("title"));
                                appreciateCount.setText(""+limelight.getInt("total_appreciations"));
                                new LoadImageTask(avatar).execute(Configuration.BASE_URL+limelight.getString("user_avatar_url"));
                                Log.e(TAG,"is bg url empty "+TextUtils.isEmpty(limelight.getString("bg_url")));
                                if(TextUtils.isEmpty(limelight.getString("bg_url")) ||"null".equalsIgnoreCase(limelight.getString("bg_url")) )
                                {
                                    Log.e(TAG,"Media url is "+limelight.getString("media_url"));
                                    new LoadImageTask(mediaBg).execute(Configuration.BASE_URL+limelight.getString("media_url"));
                                }
                                else
                                {
                                    new LoadImageTask(mediaBg).execute(Configuration.BASE_URL+limelight.getString("bg_url"));
                                }
                                int mediaType = Integer.parseInt(limelight.getString("media_type"));
                                if(Configuration.AUDIO == mediaType)
                                {
                                    mediaIconView.setText(Configuration.FA_AUDIO);
                                }
                                else if(Configuration.IMAGE == mediaType)
                                {
                                    mediaIconView.setText(Configuration.FA_IMAGE);
                                }
                                toggleLoading(false);
                                toggleNoContent(false);
                            }
                            catch (JSONException jse)
                            {
                                jse.printStackTrace();
                                toggleNoContent(true);
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            toggleLoading(false);
                            toggleNoContent(true);
                        }
                    }
            );
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
                try
                {
                    Animation in = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
                    image.startAnimation(in);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                image.setImageBitmap(bitmap);
            }
        }
    }


    private void toggleLoading(boolean show)
    {
        loadingContent.setVisibility(show?View.VISIBLE:View.GONE);
    }

    private void toggleNoContent(boolean show)
    {
        noContent.setVisibility(show?View.VISIBLE:View.GONE);
    }

}
