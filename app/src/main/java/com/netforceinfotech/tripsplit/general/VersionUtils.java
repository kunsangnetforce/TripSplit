package com.netforceinfotech.tripsplit.general;

import android.os.Build;

// http://developer.android.com/intl/zh-cn/guide/topics/manifest/uses-sdk-element.html
// 用于判断Android的版本
public class VersionUtils {

    // 获取当前Android的SDK版本
    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    // The current development codename, "REL" is a release build
    public static String getSdkCodename() {
        return Build.VERSION.CODENAME;
    }

    // 判断当前Android的SDK版本是否大于某个版本
    public static boolean isSDKVersion(int version) {
        return getSdkVersion() >= version;
    }

    // Android 2.2, Froyo, api level 8
    public static boolean isFroyo() {
        return getSdkVersion() == Build.VERSION_CODES.FROYO;
    }

    // Android 2.3, Gingerbread, api level 9
    public static boolean isGingerbread() {
        return getSdkVersion() == Build.VERSION_CODES.GINGERBREAD;
    }

    // Android 3.0, Honeycomb, api level 11
    public static boolean isHoneycomb() {
        return getSdkVersion() == Build.VERSION_CODES.HONEYCOMB;
    }

    // Android 4.0, IceCreamSandwich, api level 14
    public static boolean isIceCreamSandwich() {
        return getSdkVersion() == Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    // Android 4.1, JELLY_BEAN, api level 16
    public static boolean isJellyBean() {
        return getSdkVersion() == Build.VERSION_CODES.JELLY_BEAN;
    }

    // Android 4.4, Kitkat, api level 19
    public static boolean isKitkat() {
        return getSdkVersion() == Build.VERSION_CODES.KITKAT;
    }

    // Android 5.0, Lollipop, api level 21
    public static boolean isLollipop() {
        return getSdkVersion() == Build.VERSION_CODES.LOLLIPOP;
    }

}