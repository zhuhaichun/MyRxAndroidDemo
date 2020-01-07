package com.android.haichun.myrxandroiddemo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtils {
    public static boolean checkNet(Context context){
        boolean mWifConnected = isWifiConnected(context);
        boolean mMobileConnected = isMobileConnected(context);
        return mWifConnected || mMobileConnected;
    }

    private static boolean isMobileConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return info != null && info.isConnected();
    }

    private static boolean isWifiConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return info != null && info.isConnected();
    }
}
