package com.candeo.app.ui;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.response.ResponseListener;
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
    private NumberPicker picker;
    private final static String TAG="Candeo - response";
    private ContextThemeWrapper contextThemeWrapper=null;
    private static final String APPRECIATE_URL = Configuration.BASE_URL +"/api/v1/contents/responses/appreciate";
    private static final String SKIP_URL = Configuration.BASE_URL +"/api/v1/contents/responses/skip";
    private boolean isSkip=false;
    private int state;
    private String responseScore;
    private String responseText;
    private View dialog;
    private String showcaseId;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        title = getArguments().getString("title");
        introText=getArguments().getString("introText");
        choices=getArguments().getStringArray("choices");
        positiveText = getArguments().getString("positiveText");
        position = getArguments().getInt("position");
        showcaseId=getArguments().getString("showcaseId");
        state = getArguments().getInt("responseType");
        responseListener = (ResponseListener)getArguments().getParcelable("adapter");
        Log.e(TAG,"responseListener is"+responseListener);
        contextThemeWrapper = new ContextThemeWrapper(getActivity(),R.style.Theme_AppCompat_Light_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater().cloneInContext(contextThemeWrapper);
        AlertDialog.Builder builder = new AlertDialog.Builder(contextThemeWrapper)
        .setTitle(title)
        .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //Success pressed
                dialogInterface.dismiss();
                String url = APPRECIATE_URL;
                responseScore=""+(picker.getValue()+1);
                responseText=((TextView)dialog.findViewById(R.id.candeo_response_body)).getText().toString();
                Log.e(TAG,"is skip "+ isSkip);
                if (isSkip) {
                    url = SKIP_URL;
                }
                HashMap<String, String> payload  = new HashMap<>();
                payload.put("rating",responseScore);
                payload.put("feedback",responseText);
                payload.put("user_id", Preferences.getUserRowId(getActivity()));
                payload.put("showcase_id",showcaseId);
                CandeoApplication.getInstance().getAppRequestQueue().add(new SendResponseRequest(payload,url));
                responseListener.onResponseClick(position);
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog = inflater.inflate(R.layout.response_layout, null);
        picker = (NumberPicker)dialog.findViewById(R.id.candeo_response_chooser);
        picker.setMinValue(0);
        picker.setMaxValue(choices.length - 1);
        picker.setDisplayedValues(choices);
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                responseScore=""+(picker.getValue()+1);
                Log.e(TAG,"Response score is "+responseScore);
            }
        });
        if(TextUtils.isEmpty(introText))
        {
            (dialog.findViewById(R.id.candeo_response_title)).setVisibility(View.GONE);
            isSkip=true;
        }
        else
        {
            (dialog.findViewById(R.id.candeo_response_title)).setVisibility(View.VISIBLE);
            ((TextView)dialog.findViewById(R.id.candeo_response_title)).setText(introText);
            isSkip=false;
        }
        builder.setView(dialog);
        AlertDialog responseDialog = builder.create();
        responseDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return responseDialog;
    }

    @Override
    public void onDestroy() {
        contextThemeWrapper = new ContextThemeWrapper(getActivity(),R.style.AppTheme_NoActionBar);
        getActivity().getLayoutInflater().cloneInContext(contextThemeWrapper);
        super.onDestroy();
    }

    class SendResponseRequest extends JsonObjectRequest
    {
        public SendResponseRequest(Map<String,String> payload, String url)
        {
            super(Method.POST,
                    url,
                    new JSONObject(payload),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "Response is "+response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Something went wrong");
                        }
                    });
        }
    }
}
