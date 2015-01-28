package com.candeo.app.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.candeo.app.Configuration;
import com.candeo.app.R;
import com.candeo.app.user.LoginActivity;
import com.candeo.app.util.CandeoUtil;
import com.candeo.app.util.Preferences;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Partho on 1/12/14.
 */
public class ShowcaseAdapter extends PagerAdapter {

    private Activity activity;
    private ViewPager pager;
    private List<HashMap<String, String>> showcases;

    public ShowcaseAdapter(Activity activity, ViewPager pager)
    {
        this.activity=activity;
        this.pager = pager;
    }
    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o == view;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = activity.getLayoutInflater().inflate(R.layout.showcase_item, container, false);
        TextView copyRightView = (TextView)view.findViewById(R.id.candeo_copyright_icon);
        TextView appreciateIconView = (TextView)view.findViewById(R.id.candeo_appreciate_icon);
        TextView mediaIconView = (TextView)view.findViewById(R.id.candeo_showcase_media_icon);
        Button appreciateButtonView = (Button)view.findViewById(R.id.candeo_showcase_appreciate_button);
        Button skipButtonView = (Button)view.findViewById(R.id.candeo_showcase_skip_button);
        appreciateButtonView.setTypeface(CandeoUtil.loadFont(activity.getAssets(),"fonts/applause.ttf"));
        appreciateButtonView.setText(Configuration.FA_APPRECIATE);
        appreciateButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Preferences.isUserLoggedIn(activity))
                {
                    Toast.makeText(activity,"You Appreciated",Toast.LENGTH_SHORT).show();
                    pager.setCurrentItem(position+1);
                }
                else
                {
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                }

            }
        });
        skipButtonView.setTypeface(CandeoUtil.loadFont(activity.getAssets(), "fonts/fa.ttf"));
        skipButtonView.setText(Configuration.FA_SKIP);
        skipButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Preferences.isUserLoggedIn(activity))
                {
                    Toast.makeText(activity,"Skip pressed",Toast.LENGTH_SHORT).show();
                    pager.setCurrentItem(position+1);
                }
                else
                {
                    activity.startActivity(new Intent(activity, LoginActivity.class));
                }

            }
        });
        mediaIconView.setTypeface(CandeoUtil.loadFont(activity.getAssets(),"fonts/fa.ttf"));
        mediaIconView.setText(Configuration.FA_AUDIO);
        copyRightView.setTypeface(CandeoUtil.loadFont(activity.getAssets(),"fonts/fa.ttf"));
        copyRightView.setText(Configuration.FA_COPYRIGHT);
        appreciateIconView.setTypeface(CandeoUtil.loadFont(activity.getAssets(),"fonts/applause.ttf"));
        appreciateIconView.setText(Configuration.FA_APPRECIATE);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
