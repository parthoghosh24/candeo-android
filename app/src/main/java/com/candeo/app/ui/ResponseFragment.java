package com.candeo.app.ui;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.algorithms.Security;
import com.candeo.app.content.InspirationListener;
import com.candeo.app.content.ResponseListener;
import com.candeo.app.user.LoginActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by partho on 11/2/15.
 */
public class ResponseFragment extends DialogFragment {

    private String introText;
    private String[] choices;
    private String title;
    private String positiveText;
    private int position;
    private ResponseListener responseListener = null;
    private InspirationListener inspirationListener=null;
    private NumberPicker picker;
    private final static String TAG="Candeo - response";
    private ContextThemeWrapper contextThemeWrapper=null;
    private static final String APPREICATE_RELATIVE_URL="/contents/responses/appreciate";
    private static final String APPRECIATE_URL = Configuration.BASE_URL +"/api/v1"+APPREICATE_RELATIVE_URL;
    private static final String SKIP_RELATIVE_URL="/contents/responses/skip";
    private static final String SKIP_URL = Configuration.BASE_URL +"/api/v1"+SKIP_RELATIVE_URL;
    private static final String INSPIRE_RELATIVE_URL="/contents/responses/inspire";
    private static final String INSPIRE_URL = Configuration.BASE_URL +"/api/v1"+INSPIRE_RELATIVE_URL;
    private int state;
    private String responseScore;
    private String responseText;
    private View dialog;
    private String showcaseId;
    private Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext=getActivity();
        dialog=inflater.inflate(R.layout.response_layout, container, false);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        title = getArguments().getString("title");
        introText=getArguments().getString("introText");
        choices=getArguments().getStringArray("choices");
        positiveText = getArguments().getString("positiveText");
        position = getArguments().getInt("position");
        showcaseId=getArguments().getString("showcaseId");
        state = getArguments().getInt("responseType");

//        contextThemeWrapper = new ContextThemeWrapper(getActivity(),R.style.Theme_AppCompat_Light_Dialog);
//        LayoutInflater inflater = getActivity().getLayoutInflater().cloneInContext(contextThemeWrapper);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
//        dialog = inflater.inflate(R.layout.response_layout, null);
        picker = (NumberPicker)dialog.findViewById(R.id.candeo_response_chooser);
        picker.setMinValue(0);
        picker.setMaxValue(choices.length - 1);
        picker.setDisplayedValues(choices);
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                responseScore=""+(picker.getValue()+1);
                if(Configuration.DEBUG)Log.e(TAG,"Response score is "+responseScore);
            }
        });
        if(TextUtils.isEmpty(introText))
        {
            (dialog.findViewById(R.id.candeo_response_title)).setVisibility(View.GONE);
        }
        else
        {
            (dialog.findViewById(R.id.candeo_response_title)).setVisibility(View.VISIBLE);
            ((TextView)dialog.findViewById(R.id.candeo_response_title)).setText(introText);
        }
        builder.setView(dialog);
        builder.setPositiveButton(positiveText, new ResponseDialogListener(this.responseListener,this.inspirationListener,(TextView) dialog.findViewById(R.id.candeo_response_body)));
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog responseDialog = builder.create();
        responseDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return responseDialog;
    }

    public void setResponseListener(ResponseListener responseListener)
    {
        this.responseListener=responseListener;
    }

    public void setInspirationListener(InspirationListener inspirationListener)
    {
        this.inspirationListener=inspirationListener;
    }
    @Override
    public void onDestroy() {
        contextThemeWrapper = new ContextThemeWrapper(getActivity(),R.style.AppTheme_NoActionBar);
        getActivity().getLayoutInflater().cloneInContext(contextThemeWrapper);
        super.onDestroy();
    }

    private class ResponseDialogListener implements DialogInterface.OnClickListener
    {
        private ResponseListener responseListener;
        private InspirationListener inspirationListener;
        private TextView responseBodyText;

        public ResponseDialogListener(ResponseListener responseListener, InspirationListener inspirationListener, TextView responseBodyText)
        {
            this.responseListener=responseListener;
            this.inspirationListener=inspirationListener;
            this.responseBodyText=responseBodyText;

        }

        @Override
        public void onClick(DialogInterface dialog, int which) {

            //Success pressed
            dialog.dismiss();
            String url = APPRECIATE_URL;
            String relativeUrl = APPREICATE_RELATIVE_URL;

            if(state == Configuration.INSPIRE)
            {
                url=INSPIRE_URL;
                relativeUrl=INSPIRE_RELATIVE_URL;
            }
            if(state == Configuration.SKIP)
            {
                url=SKIP_URL;
                relativeUrl=SKIP_RELATIVE_URL;
            }
            responseScore=""+(picker.getValue()+1);
            responseText=responseBodyText.getText().toString();
            HashMap<String, String> payload  = new HashMap<>();
            payload.put("rating",responseScore);
            payload.put("feedback",responseText);
            payload.put("user_id", Preferences.getUserRowId(getActivity()));
            payload.put("showcase_id",showcaseId);
            SendResponseRequest sendResponseRequest= new SendResponseRequest(payload, url, relativeUrl);
            sendResponseRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS*10, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            CandeoApplication.getInstance().getAppRequestQueue().add(sendResponseRequest);
            if(state == Configuration.APPRECIATE || state == Configuration.SKIP)
            {
                this.responseListener.onResponseClick(position);
            }
            else
            {
                this.inspirationListener.onSubmit();
            }

        }
    }
    class SendResponseRequest extends JsonObjectRequest
    {
        private Map<String, String> payload;
        private String url;
        private String relativeUrl;
        public SendResponseRequest(Map<String,String> payload, String url, String relativeUrl)
        {
            super(Method.POST,
                    url,
                    new JSONObject(payload),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if(Configuration.DEBUG)Log.e(TAG, "Response is "+response);
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
