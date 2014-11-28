package com.candeo.app.adapters;

import android.app.Activity;
import android.content.res.AssetManager;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candeo.app.R;
import com.candeo.app.util.CandeoUtil;

/**
 * Created by Partho on 23/11/14.
 * Adapter for ViewPager on Splash Screen
 */
public class TutorialPagerAdapter extends PagerAdapter
{
    Activity activity;
    AssetManager assets; //For Fonts

    public TutorialPagerAdapter (Activity activity, AssetManager assets)
    {
        this.activity=activity;
        this.assets = assets;
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
        TextView content=(TextView)view.findViewById(R.id.candeo_tutorial_text);
        TextView icon =(TextView)view.findViewById(R.id.candeo_tutorial_icon);
        icon.setTypeface(CandeoUtil.loadFont(assets,"fa.ttf"));
        icon.setTextSize(TypedValue.COMPLEX_UNIT_SP,80);
        content.setTypeface(CandeoUtil.loadFont(assets, "pt_sans.ttf"));
        content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        switch (position)
        {
            case 0:

                icon.setText("\uf0d0");
                content.setText("Inspire the world with your creativity or discovery. Make it a better place");
                break;

            case 1:
                icon.setTextSize(TypedValue.COMPLEX_UNIT_SP,40);
                icon.setText("\uf030  \uf001  \uf008");
                content.setText("Showcase inspirition. It can be your lovely smile, a video message, a song or a lovely quote");
                break;

            case 2:
                icon.setText("\uf007");
                content.setText("Be anonymous or choose to be someone's inspirtion.");
                break;
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
