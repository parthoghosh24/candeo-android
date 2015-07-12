package com.candeo.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.candeo.app.R;

import java.io.File;

/**
 * Created by partho on 11/7/15.
 */
public class CustomCameraFragment extends DialogFragment{


    private VideoView preview;
    private View dialog;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext=getActivity();
        dialog=inflater.inflate(R.layout.custom_video_preview_layout,container,false);
        initWidgets();
        return dialog;
    }

    private void initWidgets()
    {
        preview=(VideoView)dialog.findViewById(R.id.candeo_video_preview);
        preview.setVideoPath(new File(android.os.Environment.getExternalStorageDirectory(), "candeo/videos/candeovideo.mp4").getAbsolutePath());
        preview.start();

    }
}
