package com.candeo.app.content;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.Configuration;
import com.candeo.app.R;

import com.candeo.app.algorithms.Security;
import com.candeo.app.network.CandeoHttpClient;
import com.candeo.app.network.UploadMediaListener;
import com.candeo.app.network.UploadMediaTask;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PostActivity extends ActionBarActivity implements UploadMediaListener {

    private static final String TAG="Candeo - Post Activity";
    private Button audio;
    private Button image;
    private Button video;
    private Uri imageUri;
    private Bitmap bitmap;
//    Button book; //Next version. Currently running into multiple issues. Need to research alot more to find the right solution. Webview or custom viewer???
    private Button postIt;
    private TextView copyrightText;
    private EditText description;
    private EditText showcaseTitle;
    private Button audioPreview;
    private ImageView imagePreview;
    private VideoView videoPreview;
    private Button videoPreviewPlay;
    private Toolbar toolbar;
    private String mimeType;
    private String fileName;
    private ViewPager mediaChooser;
    private TextView mediaChooserText;
    private LinearLayout mediaButtonsForInspire;
    private boolean hasMedia;
    private String mediaId;
    private byte[] dataArray;
    private boolean isPlaying=false;
    private int stopPosition=0;
    private MediaPlayer player;
    private static final int REQUEST_IMAGE_CAMERA=100;
    private static final int PICK_VIDEO_FILE=200;
    private static final int REQUEST_VIDEO_CAMERA=300;
    private static final int PICK_AUDIO_FILE=400;
    private static final int REQUEST_AUDIO_RECORD=500;
    private static final int PICK_IMAGE_FILE=600;
//    private static final int PICK_EPUB_FILE=700;

    private static final int AUDIO=1;
    private static final int VIDEO=2;
    private static final int IMAGE=3;
//    private static final int BOOK=4;



    private int contentType=Configuration.INSPIRATION;
    private String type="";
    private static final String API_POST_CREATE_RELATIVE_URL="/contents/create ";
    private static final String API_POST_CREATE_URL=Configuration.BASE_URL +"/api/v1"+API_POST_CREATE_RELATIVE_URL;

    @Override
    public void onSuccess(String response) {

        if(!TextUtils.isEmpty(response))
        {
            try {
                JSONObject json = new JSONObject(response);
                mediaId = json.getString("id");
                hasMedia=true;
                Log.e(TAG,"Media id is "+mediaId);
            }
            catch (JSONException jsonex)
            {
                jsonex.printStackTrace();
            }

        }
    }

    @Override
    public void onFailure(String response) {

        if(!TextUtils.isEmpty(response))
        {
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        toolbar = (Toolbar)findViewById(R.id.candeo_post_toolbar);
        type = getIntent().getStringExtra("type");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mediaChooser = (ViewPager)findViewById(R.id.candeo_showcase_media_chooser);
        mediaChooserText =(TextView)findViewById(R.id.candeo_showcase_media_chooser_text);
        mediaButtonsForInspire = (LinearLayout)findViewById(R.id.candeo_post_top_buttons);
        audioPreview = (Button)findViewById(R.id.candeo_audio_preview);
        audioPreview.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/fa.ttf"));
        audioPreview.setText("\uf04b");
        imagePreview=(ImageView)findViewById(R.id.candeo_image_preview);
        videoPreview=(VideoView)findViewById(R.id.candeo_video_preview);
        videoPreviewPlay=(Button)findViewById(R.id.candeo_video_preview_play);
        videoPreviewPlay.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/fa.ttf"));
        videoPreviewPlay.setText("\uf04c");
        description=(EditText)findViewById(R.id.candeo_content_create);
        showcaseTitle = (EditText)findViewById(R.id.candeo_post_title);
        audio=(Button)findViewById(R.id.candeo_audio);
        audio.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/fa.ttf"));
        audio.setText(Configuration.FA_AUDIO);
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
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
        image.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/fa.ttf"));
        image.setText(Configuration.FA_IMAGE);
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
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, UUID.randomUUID().toString()+".jpg");
                            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
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
        video.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/fa.ttf"));
        video.setText(Configuration.FA_VIDEO);
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
                            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 16*1024*1024); //16 MB Limit
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            startActivityForResult(intent, REQUEST_VIDEO_CAMERA);
                        }
                        else if(choices[which].equals("Fetch From Gallery"))
                        {
                            Intent intent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 16*1024*1024); //16 MB Limit
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

//        book=(Button)findViewById(R.id.candeo_book);
//        book.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/fa.ttf"));
//        book.setText(Configuration.FA_BOOK);
//        book.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("epub/*");
//                startActivityForResult(Intent.createChooser(intent,"Select EPUB"),PICK_EPUB_FILE);
//            }
//        });

        postIt=(Button)findViewById(R.id.candeo_post_it);
        postIt.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/fa.ttf"));
        postIt.setText(Configuration.FA_MAGIC);
        postIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Configuration.SHOWCASE == contentType)
                {
                  if(!hasMedia || TextUtils.isEmpty(showcaseTitle.getText()))
                  {
                      Toast.makeText(getApplicationContext(),"Media and Title mandatory for showcase",Toast.LENGTH_LONG).show();
                  }
                  else
                  {
                      Map<String, String> payload = new HashMap<>();
                      payload.put("type",Integer.toString(Configuration.SHOWCASE));
                      payload.put("media_id",mediaId);
                      payload.put("user_id",Preferences.getUserRowId(getApplicationContext()));
                      payload.put("title",showcaseTitle.getText().toString());
                      CandeoApplication.getInstance().getAppRequestQueue().add(new PostContentRequest(payload));
                  }
                }
                else
                {
                    if(hasMedia || !TextUtils.isEmpty(description.getText()))
                    {
                        Map<String, String> payload = new HashMap<>();
                        payload.put("type",Integer.toString(Configuration.INSPIRATION));
                        if(hasMedia)
                        {
                            payload.put("media_id",mediaId);
                        }
                        payload.put("user_id",Preferences.getUserRowId(getApplicationContext()));
                        payload.put("description",description.getText().toString());
                        CandeoApplication.getInstance().getAppRequestQueue().add(new PostContentRequest(payload));

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Media and Title mandatory for showcase",Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        copyrightText=(TextView)findViewById(R.id.candeo_copyright_text);

        if("showcase".equalsIgnoreCase(type))                                                               
        {
            contentType=Configuration.SHOWCASE;
            copyrightText.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/fa.ttf"));
            copyrightText.setText(Configuration.FA_COPYRIGHT + " " + Preferences.getUserName(getApplicationContext()));
            copyrightText.setVisibility(View.VISIBLE);
            showcaseTitle.setVisibility(View.VISIBLE);
            mediaChooser.setVisibility(View.VISIBLE);
            mediaChooser.setAdapter(new ShowcaseMediaChooserAdapter(PostActivity.this));
            mediaChooserText.setVisibility(View.VISIBLE);
            description.setVisibility(View.GONE);
            mediaButtonsForInspire.setVisibility(View.GONE);
            setTitle("Showcase your talent");

        }
        else
        {
            contentType=Configuration.INSPIRATION;
            copyrightText.setVisibility(View.GONE);
            showcaseTitle.setVisibility(View.GONE);
            mediaChooser.setVisibility(View.GONE);
            description.setVisibility(View.VISIBLE);
            mediaButtonsForInspire.setVisibility(View.VISIBLE);
            mediaChooserText.setVisibility(View.GONE);
            setTitle("Inspire the world");
        }

    }


    class PostContentRequest extends JsonObjectRequest
    {
        private Map<String, String> payload;
        public PostContentRequest(final Map<String,String> payload)
        {
            super(Method.POST,
                    API_POST_CREATE_URL,
                    new JSONObject(payload),
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            try
                            {
                                String id = response.getString("id");
                                Log.e(PostActivity.class.getName(),"AND THE ID IS "+id);
                                Intent contentIntent = new Intent(PostActivity.this,ContentActivity.class);
                                contentIntent.putExtra("id",id);
                                contentIntent.putExtra("type",contentType);
                                startActivity(contentIntent);
                                finish();
                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("Something went wrong");
                        }
                    });
            this.payload=payload;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            if (Preferences.isUserLoggedIn(getApplicationContext()) && !TextUtils.isEmpty(Preferences.getUserEmail(getApplicationContext()))) {
                String secret="";
                params.put("email", Preferences.getUserEmail(getApplicationContext()));
                secret=Preferences.getUserApiKey(getApplicationContext());
                String message = API_POST_CREATE_RELATIVE_URL+"|"+new JSONObject(payload).toString();
                params.put("message", message);
                Log.e(TAG,"secret->"+secret);
                String hash = Security.generateHmac(secret, message);
                Log.e(TAG,"hash->"+hash);
                params.put("Authorization", "Token token=" + hash);
            }
            return params;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri=null;
        Cursor cursor=null;
        String filePath="";
        File file=null;
        ByteArrayOutputStream bos=null;
        int width=0;
        int height=0;
        ExifInterface exifInterface = null;
        BitmapFactory.Options options = null;
        if(resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case REQUEST_IMAGE_CAMERA:
                    filePath = CandeoUtil.getRealPathFromUri(imageUri,getContentResolver());
                    file = new File(filePath);
                    Log.e(TAG,"Capture image path is "+filePath);
                    fileName = file.getName();
                    mimeType=CandeoUtil.getMimeType(imageUri, PostActivity.this);
                    options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    options.inDither = true;
                    bitmap = BitmapFactory.decodeFile(filePath,options);
                    try {
                        exifInterface = new ExifInterface(filePath);
                        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                        Log.e(TAG,"orientation in image capture is "+orientation);
                        new RotateTask(orientation).execute(bitmap);
                    }
                    catch(IOException ioe)
                    {
                        ioe.printStackTrace();
                    }

                    break;

                case PICK_IMAGE_FILE:
                    uri = data.getData();
                    filePath = CandeoUtil.getRealPathFromUri(uri,getContentResolver());
                    file = new File(filePath);
                    Log.e(TAG,"Picked image path is "+filePath);
                    mimeType=CandeoUtil.getMimeType(uri, PostActivity.this);
                    fileName = file.getName();
                    options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    options.inDither = true;
                    bitmap=BitmapFactory.decodeFile(filePath, options);
                    try {
                        exifInterface = new ExifInterface(filePath);
                        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                        Log.e(TAG,"orientation in pick image is "+orientation);
                        new RotateTask(orientation).execute(bitmap);
                    }
                    catch(IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                    break;
                case REQUEST_VIDEO_CAMERA:
                    uri = data.getData();
                    try {
                        file = new File(android.os.Environment
                                .getExternalStorageDirectory(), "candeo/videos/candeovideo.mp4");
                        mimeType=CandeoUtil.getMimeType(uri, PostActivity.this);
                        fileName = file.getName();
                        dataArray = CandeoUtil.fileToByteArray(file);
                        if(dataArray == null)
                        {
                            CandeoUtil.appAlertDialog(PostActivity.this,"Please Upload video of size upto 16 mb. Bigger file support coming soon!");
                        }
                        else
                        {
                            playVideo(uri);
                            new UploadMediaTask(PostActivity.this,
                                    Configuration.VIDEO,
                                    dataArray,
                                    fileName,
                                    mimeType,
                                    PostActivity.this).execute(Configuration.MEDIA_UPLOAD_URL);
                        }

                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }


                    break;
                case PICK_VIDEO_FILE:
                    uri = data.getData();
                    cursor = getContentResolver().query(uri, new String[]{MediaStore.Audio.Media.DATA}, null, null, null);
                    cursor.moveToFirst();
                    filePath = cursor.getString(0);
                    cursor.close();
                    file = new File(filePath);
                    mimeType=CandeoUtil.getMimeType(uri, PostActivity.this);
                    fileName = file.getName();
                    dataArray = CandeoUtil.fileToByteArray(file);
                    if(dataArray == null)
                    {
                        CandeoUtil.appAlertDialog(PostActivity.this,"Please Upload video of size upto 16 mb. Bigger file support coming soon!");
                    }
                    else
                    {
                        playVideo(uri);
                        new UploadMediaTask(PostActivity.this,
                                Configuration.VIDEO,
                                dataArray,
                                fileName,
                                mimeType,
                                PostActivity.this).execute(Configuration.MEDIA_UPLOAD_URL);
                    }
                    break;
                case REQUEST_AUDIO_RECORD:
                    filePath= data.getStringExtra("path");
                    System.out.println("Path is "+ filePath);
                    file = new File(filePath);
                    mimeType=CandeoUtil.getMimeType(Uri.fromFile(file),PostActivity.this);
                    fileName = file.getName();
                    dataArray = CandeoUtil.fileToByteArray(file);
                    System.out.println("FILE IS "+fileName);
                    playAudio(filePath);
                    new UploadMediaTask(PostActivity.this,
                            Configuration.AUDIO,
                            dataArray,
                            fileName,
                            mimeType,
                            PostActivity.this).execute(Configuration.MEDIA_UPLOAD_URL);
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
                    System.out.println("Path is "+ uri.getPath());
                    playAudio(uri);
                    new UploadMediaTask(PostActivity.this,
                            Configuration.AUDIO,
                            dataArray,
                            fileName,
                            mimeType,
                            PostActivity.this).execute(Configuration.MEDIA_UPLOAD_URL);
                    break;
//                case PICK_EPUB_FILE:
//                    break;
            }

        }
    }

    private void playVideo(Uri uri)
    {
        videoPreview.setVideoURI(uri);
        imagePreview.setVisibility(View.GONE);
        videoPreview.setVisibility(View.VISIBLE);
        videoPreviewPlay.setVisibility(View.VISIBLE);
        videoPreviewPlay.setText(Configuration.FA_PAUSE);
        audioPreview.setVisibility(View.GONE);
        videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                player = mp;
                player.start();
            }
        });

        videoPreviewPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!player.isPlaying())
                {
                  player.start();
                  videoPreviewPlay.setText(Configuration.FA_PAUSE);
                }
                else
                {
                    player.pause();
                    videoPreviewPlay.setText(Configuration.FA_PLAY);
                }


                if(videoPreview!=null)
                {
                    videoPreview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            player.pause();
                            player.seekTo(0);
                            videoPreviewPlay.setText(Configuration.FA_PLAY);
                        }
                    });
                }
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
            audioPreview.setText(Configuration.FA_PAUSE);
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
                    audioPreview.setText(Configuration.FA_PAUSE);
                }
                else
                {
                    isPlaying=false;
                    stopPosition=player.getCurrentPosition();
                    player.pause();
                    audioPreview.setText(Configuration.FA_PLAY);

                }

            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });

    }

    class RotateTask extends AsyncTask<Bitmap, Void, Bitmap>
    {
        private int orientation = ExifInterface.ORIENTATION_UNDEFINED;
        public RotateTask(int orientation)
        {
            this.orientation = orientation;
        }
        private ProgressDialog pDialog=new ProgressDialog(PostActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                pDialog.setMessage("Preparing Image...");
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(false);
                pDialog.show();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            Bitmap bitmap =params[0];
            bitmap = rotateBitmap(bitmap, orientation);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(pDialog!=null)
            {
                pDialog.dismiss();
                if(result!=null)
                {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap=result;
                    bitmap.compress(Bitmap.CompressFormat.JPEG,70,bos);
                    dataArray = bos.toByteArray();
                    System.out.println("Bitmap is " + bitmap);
                    if(bitmap.getWidth() > bitmap.getHeight())
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

                    new UploadMediaTask(PostActivity.this,
                            Configuration.IMAGE,
                            bos.toByteArray(),
                            fileName,
                            mimeType,
                            PostActivity.this).execute(Configuration.MEDIA_UPLOAD_URL);
                }
            }
        }

    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation)
    {
        Matrix matrix = new Matrix();
        try
        {
            switch (orientation)
            {
                case ExifInterface.ORIENTATION_NORMAL:
                    return bitmap;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1,1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1,1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1,1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1,1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;

            }
            Bitmap bitmapRotated = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            return bitmapRotated;

        }
        catch (OutOfMemoryError ome)
        {
            ome.printStackTrace();
            return  bitmap;
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

    public class ShowcaseMediaChooserAdapter extends PagerAdapter {
        Activity activity;

        public ShowcaseMediaChooserAdapter (Activity activity)
        {
            this.activity=activity;
        }
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = activity.getLayoutInflater().inflate(R.layout.showcase_media_chooser, container, false);
            container.addView(view);
            TextView content=(TextView)view.findViewById(R.id.candeo_showcase_media_chooser_text);
            TextView icon =(TextView)view.findViewById(R.id.candeo_showcase_media_chooser_icon);
            icon.setTypeface(CandeoUtil.loadFont(activity.getAssets(), "fonts/fa.ttf"));
            icon.setTextSize(TypedValue.COMPLEX_UNIT_SP,80);

            content.setTypeface(CandeoUtil.loadFont(activity.getAssets(), "fonts/pt_sans.ttf"));
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            switch (position)
            {
                case 0:

                    icon.setText(Configuration.FA_AUDIO);
                    content.setText("Showcase your talent for music");
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(player!=null)
                            {
                                player.stop();
                                player.release();
                                player=null;
                                audioPreview.setVisibility(View.GONE);
                                audioPreview.setText(Configuration.FA_PLAY);
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
                    break;

                case 1:
                    icon.setText(Configuration.FA_IMAGE);
                    content.setText("Bring out the artist in you");
                    view.setOnClickListener(new View.OnClickListener() {
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
                                        ContentValues values = new ContentValues();
                                        values.put(MediaStore.Images.Media.TITLE, UUID.randomUUID().toString()+".jpg");
                                        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
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
                    break;

            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


}


