package com.jiangyy.easydialog.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by liujie on 1/7/18.
 */

public class FontUtility {
    public static Typeface getOfficinaSansCBook(Context context){
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "fonts/Seravek.ttf");
        return custom_font;
    }
    public static Typeface getOfficinaSansCBold(Context context){
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "fonts/Seravek-Bold.ttf");
        return custom_font;
    }
    public static Typeface getSeravekBoldItalic(Context context){
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "fonts/Seravek-BoldItalic.ttf");
        return custom_font;
    }
    public static Typeface getSeravekExtraLight(Context context){
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "fonts/SeravekExtraLight.ttf");
        return custom_font;
    }
    public static Typeface getSeravekExtraLightItalic(Context context){
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "fonts/Seravek-ExtraLightItalic.ttf");
        return custom_font;
    }
    public static Typeface getSeravekItalic(Context context){
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "fonts/Seravek-Italic.ttf");
        return custom_font;
    }
    public static Typeface getSeravekLight(Context context){
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "fonts/Seravek-Light.ttf");
        return custom_font;
    }
    public static Typeface getSeravekLightItalic(Context context){
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "fonts/Seravek-LightItalic.ttf");
        return custom_font;
    }
    public static Typeface getSeravekMedium(Context context){
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "fonts/Seravek-Medium.ttf");
        return custom_font;
    }
    public static Typeface getSeravekMediumItalic(Context context){
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "fonts/Seravek-MediumItalic.ttf");
        return custom_font;
    }
}
