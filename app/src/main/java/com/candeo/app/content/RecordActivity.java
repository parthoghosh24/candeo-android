package com.candeo.app.content;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.util.CandeoUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RecordActivity extends Activity {

    Button record;
    Button ok;
    Button cancel;
    Button stop;
    Button play;
    LinearLayout controls;
    MediaRecorder recorder;
    MediaPlayer player;
    boolean isPlaying=false;
    String outputFile="";
    TextView recordTimer;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Amplitude.getInstance().logEvent("Record Activity loaded");
        setFinishOnTouchOutside(false);
        controls=(LinearLayout)findViewById(R.id.candeo_record_controls);
        recordTimer=(TextView)findViewById(R.id.candeo_record_timer);
        recordTimer.setText("6:00");
        recordTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 80);
        recorder= new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath()+"/candeo/audios/candeorecord.aac";
        recorder.setOutputFile(outputFile);
        recorder.setAudioChannels(2);
        recorder.setMaxDuration(360 * 1000);
        ok=(Button)findViewById(R.id.candeo_record_ok);
        cancel=(Button)findViewById(R.id.candeo_record_cancel);
        stop =(Button)findViewById(R.id.candeo_stop_record);
        record =(Button)findViewById(R.id.candeo_record);
        record.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/fa.ttf"));
        record.setText("\uf111");
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amplitude.getInstance().logEvent("Record Button clicked");
                try {
                    recorder.prepare();
                    recorder.start();
                    stop.setVisibility(View.VISIBLE);
                    controls.setVisibility(View.GONE);
                    countDownTimer = new CountDownTimer(360 * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            recordTimer.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                        }

                        @Override
                        public void onFinish() {
                            recorder.stop();
                            recorder.release();
                            recorder = null;

                        }
                    }.start();
                } catch (IOException ioe) {
                    Toast.makeText(RecordActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    ioe.printStackTrace();
                }

            }
        });
        play=(Button)findViewById(R.id.candeo_play_record);
        play.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/fa.ttf"));
        play.setTextSize(TypedValue.COMPLEX_UNIT_SP, 80);
        play.setText("\uf04b");

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amplitude.getInstance().logEvent("Play Button clicked");
                if(!isPlaying)
                {
                    isPlaying=true;
                    player=new MediaPlayer();
                    try {
                        player.setDataSource(outputFile);
                        player.prepare();
                        player.start();
                        play.setText(Configuration.FA_PAUSE);
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
                    play.setText(Configuration.FA_PLAY);

                }
                if(player!=null)
                {
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            isPlaying=false;
                            player.release();
                            player=null;
                            play.setText(Configuration.FA_PLAY);
                        }
                    });
                }

            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amplitude.getInstance().logEvent("Stop Button clicked");
                recorder.stop();
                recorder.release();
                recorder = null;
                stop.setVisibility(View.GONE);
                controls.setVisibility(View.VISIBLE);
                record.setVisibility(View.GONE);
                ok.setVisibility(View.VISIBLE);
                countDownTimer.cancel();
                recordTimer.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);

            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amplitude.getInstance().logEvent("Ok Button clicked");
                Intent intent = new Intent();
                intent.putExtra("path",outputFile);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amplitude.getInstance().logEvent("Cancel Button clicked");
                setResult(RESULT_CANCELED);
                finish();
            }
        });
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


}
