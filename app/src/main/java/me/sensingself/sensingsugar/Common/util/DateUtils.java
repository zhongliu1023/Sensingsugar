package me.sensingself.sensingsugar.Common.util;


import android.annotation.SuppressLint;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.support.v4.util.Preconditions.checkArgument;


public class DateUtils {

    public static final String FORMAT_DATE_01 = "yyyy-MM-dd";

    public static final String FORMAT_DATE_02 = "yyyy-MM-dd HH";

    public static final String FORMAT_DATE_03 = "yyyy-MM-dd HH:mm";

    public static final String FORMAT_DATE_04 = "yyyy-MM-dd HH:mm:ss";

    public static final String FORMAT_DATE_05 = "MMMM yyyy";

    public static final String FORMAT_DATE_06 = "dd MMM yyyy";

    //****************************** string 2 date *******************************

    /**
     * @param time format
     * @return date
     */
    public static Date string2Date(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String string2StringDate(String time, String format1, String format2){
        Date birthDay = DateUtils.string2Date(time, format1);
        return DateUtils.date2String(birthDay, format2);
    }
    /**
     * @param time "yyyy-MM-dd"
     * @return date
     */
    public static Date dayString2Date(String time) {
        return string2Date(time, FORMAT_DATE_01);
    }

    /**
     * @param time "yyyy-MM-dd HH"
     * @return date
     */
    public static Date hourString2Date(String time) {
        return string2Date(time, FORMAT_DATE_02);
    }

    /**
     * @param time "yyyy-MM-dd HH:mm"
     * @return date
     */
    public static Date minuteString2Date(String time) {
        return string2Date(time, FORMAT_DATE_03);
    }

    /**
     * @param time "yyyy-MM-dd HH:mm:ss"
     * @return date
     */
    public static Date secondString2Date(String time) {
        return string2Date(time, FORMAT_DATE_04);
    }

    //****************************** string 2 long *******************************

    /**
     * @param time format
     * @return microsecond
     */
    public static long string2Long(String time, String format) {
        long microsecond = 0;
        try {
            microsecond = new SimpleDateFormat(format).parse(time).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return microsecond;
    }

    /**
     * @param "yyyy-MM-dd"
     * @return microsecond
     */
    public static long dayString2Long(String time) {
        return string2Long(time, FORMAT_DATE_01);
    }

    /**
     * @param "yyyy-MM-dd HH"
     * @return microsecond
     */
    public static long hourString2Long(String time) {
        return string2Long(time, FORMAT_DATE_02);
    }

    /**
     * @param "yyyy-MM-dd HH:mm"
     * @return microsecond
     */
    public static long minuteString2Long(String time) {
        return string2Long(time, FORMAT_DATE_03);
    }

    /**
     * @param "yyyy-MM-dd HH:mm:ss"
     * @return microsecond
     */
    public static long secondString2Long(String time) {
        return string2Long(time, FORMAT_DATE_04);
    }

    //****************************** date 2 string *******************************

    /**
     * @param date
     * @return format
     */
    public static String date2String(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * @param date
     * @return "yyyy-MM-dd"
     */
    public static String date2DayString(Date date) {
        return date2String(date, FORMAT_DATE_01);
    }

    /**
     * @param date
     * @return "yyyy-MM-dd HH"
     */
    public static String date2HourString(Date date) {
        return date2String(date, FORMAT_DATE_02);
    }

    /**
     * @param date
     * @return "yyyy-MM-dd HH:mm"
     */
    public static String date2MinuteString(Date date) {
        return date2String(date, FORMAT_DATE_03);
    }

    /**
     * @param date
     * @return "yyyy-MM-dd HH:mm:ss"
     */
    public static String date2SecondString(Date date) {
        return date2String(date, FORMAT_DATE_04);
    }

    //****************************** long 2 string *******************************

    /**
     * @param microsecond
     * @return format
     */
    public static String long2String(long microsecond, String format) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(microsecond * 1000);
        return date2String(c.getTime(), format);
    }

    /**
     * @param microsecond
     * @return "yyyy-MM-dd"
     */
    public static String long2DayString(long microsecond) {
        return long2String(microsecond, FORMAT_DATE_01);
    }

    /**
     * @param microsecond
     * @return "yyyy-MM-dd HH"
     */
    public static String long2HourString(long microsecond) {
        return long2String(microsecond, FORMAT_DATE_02);
    }

    /**
     * @param microsecond
     * @return "mm"
     */
    public static String long2Minute(long microsecond) {
        return long2String(microsecond, "mm");
    }

    /**
     * @param microsecond
     * @return "yyyy-MM-dd HH:mm"
     */
    public static String long2MinuteString(long microsecond) {
        return long2String(microsecond, FORMAT_DATE_03);
    }

    /**
     * @param microsecond
     * @return "yyyy-MM-dd HH:mm:ss"
     */
    public static String long2SecondString(long microsecond) {
        return long2String(microsecond, FORMAT_DATE_04);
    }
    public static String long2MonthYearString(long microsecond) {
        return long2String(microsecond, FORMAT_DATE_05);
    }
    //*****************************************************************

    /**
     * @param year
     * @param month
     * @param day
     * @return "yyyy-MM-dd"
     */
    public static String combinDayString(int year, int month, int day) {
        return year + "-" + month + "-" + day;
    }

    /**
     * @param year
     * @param month
     * @param day
     * @param hour
     * @return "yyyy-MM-dd HH"
     */
    public static String combinHourString(int year, int month, int day, int hour) {
        return year + "-" + month + "-" + day + " " + hour;
    }

    /**
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @return "yyyy-MM-dd HH:mm"
     */
    public static String combinMinuteString(int year, int month, int day, int hour, int minute) {
        return year + "-" + month + "-" + day + " " + hour + ":" + minute;
    }

    //***************************************************************

    /**
     * Get the whole 10 minutes per hour
     *
     * @param microsecond
     */
    public static String long2TenString(long microsecond) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(microsecond * 1000);
        String hour = date2HourString(c.getTime());
        String second = Integer.parseInt(long2Minute(microsecond)) / 10 + "0";
        return hour + ":" + second;
    }

    /**
     * According to the start time, end time to get all the two time periods within the date
     *
     * @param starttime
     * @param endtime
     * @return
     */
    public static List<String> getDatePeriods(String starttime, String endtime) {
        List<String> periods = new ArrayList<String>();
        Date start = dayString2Date(starttime);
        Date end = dayString2Date(endtime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        Date temp = calendar.getTime();
        long endTime = end.getTime();
        while (temp.before(end) || temp.getTime() == endTime) {
            periods.add(date2DayString(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            temp = calendar.getTime();
        }
        return periods;
    }

    /**
     * Convert 24hr time to 12hr
     *
     * This method convert 24hr time to 12hr time
     *
     * Date: Fri 8, Sep 2017 Appstute technologies, Pune
     * Developer: Pratik Pitale
     *
     */
    public static String twentyFourToTwelveConverter(String date){
        String subString[] = date.split(":");
        int hrs = Integer.parseInt(subString[0]);
        int min = Integer.parseInt(subString[1]);
        String resultDate = "";
        if(hrs == 0){
            resultDate = "12:"+min+"PM";
        }else if(hrs > 12){
            resultDate = ""+(hrs - 12)+":"+min+"PM";
        }else {
            if(hrs == 12){
                resultDate = "12:"+min+"PM";
            }else{
                resultDate = ""+hrs+":"+min+"AM";
            }
        }
        return resultDate;
    }

    /**
     *
     * Convert 24hr time to 12hr without convention
     *
     * @param time
     * @return
     */
    public static String timeConverterWithoutConvention(String time){
        String subString[] = time.split(":");
        int hrs = Integer.parseInt(subString[0]);
        int min = Integer.parseInt(subString[1]);
        String resultDate = "";
        if(hrs == 0){
            resultDate = "12:"+min;
        }else if(hrs > 12){
            resultDate = ""+(hrs - 12)+":"+min;
        }else {
            if(hrs == 12){
                resultDate = "12:"+min;
            }else{
                resultDate = ""+hrs+":"+min;
            }
        }
        return resultDate;
    }

    public static String timeConverterWithConvention(String date){
        String subString[] = date.split(":");
        int hrs = Integer.parseInt(subString[0]);
        int min = Integer.parseInt(subString[1]);
        String resultDate = "";
        if(hrs == 0){
            resultDate = "12:"+min+":PM";
        }else if(hrs > 12){
            resultDate = ""+(hrs - 12)+":"+min+":PM";
        }else {
            if(hrs == 12){
                resultDate = "12:"+min+":PM";
            }else{
                resultDate = ""+hrs+":"+min+":AM";
            }
        }
        return resultDate;
    }

    /**
     * Add Time convention
     */
    public static String addTimeConvention(int time){
        if(time < 24){
            return time < 12 ? "am" : "pm";
        }else{
            return "";
        }
    }

    /**
     *
     * Get yesterday's date
     *
     *  24*60*60*1000 for yesterday date
     *
     *  2*24*60*60*1000 for day before yesterday
     *
     *  7*24*60*60*1000 for previous week day
     *
     *   Date: Wed 13, Sep 2017 Appstute technologies, Pune
     *   Developer: Pratik Pitale
     *
     */
    public static String getDayBefore(int dayBefore){

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //2017-09-12
        return df.format(new Date(System.currentTimeMillis()-(dayBefore*24*60*60*1000)));

        //Tue Sep 12 15:52:01 GMT+05:30 2017
        //return new Date(System.currentTimeMillis()-(dayBefore*24*60*60*1000));

        /*Date formats:
        "yyyy.MM.dd G 'at' HH:mm:ss z"  2001.07.04 AD at 12:08:56 PDT
        "EEE, MMM d, ''yy"  Wed, Jul 4, '01
        "h:mm a"    12:08 PM
        "hh 'o''clock' a, zzzz" 12 o'clock PM, Pacific Daylight Time
        "K:mm a, z" 0:08 PM, PDT
        "yyyyy.MMMMM.dd GGG hh:mm aaa"  02001.July.04 AD 12:08 PM
        "EEE, d MMM yyyy HH:mm:ss Z"    Wed, 4 Jul 2001 12:08:56 -0700
        "yyMMddHHmmssZ" 010704120856-0700
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"   2001-07-04T12:08:56.235-0700
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"   2001-07-04T12:08:56.235-07:00
        "YYYY-'W'ww-u"  2001-W27-3*/
    }

    public static String getCurrentDate() {
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    public static String getCurrentDateTime(){
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }

    public static String getCurrentDay(){

        Calendar now = Calendar.getInstance();
        return String.valueOf(now.get(Calendar.DAY_OF_MONTH));

        /*LocalDateTime localDateTime = LocalDateTime.now();
        return String.valueOf(localDateTime.getDayOfMonth());*/
    }

    public static int getDaysDifference(String date1, String date2){
        Date d1 = convertStringToDate(date1, CalendarUtil.yyyy_MM_dd);
        Date d2 = convertStringToDate(date2, CalendarUtil.yyyy_MM_dd);
        int daysdiff = 0;
        long diff = d2.getTime() - d1.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
        daysdiff = (int) diffDays;
        return daysdiff;
    }

    public static Date convertStringToDate(String sDate, SimpleDateFormat sdf){
        Date date = null;
        try {
            date = sdf.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static int getTimeDifferenceInMinutes(String startTime, String endTime){
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
        DateTime startDate = formatter.parseDateTime(startTime);
        DateTime endDate = formatter.parseDateTime(endTime);
        Minutes minutes = Minutes.minutesBetween(startDate, endDate);
        return minutes.getMinutes();
    }

    public static int getMinutes(String dateTime){
        DateTimeFormatter formatter = DateTimeFormat.forPattern("HH:mm");
        DateTime startDate = formatter.parseDateTime(dateTime);
        return startDate.getMinuteOfDay();
    }

    @SuppressLint("RestrictedApi")
    public static String getDayOfMonthSuffix(final int n) {
        checkArgument(n >= 1 && n <= 31, "illegal day of month: " + n);
        if (n >= 11 && n <= 13) {
            return String.valueOf(n) + "th";
        }
        switch (n % 10) {
            case 1:  return  String.valueOf(n) + "st";
            case 2:  return  String.valueOf(n) + "nd";
            case 3:  return  String.valueOf(n) + "rd";
            default: return  String.valueOf(n) + "th";
        }
    }

    public static String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        dob.set(year, month, day);
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (age == 0 && today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age++;
        }
        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();
        return ageS;
    }
}
