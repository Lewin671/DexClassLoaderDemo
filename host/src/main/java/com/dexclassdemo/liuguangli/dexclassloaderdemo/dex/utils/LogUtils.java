package com.dexclassdemo.liuguangli.dexclassloaderdemo.dex.utils;

import android.util.Log;

public class LogUtils {

    private static boolean isDebug = true; // 是否开启日志

    private LogUtils() {

    }

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    public static int v(String tag, String msg) {
        if (isDebug) {
            return Log.v(tag, msg);
        }
        return 0;
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (isDebug) {
            return Log.v(tag, msg, tr);
        }
        return 0;
    }

    public static int d(String tag, String msg) {
        if (isDebug) {
            return Log.d(tag, msg);
        }
        return 0;
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (isDebug) {
            return Log.d(tag, msg, tr);
        }
        return 0;
    }

    public static int i(String tag, String msg) {
        if (isDebug) {
            return Log.i(tag, msg);
        }
        return 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (isDebug) {
            return Log.i(tag, msg, tr);
        }
        return 0;
    }

    public static int w(String tag, String msg) {
        if (isDebug) {
            return Log.w(tag, msg);
        }
        return 0;
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (isDebug) {
            return Log.w(tag, msg, tr);
        }
        return 0;
    }

    public static int w(String tag, Throwable tr) {
        if (isDebug) {
            return Log.w(tag, tr);
        }
        return 0;
    }

    public static int e(String tag, String msg) {
        if (isDebug) {
            return Log.e(tag, msg);
        }
        return 0;
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (isDebug) {
            return Log.e(tag, msg, tr);
        }
        return 0;
    }

    public static int wtf(String tag, String msg) {
        if (isDebug) {
            return Log.wtf(tag, msg);
        }
        return 0;
    }

    public static int wtf(String tag, Throwable tr) {
        if (isDebug) {
            return Log.wtf(tag, tr);
        }
        return 0;
    }

    public static int wtf(String tag, String msg, Throwable tr) {
        if (isDebug) {
            return Log.wtf(tag, msg, tr);
        }
        return 0;
    }

    public static boolean isLoggable(String tag, int level) {
        return Log.isLoggable(tag, level);
    }

    public static String getStackTraceString(Throwable tr) {
        return Log.getStackTraceString(tr);
    }

    public static int println(int priority, String tag, String msg) {
        if (isDebug) {
            return Log.println(priority, tag, msg);
        }
        return 0;
    }
}

