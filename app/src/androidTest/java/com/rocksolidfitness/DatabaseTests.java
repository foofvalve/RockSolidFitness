package com.rocksolidfitness;


import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DatabaseTests extends AndroidTestCase
{
    private final String TAG = "com.rocksolidfitness";
    private RenamingDelegatingContext context;
    private SessionsDataSource dataSource;

    public void setUp()
    {
        context = new RenamingDelegatingContext(getContext(), "test_v3_");
        dataSource = new SessionsDataSource(context);
        dataSource.open();
    }

    public void testSessionCRUD()
    {
        //Create
        Session testSession = new Session(Session.State.PLANNED, "Running", "Unit Test: Easy fartlek run", 45, new DateTime());
        long recId = dataSource.createSession(testSession);
        Log.d(TAG, "Created session with Id: " + recId);
        assertTrue(recId != 0);

        //Read
        Session savedSession = dataSource.getSessionById(recId);
        Log.d(TAG, "Saved session : " + savedSession.toString());

        //Update
        savedSession.sessionState = Session.State.COMPLETE;
        savedSession.sport = "Cycling";
        savedSession.description = "Long Ride";
        savedSession.setDateOfSession(Utils.getNextDay());
        savedSession.duration = 122;
        savedSession.setDistance(context, 15);
        savedSession.notes = "Lorem Ipsum is simply's dummy, text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typeset";
        savedSession.avgHrRate = 78;
        savedSession.location = "Brisbane";
        savedSession.caloriesBurnt = 1080;
        savedSession.weight = 60.25;
        savedSession.raceName = "Ironman NZ";
        savedSession.trainingWeek = 13;
        int updatedRecId = dataSource.updateSession(savedSession);

        //Read
        Session updatedSession = dataSource.getSessionById(updatedRecId);
        Log.d(TAG, "Updated session : " + updatedSession.toString());
        assertTrue(savedSession.sessionState.equals(updatedSession.sessionState));
        assertTrue(savedSession.sport.equals(updatedSession.sport));
        assertTrue(savedSession.getDateOfSession().getMonthOfYear() == updatedSession.getDateOfSession().getMonthOfYear());
        assertTrue(savedSession.getDateOfSession().getDayOfMonth() == updatedSession.getDateOfSession().getDayOfMonth());
        assertTrue(savedSession.getDateOfSession().getYear() == updatedSession.getDateOfSession().getYear());
        assertTrue(savedSession.duration == updatedSession.duration);
        assertTrue(savedSession.getDistance() == updatedSession.getDistance());
        assertTrue(savedSession.notes.equals(updatedSession.notes));
        assertTrue(savedSession.avgHrRate == updatedSession.avgHrRate);
        assertTrue(savedSession.location.equals(updatedSession.location));
        assertTrue(savedSession.caloriesBurnt == updatedSession.caloriesBurnt);
        assertTrue(savedSession.weight == updatedSession.weight);
        assertTrue(savedSession.raceName.equals(updatedSession.raceName));
        assertTrue(savedSession.trainingWeek == updatedSession.trainingWeek);
        assertTrue(savedSession.getSessionWeek() == updatedSession.getSessionWeek());
        assertTrue(savedSession.getSessionYear() == updatedSession.getSessionYear());
        assertTrue(savedSession.getUomAsString().equals("KM"));

        //Delete
        dataSource.deleteSession(updatedSession);
        Session deletedSession = dataSource.getSessionById(updatedSession.id);
        assertTrue(deletedSession == null);
    }

    public void testSessionNotFound()
    {
        Session nonExistentSession = dataSource.getSessionById(10001);
        assertTrue(nonExistentSession == null);
    }

    public void testCreateSessionUsingDateString()
    {
        long recId = dataSource.createSession(new Session(Session.State.PLANNED, "Cycling", "Big Bike", 180, Utils.convertSQLiteDate("2014-05-05")));
        Log.d(TAG, "Created session with Id: " + recId);
        assertTrue(recId != 0);
    }

    public void testLoadingTestData()
    {
        dataSource.loadSmallTestDataSet();
        dataSource.loadDynamicTestData();
        dataSource.loadLargeDataSet();
    }


    public void testSportsCRUs()
    {
        //Create
        String oldSport = "Kayaking";
        long recId = dataSource.createSport(oldSport);
        assertEquals(recId, -1);

        //Read
        List<String> sports = dataSource.getSports();
        assertTrue(sports.contains(oldSport));
        Log.d(TAG, "testSportsCRU - Number of sport records : " + sports.size());

        //Update
        Session testSession = new Session(Session.State.PLANNED, oldSport, "Unit Test: Easy fartlek run", 45, new DateTime());
        long id = dataSource.createSession(testSession);

        dataSource.renameSport(oldSport, "SomethingElse");
        testSession = dataSource.getSessionById(id);
        Log.d(TAG, "testSportsCRU - Session after sport renamed : " + testSession.toString());
        assertTrue(testSession.sport.trim().equals("SomethingElse"));
    }

    public void testRenameNonExistentSport()
    {
        int recId = dataSource.renameSport("NonExistent", "Something else");
        assertTrue(recId == -1);
    }

    public void testAddingDupeSport()
    {
        dataSource.createSport("Kayaking");
        assertTrue(dataSource.createSport("Kayaking") == -1);
    }

    public void testGetSessionsForTheCurrentWeek()
    {
        dataSource.loadDynamicTestData();
        List<Session> sessionsForThisWeek = dataSource.getAllSessionsForCurrentWeek();
        for (Session sport : sessionsForThisWeek)
            Log.d(TAG, sport.toString());
    }

    public void testDailySnapshots()
    {
        dataSource.loadDynamicTestData();
        List<DailySnapshot> dailySnapshots = new ArrayList<DailySnapshot>();

        //TODO: dataSource.getMaxWeek <-> dataSource.getMinWeek
        for (int week = 46; week <= 49; week++)
        {
            dailySnapshots.add(new DailySnapshot(week).flagAsPlaceholder());

            List<Session> sessionsForWeek = dataSource.getAllSessionsForWeek(week, 2014);
            for (int dayOfWeek = 1; dayOfWeek <= 7; dayOfWeek++)
            {
                DailySnapshot dailySnapshot = new DailySnapshot(week);
                dailySnapshot.mBelongsToDayOfWeek = dayOfWeek;
                for (Session session : sessionsForWeek)
                {
                    if (session.getDateOfSession().getDayOfWeek() == dayOfWeek)
                        dailySnapshot.addSession(session);
                }
                dailySnapshots.add(dailySnapshot);
            }
        }

        for (DailySnapshot d : dailySnapshots)
            Log.d("", d.toString());
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
        assertTrue("1 hour".equals(testSession.getFormattedDuration(context)));

        testSession.duration = 59;
        assertTrue("59 minutes".equals(testSession.getFormattedDuration(context)));

        testSession.duration = 61;
        Log.d(TAG, testSession.getFormattedDuration(context));
        assertTrue("1 hours 1 minutes".equals(testSession.getFormattedDuration(context)));

        testSession.duration = 120;
        Log.d(TAG, testSession.getFormattedDuration(context));
        assertTrue("2 hours".equals(testSession.getFormattedDuration(context)));

        testSession.duration = 230;
        Log.d(TAG, testSession.getFormattedDuration(context));
        assertTrue("3 hours 50 minutes".equals(testSession.getFormattedDuration(context)));

    }

    public void testGetSessionDescForSpinner()
    {
        dataSource.loadDynamicTestData();
        String[] blah = dataSource.getSessDescSpinner("");
        assertTrue(blah.length != 0);

        blah = dataSource.getSessDescSpinner("Swimming");
        assertTrue(blah.length != 0);
        for (int i = 0; i < blah.length; i++)
            Log.d(TAG, "Swimming:" + blah[i]);
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
        dataSource.close();
        super.tearDown();
    }
}
