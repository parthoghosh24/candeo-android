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
import com.candeo.app.util.Preferences;


public class SplashActivity extends Activity {


    TextView textView;
    ViewPager tutorialPager;
    VideoView splashView;
    Button button;
    TextView indicator1;
    TextView indicator2;
    TextView indicator3;
    TextView indicator4;

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
        tutorialPager.setAdapter(new TutorialPagerAdapter(this));
        indicator1=(TextView)findViewById(R.id.candeo_pager_indicator_1);
        indicator2=(TextView)findViewById(R.id.candeo_pager_indicator_2);
        indicator3=(TextView)findViewById(R.id.candeo_pager_indicator_3);
        indicator4=(TextView)findViewById(R.id.candeo_pager_indicator_4);

        indicator1.setTypeface(CandeoUtil.loadFont(getAssets(),"fonts/fa.ttf"));
        indicator1.setText(Configuration.FA_CIRCLE);
        indicator2.setTypeface(CandeoUtil.loadFont(getAssets(),"fonts/fa.ttf"));
        indicator2.setText(Configuration.FA_CIRCLE_O);
        indicator3.setTypeface(CandeoUtil.loadFont(getAssets(),"fonts/fa.ttf"));
        indicator3.setText(Configuration.FA_CIRCLE_O);
        indicator4.setTypeface(CandeoUtil.loadFont(getAssets(),"fonts/fa.ttf"));
        indicator4.setText(Configuration.FA_CIRCLE_O);

        tutorialPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0)
                {
                    indicator1.setText(Configuration.FA_CIRCLE);
                    indicator2.setText(Configuration.FA_CIRCLE_O);
                    indicator3.setText(Configuration.FA_CIRCLE_O);
                    indicator3.setText(Configuration.FA_CIRCLE_O);
                }
                if(position == 1)
                {
                    indicator1.setText(Configuration.FA_CIRCLE_O);
                    indicator2.setText(Configuration.FA_CIRCLE);
                    indicator3.setText(Configuration.FA_CIRCLE_O);
                    indicator3.setText(Configuration.FA_CIRCLE_O);
                }
                if(position == 2)
                {
                    indicator1.setText(Configuration.FA_CIRCLE_O);
                    indicator2.setText(Configuration.FA_CIRCLE_O);
                    indicator3.setText(Configuration.FA_CIRCLE);
                    indicator4.setText(Configuration.FA_CIRCLE_O);
                }
                if(position == 3)
                {
                    indicator1.setText(Configuration.FA_CIRCLE_O);
                    indicator2.setText(Configuration.FA_CIRCLE_O);
                    indicator3.setText(Configuration.FA_CIRCLE_O);
                    indicator4.setText(Configuration.FA_CIRCLE);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        textView.setTypeface(CandeoUtil.loadFont(getAssets(), "fonts/freebooter.ttf"),Typeface.BOLD);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 100);
        textView.setText("Candeo");

        button = (Button)findViewById(R.id.candeo_button);
        button.setText("Start Inspiring");
        button.setBackgroundColor(getResources().getColor(R.color.material_blue_grey_800));
        button.setTextColor(Color.WHITE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                Preferences.setFirstRun(getApplicationContext(),false);
                startActivity(intent);
                finish();

            }
        });
        splashView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.setVolume(0f, 0f);
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


