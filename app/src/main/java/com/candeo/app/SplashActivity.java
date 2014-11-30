package com.candeo.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;
import com.candeo.app.adapters.TutorialPagerAdapter;
import com.candeo.app.home.HomeActivity;
import com.candeo.app.util.CandeoUtil;


public class SplashActivity extends Activity {


    TextView textView;
    ViewPager tutorialPager;
    VideoView splashView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        splashView=(VideoView)findViewById(R.id.candeo_splash_bg);
        splashView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.bg));
        textView=(TextView)findViewById(R.id.test);
        tutorialPager=(ViewPager)findViewById(R.id.tutorial);
        tutorialPager.setAdapter(new TutorialPagerAdapter(this,getAssets()));
        textView.setTypeface(CandeoUtil.loadFont(getAssets(),"freebooter.ttf"),Typeface.BOLD);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 100);
        textView.setText("Candeo");

        button = (Button)findViewById(R.id.candeo_button);
        button.setText("Start Inspiring");
        button.setBackgroundColor(getResources().getColor(R.color.material_blue_grey_800));
        button.setTextColor(Color.WHITE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }
        });
        splashView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.setVolume(0f,0f);
            }
        });
        splashView.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash, menu);
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


