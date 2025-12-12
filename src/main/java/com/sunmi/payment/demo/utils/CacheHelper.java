package com.sunmi.payment.demo.utils;

import com.sunmi.payment.demo.app.MyApp;
import com.tencent.mmkv.MMKV;

public class CacheHelper {

    private static final MMKV mmkv;

    static {
        MMKV.initialize(MyApp.myApp);
        mmkv = MMKV.defaultMMKV();
    }

    public static boolean saveString(String key, String value) {
        return mmkv.encode(key, value);
    }

    public static String getString(String key) {
        return mmkv.decodeString(key, "");
    }

    public static String getString(String key, String defaultValue) {
        return mmkv.decodeString(key, defaultValue);
    }

    public static boolean saveBool(String key, boolean value) {
        return mmkv.encode(key, value);
    }

    public static boolean getBool(String key, boolean defaultValue) {
        return mmkv.decodeBool(key, defaultValue);
    }


}
