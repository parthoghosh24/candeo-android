package com.candeo.app.content;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.candeo.app.CandeoApplication;
import com.candeo.app.R;
import com.candeo.app.response.AppreciateActivity;
import com.candeo.app.response.GetInspiredActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.JSONParser;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class ContentActivity extends ActionBarActivity implements MediaController.MediaPlayerControl{

    TextView description = null;
    Toolbar toolbar;
    TextView username = null;
    Button getInspired=null;
    Button appreciate=null;
    private String contentURL = CandeoApplication.baseUrl+"/api/v1/contents";
    MediaController mediaController;
    MediaPlayer mediaPlayer;
    VideoView  videoView;
    ImageView imageView;
    LinearLayout contentViewer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        toolbar = (Toolbar)findViewById(R.id.candeo_content_toolbar);
        setSupportActionBar(toolbar);
        contentViewer=(LinearLayout)findViewById(R.id.candeo_content_viewer);
        setTitle("Candeo");
        String id = getIntent().getStringExtra("contentId");
        System.out.println("ID is :"+id);
        if(id!=null)
        {
            contentURL=contentURL+"/"+id;
            new LoadContent().execute(contentURL);
        }

        getInspired = (Button)findViewById(R.id.get_inspired);
        getInspired.setTypeface(CandeoUtil.loadFont(getAssets(),"response.ttf"));
        getInspired.setText("\ue800");
        getInspired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ContentActivity.this, "I got inspired", Toast.LENGTH_LONG).show();
                //getInspired.setEnabled(false);
                startActivity(new Intent(ContentActivity.this, GetInspiredActivity.class));
            }
        });

        appreciate=(Button)findViewById(R.id.appreciate);
        appreciate.setTypeface(CandeoUtil.loadFont(getAssets(),"response.ttf"));
        appreciate.setText("\ue801");
        appreciate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ContentActivity.this, "I appreciated", Toast.LENGTH_LONG).show();
                //appreciate.setEnabled(false);
                startActivity(new Intent(ContentActivity.this, AppreciateActivity.class));
            }
        });




    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mediaController!=null)
        {
            mediaController.hide();
        }

        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        if(videoView!=null)
        {
            videoView.stopPlayback();
        }

    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
        }
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public int getBufferPercentage() {
        return (mediaPlayer.getCurrentPosition()*100)/mediaPlayer.getDuration();
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if((Integer)contentViewer.getTag() == 1 || (Integer)contentViewer.getTag() == 2)
        {
            mediaController.show(0);
        }
        return false;
    }

    private class LoadContent extends AsyncTask<String, String, JSONObject> {

        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ContentActivity.this);
            pDialog.setMessage("Loading Content...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            return JSONParser.parseGET(urls[0]);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
                pDialog.dismiss();
                int type = jsonObject.optInt("media_type");
                System.out.println("AND THE CONTENT IS: "+jsonObject.toString());
                contentViewer.setTag(type);
                if(type>0)
                {
                    mediaController = new MediaController(ContentActivity.this);
                    String mediaUrl=CandeoApplication.baseUrl+jsonObject.optString("media");
                    switch (type)
                    {
                        case 1: //audio
                            try
                            {
                                mediaPlayer= new MediaPlayer();
                                mediaController = new MediaController(ContentActivity.this);
                                mediaController.setMediaPlayer(ContentActivity.this);
                                mediaController.setAnchorView(contentViewer);
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mediaPlayer.setDataSource(mediaUrl);
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mediaController.show(0);
                                        mediaPlayer.start();
                                    }
                                });

                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }

                            break;
                        case 2: //video

                            videoView = new VideoView(ContentActivity.this);
                            System.out.println("MEDIA URL is : "+mediaUrl);
                            videoView.setVideoPath(mediaUrl);
                            videoView.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            contentViewer.addView(videoView);
                            mediaController.setMediaPlayer(videoView);
                            mediaController.setAnchorView(contentViewer);
                            videoView.setMediaController(mediaController);
                            videoView.seekTo(100);
                            videoView.start();
                            break;
                        case 3:
                            //Image
                            imageView = new ImageView(ContentActivity.this);
                            imageView.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            contentViewer.addView(imageView);
                            System.out.println("MEDIA URL is : "+mediaUrl);
                            new LoadImageTask(mediaUrl,imageView).execute();
                            break;
                    }
                }
                description = (TextView)findViewById(R.id.description);
                username = (TextView)findViewById(R.id.username);
                description.setText(jsonObject.optString("desc"));
                System.out.println("User is "+jsonObject.optString("user"));
                username.setText(jsonObject.optString("user"));

        }
    }


    private class LoadImageTask extends AsyncTask<String, String, Bitmap>
    {
        private ProgressDialog pDialog;
        private String url="";
        private ImageView imageView;

        LoadImageTask(String url, ImageView imageView)
        {
            this.url=url;
            this.imageView=imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ContentActivity.this);
            pDialog.setMessage("Loading Content...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap=null;
            try
            {
                URL imageUrl = new URL(url);
                bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
            }
            catch (IOException ioe)
            {
                    ioe.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            System.out.println("BITMAP IS "+bitmap);
            if(bitmap!=null)
            {
                int height=bitmap.getHeight();
                int width=bitmap.getWidth();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
                if(width > height)
                {
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,640,400,false));
                }
                else
                {
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,400,640,false));
                }
            }
            pDialog.dismiss();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
