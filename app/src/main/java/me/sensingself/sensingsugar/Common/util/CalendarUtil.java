package me.sensingself.sensingsugar.Common.util;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class CalendarUtil {

    static String[] days = new String[]{"SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
    private static String weekStartDate;
    private static String weekEndDate;
    public static SimpleDateFormat yyyy_MMMM_d = new SimpleDateFormat("yyyy-MMMM-d");
    public static SimpleDateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat yyyy_MM_dd_HH_mm = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String findDayName(String date) {
        Calendar calendar = new GregorianCalendar();
        Date outputDate1 = null;
        String dayName = null;
        try {
            outputDate1 = yyyy_MM_dd.parse(date);
            long timeInMilliseconds = outputDate1.getTime();
            // Log.d("TimeInMiliseconds", timeInMilliseconds + "");
            calendar.setTimeInMillis(timeInMilliseconds);
            dayName = days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayName;
    }

    public static int findWeekNumber(String date) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        Date outputDate1 = null;
        int weekNumber = 0;
        try {
            outputDate1 = yyyy_MM_dd.parse(date);
            long timeInMilliseconds = outputDate1.getTime();
            //  Log.d("TimeInMiliseconds", timeInMilliseconds + "");
            calendar.setTimeInMillis(timeInMilliseconds);
            calendar.setFirstDayOfWeek(Calendar.SUNDAY);
            calendar.getFirstDayOfWeek();
             weekNumber = calendar.get(Calendar.WEEK_OF_YEAR);
            // Log.d("date in string ", weekNumber + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return weekNumber;
    }

    public static List<String> getStartEndOFWeek(int enterWeek, int enterYear, SimpleDateFormat formatter) {
        List<String> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(Locale.US);
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, enterWeek);
        calendar.set(Calendar.YEAR, enterYear);

        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMMM-d"); // PST`
        Date startDate = calendar.getTime();
        weekStartDate = formatter.format(startDate);

        calendar.add(Calendar.DATE, 6);
        Date enddate = calendar.getTime();
        weekEndDate = formatter.format(enddate);
        days.add(weekStartDate);
        days.add(weekEndDate);
        return days;
    }
    public static List<String> getStartOFWeekInYear(int enterMonth, int enterYear, SimpleDateFormat formatter) {
        List<String> days = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(Locale.US);

        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMMM-d"); // PST`
        int weeks = calendar.getActualMaximum(Calendar.WEEK_OF_YEAR);
        for (int i = enterMonth-2 ; i <= enterMonth + 2 ; i ++){
            calendar.clear();
            calendar.set(Calendar.WEEK_OF_YEAR, i);
            calendar.set(Calendar.YEAR, enterYear);
            Date startDate = calendar.getTime();
            weekStartDate = formatter.format(startDate);
            days.add(weekStartDate);
        }
        return days;
    }
    public static int getDaysInMonth(int monthNumber,int year)
    {
        int days=0;
        if(monthNumber>=0 && monthNumber<12){
            try
            {
                Calendar calendar = Calendar.getInstance();
                int date = 1;
                calendar.set(year, monthNumber, date);
                days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            } catch (Exception e)
            {
                if(e!=null)
                    e.printStackTrace();
            }
        }
        return days;
    }

    public static String saturdayDate(String monDate) {
        Calendar cal = null;
        try {
            Date date = yyyy_MM_dd.parse(monDate);
            cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, +5);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return yyyy_MM_dd.format(cal.getTime());
    }

    public static int getYear(String date){
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date dateObj = null;
        try {
            dateObj = yyyy_MM_dd.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(dateObj);
        return calendar.get(Calendar.YEAR);
    }
    public static int getMonth(String date) {
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date dateObj = null;
        try {
            dateObj = yyyy_MM_dd.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(dateObj);
        return calendar.get(Calendar.MONTH);
    }
    public static String getNextTimeInterval() {
        Calendar calendar = Calendar.getInstance();
        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = unroundedMinutes % 10;
        calendar.add(Calendar.MINUTE, mod == 0 ? 10 : 10 - mod);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        //SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        isValidDate(yyyy_MM_dd_HH_mm.format(calendar.getTime()));
        Log.d("getNextTimeInterval", "" + yyyy_MM_dd_HH_mm.format(calendar.getTime()));
        return yyyy_MM_dd_HH_mm.format(calendar.getTime());
    }

    public static boolean isValidDate(String date) {
        //date = "2017-0011-17 15:00";
        boolean result = true;
        try {
            String[] spitedDate = date.split(" ");
            String dateString = spitedDate[0];
            String timeString = spitedDate[1];
            String[] dateStringSplit = dateString.split("-");
            String[] timeStringSplit = timeString.split(":");

            for (int i = 0; i < dateStringSplit.length; i++) {
                if (i == 0) {
                    if (dateStringSplit[0].length() != 4) {
                        result = false;
                    }
                } else {
                    if (dateStringSplit[i].length() != 2) {
                        result = false;
                    }
                }
            }

            for (String subString : timeStringSplit) {
                if (subString.length() != 2) {
                    result = false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            result = false;
        }

        if(result){
            Log.d("getNextTimeInterval", "Valid");
        }else{
            Log.d("getNextTimeInterval", "Invalid");
        }
        return result;
    }

    public static int getWeekDifference(String startDay, String endDay){
        Date d1 = null, d2 = null;
        try {
            d1 = CalendarUtil.yyyy_MM_dd.parse(startDay);
            d2 = CalendarUtil.yyyy_MM_dd.parse(endDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateTime dateTimeStart = new DateTime(d1);
        DateTime dateTimeEnd = new DateTime(d2);

        int totalWeeks = 0;
        if(findWeekNumber(startDay) != findWeekNumber(endDay)){
            totalWeeks = Weeks.weeksBetween(dateTimeStart, dateTimeEnd).getWeeks();
            totalWeeks = totalWeeks + 2;
        }else{
            totalWeeks = 1;
        }
        return totalWeeks;
    }

    public static String getYesterday(String inputDate) {
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = yyyy_MM_dd.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        return yyyy_MM_dd.format(calendar.getTime());
    }
}
