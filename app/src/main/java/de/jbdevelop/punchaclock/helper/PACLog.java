package de.jbdevelop.punchaclock.helper;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

/**
 * Created by Benjamin on 28.04.16.
 */
public class PACLog {
    private static final String LOGCAT_TAG = "PAC-Log";
    private static final String TYPE_INFO = "I";
    private static final String TYPE_DEBUG = "D";
    private static final String TYPE_VERBOSE = "V";
    private static final String TYPE_ERROR = "E";
    private static final String TYPE_WARN = "W";
    private static final String TYPE_WTF = "WTF";

    public static void i(String tag, String data) {
        Log.i(tag,data);
    }

    public static void d(String tag, String data) {
        Log.d(tag,data);
    }

    public static void e(String tag, String data) {
        Log.e(tag,data);
    }

    public static void v(String tag, String data) {
        Log.v(tag,data);
    }

    public static void w(String tag, String data) {
        Log.w(tag,data);
    }

    public static void wtf(String tag, String data) {
        Log.wtf(tag,data);
    }
}
