package com.candeo.app.transformers;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Partho on 30/11/14.
 */

/**
 * http://developer.android.com/training/animation/screen-slide.html
 */
public class ShowcaseTransformer implements ViewPager.PageTransformer{

    private static final float MIN_SCALE = 0.75f;

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        if(position < -1) //page is way off to the left of screen
        {
            view.setAlpha(0);
        }
        else if(position <= 0)
        {
            view.setAlpha(1);
            view.setTranslationX(0);
            view.setScaleX(1);
            view.setScaleY(1);
        }
        else if(position <= 1)
        {
            view.setAlpha(1-position);
            view.setTranslationX(pageWidth*-position);
            float scaleFactor= MIN_SCALE+(1-MIN_SCALE)*(1-Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }
        else //page is way off to the right of screen
        {
            view.setAlpha(0);
        }


    }
}
