package com.candeo.app.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.adapters.LimelightAdapter;
import com.candeo.app.R;
import com.candeo.app.algorithms.Security;
import com.candeo.app.content.PostActivity;
import com.candeo.app.transformers.ShowcaseTransformer;
import com.candeo.app.ui.NonSwipeablePager;
import com.candeo.app.user.LoginActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private ViewPager parentHomePager;
    private NonSwipeablePager showcasePager;
    private Button create;
    private Button feed;
    private Button user;
    private ArrayList<HashMap<String, String>> showcases = new ArrayList<>();
    private LimelightAdapter pagerAdapter;
    private View homeView=null;
    private View loadingContent = null;
    private View noContent =null;
    private static final String TAG="Candeo - HomeFrag";
    private static final String HAS_USER_POSTED_RELATIVE_URL="/users/posted/%s";
    private static final String HAS_USER_POSTED_URL=Configuration.BASE_URL+"/api/v1"+HAS_USER_POSTED_RELATIVE_URL;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

            homeView= inflater.inflate(R.layout.fragment_home, container, false);
            loadingContent = homeView.findViewById(R.id.candeo_data_loading);
            noContent = homeView.findViewById(R.id.candeo_no_content);
            ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(),"fonts/applause.ttf"));
            ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setText(Configuration.FA_APPRECIATE);
            ((TextView)loadingContent.findViewById(R.id.candeo_progress_text)).setText("Fetching Showcases...");
            ((TextView)noContent.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/applause.ttf"));
            ((TextView)noContent.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_APPRECIATE);
            ((TextView)noContent.findViewById(R.id.candeo_no_content_text)).setText("No More Showcases to fetch right now");
            toggleLoading(true);
            toggleNoContent(false);
            parentHomePager=(ViewPager)getActivity().findViewById(R.id.home_pager);
            showcasePager = (NonSwipeablePager)homeView.findViewById(R.id.candeo_showcase_pager);
            showcasePager.setPageTransformer(true, new ShowcaseTransformer());
            create = (Button)homeView.findViewById(R.id.candeo_init_post);
            feed=(Button)homeView.findViewById(R.id.candeo_feed);
            user=(Button)homeView.findViewById(R.id.candeo_user);
            create.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
            create.setText(Configuration.FA_MAGIC);
            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Preferences.isUserLoggedIn(getActivity())) {
                        CandeoUtil.showProgress(getActivity(),"Please Wait...",Configuration.FA_MAGIC);
                        CandeoApplication.getInstance().getAppRequestQueue().add(new CheckUserPostedRequest(Preferences.getUserRowId(getActivity())));
                    } else {
                        Intent postIntent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(postIntent);
                    }


                }
            });
            feed.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
            feed.setText(Configuration.FA_STATS);
            feed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    parentHomePager.setCurrentItem(0);
                }
            });
            user.setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
            user.setText(Configuration.FA_USER);
            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Preferences.isUserLoggedIn(getActivity()))
                    {
                        parentHomePager.setCurrentItem(2);
                    }
                    else
                    {
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }

                }
            });

        return homeView;
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onGetLimelightComplete(JSONObject response)
    {
        if(Configuration.DEBUG)
        {
            if(Configuration.DEBUG)Log.e(TAG,"Getting limelight list");
        }
        try {
            JSONArray list = response.getJSONArray("limelights");
            showcases.clear();
            for(int index=0; index<list.length();++index)
            {
                JSONObject limelight = list.getJSONObject(index);
                HashMap<String, String> limelightMap = new HashMap<>();
                limelightMap.put("id",Integer.toString(limelight.getInt("id")));
                showcases.add(limelightMap);
            }
            HashMap<String, String> limelightMap = new HashMap<>();
            limelightMap.put("id","-1"); //End marker
            showcases.add(limelightMap);
            if(showcases!=null && showcases.size()>0)
            {
                if(Configuration.DEBUG)Log.e(TAG,"fm "+getActivity());
               pagerAdapter = new LimelightAdapter(showcasePager,getActivity().getSupportFragmentManager(),showcases);
                showcasePager.setAdapter(pagerAdapter);
//               pagerAdapter.notifyDataSetChanged();
                toggleLoading(false);
                toggleNoContent(false);
            }
        }
        catch (NullPointerException|JSONException jse)
        {
            jse.printStackTrace();
            if(Configuration.DEBUG)Log.e(TAG,"Error is "+jse.getLocalizedMessage());
            toggleLoading(false);
            toggleNoContent(true);
        }




    }

    class CheckUserPostedRequest extends JsonObjectRequest
    {
        private String id;
        public CheckUserPostedRequest(String id)
        {
            super(String.format(HAS_USER_POSTED_URL,id),
                    null,
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
                                        if(posted)
                                        {
                                            String date = hasPosted.getString("start_date");
                                            CandeoUtil.appAlertDialog(getActivity(),"Hey there, You have already performed for this week. You will be again able to post "+CandeoUtil.formatDateString(date)+" onwards. Meanwhile, you can check other performances and help the right talent to grow.");
                                        }
                                        else
                                        {
                                            Intent postIntent = new Intent(getActivity(), PostActivity.class);
                                            postIntent.putExtra("type", "showcase");
                                            startActivity(postIntent);
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
                String message = String.format(HAS_USER_POSTED_RELATIVE_URL,id);
                params.put("message", message);
                if(Configuration.DEBUG)Log.e(TAG,"secret->"+secret);
                String hash = Security.generateHmac(secret, message);
                if(Configuration.DEBUG)Log.e(TAG,"hash->"+hash);
                params.put("Authorization", "Token token=" + hash);

            }
            return params;
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
