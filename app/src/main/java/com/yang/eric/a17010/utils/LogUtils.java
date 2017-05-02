package com.yang.eric.a17010.utils;

import android.util.Log;

/**
 * Created by Yang on 2017/4/20.
 */

public class LogUtils {
    private static boolean flag = false;
    public static void debug(boolean f) {
        flag = f;
    }

    public static void d(String tag, String message) {
        if (flag) {
            Log.d(tag, message);
        }
    }
    public static void e(String tag, String message) {
        if (flag) {
            Log.e(tag, message);
        }
    }
}
