package com.candeo.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.candeo.app.util.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class ContentActivity extends Activity {

    TextView description = null;
    TextView username = null;
    Button getInspired=null;
    private String domain="http://192.168.0.104:3000";
    private String contentURL = domain+"/api/v1/contents";
    Typeface freeBooter;
    MediaController mediaController;
    MediaPlayer mediaPlayer;
    VideoView  videoView;
    LinearLayout contentViewer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        contentViewer=(LinearLayout)findViewById(R.id.candeo_content_viewer);
        freeBooter= loadFontFreeBooter();
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView title = (TextView) findViewById(titleId);
        setTitle("Candeo");
        getActionBar().setIcon(android.R.color.transparent);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        title.setTypeface(freeBooter, Typeface.BOLD);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        String id = getIntent().getStringExtra("contentId");
        System.out.println("ID is :"+id);
        if(id!=null)
        {
            contentURL=contentURL+"/"+id;
            new LoadContent().execute(contentURL);
        }

        getInspired = (Button)findViewById(R.id.get_inspired);
        getInspired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ContentActivity.this, "I got inspired", Toast.LENGTH_LONG).show();
                getInspired.setEnabled(false);
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
                if(type>0)
                {
                    mediaController = new MediaController(ContentActivity.this);
                    String mediaUrl=domain+jsonObject.optString("media");
                    switch (type)
                    {
                        case 1:
                            try
                            {
                                mediaPlayer= new MediaPlayer();
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mediaPlayer.setDataSource(mediaUrl);
                                mediaPlayer.prepareAsync();
                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp = mediaPlayer;
                                        mp.start();
                                    }
                                });
                                mediaController.setAnchorView(contentViewer);

                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }

                            break;
                        case 2:
                            videoView = new VideoView(ContentActivity.this);
                            System.out.println("MEDIA URL is : "+mediaUrl);
                            videoView.setVideoPath(mediaUrl);
                            videoView.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            contentViewer.addView(videoView);
                            mediaController.setMediaPlayer(videoView);
                            mediaController.setAnchorView(videoView);
                            videoView.setMediaController(mediaController);
                            videoView.start();
                            break;
                        case 3:
                            //Image
                            break;
                    }
                }
                description = (TextView)findViewById(R.id.description);
                username = (TextView)findViewById(R.id.username);
                description.setText(jsonObject.optString("desc"));
                username.setText(jsonObject.optString("username"));

        }
    }


    private Typeface loadFontFreeBooter()
    {
        Typeface fontAwesome = Typeface.createFromAsset(getAssets(),"freebooter.ttf");
        return  fontAwesome;
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
