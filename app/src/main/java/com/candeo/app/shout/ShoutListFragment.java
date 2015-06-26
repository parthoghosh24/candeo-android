package com.candeo.app.shout;


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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.adapters.ShoutListAdapter;
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


public class ShoutListFragment extends Fragment {

    private RecyclerView shoutList;
    private ShoutListAdapter shoutListAdapter;
    private List<HashMap<String,Object>> shouts;
    private LinearLayoutManager shoutListLayoutManager;
    private View noShoutListContent;
    private View loadingContent;
    private View root;
    private static final String GET_SHOUT_LIST_RELATIVE_API="/shouts/list/%s";
    private static final String GET_SHOUT_LIST_API=Configuration.BASE_URL+"/api/v1"+GET_SHOUT_LIST_RELATIVE_API;
    private static final String TAG="shout_list";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root= inflater.inflate(R.layout.fragment_shout_list, container, false);
        initWidgets();
        return root;
    }

    private void initWidgets()
    {
        noShoutListContent = root.findViewById(R.id.candeo_shout_list_no_content);
        loadingContent = root.findViewById(R.id.candeo_loading_content);
        shoutList = (RecyclerView)root.findViewById(R.id.candeo_shout_list);
        shoutList.setHasFixedSize(true);
        shoutList.setAdapter(shoutListAdapter);
        shoutListLayoutManager= new LinearLayoutManager(getActivity());
        shoutListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        shoutList.setLayoutManager(shoutListLayoutManager);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setText(Configuration.FA_BULLHORN);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_text)).setText("Loading Shouts...");
        ((TextView) loadingContent.findViewById(R.id.candeo_progress_text)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        CandeoUtil.toggleView(loadingContent, true);
        ((TextView)noShoutListContent.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        ((TextView)noShoutListContent.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_BULLHORN);
        ((TextView)noShoutListContent.findViewById(R.id.candeo_no_content_icon)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        ((TextView)noShoutListContent.findViewById(R.id.candeo_no_content_text)).setText("No Shouts yet.");
        ((TextView)noShoutListContent.findViewById(R.id.candeo_no_content_text)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        CandeoUtil.toggleView(noShoutListContent, false);

        requestRefresh();
    }

    public void requestRefresh()
    {
        shouts = new ArrayList<>();
        GetUserShoutList request = new GetUserShoutList(Preferences.getUserRowId(getActivity()));
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        CandeoApplication.getInstance().getAppRequestQueue().add(request);

    }

    private class GetUserShoutList extends JsonObjectRequest
    {
        private String id;
        public GetUserShoutList(String id)
        {
            super(Method.GET,
                    String.format(GET_SHOUT_LIST_API,id),
                    new JSONObject(),
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            if(response!=null)
                            {
                                try {
                                    Log.e(TAG,"IN HERE BRAAAH");
                                    JSONArray array = response.getJSONArray("shouts");
                                    Log.e(TAG, "list array is "+array.toString());
                                    shouts.clear();
                                    CandeoUtil.toggleView(loadingContent,false);
                                    if(array.length()>0)
                                    {
                                        CandeoUtil.toggleView(noShoutListContent,false);
                                        for(int index=0; index<array.length();++index)
                                        {
                                            JSONObject object= array.getJSONObject(index);
                                            HashMap<String,Object> shout = new HashMap<>();
                                            shout.put("id",object.getString("id"));
                                            shout.put("body",object.getString("body"));
                                            shout.put("is_public",object.getBoolean("is_public"));
                                            shout.put("avatar_path",object.getString("avatar_path"));
                                            shout.put("name",object.getString("name"));
                                            shout.put("timestamp",object.getString("created_at_timestamp"));
                                            shouts.add(shout);
                                        }

                                        if(shoutListAdapter == null)
                                        {
                                            shoutListAdapter = new ShoutListAdapter(getActivity(),shouts);
                                        }
                                        shoutListAdapter.notifyDataSetChanged();
                                        shoutList.setVisibility(View.VISIBLE);
                                        shoutList.setAdapter(shoutListAdapter);
                                    }

                                }
                                catch (JSONException jse)
                                {
                                    jse.printStackTrace();
                                    CandeoUtil.toggleView(noShoutListContent,true);
                                    CandeoUtil.toggleView(loadingContent,false);
                                }

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if(Configuration.DEBUG)Log.e(TAG, "error is " + error.getLocalizedMessage());
                            CandeoUtil.toggleView(loadingContent,false);
                            CandeoUtil.toggleView(noShoutListContent,true);

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
                String message = String.format(GET_SHOUT_LIST_RELATIVE_API,id);
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
