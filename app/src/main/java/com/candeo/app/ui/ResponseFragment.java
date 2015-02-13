package com.candeo.app.ui;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.candeo.app.R;
import com.candeo.app.response.ResponseListener;

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



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        title = getArguments().getString("title");
        introText=getArguments().getString("introText");
        choices=getArguments().getStringArray("choices");
        positiveText = getArguments().getString("positiveText");
        position = getArguments().getInt("position");
        responseListener = (ResponseListener)getArguments().getParcelable("adapter");
        Log.e(TAG,"responseListener is"+responseListener);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
        .setTitle(title)
        .setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Success pressed
                dialog.dismiss();
                responseListener.onResponseClick(position);
            }
        })
        .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        View dialog = getActivity().getLayoutInflater().inflate(R.layout.response_layout,null);
        picker = (NumberPicker)dialog.findViewById(R.id.candeo_response_chooser);
        picker.setMinValue(0);
        picker.setMaxValue(choices.length - 1);
        picker.setDisplayedValues(choices);
        ((TextView)dialog.findViewById(R.id.candeo_response_title)).setText(introText);
        builder.setView(dialog);

        return builder.create();
    }
}
