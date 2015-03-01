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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.adapters.UserPagerAdapter;
import com.candeo.app.util.CandeoUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private static final String GET_USER_FANS_API=Configuration.BASE_URL+"/api/v1/users/%s/fans/%s";
    private static final String GET_USER_PROMOTED_API=Configuration.BASE_URL+"/api/v1/users/%s/promoted/%s";
    private String userId="";

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
        CandeoApplication.getInstance().getAppRequestQueue().add(new GetUserFans(userId,"now"));
        CandeoApplication.getInstance().getAppRequestQueue().add(new GetUserPromoted(userId,"now"));


    }

    private class GetUserFans extends JsonObjectRequest
    {
        public GetUserFans(String id, String lastTimeStamp)
        {
            super(Method.GET,
                    String.format(GET_USER_FANS_API,id,lastTimeStamp),
                    null,
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
        }
    }

    private class GetUserPromoted extends JsonObjectRequest
    {
        public GetUserPromoted(String id, String lastTimeStamp)
        {
            super(Method.GET,
                    String.format(GET_USER_PROMOTED_API,id,lastTimeStamp),
                    null,
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
        }
    }

    private void toggleNoContent(View view, boolean show)
    {
        view.setVisibility(show?View.VISIBLE:View.GONE);
    }


}
