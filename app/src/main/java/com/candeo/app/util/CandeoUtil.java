package com.candeo.app.util;

import android.content.res.AssetManager;
import android.graphics.Typeface;

/**
 * Created by Partho on 23/11/14.
 *
 * This is the Util class for Candeo which will hold all the helper methods
 */
public class CandeoUtil {

    public static Typeface loadFont(AssetManager assetsLocation, String fontFile)
    {
        return Typeface.createFromAsset( assetsLocation,fontFile);
    }
}
