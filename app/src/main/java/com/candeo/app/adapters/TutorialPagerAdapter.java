package com.candeo.app.adapters;

import android.app.Activity;
import android.content.res.AssetManager;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.util.CandeoUtil;

/**
 * Created by Partho on 23/11/14.
 * Adapter for ViewPager on Splash Screen
 */
public class TutorialPagerAdapter extends PagerAdapter
{
    Activity activity;

    public TutorialPagerAdapter (Activity activity)
    {
        this.activity=activity;
    }
    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o == view;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = activity.getLayoutInflater().inflate(R.layout.tutorial_pager_item, container, false);
        container.addView(view);
        TextView content=(TextView)view.findViewById(R.id.candeo_tutorial_text);
        TextView icon1 =(TextView)view.findViewById(R.id.candeo_tutorial_icon_1);
        TextView icon2 =(TextView)view.findViewById(R.id.candeo_tutorial_icon_2);
        icon2.setVisibility(View.GONE);
        icon1.setTypeface(CandeoUtil.loadFont(activity.getAssets(), "fonts/fa.ttf"));
        icon1.setTextSize(TypedValue.COMPLEX_UNIT_SP,80);
        content.setTypeface(CandeoUtil.loadFont(activity.getAssets(), "fonts/caviar_bold.ttf"));
        content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        switch (position)
        {
            case 0:

                icon1.setText(Configuration.FA_MAGIC);
                icon2.setVisibility(View.GONE);
                content.setText("Rock the world by that \"one\" performance every week. Bestow this world with your precious talent.");
                break;

            case 1:
                icon1.setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
                icon2.setVisibility(View.GONE);
                icon1.setText(Configuration.FA_AUDIO+" "+Configuration.FA_IMAGE);
                content.setText("Choose your favorite medium to showcase your talent and rock out.");
                break;

            case 2:
                icon1.setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
                icon1.setTypeface(CandeoUtil.loadFont(activity.getAssets(),"fonts/applause.ttf"));
                icon1.setText(Configuration.FA_APPRECIATE);
                icon2.setVisibility(View.VISIBLE);
                icon2.setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
                icon2.setTypeface(CandeoUtil.loadFont(activity.getAssets(),"fonts/response.ttf"));
                icon2.setText(" "+Configuration.FA_INSPIRE);
                content.setText("Earn appreciations and get ranked every week. Inspire and make the world a better place.");
                break;

            case 3:
                icon1.setText(Configuration.FA_USER);
                icon2.setVisibility(View.GONE);
                content.setText("Be a star with your own fanbase or choose to promote the right talent.");
                break;
        }
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
