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

public class CreatedFragment extends Fragment {


    private View noUserCreatedContent;
    private static final String TAG="Candeo-User Creation";
    private View mRoot;
    private LinearLayoutManager creationsLinearLayoutManager;
    private UserPagerAdapter creationsAdapter;
    private List<HashMap<String,String>> creationList;
    private RecyclerView creations;
    private static final String GET_USER_CREATIONS_API=Configuration.BASE_URL+"/api/v1/users/%s/showcases/%s";
    private String userId="";

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
        ((TextView)noUserCreatedContent.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(),"fonts/fa.ttf"));
        ((TextView)noUserCreatedContent.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_MAGIC);
        ((TextView)noUserCreatedContent.findViewById(R.id.candeo_no_content_icon)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        ((TextView)noUserCreatedContent.findViewById(R.id.candeo_no_content_text)).setText("No content created yet.");
        ((TextView)noUserCreatedContent.findViewById(R.id.candeo_no_content_text)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        toggleNoContent(noUserCreatedContent,true);
        creationList = new ArrayList<>();
        CandeoApplication.getInstance().getAppRequestQueue().add(new GetUserCreations(userId,"now"));

    }

    private class GetUserCreations extends JsonObjectRequest
    {
        public GetUserCreations(String id, String lastTimeStamp)
        {
            super(Method.GET,
                    String.format(GET_USER_CREATIONS_API,id,lastTimeStamp),
                    null,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            if(response!=null)
                            {
                                try {

                                    JSONArray array = response.getJSONArray("showcases");
                                    if(array.length()>0)
                                    {
                                        toggleNoContent(noUserCreatedContent,false);
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
                                        }
                                        if(creationsAdapter == null)
                                        {
                                            creationsAdapter = new UserPagerAdapter(creationList,UserPagerAdapter.CREATED,getActivity());
                                        }
                                        else
                                        {
                                            creationsAdapter.notifyDataSetChanged();
                                        }
                                        creations.setVisibility(View.VISIBLE);
                                        creations.setAdapter(creationsAdapter);
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
                        }
                    });
        }
    }

    private void toggleNoContent(View view, boolean show)
    {
        view.setVisibility(show?View.VISIBLE:View.GONE);
    }

}
