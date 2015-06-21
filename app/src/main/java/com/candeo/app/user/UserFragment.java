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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.adapters.GeneralUserPagerAdapter;
import com.candeo.app.adapters.UserContentAdapter;
import com.candeo.app.algorithms.Security;
import com.candeo.app.home.HomeActivity;
import com.candeo.app.ui.SlidingTabLayout;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.NetworkUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment implements UserProfileUpdateListener {


    private ImageView userAvatar;
    private TextView userName;
    private TextView userBio;
    private String name;
    private String bio;
    private String avatarUrl;

    //stats
    private TextView appreciateIcon;
    private TextView appreciateCount;

    private TextView inspireIcon;
    private TextView inspireCount;

    private TextView currentRank;
    private TextView highestRank;

    private SlidingTabLayout slidingTabs;

    private ViewPager userContentPager;
    private CreatedFragment createdFragment;
    private SocialFragment socialFragment;
    private DiscoveryFragment discoveryFragment;
    private FragmentStatePagerAdapter contentAdapter;
    private View notLoggedIn;
    private View loadingContent = null;
    private View root;
    private String userId="";
    private LinearLayout updateUserVeil;
    private LinearLayout updateUserButton;
    private TextView updateProfileText;
    private UserProfileUpdateListener userProfileUpdateListener=null;
    private final static String GET_USER_RELATIVE_API = "/users/%s";
    private final static String GET_USER_API = Configuration.BASE_URL + "/api/v1" + GET_USER_RELATIVE_API;

    private final static String TAG="Candeo - User Fragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            root=inflater.inflate(R.layout.fragment_user, container, false);

            userProfileUpdateListener=this;

            if(Configuration.DEBUG)
            {
                if(Configuration.DEBUG)Log.e(TAG,"User API KEY is "+ Preferences.getUserApiKey(getActivity()));
                if(Configuration.DEBUG)Log.e(TAG,"User username is "+ Preferences.getUserUsername(getActivity()));
                if(Configuration.DEBUG)Log.e(TAG,"User Full Name is "+ Preferences.getUserName(getActivity()));
                if(Configuration.DEBUG)Log.e(TAG,"User Email is "+ Preferences.getUserEmail(getActivity()));
                if(Configuration.DEBUG)Log.e(TAG,"User Server Db row Id is "+ Preferences.getUserRowId(getActivity()));
            }
            initWidgets();
            return root;
    }

    @Override
    public void onProfileUpdate(HashMap<String, String> params) {
        if(params!=null && params.size()>0)
        {
            if(!TextUtils.isEmpty(params.get("name")))
            {
                userName.setText(params.get("name"));
            }

            if(!TextUtils.isEmpty(params.get("bio")))
            {
                userBio.setText(params.get("bio"));
            }

            if(!TextUtils.isEmpty(params.get("avatarUrl")))
            {
                new LoadImageTask().execute(params.get("avatarUrl"));
            }

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void requestRefresh(Activity activity)
    {
            GetUserRequest userRequest = new GetUserRequest(userId);
            userRequest.setShouldCache(false);
            userRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*10, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            CandeoApplication.getInstance().getAppRequestQueue().add(userRequest);
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
                if(Configuration.DEBUG)Log.e(TAG,"I am called");
            }
            if(response.length()>0)
            {
                try {
                    JSONObject user =response.getJSONObject("user");
                    userId = user.getString("id");
                    if(Configuration.DEBUG)Log.e(TAG,"USER ID IS "+userId);
                    name=user.getString("name");
                    if(getActivity() instanceof UserActivity)
                    {
                        ((UserActivity)getActivity()).getSupportActionBar().setTitle(name);
                    }
                    bio=user.getString("about");
                    avatarUrl=user.getString("avatar_path");
                    userName.setText(name);
                    userBio.setText(TextUtils.isEmpty(bio) ? Configuration.CANDEO_DEFAULT_BIO : bio);
                    appreciateCount.setText("" + user.getInt("total_appreciations"));
                    inspireCount.setText("" + user.getInt("total_inspires"));
                    currentRank.setText("" + user.get("current_rank"));
                    highestRank.setText("" + user.get("highest_rank"));
                    new LoadImageTask().execute(avatarUrl);
                    Bundle bundle = new Bundle();
                    bundle.putString("userId",userId);
                    bundle.putString("name",userName.getText().toString());
                    createdFragment = new CreatedFragment();
                    createdFragment.setArguments(bundle);
                    socialFragment = new SocialFragment();
                    socialFragment.setArguments(bundle);
                    discoveryFragment = new DiscoveryFragment();
                    discoveryFragment.setArguments(bundle);
                    boolean isSameUser = getActivity()!=null && Preferences.getUserRowId(getActivity()).equalsIgnoreCase(userId);
                    contentAdapter = isSameUser? new UserContentAdapter(getChildFragmentManager(), createdFragment,socialFragment,discoveryFragment) : new GeneralUserPagerAdapter(getActivity().getSupportFragmentManager(), createdFragment,socialFragment);
                    userContentPager.setAdapter(contentAdapter);
                    slidingTabs.setViewPager(userContentPager);
                    slidingTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                        @Override
                        public int getIndicatorColor(int position) {
                            return getResources().getColor(R.color.candeo_light_btn_blue);
                        }

                        @Override
                        public int getDividerColor(int position) {
                            return 0;
                        }
                    });
                    userContentPager.setCurrentItem(0);
                    if(Configuration.DEBUG)Log.e(TAG,"page current item is "+userContentPager.getCurrentItem());
                    if(Preferences.isUserLoggedIn(getActivity()) || !TextUtils.isEmpty(userId))
                    {
                        notLoggedIn.setVisibility(View.GONE);
                    }
                    else
                    {
                        notLoggedIn.setVisibility(View.VISIBLE);
                    }
                    if(Preferences.isUserLoggedIn(getActivity()) && !TextUtils.isEmpty(userId) && userId.equalsIgnoreCase(Preferences.getUserRowId(getActivity())))
                    {
                        updateUserVeil.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        updateUserVeil.setVisibility(View.GONE);
                    }
                    if(Configuration.DEBUG)Log.e(TAG,"USER LOADED");

                }
                catch (JSONException je)
                {
                    je.printStackTrace();
                    if(Configuration.DEBUG)Log.e(TAG,"ERROR IS "+je.getLocalizedMessage());
                }
                toggleLoading(false);

            }
            else
            {
                Toast.makeText(getActivity(),"Failed to fetch user! Try again",Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void initWidgets()
    {
        loadingContent = root.findViewById(R.id.candeo_data_loading);
        userId=getArguments().getString("id");
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(),"fonts/fa.ttf"));
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setText(Configuration.FA_USER);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_text)).setText("Loading User...");
        toggleLoading(true);
        userAvatar = (CircleImageView)root.findViewById(R.id.candeo_user_avatar);
        userAvatar.setImageURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/"+ R.raw.default_avatar));
        userName = (TextView)root.findViewById(R.id.candeo_user_name_text);
        userBio=(TextView)root.findViewById(R.id.candeo_user_bio);
        appreciateIcon = (TextView)root.findViewById(R.id.candeo_user_appreciate_icon);
        appreciateIcon.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/applause.ttf"));
        appreciateIcon.setText(Configuration.FA_APPRECIATE);
        appreciateCount=(TextView)root.findViewById(R.id.candeo_user_appreciate_count);
        inspireIcon=(TextView)root.findViewById(R.id.candeo_user_inspired_icon);
        inspireIcon.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/response.ttf"));
        inspireIcon.setText(Configuration.FA_INSPIRE);
        inspireCount=(TextView)root.findViewById(R.id.candeo_user_inspired_count);
        currentRank=(TextView)root.findViewById(R.id.candeo_user_current_rank_value);
        highestRank=(TextView)root.findViewById(R.id.candeo_user_highest_rank_value);
        slidingTabs = (SlidingTabLayout)root.findViewById(R.id.candeo_user_sliding_tabs);
        userContentPager=(ViewPager)root.findViewById(R.id.candeo_user_content_pager);
        updateUserVeil=(LinearLayout)root.findViewById(R.id.candeo_user_update_veil);
        updateUserButton=(LinearLayout)root.findViewById(R.id.candeo_user_update_button);
        if(Configuration.DEBUG)Log.e(TAG,"is same user? "+(Preferences.isUserLoggedIn(getActivity()) && !TextUtils.isEmpty(userId) && userId.equalsIgnoreCase(Preferences.getUserRowId(getActivity()))));
        updateProfileText=(TextView)root.findViewById(R.id.candeo_user_update_profile_text);
        updateProfileText.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(),"fonts/fa.ttf"));
        updateProfileText.setText(Configuration.FA_PENCIL);
        updateUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amplitude.getInstance().logEvent("User update profile button clicked");
                ProfileUpdateFragment profileUpdateFragment = new ProfileUpdateFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name",name);
                bundle.putString("bio",bio);
                bundle.putString("avatarUrl",avatarUrl);
                profileUpdateFragment.setArguments(bundle);
                profileUpdateFragment.setUpdateProfileListener(userProfileUpdateListener);
                profileUpdateFragment.setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Light_Dialog);
                profileUpdateFragment.show(getActivity().getSupportFragmentManager(), "Update Profile");
            }
        });

        notLoggedIn = root.findViewById(R.id.candeo_user_not_logged_in);
        ((TextView)notLoggedIn.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(),"fonts/fa.ttf"));
        ((TextView)notLoggedIn.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_USER);
        notLoggedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        if(Preferences.isUserLoggedIn(getActivity()) || !TextUtils.isEmpty(userId))
        {
            notLoggedIn.setVisibility(View.GONE);
        }
        else
        {
            notLoggedIn.setVisibility(View.VISIBLE);
        }
        requestRefresh(getActivity());

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

    private void toggleLoading(boolean show)
    {
        loadingContent.setVisibility(show?View.VISIBLE:View.GONE);
    }

    private class GetUserRequest extends JsonObjectRequest {
        private String id;
        public GetUserRequest(String id) {
            super(Method.GET,
                    String.format(GET_USER_API, id),
                    new JSONObject(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            onGetUserComplete(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(Configuration.DEBUG)Log.e(TAG, "error is " + error.getLocalizedMessage());
                        }
                    });
            this.id=id;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            String secret="";
            if (Preferences.isUserLoggedIn(getActivity()) && !TextUtils.isEmpty(Preferences.getUserEmail(getActivity()))) {
                params.put("email", Preferences.getUserEmail(getActivity()));
                secret=Preferences.getUserApiKey(getActivity());

            } else {
                params.put("email", "");
                secret=Configuration.CANDEO_DEFAULT_SECRET;
            }
            String message = String.format(GET_USER_RELATIVE_API,id);
            params.put("message", message);
            if(Configuration.DEBUG)Log.e(TAG,"secret->"+secret);
            String hash = Security.generateHmac(secret, message);
            if(Configuration.DEBUG)Log.e(TAG,"hash->"+hash);
            params.put("Authorization", "Token token=" + hash);
            return params;
        }
    }

}
