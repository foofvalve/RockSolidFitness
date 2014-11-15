package com.rocksolidfitness;


import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Utils
{
    static DateTimeFormatter isoFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

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
        return dayOffset < 0 ? offsetDate.minusDays(1) : offsetDate.plusDays(1);
    }

    public static DateTime getNextDay()
    {
        DateTime tomorrow = new DateTime();
        return tomorrow.plusDays(1);
    }
}
