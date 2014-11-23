package com.candeo.app.content;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.candeo.app.R;
import com.candeo.app.util.CandeoUtil;

import java.io.File;
import java.io.IOException;

public class PostActivity extends Activity {

    Button audio;
    Button image;
    Button video;
    Button postIt;
    Switch selector;
    TextView copyrightText;
    Button audioPreview;
    ImageView imagePreview;
    VideoView videoPreview;
    Button videoPreviewPlay;
    boolean isPlaying=false;
    MediaPlayer player;
    private static final int REQUEST_IMAGE_CAMERA=100;
    private static final int PICK_VIDEO_FILE=200;
    private static final int REQUEST_VIDEO_CAMERA=300;
    private static final int PICK_AUDIO_FILE=400;
    private static final int REQUEST_AUDIO_RECORD=500;
    private static final int PICK_IMAGE_FILE=600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        setFinishOnTouchOutside(false);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight =getResources().getDisplayMetrics().heightPixels;
        getWindow().setLayout((6*screenWidth)/7,(3*screenHeight)/5);

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
                            File f = new File(android.os.Environment
                                    .getExternalStorageDirectory(), "temp.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
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
        selector=(Switch)findViewById(R.id.candeo_selector);
        selector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    copyrightText.setTypeface(CandeoUtil.loadFont(getAssets(), "fa.ttf"));
                    copyrightText.setText("\uf1f9 Anonymous");
                    copyrightText.setVisibility(View.VISIBLE);
                }
                else
                {
                    copyrightText.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if(requestCode == REQUEST_IMAGE_CAMERA || requestCode == PICK_IMAGE_FILE)
            {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imagePreview.setImageBitmap(bitmap);
                imagePreview.setVisibility(View.VISIBLE);
                videoPreview.setVisibility(View.GONE);
                videoPreviewPlay.setVisibility(View.GONE);
                audioPreview.setVisibility(View.GONE);
            }
            else if(requestCode == REQUEST_VIDEO_CAMERA|| requestCode == PICK_VIDEO_FILE)
            {

            }
            else if(requestCode == REQUEST_AUDIO_RECORD|| requestCode == PICK_AUDIO_FILE)
            {
                final String path = data.getStringExtra("path");
                System.out.println("Path is "+ path);
                player = new MediaPlayer();
                imagePreview.setVisibility(View.GONE);
                videoPreview.setVisibility(View.GONE);
                videoPreviewPlay.setVisibility(View.GONE);
                audioPreview.setVisibility(View.VISIBLE);
                audioPreview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isPlaying)
                        {
                            isPlaying=true;
                            player=new MediaPlayer();
                            try {
                                player.setDataSource(path);
                                player.prepare();
                                player.start();
                                audioPreview.setText("\uf04c");
                            }catch (IOException ioe)
                            {
                                ioe.printStackTrace();
                            }

                        }
                        else
                        {
                            isPlaying=false;
                            player.release();
                            player=null;
                            audioPreview.setText("\uf04b");

                        }
                        if(player!=null)
                        {
                            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    isPlaying=false;
                                    player.release();
                                    player=null;
                                    audioPreview.setText("\uf04b");
                                }
                            });
                        }

                    }
                });

            }

        }
    }

}
