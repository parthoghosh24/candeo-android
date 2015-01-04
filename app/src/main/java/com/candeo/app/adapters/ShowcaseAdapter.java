package com.candeo.app.adapters;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.candeo.app.R;
import com.candeo.app.util.CandeoUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Partho on 1/12/14.
 */
public class ShowcaseAdapter extends PagerAdapter {

    private Activity activity;
    private List<HashMap<String, String>> showcases;

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
        TextView copyRightView = (TextView)view.findViewById(R.id.candeo_copyright_icon);
        TextView inspiredIconView = (TextView)view.findViewById(R.id.candeo_inspired_icon);
        TextView appreciateIconView = (TextView)view.findViewById(R.id.candeo_appreciated_icon);
        copyRightView.setTypeface(CandeoUtil.loadFont(activity.getAssets(),"fonts/fa.ttf"));
        copyRightView.setText("\uf1f9");
        inspiredIconView.setTypeface(CandeoUtil.loadFont(activity.getAssets(),"fonts/response.ttf"));
        inspiredIconView.setText("\ue800");
        appreciateIconView.setTypeface(CandeoUtil.loadFont(activity.getAssets(),"fonts/applause.ttf"));
        appreciateIconView.setText("\ue600");
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
