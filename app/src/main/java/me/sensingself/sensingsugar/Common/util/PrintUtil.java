package me.sensingself.sensingsugar.Common.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by liujie on 1/26/18.
 */

public class PrintUtil {
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showTempToast(Context context, String message) {
//        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showLog(String tag, String message) {
        //  Log.d(tag, message);
    }
}