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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.response.AppreciateActivity;
import com.candeo.app.response.GetInspiredActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.JSONParser;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContentActivity extends ActionBarActivity{

    private static final String TAG="Candeo-Content Activity";
    private Toolbar toolbar;
    private Button launchBook=null; //temporary
    private String contentURL = Configuration.BASE_URL +"/api/v1/contents";
    private Button play = null;
    private int stopPosition=0;
    private MediaPlayer mediaPlayer;
    private FrameLayout videoHolder;
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

//    private Book book;
//    private TableOfContents bookToc;
//    private EpubCore epubCore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
//        epubCore = new EpubCore();
        toolbar = (Toolbar)findViewById(R.id.candeo_content_toolbar);
        videoHolder=(FrameLayout)findViewById(R.id.candeo_content_viewer_holder);
        videoView =(VideoView)findViewById(R.id.candeo_video_viewer);
        play = (Button)findViewById(R.id.candeo_media_play);
        play.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/fa.ttf"));
        play.setText("\uf04b");
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
        String id = getIntent().getStringExtra("id");
        int type = getIntent().getIntExtra("type",Configuration.SHOWCASE);
        System.out.println("ID is :"+id);
        if(id!=null)
        {
            contentURL=contentURL+"/"+id+"/"+type;
            new LoadContent().execute(contentURL);
        }






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
                Log.e(TAG,"AND THE CONTENT IS: "+jsonObject.toString());
                contentViewer.setTag(type);
                if(type>0)
                {
                    final String mediaUrl=Configuration.BASE_URL +jsonObject.optString("media_url");
                    final String bgUrl =Configuration.BASE_URL +jsonObject.optString("bg_url");
                    switch (type)
                    {
                        case 1: //audio
                            videoHolder.setVisibility(View.VISIBLE);
                            imageView.setVisibility(View.GONE);
                            launchBook.setVisibility(View.GONE);
                            new LoadImageTask(bgUrl,bgImageView).execute();
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

                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }

                            break;
                        case 2: //video
                            videoHolder.setVisibility(View.VISIBLE);
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
                            videoHolder.setVisibility(View.GONE);
                            play.setVisibility(View.GONE);
                            imageView.setVisibility(View.VISIBLE);
                            launchBook.setVisibility(View.GONE);
                            Log.e(TAG, "MEDIA URL is : "+mediaUrl);
                            new LoadImageTask(mediaUrl,imageView).execute();
                            break;

//                        case 4:
//                            //Book
//                            videoHolder.setVisibility(View.GONE);
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

}
