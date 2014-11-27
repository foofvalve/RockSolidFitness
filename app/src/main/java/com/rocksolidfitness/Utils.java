package com.rocksolidfitness;


import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.List;

class Utils
{
    private final static DateTimeFormatter isoFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public static DateTime convertSQLiteDate(String rawDateTime)
    {
        if (rawDateTime == null) return null;

        if (rawDateTime.length() != 19)
            rawDateTime += " 00:00:00";

        return isoFormat.parseDateTime(rawDateTime);
    }

    public static String convertDateToSQLiteCompliant(DateTime sessionDate)
    {
        return sessionDate.toString(isoFormat);
    }

    public static String getTodayDatesSQLiteCompliant()
    {
        DateTime now = new DateTime();
        return now.toString(isoFormat);
    }

    public static DateTime getDateOffsetByNDays(int dayOffset)
    {
        DateTime offsetDate = new DateTime();
        if (dayOffset < 0)
            offsetDate = offsetDate.minusDays(Math.abs(dayOffset));
        else
            offsetDate = offsetDate.plusDays(dayOffset);

        return offsetDate;
    }

    public static DateTime getNextDay()
    {
        DateTime tomorrow = new DateTime();
        return tomorrow.plusDays(1);
    }

    public static String formatDateForDisplay(DateTime rawDate)
    {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("E d MMMM yyyy");
        return rawDate.toString(formatter);
    }

    public static void printSessionsToConsole(List<Session> sessionsForThisWeek)
    {
        for (Session sport : sessionsForThisWeek)
            Log.d("", sport.toString());
    }

    public static HashMap<String, DateTime> getDateFromWeekAndYear(DateTime dte)
    {
        HashMap<String, DateTime> daysOfGivenWeek = new HashMap<String, DateTime>();
        DateTime firstDayOfWeek = dte.dayOfWeek().withMinimumValue();
        daysOfGivenWeek.put(DateTimeConstants.MONDAY + "", firstDayOfWeek);
        daysOfGivenWeek.put(DateTimeConstants.TUESDAY + "", firstDayOfWeek.plusDays(1));
        daysOfGivenWeek.put(DateTimeConstants.WEDNESDAY + "", firstDayOfWeek.plusDays(2));
        daysOfGivenWeek.put(DateTimeConstants.THURSDAY + "", firstDayOfWeek.plusDays(3));
        daysOfGivenWeek.put(DateTimeConstants.FRIDAY + "", firstDayOfWeek.plusDays(4));
        daysOfGivenWeek.put(DateTimeConstants.SATURDAY + "", firstDayOfWeek.plusDays(5));
        daysOfGivenWeek.put(DateTimeConstants.SUNDAY + "", firstDayOfWeek.plusDays(6));
        return daysOfGivenWeek;
    }


    public static float getScreenHeightInDp(Context context)
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels / displayMetrics.density;
    }

    public static float getScreenWidthInDp(Context context)
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels / displayMetrics.density;
    }
}
