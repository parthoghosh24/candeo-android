package com.candeo.app.shout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.algorithms.Security;
import com.candeo.app.util.Preferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by partho on 23/6/15.
 */
public class ShoutPostFragment extends DialogFragment {

    private final static String TAG="shout_post";
    private boolean isPublic = true;
    private Context mContext=null;
    private View dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext=getActivity();
        dialog=inflater.inflate(R.layout.shout_post_layout,container,false);
        initWidgets();
        return dialog;
    }

    private void initWidgets()
    {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    class CreateShoutRequest extends JsonObjectRequest
    {
        private Map<String, String> payload;
        private String url;
        private String relativeUrl;
        public CreateShoutRequest(Map<String,String> payload, String url, String relativeUrl)
        {
            super(Method.POST,
                    url,
                    new JSONObject(payload),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if(Configuration.DEBUG) Log.e(TAG, "Response is " + response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Something went wrong");
                        }
                    });

            this.payload=payload;
            this.url=url;
            this.relativeUrl=relativeUrl;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            if(Configuration.DEBUG)Log.e(TAG,"getActivity is "+mContext);
            if (Preferences.isUserLoggedIn(mContext) && !TextUtils.isEmpty(Preferences.getUserEmail(mContext))) {
                String secret="";
                params.put("email", Preferences.getUserEmail(mContext));
                secret=Preferences.getUserApiKey(mContext);
                String message = relativeUrl+"|"+new JSONObject(payload).toString();
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
