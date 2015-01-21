package com.candeo.app.content;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.book.BookRenderActivity;
import com.candeo.app.book.EpubCore;
import com.candeo.app.models.ebook.Book;
import com.candeo.app.models.ebook.TableOfContents;
import com.candeo.app.response.AppreciateActivity;
import com.candeo.app.response.GetInspiredActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.JSONParser;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ContentActivity extends ActionBarActivity{

    private static final String TAG="Candeo-Content Activity";
    private TextView description = null;
    private Toolbar toolbar;
    private TextView username = null;
    private Button getInspired=null;
    private Button appreciate=null;
    private Button launchBook=null; //temporary
    private String contentURL = CandeoApplication.baseUrl+"/api/v1/contents";
    private Button play = null;
    private int stopPosition=0;
    private MediaPlayer mediaPlayer;
    private FrameLayout videoHolder;
    private VideoView  videoView;
    private ImageView imageView;
    private LinearLayout contentViewer;
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

        String id = getIntent().getStringExtra("contentId");
        System.out.println("ID is :"+id);
        if(id!=null)
        {
            contentURL=contentURL+"/"+id;
            new LoadContent().execute(contentURL);
        }

        getInspired = (Button)findViewById(R.id.get_inspired);
        getInspired.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/response.ttf"));
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
        appreciate.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/applause.ttf"));
        appreciate.setText("\ue600");
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
                System.out.println("AND THE CONTENT IS: "+jsonObject.toString());
                contentViewer.setTag(type);
                if(type>0)
                {
                    final String mediaUrl=CandeoApplication.baseUrl+jsonObject.optString("media");
                    switch (type)
                    {
                        case 1: //audio
                            videoHolder.setVisibility(View.GONE);
                            imageView.setVisibility(View.GONE);
                            launchBook.setVisibility(View.GONE);
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
                            System.out.println("MEDIA URL is : "+mediaUrl);
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
                description = (TextView)findViewById(R.id.description);
                username = (TextView)findViewById(R.id.username);
                description.setText(jsonObject.optString("desc"));
                username.setText(jsonObject.optString("user"));

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
