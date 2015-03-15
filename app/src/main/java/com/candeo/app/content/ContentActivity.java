package com.candeo.app.content;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.home.HomeActivity;
import com.candeo.app.ui.ResponseFragment;
import com.candeo.app.user.LoginActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.JSONParser;
import com.candeo.app.util.Preferences;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContentActivity extends ActionBarActivity{

    private static final String TAG="Candeo-Content Activity";
    private Toolbar toolbar;
    private Button launchBook=null; //temporary
    private final static String CONTENT_RELATIVE_URL="/contents/%s/%s";
    private final static String CONTENT_URL = Configuration.BASE_URL +"/api/v1"+CONTENT_RELATIVE_URL;
    private TextView play = null;
    private int stopPosition=0;
    private MediaPlayer mediaPlayer;
    private FrameLayout candeoContentHolder;
    private VideoView  videoView;
    private ImageView imageView;
    private LinearLayout contentViewer;
    private ImageView bgImageView;
    private CircleImageView userAvatar;
    private TextView userName;
    private TextView title;
    private TextView appreciateIcon;
    private TextView appreciateCount;
    private TextView skipIcon;
    private TextView skipCount;
    private TextView inspiredIcon;
    private TextView inspiredCount;
    private Button getInspiredButton;
    private TextView createdAt;
    private View loadingContent;
    private RelativeLayout candeoMediaControl;
    private String id;

//    private Book book;
//    private TableOfContents bookToc;
//    private EpubCore epubCore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        id = getIntent().getStringExtra("id");
//        epubCore = new EpubCore();
        toolbar = (Toolbar)findViewById(R.id.candeo_content_toolbar);
        loadingContent = findViewById(R.id.candeo_loading_content);
        candeoMediaControl = (RelativeLayout)findViewById(R.id.candeo_content_media_control);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTypeface(CandeoUtil.loadFont(getAssets(),"fonts/fa.ttf"));
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setText(Configuration.FA_MAGIC);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_text)).setText("Loading Content...");
        candeoContentHolder =(FrameLayout)findViewById(R.id.candeo_content_viewer_holder);
        videoView =(VideoView)findViewById(R.id.candeo_video_viewer);
        play = (TextView)findViewById(R.id.candeo_media_play);
        play.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/fa.ttf"));
        play.setText(Configuration.FA_PLAY_ROUND);
        imageView = (ImageView)findViewById(R.id.candeo_image_viewer);
        launchBook = (Button)findViewById(R.id.candeo_book_launcher);
        setSupportActionBar(toolbar);
        setTitle("Candeo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        contentViewer=(LinearLayout)findViewById(R.id.candeo_content_viewer);
        bgImageView=(ImageView)findViewById(R.id.candeo_content_bg_image);
        userAvatar=(CircleImageView)findViewById(R.id.candeo_content_owner_avatar);
        userName =(TextView)findViewById(R.id.candeo_content_owner_name);
        title=(TextView)findViewById((R.id.candeo_content_title_text));
        appreciateIcon=(TextView)findViewById(R.id.candeo_content_appreciate_icon);
        appreciateIcon.setTypeface(CandeoUtil.loadFont(getAssets(),"fonts/applause.ttf"));
        appreciateIcon.setText(Configuration.FA_APPRECIATE);
        skipIcon=(TextView)findViewById(R.id.candeo_content_skip_icon);
        skipIcon.setTypeface(CandeoUtil.loadFont(getAssets(),"fonts/fa.ttf"));
        skipIcon.setText(Configuration.FA_SKIP);
        inspiredIcon=(TextView)findViewById(R.id.candeo_content_inspired_icon);
        inspiredIcon.setTypeface(CandeoUtil.loadFont(getAssets(),"fonts/response.ttf"));
        inspiredIcon.setText(Configuration.FA_INSPIRE);
        getInspiredButton=(Button)findViewById(R.id.candeo_content_inspire_button);
        getInspiredButton.setTypeface(CandeoUtil.loadFont(getAssets(),"fonts/response.ttf"));
        getInspiredButton.setText(Configuration.FA_INSPIRE);
        getInspiredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"I got inspired",Toast.LENGTH_LONG).show();
                if(!Preferences.isUserLoggedIn(ContentActivity.this))
                {


                    startActivity(new Intent(ContentActivity.this,LoginActivity.class));
                }
                else
                {

                    ResponseFragment response = new ResponseFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("introText", "I feel");
                    bundle.putStringArray("choices", Configuration.INSPIRE_LIST);
                    bundle.putString("title", "Get Inspired");
                    bundle.putString("positiveText", "Get Inspired");
                    bundle.putString("showcaseId", id);
                    bundle.putInt("responseType", Configuration.INSPIRE);
                    response.setArguments(bundle);
                    response.show(ContentActivity.this.getSupportFragmentManager(), "Appreciate");
                }
            }
        });


        createdAt=(TextView)findViewById(R.id.candeo_content_created_at);
        appreciateCount=(TextView)findViewById(R.id.candeo_content_appreciate_count);
        skipCount=(TextView)findViewById(R.id.candeo_content_skip_count);
        inspiredCount=(TextView)findViewById(R.id.candeo_content_inspired_count);
        int type = getIntent().getIntExtra("type",Configuration.SHOWCASE);
        Log.e(TAG,"ID is :"+id);
        Log.e(TAG,"Type is :"+type);
        if(id!=null)
        {
            new LoadContent(String.format(CONTENT_RELATIVE_URL,id,type)).execute(String.format(CONTENT_URL,id,type));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
//        Intent intent = new Intent(ContentActivity.this, HomeActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer!=null)
        {
            mediaPlayer.release();
        }
    }

    private class LoadContent extends AsyncTask<String, String, JSONObject> {


        private String relativeUrl;

        public LoadContent(String relativeUrl)
        {
            this.relativeUrl=relativeUrl;
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            return JSONParser.parseGET(urls[0], getApplicationContext(), relativeUrl);
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
                loadingContent.setVisibility(View.GONE);
                int type = jsonObject.optInt("media_type");
                Log.e(TAG,"AND THE CONTENT IS: "+jsonObject.toString());
                contentViewer.setTag(type);
                if(type>0)
                {
                    final String mediaUrl=Configuration.BASE_URL +jsonObject.optString("media_url");
                    final String bgUrl =Configuration.BASE_URL +jsonObject.optString("bg_url");
                    switch (type)
                    {
                        case 1: //audio
                            candeoContentHolder.setVisibility(View.VISIBLE);
                            imageView.setVisibility(View.GONE);
                            launchBook.setVisibility(View.GONE);
                            new LoadImageTask(bgUrl,bgImageView).execute();
                            playAudio(mediaUrl);
                            break;
                        case 2: //video
                            candeoContentHolder.setVisibility(View.VISIBLE);
                            videoView.setVisibility(View.VISIBLE);
                            play.setVisibility(View.VISIBLE);
                            play.setText("\uf04c");
                            imageView.setVisibility(View.GONE);
                            launchBook.setVisibility(View.GONE);
                            System.out.println("MEDIA URL is : " + mediaUrl);
                            playVideo(mediaUrl);
                            break;

                        case 3:
                            //Image
                            candeoContentHolder.setVisibility(View.GONE);
                            play.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                            launchBook.setVisibility(View.GONE);
                            Log.e(TAG, "MEDIA URL is : "+mediaUrl);
                            new LoadImageTask(mediaUrl,imageView).execute();
                            break;

//                        case 4:
//                            //Book
//                            candeoContentHolder.setVisibility(View.GONE);
//                            imageView.setVisibility(View.GONE);
//                            launchBook.setVisibility(View.VISIBLE);
//
//                            launchBook.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    new FetchBookTask().execute(mediaUrl);
//                                }
//                            });
//                            break;


                    }
                }
                new LoadImageTask(Configuration.BASE_URL +jsonObject.optString("user_avatar_url"),userAvatar).execute();
                userName.setText(jsonObject.optString("user_name"));
                title.setText(jsonObject.optString("title"));
                appreciateCount.setText(jsonObject.optString("appreciate_count"));
                skipCount.setText(jsonObject.optString("skip_count"));
                inspiredCount.setText(jsonObject.optString("inspired_count"));
                createdAt.setText(jsonObject.optString("created_at"));

        }
    }

    private void playVideo(String url)
    {
        videoView.setVideoPath(url);
        videoView.setVisibility(View.VISIBLE);
        play.setVisibility(View.VISIBLE);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer=mp;
                mediaPlayer.start();
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mediaPlayer.isPlaying())
                {
                    videoView.start();
                    play.setText("\uf04c");
                }
                else
                {
                    videoView.pause();
                    play.setText("\uf04b");
                }

                if(videoView!=null)
                {
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mediaPlayer.pause();
                            mediaPlayer.seekTo(0);
                            play.setText("\uf04b");
                        }
                    });
                }
            }
        });

    }

//    private class FetchBookTask extends AsyncTask<String, Void, Void>
//    {
//
//        private ProgressDialog dialog = new ProgressDialog(ContentActivity.this);
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            dialog.setMessage("Processing Book...");
//            dialog.setIndeterminate(false);
//            dialog.setCancelable(true);
//            dialog.show();
//        }
//
//        @Override
//        protected Void doInBackground(String... params) {
//            try
//            {
//                URL url = new URL(params[0]);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.connect();
//                Log.e(ContentActivity.class.getName(),"REQUEST METHOD IS "+urlConnection.getRequestMethod());
//                File file = new File(Environment.getExternalStorageDirectory()+"/candeo/books/tmp.epub");
//                FileOutputStream fos = new FileOutputStream(file);
//                InputStream inputStream = urlConnection.getInputStream();
//                byte[] buffer = new byte[1024];
//                int bufferLength=0;
//                while((bufferLength=inputStream.read(buffer))>0)
//                {
//                    fos.write(buffer,0,bufferLength);
//                }
//                fos.close();
//                String filePath =Environment.getExternalStorageDirectory()+"/candeo/books/tmp.epub";
////                String unzippedBookPath = epubCore.unzipEpub(filePath,"tmp");
////                book = new Book();
////                book = epubCore.loadBook(unzippedBookPath,book);
////                bookToc = epubCore.getToc();
//
//            }
//            catch (IOException ioe)
//            {
//                ioe.printStackTrace();
//            }
//
//
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            if(dialog!=null)
//            {
//                dialog.dismiss();
//            }
//            Intent intent = new Intent(ContentActivity.this, BookRenderActivity.class);
//            intent.putExtra(Configuration.INTENTBOOK,book);
//            intent.putParcelableArrayListExtra(Configuration.INTENTCHAPTERLIST,bookToc.getChapters());
//            intent.putExtra(Configuration.INTENTBASEURL,epubCore.getBaseUrl());
//            startActivity(intent);
//
//        }
//    }

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
            pDialog.setMessage("Loading Image...");
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
                Log.e(TAG,"Bitmap width is "+ width+" and height is "+height);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
                getWindowManager().getDefaultDisplay().getMetrics(CandeoApplication.displayMetrics);
                int screenWidth=CandeoApplication.displayMetrics.widthPixels;
                Log.e(TAG,"Screen width is "+ screenWidth);
                double scaleFactor = (width*1.0)/(screenWidth*1.0);
                Log.e(TAG,"Scale Factor is "+scaleFactor);
                int calculatedHeight=height;
                if(scaleFactor>0)
                {
                    calculatedHeight = (int)(height/scaleFactor);
                }

                Log.e(TAG,"Calculated width is "+ screenWidth+" and height is "+calculatedHeight);
                Animation in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                imageView.startAnimation(in);
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,screenWidth,calculatedHeight,false));
            }
            pDialog.dismiss();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.content, menu);
        MenuItem settings = menu.getItem(0);
        settings.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private void playAudio(String mediaUrl)
    {


        try
        {
            mediaPlayer= new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(mediaUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });

            candeoMediaControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG,"Media control clicked");
                    if(play.getVisibility() == View.VISIBLE)
                    {
                        mediaPlayer.start();
                        Animation out = AnimationUtils.makeOutAnimation(getApplicationContext(), true);
                        candeoMediaControl.startAnimation(out);
                        candeoMediaControl.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        play.setVisibility(View.GONE);
                    }
                    else
                    {
                        mediaPlayer.pause();
                        Animation in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                        candeoMediaControl.startAnimation(in);
                        candeoMediaControl.setBackgroundColor(getResources().getColor(R.color.candeo_translucent_black));
                        play.setVisibility(View.VISIBLE);
                    }
                }
            });

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

}
