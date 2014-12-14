package com.candeo.app.content;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.candeo.app.CandeoApplication;
import com.candeo.app.R;
import com.candeo.app.network.CandeoHttpClient;
import com.candeo.app.util.CandeoUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

public class PostActivity extends ActionBarActivity {

    Button audio;
    Button image;
    Button video;
    Button postIt;
    Switch selector;
    TextView copyrightText;
    EditText showcaseTitleText;
    EditText description;
    Button audioPreview;
    ImageView imagePreview;
    VideoView videoPreview;
    Button videoPreviewPlay;
    Toolbar toolbar;
    String mimeType;
    String fileName;
    int mediaType;
    byte[] dataArray;
    boolean isPlaying=false;
    int stopPosition=0;
    MediaPlayer player;
    private static final int REQUEST_IMAGE_CAMERA=100;
    private static final int PICK_VIDEO_FILE=200;
    private static final int REQUEST_VIDEO_CAMERA=300;
    private static final int PICK_AUDIO_FILE=400;
    private static final int REQUEST_AUDIO_RECORD=500;
    private static final int PICK_IMAGE_FILE=600;

    private static final int AUDIO=1;
    private static final int VIDEO=2;
    private static final int IMAGE=3;

    private static final int INSPIRATION=1;
    private static final int SHOWCASE=2;

    private int contentType=INSPIRATION;

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
        description=(EditText)findViewById(R.id.candeo_content_create);

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
                                    .getExternalStorageDirectory(), "candeo/videos/candeovideo.mp4");
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
                new PostContentTask().execute();
                //finish();
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
                    contentType=SHOWCASE;
                    copyrightText.setTypeface(CandeoUtil.loadFont(getAssets(), "fa.ttf"));
                    copyrightText.setText("\uf1f9 Anonymous");
                    copyrightText.setVisibility(View.VISIBLE);
                    showcaseTitleText.setVisibility(View.VISIBLE);
                }
                else
                {
                    contentType=INSPIRATION;
                    copyrightText.setVisibility(View.GONE);
                    showcaseTitleText.setVisibility(View.GONE);
                }
            }
        });

    }

    private class PostContentTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PostActivity.this);
            System.out.println("Dialog is "+pDialog);
            pDialog.setMessage("Creating Magic...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try
            {
                String url =CandeoApplication.baseUrl+"/api/v1/contents/create ";
                System.out.println("URL Is "+url);
                CandeoHttpClient client = new CandeoHttpClient(url);
                client.connectForMultipart();
                client.addFormPart("type", Integer.toString(contentType));
                client.addFormPart("description",description.getText().toString());
                client.addFormPart("user_id","1");
                client.addFormPart("tag","inspire");
                System.out.println("FILE is " + fileName);
                client.addFormPart("media_type",Integer.toString(mediaType));
                client.addFilePart("media",fileName,dataArray, mimeType);
                client.finishMultipart();
                System.out.println("Response is "+client.getResponse());
            }
            catch (IOException ie)
            {

                ie.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            super.onPostExecute(s);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap=null;
        Uri uri=null;
        Cursor cursor=null;
        String filePath="";
        File file=null;
        ByteArrayOutputStream bos=null;
        int width=0;
        int height=0;

        if(resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case REQUEST_IMAGE_CAMERA:
                    bos = new ByteArrayOutputStream();
                    bitmap = (Bitmap) data.getExtras().get("data");
                    height=bitmap.getHeight();
                    width=bitmap.getWidth();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
                    mimeType="image/jpeg";
                    fileName = "candeoimage.jpg";
                    dataArray = bos.toByteArray();
                    mediaType=IMAGE;
                    System.out.println("Bitmap is " + bitmap);
                    if(width > height)
                    {
                        imagePreview.setImageBitmap(Bitmap.createScaledBitmap(bitmap,320,240,false));
                    }
                    else
                    {
                        imagePreview.setImageBitmap(Bitmap.createScaledBitmap(bitmap,240,320,false));
                    }

                    imagePreview.setVisibility(View.VISIBLE);
                    videoPreview.setVisibility(View.GONE);
                    videoPreviewPlay.setVisibility(View.GONE);
                    audioPreview.setVisibility(View.GONE);
                    break;
                case PICK_IMAGE_FILE:
                    uri = data.getData();
                    cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                    cursor.moveToFirst();
                    filePath = cursor.getString(0);
                    cursor.close();
                    file = new File(filePath);
                    mimeType=CandeoUtil.getMimeType(uri, PostActivity.this);
                    fileName = file.getName();
                    bitmap=BitmapFactory.decodeFile(filePath);
                    height=bitmap.getHeight();
                    width=bitmap.getWidth();
                    bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
                    dataArray = CandeoUtil.fileToByteArray(file);
                    mediaType=IMAGE;
                    if(width > height)
                    {
                        imagePreview.setImageBitmap(Bitmap.createScaledBitmap(bitmap,320,240,false));
                    }
                    else
                    {
                        imagePreview.setImageBitmap(Bitmap.createScaledBitmap(bitmap,240,320,false));
                    }
                    imagePreview.setVisibility(View.VISIBLE);
                    videoPreview.setVisibility(View.GONE);
                    videoPreviewPlay.setVisibility(View.GONE);
                    audioPreview.setVisibility(View.GONE);
                    break;
                case REQUEST_VIDEO_CAMERA:
                    uri = data.getData();
                    try {
                        file = new File(android.os.Environment
                                .getExternalStorageDirectory(), "candeo/videos/candeovideo.mp4");
                        mimeType=CandeoUtil.getMimeType(uri, PostActivity.this);
                        fileName = file.getName();
                        dataArray = CandeoUtil.fileToByteArray(file);
                        mediaType=VIDEO;
                        playVideo(uri);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }


                    break;
                case PICK_VIDEO_FILE:
                    uri = data.getData();
                    cursor = getContentResolver().query(uri, new String[]{MediaStore.Audio.Media.DATA},null,null,null);
                    cursor.moveToFirst();
                    filePath = cursor.getString(0);
                    cursor.close();
                    file = new File(filePath);
                    mimeType=CandeoUtil.getMimeType(uri, PostActivity.this);
                    fileName = file.getName();
                    dataArray = CandeoUtil.fileToByteArray(file);
                    mediaType=VIDEO;
                    playVideo(uri);
                    break;
                case REQUEST_AUDIO_RECORD:
                    filePath= data.getStringExtra("path");
                    System.out.println("Path is "+ filePath);
                    file = new File(filePath);
                    mimeType=CandeoUtil.getMimeType(Uri.fromFile(file),PostActivity.this);
                    fileName = file.getName();
                    mediaType=AUDIO;
                    dataArray = CandeoUtil.fileToByteArray(file);
                    System.out.println("FILE IS "+fileName);
                    playAudio(filePath);
                    break;
                case PICK_AUDIO_FILE:
                    uri = data.getData();
                    cursor = getContentResolver().query(uri, new String[]{MediaStore.Audio.Media.DATA},null,null,null);
                    cursor.moveToFirst();
                    filePath = cursor.getString(0);
                    cursor.close();
                    file = new File(filePath);
                    mimeType=CandeoUtil.getMimeType(uri, PostActivity.this);
                    fileName = file.getName();
                    dataArray = CandeoUtil.fileToByteArray(file);
                    mediaType=AUDIO;
                    System.out.println("Path is "+ uri.getPath());
                    playAudio(uri);
                    break;
            }

        }
    }

    private void playVideo(Uri uri)
    {
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
