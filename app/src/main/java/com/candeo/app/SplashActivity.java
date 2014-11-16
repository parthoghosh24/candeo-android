package com.candeo.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.candeo.app.home.HomeActivity;


public class SplashActivity extends Activity {


    TextView textView;
    ViewPager tutorialPager;
    Button button;
    Typeface font;
    Typeface freeBooter;
    Typeface ptmono;
    Typeface ptsans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        textView=(TextView)findViewById(R.id.test);
        tutorialPager=(ViewPager)findViewById(R.id.tutorial);
        tutorialPager.setAdapter(new TutorialPagerAdapter(this));
        font = loadFontAwesome();
        freeBooter= loadFontFreeBooter();
        ptmono = loadFontPTmono();
        ptsans = loadFontPTsans();
        textView.setTypeface(freeBooter,Typeface.BOLD);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 100);
        textView.setText("Candeo");

        button = (Button)findViewById(R.id.candeo_button);
        button.setText("Start Inspiring");
        button.setBackgroundColor(getResources().getColor(R.color.material_blue_500));
        button.setTextColor(Color.WHITE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }
        });
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

    /**
     * @partho
     *
     * Loading Font Awesome
     */
    private Typeface loadFontAwesome()
    {
        Typeface fontAwesome = Typeface.createFromAsset(getAssets(),"fa.ttf");
        return  fontAwesome;
    }

    private Typeface loadFontFreeBooter()
    {
        Typeface fontAwesome = Typeface.createFromAsset(getAssets(),"freebooter.ttf");
        return  fontAwesome;
    }

    private Typeface loadFontPTmono()
    {
        Typeface fontAwesome = Typeface.createFromAsset(getAssets(),"pt_mono.ttf");
        return  fontAwesome;
    }

    private Typeface loadFontPTsans()
    {
        Typeface fontAwesome = Typeface.createFromAsset(getAssets(),"pt_sans.ttf");
        return  fontAwesome;
    }

    class TutorialPagerAdapter extends PagerAdapter
    {
        Activity activity;

        TutorialPagerAdapter (Activity activity)
        {
            this.activity=activity;
        }
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = activity.getLayoutInflater().inflate(R.layout.tutorial_pager_item, container, false);
            container.addView(view);
            TextView content=(TextView)view.findViewById(R.id.tutorial_text);
            content.setTypeface(ptsans);
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            switch (position)
            {
                case 0:
                    content.setText("Inspire The World or get Inspired");
                    break;
                case 1:
                    content.setText("Share Or Create To Make the world a better World");
                    break;
                case 2:
                    content.setText("Be a hidden motivator or let the world know of your greatness");
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


