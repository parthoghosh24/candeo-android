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

public class SocialFragment extends Fragment {

    private static final String TAG="Candeo-UserSocial";
    private View mRoot;
    private UserPagerAdapter fansAdapter;
    private UserPagerAdapter promotedAdapter;
    private List<HashMap<String,String>> fansList;
    private List<HashMap<String,String>> promotedList;
    private RecyclerView fans;
    private RecyclerView promoted;
    private LinearLayoutManager fansLinearLayoutManager;
    private LinearLayoutManager promotedLinearLayoutManager;
    private View noFans;
    private View noPromoted;
    private static final String GET_USER_FANS_RELATIVE_API="/users/%s/fans/%s";
    private static final String GET_USER_FANS_API=Configuration.BASE_URL+"/api/v1"+GET_USER_FANS_RELATIVE_API;
    private static final String GET_USER_PROMOTED_RELATIVE_API="/users/%s/promoted/%s";
    private static final String GET_USER_PROMOTED_API=Configuration.BASE_URL+"/api/v1"+GET_USER_PROMOTED_RELATIVE_API;
    private String userId="";
    private String name="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRoot=inflater.inflate(R.layout.fragment_social, container, false);
        initWidgets();
        return mRoot;
    }

    private void initWidgets()
    {

        userId=getArguments().getString("userId");
        name=getArguments().getString("name");
        fans =(RecyclerView)mRoot.findViewById(R.id.candeo_user_social_fans);
        promoted=(RecyclerView)mRoot.findViewById(R.id.candeo_user_social_promoted);
        fansLinearLayoutManager = new LinearLayoutManager(getActivity());
        fansLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        fans.setLayoutManager(fansLinearLayoutManager);
        promotedLinearLayoutManager = new LinearLayoutManager(getActivity());
        promotedLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        promoted.setLayoutManager(promotedLinearLayoutManager);
        noFans=mRoot.findViewById(R.id.candeo_user_social_no_fans);
        noPromoted=mRoot.findViewById(R.id.candeo_user_social_no_promoted);
        ((TextView)noFans.findViewById(R.id.candeo_no_content_single_line_mid)).setText("fans following");
        ((TextView)noPromoted.findViewById(R.id.candeo_no_content_single_line_mid)).setText("stars promoted");
        toggleNoContent(noFans,true);
        toggleNoContent(noPromoted,true);
        fansList = new ArrayList<>();
        promotedList = new ArrayList<>();
        if(userId.equalsIgnoreCase(Preferences.getUserRowId(getActivity())))
        {
            ((TextView)mRoot.findViewById(R.id.candeo_user_social_fans_header)).setText("My Fans");
            ((TextView)mRoot.findViewById(R.id.candeo_user_social_promoted_header)).setText("My Promoted Stars");
        }
        else
        {
            ((TextView)mRoot.findViewById(R.id.candeo_user_social_fans_header)).setText(name+"'s Fans");
            ((TextView)mRoot.findViewById(R.id.candeo_user_social_promoted_header)).setText(name+"'s Promoted Stars");
        }
        CandeoApplication.getInstance().getAppRequestQueue().add(new GetUserFans(userId,"now"));
        CandeoApplication.getInstance().getAppRequestQueue().add(new GetUserPromoted(userId,"now"));


    }

    private class GetUserFans extends JsonObjectRequest
    {
        private String id, lastTimeStamp;
        public GetUserFans(String id, String lastTimeStamp)
        {
            super(Method.GET,
                    String.format(GET_USER_FANS_API,id,lastTimeStamp),
                    new JSONObject(),
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            if(response!=null)
                            {
                                try {

                                    JSONArray array = response.getJSONArray("fans");
                                    if(array.length()>0)
                                    {
                                        toggleNoContent(noFans,false);
                                        for(int index=0; index<array.length();++index)
                                        {
                                            JSONObject object= array.getJSONObject(index);
                                            HashMap<String,String> fans = new HashMap<>();
                                            fans.put("id", object.getString("id"));
                                            fans.put("user_name", object.getString("name"));
                                            fans.put("avatar_path", Configuration.BASE_URL + object.getString("avatar_path"));
                                            fansList.add(fans);
                                        }
                                        if(fansAdapter == null)
                                        {
                                            fansAdapter = new UserPagerAdapter(fansList,UserPagerAdapter.FANS,getActivity());
                                        }
                                        else
                                        {
                                            fansAdapter.notifyDataSetChanged();
                                        }
                                        fans.setVisibility(View.VISIBLE);
                                        fans.setAdapter(fansAdapter);
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
            String secret="";
            if (Preferences.isUserLoggedIn(getActivity()) && !TextUtils.isEmpty(Preferences.getUserEmail(getActivity()))) {
                params.put("email", Preferences.getUserEmail(getActivity()));
                secret=Preferences.getUserApiKey(getActivity());

            } else {
                params.put("email", "");
                secret=Configuration.CANDEO_DEFAULT_SECRET;
            }
            String message = String.format(GET_USER_FANS_RELATIVE_API,id,lastTimeStamp);
            params.put("message", message);
            Log.e(TAG,"secret->"+secret);
            String hash = Security.generateHmac(secret, message);
            Log.e(TAG,"hash->"+hash);
            params.put("Authorization", "Token token=" + hash);
            return params;
        }
    }

    private class GetUserPromoted extends JsonObjectRequest
    {
        private String id, lastTimeStamp;
        public GetUserPromoted(String id, String lastTimeStamp)
        {
            super(Method.GET,
                    String.format(GET_USER_PROMOTED_API,id,lastTimeStamp),
                    new JSONObject(),
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            if(response!=null)
                            {


                                try {
                                    JSONArray array = response.getJSONArray("promoted");
                                    Log.e(TAG,"Promoted count "+array.length());
                                    if(array.length()>0)
                                    {
                                        toggleNoContent(noPromoted,false);
                                        for(int index=0; index<array.length();++index)
                                        {
                                            JSONObject object= array.getJSONObject(index);
                                            HashMap<String,String> promoted = new HashMap<>();
                                            promoted.put("id", object.getString("id"));
                                            promoted.put("user_name", object.getString("name"));
                                            promoted.put("avatar_path", Configuration.BASE_URL + object.getString("avatar_path"));
                                            promotedList.add(promoted);
                                        }
                                        if(promotedAdapter == null)
                                        {
                                            promotedAdapter= new UserPagerAdapter(promotedList,UserPagerAdapter.PROMOTED,getActivity());
                                        }
                                        else
                                        {
                                            promotedAdapter.notifyDataSetChanged();
                                        }
                                        promoted.setVisibility(View.VISIBLE);

                                        promoted.setAdapter(promotedAdapter);
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
            String secret="";
            if (Preferences.isUserLoggedIn(getActivity()) && !TextUtils.isEmpty(Preferences.getUserEmail(getActivity()))) {
                params.put("email", Preferences.getUserEmail(getActivity()));
                secret=Preferences.getUserApiKey(getActivity());

            } else {
                params.put("email", "");
                secret=Configuration.CANDEO_DEFAULT_SECRET;
            }
            String message = String.format(GET_USER_PROMOTED_RELATIVE_API,id,lastTimeStamp);
            params.put("message", message);
            Log.e(TAG,"secret->"+secret);
            String hash = Security.generateHmac(secret, message);
            Log.e(TAG,"hash->"+hash);
            params.put("Authorization", "Token token=" + hash);
            return params;
        }
    }

    private void toggleNoContent(View view, boolean show)
    {
        view.setVisibility(show?View.VISIBLE:View.GONE);
    }


}
