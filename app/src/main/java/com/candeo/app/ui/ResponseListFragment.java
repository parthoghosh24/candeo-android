package com.candeo.app.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import com.candeo.app.adapters.ResponseAdapter;
import com.candeo.app.algorithms.Security;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by partho on 30/4/15.
 */
public class ResponseListFragment extends DialogFragment {

    private RecyclerView responseList;
    private ResponseAdapter responseListAdapter;
    private TextView responseIcon;
    private TextView responseTitle;
    private View noContent;
    private View loadingContent;
    private View mRoot;
    private Context mContext;
    private String type;
    private String contentId;
    private static final String TAG="Responselist";
    private final static String GET_RESPONSE_LIST_RELATIVE_API = "/contents/responses/%s/%s";
    private final static String GET_RESPONSE_LIST_API = Configuration.BASE_URL + "/api/v1" + GET_RESPONSE_LIST_RELATIVE_API ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mRoot=inflater.inflate(R.layout.fragment_response_list,container,false);
        initWidgets();
        return mRoot;
    }

    private void initWidgets()
    {
        noContent = mRoot.findViewById(R.id.candeo_no_response_list);
        type="1";
        ((TextView)noContent.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
        ((TextView)noContent.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_APPRECIATE);
        ((TextView)noContent.findViewById(R.id.candeo_no_content_text)).setText("No Appreciations yet!");
        CandeoUtil.toggleView(noContent,false);
        loadingContent= mRoot.findViewById(R.id.candeo_loading_response_list);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setText(Configuration.FA_APPRECIATE);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_text)).setText("Loading Appreciations...");
        CandeoUtil.toggleView(loadingContent,true);
        loadingContent=mRoot.findViewById(R.id.candeo_loading_response_list);
        responseIcon=(TextView)mRoot.findViewById(R.id.candeo_response_list_icon);
        responseTitle=(TextView)mRoot.findViewById(R.id.candeo_response_list_title);
        String title = getArguments().getString("title");
        contentId = getArguments().getString("contentId");
        responseTitle.setText(title);
        String icon = Configuration.FA_APPRECIATE;
        responseIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/applause.ttf"));
        if(!"Appreciations".equalsIgnoreCase(title))
        {
            icon= Configuration.FA_INSPIRE;
            type="2";
            responseIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/response.ttf"));
            ((TextView)noContent.findViewById(R.id.candeo_no_content_icon)).setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/response.ttf"));
            ((TextView)noContent.findViewById(R.id.candeo_no_content_icon)).setText(Configuration.FA_INSPIRE);
            ((TextView)noContent.findViewById(R.id.candeo_no_content_text)).setText("No Inspirations yet!");
            ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTypeface(CandeoUtil.loadFont(mContext.getAssets(),"fonts/response.ttf"));
            ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setText(Configuration.FA_INSPIRE);
            ((TextView)loadingContent.findViewById(R.id.candeo_progress_text)).setText("Loading Inspirations...");
        }
        responseIcon.setText(icon);
        responseList = (RecyclerView)mRoot.findViewById(R.id.candeo_response_list);
        responseList.setLayoutManager(new LinearLayoutManager(mContext));
        GetContentResponse getContentResponse = new GetContentResponse();
        CandeoApplication.getInstance().getAppRequestQueue().add(getContentResponse);

    }

    private class GetContentResponse extends JsonObjectRequest
    {
        public GetContentResponse()
        {
            super(Method.GET,
                    String.format(GET_RESPONSE_LIST_API,type,contentId),
                    new JSONObject(),
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            if(response!=null)
                            {
                                CandeoUtil.toggleView(noContent,false);
                                CandeoUtil.toggleView(loadingContent,false);
                                try {

                                    JSONArray responses = response.getJSONArray("responses");
                                    if(Configuration.DEBUG)Log.e(TAG,"responses size "+responses.length());
                                    if(responseListAdapter == null)
                                    {
                                        responseListAdapter = new ResponseAdapter(mContext,responses,type);
                                    }
                                    else
                                    {
                                        responseListAdapter.notifyDataSetChanged();
                                    }

                                    responseList.setAdapter(responseListAdapter);


                                }
                                catch (JSONException jse)
                                {
                                    if(Configuration.DEBUG) Log.e(TAG, "Something wrong happened");
                                    jse.printStackTrace();
                                    CandeoUtil.toggleView(noContent,true);
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
                            CandeoUtil.toggleView(noContent,true);

                        }
                    });
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
            if(Configuration.DEBUG)Log.e(TAG,"email is "+params.get("email"));
            String message = String.format(GET_RESPONSE_LIST_RELATIVE_API,type,contentId);
            params.put("message", message);
            if(Configuration.DEBUG)Log.e(TAG,"secret->"+secret);
            String hash = Security.generateHmac(secret, message);
            if(Configuration.DEBUG)Log.e(TAG,"hash->"+hash);
            params.put("Authorization", "Token token=" + hash);
            return params;
        }
    }


}
