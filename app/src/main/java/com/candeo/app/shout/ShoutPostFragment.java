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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.algorithms.Security;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by partho on 23/6/15.
 */
public class ShoutPostFragment extends DialogFragment{

    private final static String TAG="shout_post";
    private boolean isPublic = true;
    private Context mContext=null;
    private View dialog;
    private EditText body;
    private Button shout;
    private Button cancel;
    private TextView typeIcon, typeText, totalParticipants;
    private ShoutListener shoutListener;
    private int count;
    private String ids,type;
    private static final String SHOUT_POST_RELATIVE_URL="/shouts/create";
    private static final String SHOUT_POST_URL = Configuration.BASE_URL +"/api/v1"+SHOUT_POST_RELATIVE_URL;

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
        body = (EditText)dialog.findViewById(R.id.candeo_shout_post_body);
        shout = (Button)dialog.findViewById(R.id.candeo_shout_post_ok);
        shout.setEnabled(true);
        cancel = (Button)dialog.findViewById(R.id.candeo_shout_post_cancel);
        typeIcon =(TextView)dialog.findViewById(R.id.candeo_shout_post_type);
        typeText=(TextView)dialog.findViewById(R.id.candeo_shout_post_type_text);
        totalParticipants=(TextView)dialog.findViewById(R.id.candeo_shout_post_total_participants);
        totalParticipants.setVisibility(View.GONE);
        typeIcon.setTypeface(CandeoUtil.loadFont(mContext.getAssets(), "fonts/fa.ttf"));
        typeIcon.setText(Configuration.FA_UNLOCK);
        count = getArguments().getInt("participants");
        ids = getArguments().getString("ids");
        type="1";
        if(count>0)
        {
            type="0";
            typeIcon.setText(Configuration.FA_LOCK);
            typeIcon.setTextColor(getActivity().getResources().getColor(R.color.candeo_private_red));
            typeText.setText("PRIVATE");
            typeText.setTextColor(getActivity().getResources().getColor(R.color.candeo_private_red));
            totalParticipants.setVisibility(View.VISIBLE);
            totalParticipants.setText("Total Participants are "+count);
        }
        shout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(body.getText().toString().length()>0)
                {
                    shout.setEnabled(false);
                    HashMap<String, String> payload = new HashMap<>();
                    payload.put("id",Preferences.getUserRowId(mContext));
                    payload.put("ids",ids);
                    payload.put("type",type);
                    payload.put("body",body.getText().toString());
                    CreateShoutRequest sendResponseRequest= new CreateShoutRequest(payload);
                    sendResponseRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*10, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    CandeoApplication.getInstance().getAppRequestQueue().add(sendResponseRequest);
                }
                else
                {
                    Toast.makeText(mContext,"Please enter some text to shout",Toast.LENGTH_LONG).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


    public void setShoutListener(ShoutListener shoutListener)
    {
        this.shoutListener=shoutListener;

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    class CreateShoutRequest extends JsonObjectRequest
    {
        private Map<String, String> payload;
        public CreateShoutRequest(Map<String,String> payload)
        {
            super(Method.POST,
                    SHOUT_POST_URL,
                    new JSONObject(payload),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if(Configuration.DEBUG) Log.e(TAG, "Response is " + response);
                            shoutListener.onSuccess();
                            dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Something went wrong");
                            shoutListener.onFailure();
                            dismiss();
                        }
                    });

            this.payload=payload;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            if(Configuration.DEBUG)Log.e(TAG,"getActivity is "+mContext);
            if (Preferences.isUserLoggedIn(mContext) && !TextUtils.isEmpty(Preferences.getUserEmail(mContext))) {
                String secret="";
                params.put("email", Preferences.getUserEmail(mContext));
                secret=Preferences.getUserApiKey(mContext);
                String message = SHOUT_POST_RELATIVE_URL+"|"+new JSONObject(payload).toString();
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
