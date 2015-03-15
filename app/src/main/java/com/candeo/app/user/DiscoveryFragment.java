package com.candeo.app.user;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.adapters.UserPagerAdapter;
import com.candeo.app.algorithms.Security;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DiscoveryFragment extends Fragment {

    private static final String TAG="Candeo-Discover";
    private View mRoot;
    private UserPagerAdapter appreciationsAdapter;
    private UserPagerAdapter inspirationsAdapter;
    private List<HashMap<String,String>> appreciationList;
    private List<HashMap<String,String>> inspirationList;
    private RecyclerView appreciations;
    private RecyclerView inspirations;
    private LinearLayoutManager appreciationsLinearLayoutManager;
    private LinearLayoutManager inspirationsLinearLayoutManager;
    private View noAppreciations;
    private View noInspirations;
    private static final String GET_USER_APPRECIATIONS_RELATIVE_API="/users/%s/appreciations/%s";
    private static final String GET_USER_APPRECIATIONS_API=Configuration.BASE_URL+"/api/v1"+GET_USER_APPRECIATIONS_RELATIVE_API;
    private static final String GET_USER_INSPIRATIONS_RELATIVE_API="/users/%s/inspirations/%s";
    private static final String GET_USER_INSPIRATIONS_API=Configuration.BASE_URL+"/api/v1"+GET_USER_INSPIRATIONS_RELATIVE_API;
    private String userId="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Log.e(TAG,"In Discovery Fragment create");
        mRoot=inflater.inflate(R.layout.fragment_discovery, container, false);
        initWidgets();
        return mRoot;
    }

    private void initWidgets()
    {
        userId=getArguments().getString("userId");
        appreciations =(RecyclerView)mRoot.findViewById(R.id.candeo_user_discoveries_appreciations);
        inspirations=(RecyclerView)mRoot.findViewById(R.id.candeo_user_discoveries_inspirations);
        appreciationsLinearLayoutManager = new LinearLayoutManager(getActivity());
        appreciationsLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        appreciations.setLayoutManager(appreciationsLinearLayoutManager);
        inspirationsLinearLayoutManager = new LinearLayoutManager(getActivity());
        inspirationsLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        inspirations.setLayoutManager(inspirationsLinearLayoutManager);
        noAppreciations=mRoot.findViewById(R.id.candeo_user_discoveries_no_appreciations);
        noInspirations=mRoot.findViewById(R.id.candeo_user_discoveries_no_inspirations);
        ((TextView)noAppreciations.findViewById(R.id.candeo_no_content_single_line_mid)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(),"fonts/applause.ttf"));
        ((TextView)noAppreciations.findViewById(R.id.candeo_no_content_single_line_mid)).setText(Configuration.FA_APPRECIATE);
        ((TextView)noInspirations.findViewById(R.id.candeo_no_content_single_line_mid)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/response.ttf"));
        ((TextView)noInspirations.findViewById(R.id.candeo_no_content_single_line_mid)).setText(Configuration.FA_INSPIRE);
        toggleNoContent(noAppreciations, true);
        toggleNoContent(noInspirations,true);
        appreciationList = new ArrayList<>();
        inspirationList = new ArrayList<>();
        CandeoApplication.getInstance().getAppRequestQueue().add(new GetUserAppreciations(userId,"now"));
        CandeoApplication.getInstance().getAppRequestQueue().add(new GetUserInspirations(userId,"now"));


    }


    private class GetUserAppreciations extends JsonObjectRequest
    {
        private String id;
        private String lastTimeStamp;
        public GetUserAppreciations(String id, String lastTimeStamp)
        {
            super(Method.GET,
                    String.format(GET_USER_APPRECIATIONS_API,id,lastTimeStamp),
                    null,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            if(response!=null)
                            {
                                try {

                                    JSONArray array = response.getJSONArray("appreciations");
                                    if(array.length()>0)
                                    {
                                        toggleNoContent(noAppreciations,false);
                                        for(int index=0; index<array.length();++index)
                                        {
                                            JSONObject object= array.getJSONObject(index);
                                            HashMap<String,String> appreciation = new HashMap<>();
                                            appreciation.put("id",object.getString("id"));
                                            appreciation.put("user_name",object.getString("user_name"));
                                            appreciation.put("avatar_path",Configuration.BASE_URL+object.getString("avatar_path"));
                                            if(TextUtils.isEmpty(object.getString("bg_url")) ||"null".equalsIgnoreCase(object.getString("bg_url")) )
                                            {
                                                Log.e(TAG,"Media url is "+object.getString("media_url"));
                                                appreciation.put("bg_url",Configuration.BASE_URL+object.getString("media_url"));
                                            }
                                            else
                                            {
                                                appreciation.put("bg_url",Configuration.BASE_URL+object.getString("bg_url"));
                                            }
                                            appreciation.put("user_id",object.getString("user_id"));
                                            appreciation.put("rank",object.getString("rank"));
                                            appreciation.put("created_at",object.getString("created_at"));
                                            appreciation.put("created_at_text",object.getString("created_at_text"));
                                            appreciation.put("media_type",object.getString("media_type"));
                                            appreciation.put("appreciation_count",object.getString("appreciation_count"));
                                            appreciationList.add(appreciation);
                                        }
                                        if(appreciationsAdapter == null)
                                        {
                                            appreciationsAdapter = new UserPagerAdapter(appreciationList,UserPagerAdapter.APPRECIATIONS,getActivity());
                                        }
                                        else
                                        {
                                            appreciationsAdapter.notifyDataSetChanged();
                                        }
                                        appreciations.setVisibility(View.VISIBLE);
                                        appreciations.setAdapter(appreciationsAdapter);
                                    }

                                }
                                catch (JSONException jse)
                                {
                                    jse.printStackTrace();
                                }

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "error is " + error.getLocalizedMessage());
                        }
                    });

                   this.id=id;
                   this.lastTimeStamp=lastTimeStamp;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            if (Preferences.isUserLoggedIn(getActivity()) && !TextUtils.isEmpty(Preferences.getUserEmail(getActivity()))) {
                String secret="";
                params.put("email", Preferences.getUserEmail(getActivity()));
                secret=Preferences.getUserApiKey(getActivity());
                String message = String.format(GET_USER_APPRECIATIONS_RELATIVE_API,id,lastTimeStamp);
                params.put("message", message);
                Log.e(TAG,"secret->"+secret);
                String hash = Security.generateHmac(secret, message);
                Log.e(TAG,"hash->"+hash);
                params.put("Authorization", "Token token=" + hash);

            }

            return params;
        }
    }

    private class GetUserInspirations extends JsonObjectRequest
    {
        private String id;
        private String lastTimeStamp;
        public GetUserInspirations(String id, String lastTimeStamp)
        {
            super(Method.GET,
                    String.format(GET_USER_INSPIRATIONS_API,id,lastTimeStamp),
                    null,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            if(response!=null)
                            {

                                toggleNoContent(noInspirations,false);

                                try {
                                    JSONArray array = response.getJSONArray("inspirations");
                                    Log.e(TAG,"INSPIRATIONS SIZE "+array.length());
                                    if(array.length()>0)
                                    {
                                        toggleNoContent(noInspirations,false);
                                        for(int index=0; index<array.length();++index)
                                        {
                                            JSONObject object= array.getJSONObject(index);
                                            HashMap<String,String> inspiration = new HashMap<>();
                                            inspiration.put("id",object.getString("id"));
                                            inspiration.put("user_name",object.getString("user_name"));
                                            inspiration.put("avatar_path",Configuration.BASE_URL+object.getString("avatar_path"));
                                            if(TextUtils.isEmpty(object.getString("bg_url")) ||"null".equalsIgnoreCase(object.getString("bg_url")) )
                                            {
                                                Log.e(TAG,"Media url is "+object.getString("media_url"));
                                                inspiration.put("bg_url",Configuration.BASE_URL+object.getString("media_url"));
                                            }
                                            else
                                            {
                                                inspiration.put("bg_url",Configuration.BASE_URL+object.getString("bg_url"));
                                            }
                                            inspiration.put("id",object.getString("id"));
                                            inspiration.put("user_id",object.getString("user_id"));
                                            inspiration.put("rank",object.getString("rank"));
                                            inspiration.put("created_at",object.getString("created_at"));
                                            inspiration.put("created_at_text",object.getString("created_at_text"));
                                            inspiration.put("media_type",object.getString("media_type"));
                                            inspiration.put("inspiration_count",object.getString("inspiration_count"));
                                            inspirationList.add(inspiration);
                                        }
                                        if(inspirationsAdapter == null)
                                        {
                                            inspirationsAdapter= new UserPagerAdapter(inspirationList,UserPagerAdapter.INSPIRATIONS,getActivity());
                                        }
                                        else
                                        {
                                            inspirationsAdapter.notifyDataSetChanged();
                                        }
                                        inspirations.setVisibility(View.VISIBLE);
                                        inspirations.setAdapter(inspirationsAdapter);
                                    }
                                }
                                catch (JSONException jse)
                                {
                                    jse.printStackTrace();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "error is " + error.getLocalizedMessage());
                        }
                    });
            this.id=id;
            this.lastTimeStamp=lastTimeStamp;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            if (Preferences.isUserLoggedIn(getActivity()) && !TextUtils.isEmpty(Preferences.getUserEmail(getActivity()))) {
                String secret="";
                params.put("email", Preferences.getUserEmail(getActivity()));
                secret=Preferences.getUserApiKey(getActivity());
                String message = String.format(GET_USER_INSPIRATIONS_RELATIVE_API,id,lastTimeStamp);
                params.put("message", message);
                Log.e(TAG,"secret->"+secret);
                String hash = Security.generateHmac(secret, message);
                Log.e(TAG,"hash->"+hash);
                params.put("Authorization", "Token token=" + hash);

            }
            return params;
        }
    }

    private void toggleNoContent(View view, boolean show)
    {
        view.setVisibility(show?View.VISIBLE:View.GONE);
    }





}
