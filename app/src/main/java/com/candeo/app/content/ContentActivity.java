package com.candeo.app.content;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
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

import com.amplitude.api.Amplitude;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.ui.CustomTextView;
import com.candeo.app.ui.ResponseFragment;
import com.candeo.app.ui.ResponseListFragment;
import com.candeo.app.user.LoginActivity;
import com.candeo.app.user.UserActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.JSONParser;
import com.candeo.app.util.Preferences;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContentActivity extends AppCompatActivity implements InspirationListener{

    private static final String TAG="Candeo-Content Activity";
    private Toolbar toolbar;
    private Button launchBook=null; //temporary
    private final static String CONTENT_RELATIVE_URL="/contents/%s/%s/%s";
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
    private TextView contentDescription;
    private TextView appreciateIcon;
    private TextView appreciateCount;
    private TextView skipIcon;
    private TextView skipCount;
    private TextView inspiredIcon;
    private TextView inspiredCount;
    private Button getInspiredButton;
//    private Button shareButton;
    private TextView shareButton;
    private TextView createdAt;
    private View loadingContent;
    private RelativeLayout candeoMediaControl;
    private RelativeLayout candeoWriterHolder;
    private CustomTextView candeoWriting;
    private String id;
    private BroadcastReceiver mScreenReceiver;

//    private Book book;
//    private TableOfContents bookToc;
//    private EpubCore epubCore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Amplitude.getInstance().logEvent("Content Activity loaded");
        String url = getIntent().getDataString();
        if(!TextUtils.isEmpty(url))
        {
            id = CandeoUtil.getCodeFromUrl(url);
        }
        else
        {
            id = getIntent().getStringExtra("id");
        }
//        epubCore = new EpubCore();
        toolbar = (Toolbar)findViewById(R.id.candeo_content_toolbar);
        loadingContent = findViewById(R.id.candeo_loading_content);
        candeoMediaControl = (RelativeLayout)findViewById(R.id.candeo_content_media_control);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/fa.ttf"));
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_icon)).setText(Configuration.FA_MAGIC);
        ((TextView)loadingContent.findViewById(R.id.candeo_progress_text)).setText("Loading Content...");
        candeoContentHolder =(FrameLayout)findViewById(R.id.candeo_content_viewer_holder);
        candeoWriterHolder=(RelativeLayout)findViewById(R.id.candeo_content_writing_holder);
        candeoWriting=(CustomTextView)findViewById(R.id.candeo_content_writing);
        videoView =(VideoView)findViewById(R.id.candeo_video_viewer);
        play = (TextView)findViewById(R.id.candeo_media_play);
        play.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/fa.ttf"));
        play.setText(Configuration.FA_PLAY_ROUND);
        imageView = (ImageView)findViewById(R.id.candeo_image_viewer);
        launchBook = (Button)findViewById(R.id.candeo_book_launcher);
        setSupportActionBar(toolbar);
        setTitle("Please wait...");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        contentViewer=(LinearLayout)findViewById(R.id.candeo_content_viewer);
        bgImageView=(ImageView)findViewById(R.id.candeo_content_bg_image);
        userAvatar=(CircleImageView)findViewById(R.id.candeo_content_owner_avatar);
        userName =(TextView)findViewById(R.id.candeo_content_owner_name);
        title=(TextView)findViewById((R.id.candeo_content_title_text));
        contentDescription=(TextView)findViewById(R.id.candeo_content_desc_text);
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
                Amplitude.getInstance().logEvent("Get Inspired clicked");
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
                    response.setInspirationListener(ContentActivity.this);
                    response.setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Light_Dialog);
                    response.show(ContentActivity.this.getSupportFragmentManager().beginTransaction(), "Appreciate");
                }
            }
        });
        shareButton = (TextView)findViewById(R.id.candeo_content_share_button);
        shareButton.setTypeface(CandeoUtil.loadFont(getAssets(),"fonts/fa.ttf"));
        shareButton.setText(Configuration.FA_SHARE_ALT);

        createdAt=(TextView)findViewById(R.id.candeo_content_created_at);
        appreciateCount=(TextView)findViewById(R.id.candeo_content_appreciate_count);
        skipCount=(TextView)findViewById(R.id.candeo_content_skip_count);
        inspiredCount=(TextView)findViewById(R.id.candeo_content_inspired_count);
        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userAvatar.getTag()!=null)
                {
                    Amplitude.getInstance().logEvent("Creator avatar clicked");
                    finish();
                    Intent userIntent= new Intent(getApplicationContext(), UserActivity.class);
                    userIntent.putExtra("id",userAvatar.getTag().toString());
                    startActivity(userIntent);
                }
            }
        });
        int type = getIntent().getIntExtra("type",Configuration.SHOWCASE);
        if(Configuration.DEBUG)Log.e(TAG,"ID is :"+id);
        if(Configuration.DEBUG)Log.e(TAG,"Type is :"+type);
        if(id!=null)
        {
            String userId = TextUtils.isEmpty(Preferences.getUserRowId(getApplicationContext()))?"0": Preferences.getUserRowId(getApplicationContext());
            new LoadContent(String.format(CONTENT_RELATIVE_URL,id,type,userId)).execute(String.format(CONTENT_URL,id,type,userId));
        }
        PhoneStateListener phoneStateListener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state)
                {
                    case TelephonyManager.CALL_STATE_RINGING:
                        if(mediaPlayer!=null && mediaPlayer.isPlaying())
                        {
                            mediaPlayer.pause();
                        }
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if(mediaPlayer!=null && !mediaPlayer.isPlaying())
                        {
                            mediaPlayer.start();
                        }
                        break;

                }

            }
        };
        TelephonyManager manager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        if(manager!=null)
        {
            manager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    @Override

    public void onSubmit() {
        getInspiredButton.setEnabled(false);
        getInspiredButton.setTextColor(getResources().getColor(R.color.candeo_light_gray));
        int count = Integer.parseInt(inspiredCount.getText().toString());
        inspiredCount.setText(""+(++count));
        Toast.makeText(getApplicationContext(),"You got inspired",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Amplitude.getInstance().startSession();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Amplitude.getInstance().endSession();
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

                if(Configuration.DEBUG)Log.e(TAG,"AND THE CONTENT IS: "+jsonObject.toString());
                contentViewer.setTag(type);

                    final String mediaUrl=jsonObject.optString("media_url");
                    if(Configuration.DEBUG)Log.e(TAG,"MEDIA URL IS "+mediaUrl);
                    final String bgUrl =jsonObject.optString("bg_url");
                    switch (type)
                    {
                        case 0: //Text
                            candeoMediaControl.setVisibility(View.GONE);
                            candeoContentHolder.setVisibility(View.VISIBLE);
                            candeoWriterHolder.setVisibility(View.VISIBLE);
                            candeoWriting.setText(jsonObject.optString("description"));
                            candeoWriting.setMovementMethod(new ScrollingMovementMethod());
                            contentDescription.setVisibility(View.GONE);
                            imageView.setVisibility(View.GONE);
                            launchBook.setVisibility(View.GONE);
                            play.setVisibility(View.GONE);
                            new LoadImageTask(bgUrl,bgImageView).execute();
                            break;
                        case 1: //audio
                            candeoWriterHolder.setVisibility(View.GONE);
                            candeoMediaControl.setVisibility(View.VISIBLE);
                            candeoContentHolder.setVisibility(View.VISIBLE);
                            imageView.setVisibility(View.GONE);
                            launchBook.setVisibility(View.GONE);
                            contentDescription.setVisibility(View.VISIBLE);
                            contentDescription.setText(jsonObject.optString("description"));
                            contentDescription.setLinkTextColor(ContentActivity.this.getResources().getColor(R.color.candeo_primary_dark));
                            Linkify.addLinks(contentDescription, Linkify.ALL);
                            new LoadImageTask(bgUrl,bgImageView).execute();
                            playAudio(mediaUrl);
                            break;
                        case 2: //video
                            candeoContentHolder.setVisibility(View.VISIBLE);
                            candeoMediaControl.setVisibility(View.VISIBLE);
                            contentDescription.setVisibility(View.VISIBLE);
                            contentDescription.setText(jsonObject.optString("description"));
                            contentDescription.setLinkTextColor(ContentActivity.this.getResources().getColor(R.color.candeo_primary_dark));
                            Linkify.addLinks(contentDescription, Linkify.ALL);
                            new LoadImageTask(bgUrl,bgImageView).execute();
                            videoView.setVisibility(View.VISIBLE);
                            imageView.setVisibility(View.GONE);
                            launchBook.setVisibility(View.GONE);
                            System.out.println("MEDIA URL is : " + mediaUrl);
                            playVideo(mediaUrl);
                            break;

                        case 3:
                            //Image
                            contentDescription.setVisibility(View.VISIBLE);
                            contentDescription.setText(jsonObject.optString("description"));
                            contentDescription.setLinkTextColor(ContentActivity.this.getResources().getColor(R.color.candeo_primary_dark));
                            Linkify.addLinks(contentDescription, Linkify.ALL);
                            candeoContentHolder.setVisibility(View.GONE);
                            candeoWriterHolder.setVisibility(View.GONE);
                            candeoMediaControl.setVisibility(View.GONE);
                            play.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                            launchBook.setVisibility(View.GONE);
                            if(Configuration.DEBUG)Log.e(TAG, "MEDIA URL is : "+mediaUrl);
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

                new LoadImageTask(jsonObject.optString("user_avatar_url"),userAvatar).execute();
                final String short_id= jsonObject.optString("short_id");
                shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Amplitude.getInstance().logEvent("Share button clicked");
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "http://www.candeoapp.com/c/" + short_id);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                                }
                });
                userName.setText(jsonObject.optString("user_name"));
                userAvatar.setTag(jsonObject.optString("user_id"));
                title.setText(jsonObject.optString("title"));
                setTitle(jsonObject.optString("title"));
                appreciateCount.setText(jsonObject.optString("appreciate_count"));
                if(Integer.parseInt(appreciateCount.getText().toString()) > 0)
                {
                    appreciateIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Amplitude.getInstance().logEvent("Appreciate icon clicked for list");
                            ResponseListFragment response = new ResponseListFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("title", "Appreciations");
                            bundle.putString("contentId",id);
                            response.setArguments(bundle);
                            response.setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Light_Dialog);
                            response.show(getSupportFragmentManager(), "Appreciations");
                        }
                    });

                    appreciateCount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Amplitude.getInstance().logEvent("Appreciate count clicked for list");
                            ResponseListFragment response = new ResponseListFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("title", "Appreciations");
                            bundle.putString("contentId",id);
                            response.setArguments(bundle);
                            response.setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Light_Dialog);
                            response.show(getSupportFragmentManager(), "Appreciations");

                        }
                    });
                }
                skipCount.setText(jsonObject.optString("skip_count"));
                inspiredCount.setText(jsonObject.optString("inspired_count"));
                if(Integer.parseInt(inspiredCount.getText().toString())>0)
                {
                    inspiredCount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Amplitude.getInstance().logEvent("Inspired count clicked for list");
                            ResponseListFragment response = new ResponseListFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("title", "Inspirations");
                            bundle.putString("contentId",id);
                            response.setArguments(bundle);
                            response.setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Light_Dialog);
                            response.show(getSupportFragmentManager(), "Inspirations");
                        }
                    });

                    inspiredIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Amplitude.getInstance().logEvent("Inspired icon clicked for list");
                            ResponseListFragment response = new ResponseListFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("title", "Inspirations");
                            bundle.putString("contentId",id);
                            response.setArguments(bundle);
                            response.setStyle(DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Light_Dialog);
                            response.show(getSupportFragmentManager(), "Inspirations");
                        }
                    });

                }
                createdAt.setText(jsonObject.optString("created_at"));
                boolean hasBeenInspired=jsonObject.optBoolean("has_been_inspired");
                if(hasBeenInspired)
                {
                    getInspiredButton.setEnabled(false);
                    getInspiredButton.setTextColor(getResources().getColor(R.color.candeo_light_gray));

                }
                else
                {
                    getInspiredButton.setEnabled(true);
                    getInspiredButton.setTextColor(getResources().getColor(R.color.abc_secondary_text_material_dark));
                }

        }
    }

    private void playVideo(String url)
    {
        videoView.setVideoPath(url);
        videoView.setVisibility(View.VISIBLE);
        candeoMediaControl.setEnabled(false);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer=mp;
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
                candeoMediaControl.setEnabled(true);
            }
        });
        candeoMediaControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amplitude.getInstance().logEvent("Video clicked");
                if (!mediaPlayer.isPlaying()) {
                    videoView.start();
                    Animation out = AnimationUtils.makeOutAnimation(getApplicationContext(), true);
                    candeoMediaControl.startAnimation(out);
                    candeoMediaControl.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    play.setVisibility(View.GONE);
                } else {
                    videoView.pause();
                    Animation in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                    candeoMediaControl.startAnimation(in);
                    candeoMediaControl.setBackgroundColor(getResources().getColor(R.color.candeo_translucent_black));
                    play.setVisibility(View.VISIBLE);
                }

                if (videoView != null) {
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mediaPlayer.start();
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
//                if(Configuration.DEBUG)Log.e(ContentActivity.class.getName(),"REQUEST METHOD IS "+urlConnection.getRequestMethod());
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
            if(!isFinishing())
            {
                pDialog = new ProgressDialog(ContentActivity.this);
                pDialog.setMessage("Loading Image...");
                pDialog.setIndeterminate(false);
                pDialog.setCancelable(true);
                pDialog.show();
            }

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
            catch (Exception e)
            {
                e.printStackTrace();
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
                if(Configuration.DEBUG)Log.e(TAG,"Bitmap width is "+ width+" and height is "+height);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
                getWindowManager().getDefaultDisplay().getMetrics(CandeoApplication.displayMetrics);
                int screenWidth=CandeoApplication.displayMetrics.widthPixels;
                if(Configuration.DEBUG)Log.e(TAG,"Screen width is "+ screenWidth);
                double scaleFactor = (width*1.0)/(screenWidth*1.0);
                if(Configuration.DEBUG)Log.e(TAG,"Scale Factor is "+scaleFactor);
                int calculatedHeight=height;
                if(scaleFactor>0)
                {
                    calculatedHeight = (int)(height/scaleFactor);
                }

                if(Configuration.DEBUG)Log.e(TAG,"Calculated width is "+ screenWidth+" and height is "+calculatedHeight);
                Animation in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                imageView.startAnimation(in);
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,screenWidth,calculatedHeight,false));
            }
            if(!isFinishing())
            {
                pDialog.dismiss();
            }

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
            Animation in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
            candeoMediaControl.startAnimation(in);
            candeoMediaControl.setBackgroundColor(getResources().getColor(R.color.candeo_translucent_black));
            play.setText("Please wait...");
            play.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            play.setVisibility(View.VISIBLE);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if(mediaPlayer.isPlaying())
                    {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
//                    mediaPlayer.start();

                    play.setText(Configuration.FA_PLAY_ROUND);
                    play.setTextSize(TypedValue.COMPLEX_UNIT_SP, 95);
                    play.setVisibility(View.VISIBLE);
                }
            });

            candeoMediaControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Configuration.DEBUG)Log.e(TAG,"Media control clicked");
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
