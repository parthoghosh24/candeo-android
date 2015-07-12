/**
 *Refering from https://github.com/yinglovezhuzhu/VideoRecorder/blob/master/VideoRecorder/src/com/opensource/videorecorder/RecorderActivity.java
 */

package com.candeo.app.ui;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.util.CandeoUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CustomVideoCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {


    private SurfaceView surfaceView;
    private MediaRecorder recorder;
    private SurfaceHolder holder;
    private Camera camera; //As usual google screws up with its APIs. Using the old API because it is here to stay for quiet some time but need to keep an eye on updates.
    private boolean recording=false;
    private int which =0;
    private File mOutputFile;
    private List<Camera.Size> mSupportVideoSizes;
    private FloatingActionButton toggleCamera,play;
    private LinearLayout record;
    private Button ok,cancel;
    private static final String TAG="custom_camera";
    private CountDownTimer countDownTimer;
    private TextView camerCountDown;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_video_camera);
        initView();
        recording=false;

    }

    private void initView()
    {
        surfaceView=(SurfaceView)findViewById(R.id.candeo_surface_camera);
        camerCountDown=(TextView)findViewById(R.id.candeo_camera_count_down);
        holder = surfaceView.getHolder();
        holder.addCallback(this);
        toggleCamera=(FloatingActionButton)findViewById(R.id.candeo_camera_toggle);
        FontAwesomeDrawable.FontAwesomeDrawableBuilder builder = new FontAwesomeDrawable.FontAwesomeDrawableBuilder(this,R.string.fa_toggle);
        builder.setColor(getResources().getColor(R.color.candeo_white));
        builder.setSize(16);
        ok=(Button)findViewById(R.id.candeo_camera_ok);
        ok.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/fa.ttf"));
        ok.setText(Configuration.FA_OK);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
        ok.setVisibility(View.GONE);
        cancel=(Button)findViewById(R.id.candeo_camera_cancel);
        cancel.setTypeface(CandeoUtil.loadFont(getAssets(),"fonts/fa.ttf"));
        cancel.setText(Configuration.FA_CANCEL);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        cancel.setVisibility(View.GONE);
        toggleCamera.setImageDrawable(builder.build());
        toggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        toggle();
                    }
                };
                thread.start();

            }
        });

        play=(FloatingActionButton)findViewById(R.id.candeo_camera_play);
        builder = new FontAwesomeDrawable.FontAwesomeDrawableBuilder(this,R.string.fa_play);
        builder.setColor(getResources().getColor(R.color.candeo_white));
        builder.setSize(16);
        play.setImageDrawable(builder.build());
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomCameraFragment preview = new CustomCameraFragment();
                preview.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
                preview.show(getSupportFragmentManager().beginTransaction(), "preview");
            }
        });
        play.setVisibility(View.GONE);

        record=(LinearLayout)findViewById(R.id.candeo_camera_record);
//        final FontAwesomeDrawable.FontAwesomeDrawableBuilder recordBuilder = new FontAwesomeDrawable.FontAwesomeDrawableBuilder(this,R.string.fa_record);
//        recordBuilder.setColor(getResources().getColor(R.color.candeo_private_red));
//        recordBuilder.setSize(16);
//        record.setImageDrawable(recordBuilder.build());
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!recording)
                {
                    deleteFile(new File(android.os.Environment.getExternalStorageDirectory(), "candeo/videos/candeovideo.mp4"));
//                    record.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.candeo_private_red)));
                    camerCountDown.setText("30");
//                    recordBuilder.setColor(getResources().getColor(R.color.candeo_white));
//                    record.setImageDrawable(recordBuilder.build());
                    startRecording();
                    play.setVisibility(View.GONE);
                    toggleCamera.setVisibility(View.GONE);
                    ok.setVisibility(View.GONE);
                    cancel.setVisibility(View.GONE);
                    countDownTimer=new CountDownTimer(30*1000,1000) { // 30 seconds
                        @Override
                        public void onTick(long millisUntilFinished) {
                            camerCountDown.setText(String.format("%d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)));
                        }

                        @Override
                        public void onFinish() {
                            //Stop recording
//                            stopRecordingOps(recordBuilder);
                            stopRecordingOps();
                        }
                    }.start();
                }
                else
                {
//                    stopRecordingOps(recordBuilder);
                    stopRecordingOps();
                }
            }
        });




    }

    private void stopRecordingOps()
    //private void stopRecordingOps(FontAwesomeDrawable.FontAwesomeDrawableBuilder recordBuilder)
    {
        //record.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.candeo_white)));
//        recordBuilder.setColor(getResources().getColor(R.color.candeo_private_red));
//        record.setImageDrawable(recordBuilder.build());
        stopRecording();
        play.setVisibility(View.VISIBLE);
        toggleCamera.setVisibility(View.VISIBLE);
        countDownTimer.cancel();
        camerCountDown.setText("30");
        ok.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);
    }

    private void deleteFile(File delFile) {
        if(delFile == null) {
            return;
        }
        final File file = new File(delFile.getAbsolutePath());
        delFile = null;
        new Thread() {
            @Override
            public void run() {
                super.run();
                if(file.exists()) {
                    file.delete();
                }
            }
        }.start();
    }


    private void toggle() {
        //myCamera is the Camera object
        if (Camera.getNumberOfCameras()>=2) {
            camera.stopPreview();
            camera.release();
            Camera.CameraInfo info = new Camera.CameraInfo();

            //"which" is just an integer flag
            switch (which) {
                case 0:
                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, info);
                    which = 1;
                    break;
                case 1:
                    camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                    Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
                    which = 0;
                    break;
            }
            try {

                int rotation = getWindowManager().getDefaultDisplay().getRotation();
                int degrees = 0;
                switch (rotation)
                {
                    case Surface.ROTATION_0: degrees = 0; break;
                    case Surface.ROTATION_90: degrees = 90; break;
                    case Surface.ROTATION_180: degrees = 180; break;
                    case Surface.ROTATION_270: degrees = 270; break;
                }
                int result;
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    result = (info.orientation + degrees) % 360;
                    result = (360 - result) % 360;  // compensate the mirror
                } else {  // back-facing
                    result = (info.orientation - degrees + 360) % 360;
                }
                camera.setDisplayOrientation(result);
                camera.setPreviewDisplay(holder);
                camera.setPreviewCallback(null);
                camera.startPreview();
            } catch (IOException exception) {
                camera.release();
                camera = null;
            }
        }
    }





    private void releaseMediaRecorder(){
        if (recorder != null) {
            recorder.reset();   // clear recorder configuration
            recorder.release(); // release the recorder object
            recorder = null;
            camera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (camera != null){
            camera.release();        // release the camera for other applications
            camera = null;
        }
    }

    private boolean prepareMediaRecorder(){
        recorder = new MediaRecorder();
        camera.unlock();
        // Step 1: Unlock and set camera to MediaRecorder
        recorder.setCamera(camera);

        // Step 2: Set sources

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        recorder.setAudioEncodingBitRate(96000);
        recorder.setAudioSamplingRate(44100);
        recorder.setVideoSize(640, 480);


        // Step 3: Set output file
        File file = new File(android.os.Environment.getExternalStorageDirectory(), "candeo/videos/candeovideo.mp4");
        recorder.setOutputFile(file.getAbsolutePath());

        // Step 4: Set the preview output

        recorder.setPreviewDisplay(holder.getSurface());
        if(which==1)//Front cam
        {
            recorder.setOrientationHint(270);
        }
        else
        {
            recorder.setOrientationHint(90);
        }


        // Step 5: Prepare configured MediaRecorder

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    private void startRecording()
    {
        if(prepareMediaRecorder())
        {
            recorder.start();
        }
        else
        {
            releaseMediaRecorder();

        }
        recording=true;
    }

    private void stopRecording()
    {
        if(recorder!=null)
        {
            recorder.stop();
            releaseMediaRecorder();
            camera.lock();
            recording=false;
        }


    }

    private void openCamera()
    {
        this.camera = Camera.open();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setRotation(90);
        parameters.set("orientation","portrait");
        camera.setParameters(parameters);
        camera.lock();
        camera.setDisplayOrientation(90);
        mSupportVideoSizes=parameters.getSupportedVideoSizes();
        if(mSupportVideoSizes==null || mSupportVideoSizes.isEmpty())
        {
            String videoSize = parameters.get("video-size");
            if(Configuration.DEBUG)Log.e(TAG, videoSize);
            mSupportVideoSizes = new ArrayList<Camera.Size>();
            if(!TextUtils.isEmpty(videoSize)) {
                String [] size = videoSize.split("x");
                if(size.length > 1) {
                    try {
                        int width = Integer.parseInt(size[0]);
                        int height = Integer.parseInt(size[1]);
                        mSupportVideoSizes.add(camera.new Size(width, height));
                    } catch (Exception e) {
                        if(Configuration.DEBUG)Log.e(TAG, e.toString());
                    }
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if(camera!=null)
        {
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            } catch (Exception e) {
                if(Configuration.DEBUG)Log.e(TAG, "Error setting camera preview: " + e.toString());
            }
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (camera != null) {
            try {
                camera.stopPreview();
            } catch (Exception e) {
                if(Configuration.DEBUG)Log.e(TAG, "Error setting camera preview: " + e.toString());
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }


    private class SizeComparator implements Comparator<Camera.Size> {

        @Override
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            return rhs.width - lhs.width;
        }
    }

}
