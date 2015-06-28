package com.candeo.app.shout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.adapters.ShoutPagerAdapter;
import com.candeo.app.algorithms.Security;
import com.candeo.app.content.PostActivity;
import com.candeo.app.ui.FontAwesomeDrawable;
import com.candeo.app.ui.SlidingTabLayout;
import com.candeo.app.user.LoginActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShoutFragment extends Fragment implements ShoutListener{


    private View homeView=null;
    private FloatingActionButton shout;
    private ViewPager shoutPager = null;
    private ShoutPagerAdapter shoutPagerAdapter = null;
    private ShoutListFragment shoutListFragment=null;
    private ShoutNetworkFragment shoutNetworkFragment = null;
    private SlidingTabLayout slidingTabs;
    public static ArrayList<String> networkIdList = new ArrayList<>();
    private static final String TAG="shoutfrag";
    private ShoutListener mListener=null;
    private View notLoggedIn;
    private static final String CAN_USER_SHOUT_RELATIVE_URL="/users/shout/%s";
    private static final String CAN_USER_SHOUT_URL=Configuration.BASE_URL+"/api/v1"+CAN_USER_SHOUT_RELATIVE_URL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        StringBuilder builer = new StringBuilder();
        homeView = inflater.inflate(R.layout.fragment_shout, container, false);
        notLoggedIn = homeView.findViewById(R.id.candeo_user_not_logged_in);
        ((TextView)notLoggedIn.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        ((TextView)notLoggedIn.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_BULLHORN);
        ((TextView)notLoggedIn.findViewById(R.id.candeo_no_content_text)).setText("Tap to signin/signup to shout to network");
        notLoggedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        if(Preferences.isUserLoggedIn(getActivity()))
        {
            notLoggedIn.setVisibility(View.GONE);
            shout = (FloatingActionButton)homeView.findViewById(R.id.candeo_create_shout);
            FontAwesomeDrawable.FontAwesomeDrawableBuilder builder = new FontAwesomeDrawable.FontAwesomeDrawableBuilder(getActivity(),R.string.fa_bullhorn);
            builder.setColor(getActivity().getResources().getColor(R.color.candeo_white));
            builder.setSize(20);
            shout.setImageDrawable(builder.build());
            slidingTabs = (SlidingTabLayout)homeView.findViewById(R.id.candeo_shout_sliding_tabs);
            shoutPager = (ViewPager)homeView.findViewById(R.id.candeo_shouts_pager);
            shoutListFragment = new ShoutListFragment();
            shoutNetworkFragment = new ShoutNetworkFragment();
            shoutPagerAdapter = new ShoutPagerAdapter(getChildFragmentManager(),shoutListFragment,shoutNetworkFragment);
            mListener=this;
            shoutPager.setAdapter(shoutPagerAdapter);
            slidingTabs.setViewPager(shoutPager);
            slidingTabs.setDistributeEvenly(true);
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

            shout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CandeoUtil.showProgress(getActivity(), "Please Wait...", Configuration.FA_BULLHORN);
                    CheckUserCanShoutRequest request = new CheckUserCanShoutRequest(Preferences.getUserRowId(getActivity()));
                    request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    CandeoApplication.getInstance().getAppRequestQueue().add(request);
                }
            });
        }
        else
        {
            notLoggedIn.setVisibility(View.VISIBLE);
        }


        return homeView;
    }

    private String getIdsAsString(ArrayList<String> ids)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("");
        if(ids.size()>0)
        {
           for(int index=0 ;index<ids.size();++index)
           {
               builder.append(ids.get(index));
               if(index!=ids.size()-1)
               {
                   builder.append(",");
               }
           }
        }
        return builder.toString();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onSuccess() {
        shoutPager.setCurrentItem(0,true);
        shoutListFragment.requestRefresh();
    }

    @Override
    public void onFailure() {
        Toast.makeText(getActivity(),"Failed to shout! Please try again",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    class CheckUserCanShoutRequest extends JsonObjectRequest
    {
        private String id;
        public CheckUserCanShoutRequest(String id)
        {
            super(Method.GET,
                    String.format(CAN_USER_SHOUT_URL,id),
                    new JSONObject(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            CandeoUtil.hideProgress();
                            if(response!=null)
                            {
                                try {
                                    JSONObject hasPosted = response.getJSONObject("response");
                                    if(hasPosted!=null)
                                    {
                                        boolean posted = hasPosted.getBoolean("state");
                                        if(!posted)
                                        {
                                            CandeoUtil.appAlertDialog(getActivity(),"You Should have at least have one fan or you should at least promote one user to shout. Create or Appreciate to shout.");
                                        }
                                        else
                                        {
                                            ShoutPostFragment shout = new ShoutPostFragment();
                                            shout.setShoutListener(mListener);
                                            Bundle bundle = new Bundle();
                                            bundle.putInt("participants", networkIdList.size());
                                            bundle.putString("ids",getIdsAsString(networkIdList));
                                            networkIdList.toArray();
                                            bundle.putStringArrayList("participantsList",networkIdList);
                                            shout.setArguments(bundle);
                                            shout.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
                                            shout.show(getChildFragmentManager().beginTransaction(), "Shout");
                                        }

                                    }
                                }
                                catch (JSONException jse)
                                {
                                    jse.printStackTrace();
                                    Toast.makeText(getActivity(),"Please try again",Toast.LENGTH_SHORT).show();;
                                }

                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            CandeoUtil.hideProgress();
                            Toast.makeText(getActivity(),"Please try again",Toast.LENGTH_SHORT).show();;
                        }
                    });
            this.id=id;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            if (Preferences.isUserLoggedIn(getActivity()) && !TextUtils.isEmpty(Preferences.getUserEmail(getActivity()))) {
                String secret="";
                params.put("email", Preferences.getUserEmail(getActivity()));
                secret=Preferences.getUserApiKey(getActivity());
                String message = String.format(CAN_USER_SHOUT_RELATIVE_URL,id);
                params.put("message", message);
                if(Configuration.DEBUG) Log.e(TAG, "secret->" + secret);
                String hash = Security.generateHmac(secret, message);
                if(Configuration.DEBUG)Log.e(TAG,"hash->"+hash);
                params.put("Authorization", "Token token=" + hash);

            }
            return params;
        }
    }


}
