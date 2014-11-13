package com.rocksolidfitness;


import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils
{
    public static Date convertSQLiteDate(String rawDateTime)
    {
        DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date convertedDate = null;

        if (rawDateTime == null) return convertedDate;

        try
        {
            convertedDate = iso8601Format.parse(rawDateTime);
        } catch (ParseException e)
        {
            Log.e("failed parsing date", e.getMessage());
        }
        return convertedDate;
    }

    public static String convertDateToSQLiteCompliant(Date sessionDate)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(sessionDate);
    }

    public static String getTodayDatesSQLiteCompliant()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    public static Date getNextDay()
    {
        Date tomorrow = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(tomorrow);
        c.add(Calendar.DATE, 1);
        tomorrow = c.getTime();
        return tomorrow;
    }
}
