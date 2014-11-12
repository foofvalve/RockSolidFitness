package com.rocksolidfitness;


import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
}
