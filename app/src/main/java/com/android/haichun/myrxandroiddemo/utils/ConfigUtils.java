package com.android.haichun.myrxandroiddemo.utils;

import android.content.Context;
import android.content.res.Configuration;

public class ConfigUtils {
    public static boolean isOrientationPortrait(Context context){
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
    }

    public static boolean isOrientationLandscape(Context context){
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }
}
