package com.candeo.app.adapters;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candeo.app.R;
import com.candeo.app.util.CandeoUtil;

/**
 * Created by dholu on 1/12/14.
 */
public class ShowcaseAdapter extends PagerAdapter {

    Activity activity;

    public ShowcaseAdapter(Activity activity)
    {
        this.activity=activity;
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
    public Object instantiateItem(ViewGroup container, int position) {
        View view = activity.getLayoutInflater().inflate(R.layout.showcase_item, container, false);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
