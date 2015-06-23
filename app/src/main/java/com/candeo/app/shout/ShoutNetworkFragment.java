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
import com.candeo.app.adapters.ShoutNetworkAdapter;
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


public class ShoutNetworkFragment extends Fragment {

    private RecyclerView shoutNetwork;
    private ShoutNetworkAdapter shoutNetworkAdapter;
    private List<HashMap<String,String>> networkList;
    private View noShoutNetworkContent;
    private View loadingContent;
    private View root;
    private LinearLayoutManager shoutNetworkLayoutManager;
    private static final String GET_SHOUT_NETWORK_RELATIVE_API="/shouts/network/%s";
    private static final String GET_SHOUT_NETWORK_API=Configuration.BASE_URL+"/api/v1"+GET_SHOUT_NETWORK_RELATIVE_API;
    private static final String TAG="shout_network";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root=inflater.inflate(R.layout.fragment_shout_network, container, false);
        initWidgets();
        return root;
    }

    private void initWidgets()
    {
        noShoutNetworkContent = root.findViewById(R.id.candeo_shout_network_no_content);
        loadingContent = root.findViewById(R.id.candeo_loading_content);
        shoutNetwork = (RecyclerView)root.findViewById(R.id.candeo_shout_network);
        shoutNetworkAdapter=null;
        shoutNetworkLayoutManager= new LinearLayoutManager(getActivity());
        shoutNetworkLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        shoutNetwork.setLayoutManager(shoutNetworkLayoutManager);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setText(Configuration.FA_USERS);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_text)).setText("Loading Network...");
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_text)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        CandeoUtil.toggleView(loadingContent, true);
        ((TextView)noShoutNetworkContent.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(getActivity().getAssets(), "fonts/fa.ttf"));
        ((TextView)noShoutNetworkContent.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_USERS);
        ((TextView)noShoutNetworkContent.findViewById(R.id.candeo_no_content_icon)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        ((TextView)noShoutNetworkContent.findViewById(R.id.candeo_no_content_text)).setText("No Network found yet. Appreciate or Get Appreciated to build one.");
        ((TextView)noShoutNetworkContent.findViewById(R.id.candeo_no_content_text)).setTextColor(getActivity().getResources().getColor(R.color.candeo_light_gray));
        CandeoUtil.toggleView(noShoutNetworkContent, false);
        networkList = new ArrayList<>();
        GetUserShoutNetwork request = new GetUserShoutNetwork(Preferences.getUserRowId(getActivity()));
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        CandeoApplication.getInstance().getAppRequestQueue().add(request);

    }

    private class GetUserShoutNetwork extends JsonObjectRequest
    {
        private String id;
        private String lastTimeStamp;
        public GetUserShoutNetwork(String id)
        {
            super(Method.GET,
                    String.format(GET_SHOUT_NETWORK_API,id),
                    new JSONObject(),
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            if(response!=null)
                            {
                                try {

                                    JSONArray array = response.getJSONArray("network");
                                    Log.e(TAG,"IN HERE BRAAAH");
                                    Log.e(TAG, "array is "+array.toString());
                                    networkList.clear();
                                    CandeoUtil.toggleView(loadingContent,false);
                                    if(array.length()>0)
                                    {
                                        CandeoUtil.toggleView(noShoutNetworkContent,false);
                                        for(int index=0; index<array.length();++index)
                                        {
                                            JSONObject object= array.getJSONObject(index);
                                            HashMap<String,String> user = new HashMap<>();
                                            user.put("id",object.getString("id"));
                                            user.put("name",object.getString("name"));
                                            user.put("bio",object.getString("about"));
                                            user.put("avatar_path",object.getString("avatar_path"));
                                            networkList.add(user);
                                        }

                                        if(shoutNetworkAdapter == null)
                                        {
                                            shoutNetworkAdapter = new ShoutNetworkAdapter(getActivity(),networkList);
                                        }
                                        shoutNetworkAdapter.notifyDataSetChanged();
                                        shoutNetwork.setVisibility(View.VISIBLE);
                                        shoutNetwork.setAdapter(shoutNetworkAdapter);
                                    }

                                }
                                catch (JSONException jse)
                                {
                                    jse.printStackTrace();
                                    CandeoUtil.toggleView(noShoutNetworkContent,true);
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
                            CandeoUtil.toggleView(noShoutNetworkContent,true);

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
                String message = String.format(GET_SHOUT_NETWORK_RELATIVE_API,id);
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
