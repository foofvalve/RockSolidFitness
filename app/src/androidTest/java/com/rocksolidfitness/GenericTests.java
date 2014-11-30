package com.rocksolidfitness;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class GenericTests extends AndroidTestCase
{
    private final String TAG = "com.rocksolidfitness";
    private RenamingDelegatingContext mContext;

    public void setUp()
    {
        mContext = new RenamingDelegatingContext(getContext(), "test_");
    }

    public void testGettingScreenSizes()
    {
        float height = Utils.getScreenHeightInDp(mContext);
        float width = Utils.getScreenWidthInDp(mContext);

        Log.d(TAG, "Height=" + height);
        Log.d(TAG, "Wdith=" + width);
    }

    public void testMeh2()
    {

        Log.d(TAG, Locale.getDefault().getCountry());
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Log.d(TAG, day + "-" + month + "-" + year);

        DateTime dte = new DateTime();
        Log.d(TAG, dte.dayOfMonth().get() + "-" + dte.getMonthOfYear() + "-" + dte.getYear());
    }

    public void testSessionFormatter()
    {
        Session testSession = new Session(Session.State.PLANNED, "Running", "Unit Test: Easy fartlek run", 45, new DateTime());
        testSession.duration = 60;
        assertTrue("1 hour".equals(testSession.getFormattedDuration(mContext)));

        testSession.duration = 59;
        assertTrue("59 minutes".equals(testSession.getFormattedDuration(mContext)));

        testSession.duration = 61;
        Log.d(TAG, testSession.getFormattedDuration(mContext));
        assertTrue("1 hours 1 minutes".equals(testSession.getFormattedDuration(mContext)));

        testSession.duration = 120;
        Log.d(TAG, testSession.getFormattedDuration(mContext));
        assertTrue("2 hours".equals(testSession.getFormattedDuration(mContext)));

        testSession.duration = 230;
        Log.d(TAG, testSession.getFormattedDuration(mContext));
        assertTrue("3 hours 50 minutes".equals(testSession.getFormattedDuration(mContext)));

    }

    public void testFirstDayOfWeek()
    {
        Log.d(TAG, Utils.getFirstDayOfWeek().toString());
    }

    public void testUtilsDateOffseter()
    {
        DateTime dte = Utils.convertSQLiteDate("2014-08-27");
        HashMap<String, DateTime> dateFromWeekAndYear = Utils.getDateFromWeekAndYear(dte);
        assertTrue("2014-08-25T00:00:00.000+10:00".equals(dateFromWeekAndYear.get(DateTimeConstants.MONDAY + "").toString()));
        assertTrue("2014-08-26T00:00:00.000+10:00".equals(dateFromWeekAndYear.get(DateTimeConstants.TUESDAY + "").toString()));
        assertTrue("2014-08-31T00:00:00.000+10:00".equals(dateFromWeekAndYear.get(DateTimeConstants.SUNDAY + "").toString()));
    }

    public void tearDown() throws Exception
    {
        super.tearDown();
    }
}
