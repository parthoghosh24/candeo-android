package com.candeo.app.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.amplitude.api.Amplitude;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
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
import com.candeo.app.ui.FontAwesomeDrawable;
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

    private NonSwipeablePager showcasePager;
    private ArrayList<HashMap<String, String>> showcases = new ArrayList<>();
    private LimelightAdapter pagerAdapter;
    private View homeView=null;
    private View loadingContent = null;
    private View noContent =null;
    private FloatingActionButton create;
    private static final String TAG="Candeo - HomeFrag";
    private final static String GET_LIMELIGHT_LIST_RELATIVE_API = "/contents/limelights/list/%s";
    private final static String GET_LIMELIGHT_LIST_API = Configuration.BASE_URL + "/api/v1" + GET_LIMELIGHT_LIST_RELATIVE_API;
    private static final String HAS_USER_POSTED_RELATIVE_URL="/users/posted/%s";
    private static final String HAS_USER_POSTED_URL=Configuration.BASE_URL+"/api/v1"+HAS_USER_POSTED_RELATIVE_URL;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

            homeView= inflater.inflate(R.layout.fragment_home, container, false);
            loadingContent = homeView.findViewById(R.id.candeo_data_loading);
            noContent = homeView.findViewById(R.id.candeo_no_content);
            if(Configuration.DEBUG)Log.e(TAG,"NO CONTENT "+noContent);
            ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/applause.ttf"));
            ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setText(Configuration.FA_APPRECIATE);
            ((TextView)loadingContent.findViewById(R.id.candeo_progress_text)).setText("Fetching Showcases...");
            ((TextView)noContent.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/applause.ttf"));
            ((TextView)noContent.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_APPRECIATE);
            ((TextView)noContent.findViewById(R.id.candeo_no_content_text)).setText("No More Showcases to fetch right now");
            CandeoUtil.toggleView(loadingContent, true);
            CandeoUtil.toggleView(noContent,false);
            showcasePager = (NonSwipeablePager)homeView.findViewById(R.id.candeo_showcase_pager);
            showcasePager.setPageTransformer(true, new ShowcaseTransformer());
            create=(FloatingActionButton)homeView.findViewById(R.id.candeo_init_post);
            FontAwesomeDrawable.FontAwesomeDrawableBuilder builder = new FontAwesomeDrawable.FontAwesomeDrawableBuilder(getActivity(),R.string.fa_magic);
            builder.setColor(getActivity().getResources().getColor(R.color.candeo_white));
            builder.setSize(20);
            create.setImageDrawable(builder.build());
            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Preferences.isUserLoggedIn(getActivity())) {
                        CandeoUtil.showProgress(getActivity(), "Please Wait...", Configuration.FA_MAGIC);
                        CheckUserPostedRequest request = new CheckUserPostedRequest(Preferences.getUserRowId(getActivity()));
                        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        CandeoApplication.getInstance().getAppRequestQueue().add(request);
                    } else {
                        Intent postIntent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(postIntent);
                    }
                }
            });

//        requestRefresh(getActivity());
        return homeView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }


    public void requestRefresh(Activity activity)
    {
        String id = TextUtils.isEmpty(Preferences.getUserRowId(activity)) ? "0" : Preferences.getUserRowId(activity);
        FetchLimelightList fetchLimelightListRequest = new FetchLimelightList(id);
        fetchLimelightListRequest.setShouldCache(false);
        fetchLimelightListRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*10, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        CandeoApplication.getInstance().getAppRequestQueue().add(fetchLimelightListRequest);
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
            CandeoUtil.toggleView(noContent,true);
            if(showcases!=null && showcases.size()>0)
            {
                if(Configuration.DEBUG)Log.e(TAG,"fm "+getActivity());
               pagerAdapter = new LimelightAdapter(showcasePager,getChildFragmentManager(),showcases);
                if(showcasePager!=null && pagerAdapter!=null)
                {
                    showcasePager.setAdapter(pagerAdapter);
                }

//               pagerAdapter.notifyDataSetChanged();
                CandeoUtil.toggleView(loadingContent,false);
                CandeoUtil.toggleView(noContent,false);
            }
        }
        catch (NullPointerException|JSONException jse)
        {
            jse.printStackTrace();
            if(Configuration.DEBUG)Log.e(TAG,"Error is "+jse.getLocalizedMessage());
            CandeoUtil.toggleView(loadingContent,false);
            CandeoUtil.toggleView(noContent,true);
        }




    }



    private class FetchLimelightList extends JsonObjectRequest {
        private String id;
        public FetchLimelightList(String id) {
            super(Method.GET,
                    String.format(GET_LIMELIGHT_LIST_API, id),
                    new JSONObject(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            onGetLimelightComplete(response);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            if(Configuration.DEBUG)Log.e(TAG, "Error occured");
                            if(Configuration.DEBUG)Log.e(TAG, "localized error while fetching is limelight " + error.getLocalizedMessage());
                            NetworkResponse response = error.networkResponse;
                            if (response != null) {
                                if(Configuration.DEBUG)Log.e(TAG, "Actual error while fetching limelight is " + new String(response.data));
                            }
                            if(noContent!=null && loadingContent!=null)
                            {
                                CandeoUtil.toggleView(noContent,true);
                                CandeoUtil.toggleView(loadingContent,false);
                            }


                        }
                    }
            );
            this.id=id;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            String secret="";
            if (getActivity()!=null && (Preferences.isUserLoggedIn(getActivity()) && !TextUtils.isEmpty(Preferences.getUserEmail(getActivity())))) {
                params.put("email", Preferences.getUserEmail(getActivity()));
                secret=Preferences.getUserApiKey(getActivity());

            } else {
                params.put("email", "");
                secret=Configuration.CANDEO_DEFAULT_SECRET;
            }
            String message = String.format(GET_LIMELIGHT_LIST_RELATIVE_API,id);
            params.put("message", message);
            if(Configuration.DEBUG)Log.e(TAG,"secret->"+secret);
            String hash = Security.generateHmac(secret, message);
            if(Configuration.DEBUG)Log.e(TAG,"hash->"+hash);
            params.put("Authorization", "Token token=" + hash);
            return params;
        }
    }



    class CheckUserPostedRequest extends JsonObjectRequest
    {
        private String id;
        public CheckUserPostedRequest(String id)
        {
            super(Method.GET,
                    String.format(HAS_USER_POSTED_URL,id),
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




}
