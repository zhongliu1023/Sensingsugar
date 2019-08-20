package me.sensingself.sensingsugar.Common.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by mac on 07/09/17.
 */

public class GmtUtil {

    private void convertToGMT() throws ParseException {
        Date date = new Date();
        SimpleDateFormat gmtFormat = new SimpleDateFormat();
        TimeZone gmtTime = TimeZone.getTimeZone("GMT");
        gmtFormat.setTimeZone(gmtTime);
        Log.d("GMT_TimeConverter_1","Current time: "+date);
        Log.d("GMT_TimeConverter_1","GMT time: "+gmtFormat.format(date));
        //gmtToLocal(gmtFormat.format(date));
    }

    public static String gmtToLocal(String format) {
        //MMM dd, yyyy h:mm a 2017-09-07 13:00:31 yyyy-dd-MM HH:mm:ss
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat
                    ("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = inputFormat.parse(format);
            return outputFormat.format(date);
        }catch (ParseException e){
            Log.d("ParseException",""+e.getMessage());
            return "";
        }
        //Log.d("GMT_TimeConverter_2","GMT to Local: "+outputText);
    }
    public static String localToGmt(String date1){
        Date date = new Date();
        SimpleDateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone gmtTime = TimeZone.getTimeZone("GMT");
        gmtFormat.setTimeZone(gmtTime);
        return gmtFormat.format(date);
    }


    public static String gmt0ToLocal(String format) {
        //MMM dd, yyyy h:mm a 2017-09-07 13:00:31 yyyy-dd-MM HH:mm:ss
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat
                    ("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            inputFormat.setTimeZone(TimeZone.getDefault());
            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = inputFormat.parse(format);
            return outputFormat.format(date);
        }catch (ParseException e){
            Log.d("ParseException",""+e.getMessage());
            return "";
        }
        //Log.d("GMT_TimeConverter_2","GMT to Local: "+outputText);
    }
    public static String localToGmt0(String date1){
        try {
            SimpleDateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            TimeZone gmtTime = TimeZone.getTimeZone("GMT+0");
            gmtFormat.setTimeZone(gmtTime);
            Date date = gmtFormat.parse(date1);
            return gmtFormat.format(date);
        }catch (ParseException e){
            Log.d("ParseException",""+e.getMessage());
            return "";
        }
    }
    public static String localToGmt1(String date1){
        try {
            SimpleDateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd");
            TimeZone gmtTime = TimeZone.getTimeZone("GMT+0");
            gmtFormat.setTimeZone(gmtTime);
            Date date = gmtFormat.parse(date1);
            return gmtFormat.format(date);
        }catch (ParseException e){
            Log.d("ParseException",""+e.getMessage());
            return "";
        }
    }
    public static String localToGmt0AfterCheckingTimeStamp(long timeStamp){
        String time = "";
        if(timeStamp/1000 > 1000000000){
            time = GmtUtil.localToGmt0(DateUtils.long2MinuteString(timeStamp/1000));
        }else {
            time = GmtUtil.localToGmt0(DateUtils.long2MinuteString(timeStamp));
        }
        return time;
    }
    public static String gmt0ToLocalOnlyMonthYear(String format) {
        //MMM dd, yyyy h:mm a 2017-09-07 13:00:31 yyyy-dd-MM HH:mm:ss
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat
                    ("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            inputFormat.setTimeZone(TimeZone.getDefault());
            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("MMMM yyyy");
            Date date = inputFormat.parse(format);
            return outputFormat.format(date);
        }catch (ParseException e){
            Log.d("ParseException",""+e.getMessage());
            return "";
        }
        //Log.d("GMT_TimeConverter_2","GMT to Local: "+outputText);
    }
}
