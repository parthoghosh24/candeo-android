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

public class CreatedFragment extends Fragment {


    private View noUserCreatedContent;
    private static final String TAG="Candeo-User Creation";
    private View mRoot;
    private LinearLayoutManager creationsLinearLayoutManager;
    private UserPagerAdapter creationsAdapter;
    private List<HashMap<String,String>> creationList;
    private RecyclerView creations;
    private static final String GET_USER_CREATIONS_RELATIVE_API="/users/%s/showcases/%s";
    private static final String GET_USER_CREATIONS_API=Configuration.BASE_URL+"/api/v1"+GET_USER_CREATIONS_RELATIVE_API;
    private String userId="";
    private boolean loading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private String lastTimestamp="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        mRoot=inflater.inflate(R.layout.fragment_created, container, false);
        initWidgets();
        return mRoot;
    }

    private void initWidgets()
    {
        userId=getArguments().getString("userId");
        creations =(RecyclerView)mRoot.findViewById(R.id.candeo_user_creations);
        creationsLinearLayoutManager = new LinearLayoutManager(getActivity());
        creationsLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        creations.setLayoutManager(creationsLinearLayoutManager);
        noUserCreatedContent = mRoot.findViewById(R.id.candeo_user_no_created_content);
        noUserCreatedContent.setBackgroundColor(getActivity().getResources().getColor(R.color.background_floating_material_light));
        ((TextView)noUserCreatedContent.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        ((TextView)noUserCreatedContent.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_MAGIC);
        ((TextView)noUserCreatedContent.findViewById(R.id.candeo_no_content_icon)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        ((TextView)noUserCreatedContent.findViewById(R.id.candeo_no_content_text)).setText("No content created yet.");
        ((TextView)noUserCreatedContent.findViewById(R.id.candeo_no_content_text)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        CandeoUtil.toggleView(noUserCreatedContent, true);
        creationList = new ArrayList<>();
        creations.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = creationsLinearLayoutManager.getChildCount();
                Log.e(TAG, "visible item count " + visibleItemCount);
                totalItemCount = creationsLinearLayoutManager.getItemCount();
                Log.e(TAG,"total item count "+totalItemCount);
                pastVisibleItems = creationsLinearLayoutManager.findFirstVisibleItemPosition();
                Log.e(TAG,"past visible item count "+pastVisibleItems);
                //TODO Not working, need to see
                if (loading) {
                    Log.e(TAG,"in loading");
                    if ( (visibleItemCount+pastVisibleItems) >= totalItemCount) {
                        loading = false;
                        loadMore();
                    }
                }
            }
        });
        CandeoApplication.getInstance().getAppRequestQueue().add(new GetUserCreations(userId,"now"));

    }

    private void loadMore()
    {
        Log.e(TAG,"last timestamp "+lastTimestamp);
        CandeoApplication.getInstance().getAppRequestQueue().add(new GetUserCreations(userId,lastTimestamp));
    }

    private class GetUserCreations extends JsonObjectRequest
    {
        private String id, lastTimeStamp;
        public GetUserCreations(String id, String lastTimeStamp)
        {
            super(Method.GET,
                    String.format(GET_USER_CREATIONS_API,id,lastTimeStamp),
                    new JSONObject(),
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            if(response!=null)
                            {
                                try {

                                    JSONArray array = response.getJSONArray("showcases");
                                    creationList.clear();
                                    if(array.length()>0)
                                    {
                                        CandeoUtil.toggleView(noUserCreatedContent,false);
                                        for(int index=0; index<array.length();++index)
                                        {
                                            JSONObject object= array.getJSONObject(index);
                                            HashMap<String,String> created = new HashMap<>();
                                            created.put("id", object.getString("id"));
                                            created.put("title", object.getString("title"));
                                            if(TextUtils.isEmpty(object.getString("bg_url")) ||"null".equalsIgnoreCase(object.getString("bg_url")) )
                                            {
                                                Log.e(TAG,"Media url is "+object.getString("media_url"));
                                                created.put("bg_url",Configuration.BASE_URL+object.getString("media_url"));
                                            }
                                            else
                                            {
                                                created.put("bg_url",Configuration.BASE_URL+object.getString("bg_url"));
                                            }
                                            created.put("user_id", object.getString("user_id"));
                                            created.put("rank", object.getString("rank"));
                                            created.put("created_at", object.getString("created_at"));
                                            created.put("created_at_text", object.getString("created_at_text"));
                                            created.put("media_type", object.getString("media_type"));
                                            created.put("appreciation_count", object.getString("appreciation_count"));
                                            created.put("inspiration_count", object.getString("inspiration_count"));
                                            creationList.add(created);
                                            if(index == array.length()-1)
                                            {
                                                lastTimestamp=object.getString("created_at");
                                            }
                                        }
                                        boolean append;
                                        if(creationsAdapter == null)
                                        {
                                            creationsAdapter = new UserPagerAdapter(UserPagerAdapter.CREATED,getActivity());
                                            append=false;
                                        }
                                        else
                                        {
                                            append=true;
                                        }
                                        creationsAdapter.addAllToList(creationList,append);
                                        creationsAdapter.notifyDataSetChanged();
                                        creations.setVisibility(View.VISIBLE);
                                        creations.setAdapter(creationsAdapter);
                                        loading=true;
                                    }

                                }
                                catch (JSONException jse)
                                {
                                    Log.e(TAG,"Something wrong happened");
                                    jse.printStackTrace();
                                }

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "error is " + error.getLocalizedMessage());
                            if(creationList.size()==0)
                            {
                                CandeoUtil.toggleView(noUserCreatedContent,true);
                            }
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
            String message = String.format(GET_USER_CREATIONS_RELATIVE_API,id,lastTimeStamp);
            params.put("message", message);
            Log.e(TAG,"secret->"+secret);
            String hash = Security.generateHmac(secret, message);
            Log.e(TAG,"hash->"+hash);
            params.put("Authorization", "Token token=" + hash);
            return params;
        }
    }



}
