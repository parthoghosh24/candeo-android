package com.candeo.app.content;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import android.widget.VideoView;

import com.candeo.app.R;
import com.candeo.app.util.CandeoUtil;

import java.io.File;
import java.io.IOException;

public class PostActivity extends ActionBarActivity {

    Button audio;
    Button image;
    Button video;
    Button postIt;
    Switch selector;
    TextView copyrightText;
    EditText showcaseTitleText;
    Button audioPreview;
    ImageView imagePreview;
    VideoView videoPreview;
    Button videoPreviewPlay;
    Toolbar toolbar;
    boolean isPlaying=false;
    int stopPosition=0;
    MediaPlayer player;
    private static final int REQUEST_IMAGE_CAMERA=100;
    private static final int PICK_VIDEO_FILE=200;
    private static final int REQUEST_VIDEO_CAMERA=300;
    private static final int PICK_AUDIO_FILE=400;
    private static final int REQUEST_AUDIO_RECORD=500;
    private static final int PICK_IMAGE_FILE=600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setFinishOnTouchOutside(false);
        toolbar = (Toolbar)findViewById(R.id.candeo_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Candeo");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        audioPreview = (Button)findViewById(R.id.candeo_audio_preview);
        audioPreview.setTypeface(CandeoUtil.loadFont(getAssets(), "fa.ttf"));
        audioPreview.setText("\uf04b");
        imagePreview=(ImageView)findViewById(R.id.candeo_image_preview);
        videoPreview=(VideoView)findViewById(R.id.candeo_video_preview);
        videoPreviewPlay=(Button)findViewById(R.id.candeo_video_preview_play);
        videoPreviewPlay.setTypeface(CandeoUtil.loadFont(getAssets(), "fa.ttf"));
        videoPreviewPlay.setText("\uf04b");

        audio=(Button)findViewById(R.id.candeo_audio);
        audio.setTypeface(CandeoUtil.loadFont(getAssets(), "fa.ttf"));
        audio.setText("\uf001");
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player!=null)
                {
                    player.stop();
                    player.release();
                    player=null;
                    audioPreview.setVisibility(View.GONE);
                    audioPreview.setText("\uf04b");
                }
                final CharSequence[] choices ={"Record Something", "Fetch From Device", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                builder.setItems(choices,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(choices[which].equals("Record Something"))
                        {
                            Intent intent =new Intent(PostActivity.this, RecordActivity.class);
                            startActivityForResult(intent,REQUEST_AUDIO_RECORD);
                        }
                        else if(choices[which].equals("Fetch From Device"))
                        {
                              Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                              startActivityForResult(Intent.createChooser(intent, "Gallery"), PICK_AUDIO_FILE);
                        }
                        else if(choices[which].equals("Cancel"))
                        {
                            dialog.dismiss();
                        }

                    }
                });
                builder.show();
            }
        });

        image=(Button)findViewById(R.id.candeo_image);
        image.setTypeface(CandeoUtil.loadFont(getAssets(), "fa.ttf"));
        image.setText("\uf030");
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Clicking image......");
                final CharSequence[] choices ={"Click Something", "Fetch From Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                builder.setItems(choices,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(choices[which].equals("Click Something"))
                        {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                           // File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                            //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, 90);
                            startActivityForResult(intent, REQUEST_IMAGE_CAMERA);
                        }
                        else if(choices[which].equals("Fetch From Gallery"))
                        {
                            Intent intent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(
                                    Intent.createChooser(intent, "Select File"),
                                    PICK_IMAGE_FILE);

                        }
                        else if(choices[which].equals("Cancel"))
                        {
                            dialog.dismiss();
                        }

                    }
                });
                builder.show();
            }
        });

        video=(Button)findViewById(R.id.candeo_video);
        video.setTypeface(CandeoUtil.loadFont(getAssets(), "fa.ttf"));
        video.setText("\uf008");
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] choices ={"Shoot Something (Upto 3 min)", "Fetch From Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                builder.setItems(choices,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(choices[which].equals("Shoot Something (Upto 3 min)"))
                        {
                            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                            File f = new File(android.os.Environment
                                    .getExternalStorageDirectory(), "temp.mp4");
                            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 180);
                            intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, 90);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            startActivityForResult(intent, REQUEST_VIDEO_CAMERA);
                        }
                        else if(choices[which].equals("Fetch From Gallery"))
                        {
                            Intent intent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("video/*");
                            startActivityForResult(
                                    Intent.createChooser(intent, "Select File"),
                                    PICK_VIDEO_FILE);

                        }
                        else if(choices[which].equals("Cancel"))
                        {
                            dialog.dismiss();
                        }

                    }
                });
                builder.show();

            }
        });

        postIt=(Button)findViewById(R.id.candeo_post_it);
        postIt.setTypeface(CandeoUtil.loadFont(getAssets(), "fa.ttf"));
        postIt.setText("\uf0d0");
        postIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        copyrightText=(TextView)findViewById(R.id.candeo_copyright_text);
        showcaseTitleText=(EditText)findViewById(R.id.candeo_post_title);
        selector=(Switch)findViewById(R.id.candeo_selector);
        selector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    copyrightText.setTypeface(CandeoUtil.loadFont(getAssets(), "fa.ttf"));
                    copyrightText.setText("\uf1f9 Anonymous");
                    copyrightText.setVisibility(View.VISIBLE);
                    showcaseTitleText.setVisibility(View.VISIBLE);
                }
                else
                {
                    copyrightText.setVisibility(View.GONE);
                    showcaseTitleText.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if(requestCode == REQUEST_IMAGE_CAMERA)
            {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                System.out.println("Bitmap is " + bitmap);
                imagePreview.setImageBitmap(bitmap);
                imagePreview.setVisibility(View.VISIBLE);
                videoPreview.setVisibility(View.GONE);
                videoPreviewPlay.setVisibility(View.GONE);
                audioPreview.setVisibility(View.GONE);
            }
            else if(requestCode == PICK_IMAGE_FILE)
            {
                   Uri uri = data.getData();
                   Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA},null,null,null);
                   cursor.moveToFirst();
                   String filePath = cursor.getString(0);
                   cursor.close();
                   imagePreview.setImageBitmap(BitmapFactory.decodeFile(filePath));
                   imagePreview.setVisibility(View.VISIBLE);
                   videoPreview.setVisibility(View.GONE);
                   videoPreviewPlay.setVisibility(View.GONE);
                   audioPreview.setVisibility(View.GONE);

            }
            else if(requestCode == REQUEST_VIDEO_CAMERA|| requestCode == PICK_VIDEO_FILE)
            {
                Uri uri = data.getData();
                videoPreview.setVideoURI(uri);
                videoPreview.start();
                videoPreview.seekTo(100);
                imagePreview.setVisibility(View.GONE);
                videoPreview.setVisibility(View.VISIBLE);
                videoPreviewPlay.setVisibility(View.GONE);
                audioPreview.setVisibility(View.GONE);
                videoPreview.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                            if(!videoPreview.isPlaying())
                            {
                                videoPreview.seekTo(stopPosition);
                                videoPreview.start();
                            }
                            else
                            {
                                stopPosition=videoPreview.getCurrentPosition();
                                videoPreview.pause();
                            }

                        if(videoPreview!=null)
                        {
                            videoPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    videoPreview.stopPlayback();
                                }
                            });
                        }
                        return true;
                    }
                });
            }
            else if(requestCode == REQUEST_AUDIO_RECORD)
            {

                final String path = data.getStringExtra("path");
                System.out.println("Path is "+ path);
                playAudio(path);
            }
            else if(requestCode == PICK_AUDIO_FILE)
            {
                final Uri uri = data.getData();
                System.out.println("Path is "+ uri.getPath());
                playAudio(uri);
            }

        }
    }

    private void playAudio(Object uri)
    {


        if(player!=null)
        {
            player.stop();
            player.release();
            player=null;
        }
        player = new MediaPlayer();
        try {
            if(uri instanceof  Uri)
            {
                player.setDataSource(PostActivity.this, (Uri)uri);
            }
            else
            {
                player.setDataSource((String)uri);
            }

            player.prepare();
            player.start();
            player.setLooping(true);
            isPlaying=true;
            audioPreview.setText("\uf04c");
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        imagePreview.setVisibility(View.GONE);
        videoPreview.setVisibility(View.GONE);
        videoPreviewPlay.setVisibility(View.GONE);
        audioPreview.setVisibility(View.VISIBLE);
        audioPreview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                System.out.println("PLAYER is : "+player);
                if(!isPlaying)
                {
                    isPlaying=true;
                    player.seekTo(stopPosition);
                    player.start();
                    audioPreview.setText("\uf04c");
                }
                else
                {
                    isPlaying=false;
                    stopPosition=player.getCurrentPosition();
                    player.pause();
                    audioPreview.setText("\uf04b");

                }
            }
        });

    }

}
