package com.candeo.app.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.adapters.UserContentAdapter;
import com.candeo.app.home.HomeActivity;
import com.candeo.app.ui.SlidingTabLayout;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.NetworkUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment {


    private ImageView userAvatar;
    private TextView userName;

    //stats
    private TextView appreciateIcon;
    private TextView appreciateCount;

    private TextView inspireIcon;
    private TextView inspireCount;

    private SlidingTabLayout slidingTabs;

    private ViewPager userContentPager;
    private CreatedFragment createdFragment;
    private SocialFragment socialFragment;
    private UserContentAdapter contentAdapter;
    private View notLoggedIn;

    private View root;

    private final static String TAG="Candeo - User Fragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            root=inflater.inflate(R.layout.fragment_user, container, false);



            if(Configuration.DEBUG)
            {
                Log.e(TAG,"User API KEY is "+ Preferences.getUserApiKey(getActivity()));
                Log.e(TAG,"User username is "+ Preferences.getUserUsername(getActivity()));
                Log.e(TAG,"User Full Name is "+ Preferences.getUserName(getActivity()));
                Log.e(TAG,"User Email is "+ Preferences.getUserEmail(getActivity()));
                Log.e(TAG,"User Server Db row Id is "+ Preferences.getUserRowId(getActivity()));
            }
            initWidgets();
            return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void onGetUserComplete(JSONObject response)
    {
        if(response!=null)
        {
            if(Configuration.DEBUG)
            {
                Log.e(TAG,"I am called");
            }
            if(response.length()>0)
            {
                try {
                    JSONObject user =response.getJSONObject("user");
                    userName.setText(user.getString("name"));
                    appreciateCount.setText(""+user.getInt("total_appreciations"));
                    inspireCount.setText(""+user.getInt("total_inspires"));
                    new LoadImageTask().execute(Configuration.BASE_URL+user.getString("avatar_path"));

                }
                catch (JSONException je)
                {
                    je.printStackTrace();
                }

            }
            else
            {
                Toast.makeText(getActivity(),"Failed to fetch user! Try again",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void initWidgets()
    {
        userAvatar = (CircleImageView)root.findViewById(R.id.candeo_user_avatar);
        userAvatar.setImageURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/"+ R.raw.default_avatar));
        userName = (TextView)root.findViewById(R.id.candeo_user_name_text);
        appreciateIcon = (TextView)root.findViewById(R.id.candeo_user_appreciate_icon);
        appreciateIcon.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/applause.ttf"));
        appreciateIcon.setText(Configuration.FA_APPRECIATE);
        appreciateCount=(TextView)root.findViewById(R.id.candeo_user_appreciate_count);
        inspireIcon=(TextView)root.findViewById(R.id.candeo_user_inspired_icon);
        inspireIcon.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/response.ttf"));
        inspireIcon.setText(Configuration.FA_INSPIRE);
        inspireCount=(TextView)root.findViewById(R.id.candeo_user_inspired_count);
        slidingTabs = (SlidingTabLayout)root.findViewById(R.id.candeo_user_sliding_tabs);
        userContentPager=(ViewPager)root.findViewById(R.id.candeo_user_content_pager);
        createdFragment = new CreatedFragment();
        socialFragment = new SocialFragment();
        contentAdapter = new UserContentAdapter((HomeActivity)getActivity(), createdFragment,socialFragment);
        userContentPager.setAdapter(contentAdapter);
        slidingTabs.setViewPager(userContentPager);
        userContentPager.setCurrentItem(0);
        Log.e(TAG,"page current item is "+userContentPager.getCurrentItem());
        notLoggedIn = root.findViewById(R.id.candeo_user_not_logged_in);
        ((TextView)notLoggedIn.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(),"fonts/fa.ttf"));
        ((TextView)notLoggedIn.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_USER);
        notLoggedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        if(Preferences.isUserLoggedIn(getActivity()))
        {
            notLoggedIn.setVisibility(View.GONE);
        }
        else
        {
            notLoggedIn.setVisibility(View.VISIBLE);
        }
    }



    private class LoadImageTask extends AsyncTask<String, String, Bitmap> {


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
                userAvatar.setImageBitmap(bitmap);
            }
        }
    }

}
