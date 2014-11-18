package com.rocksolidfitness;


import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.HashMap;
import java.util.List;

public class DatabaseTests extends AndroidTestCase
{
    private final String TAG = "UnitTest";
    private RenamingDelegatingContext context;
    private SessionsDataSource dataSource;

    public void setUp()
    {
        context = new RenamingDelegatingContext(getContext(), "test_v2_");
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
        savedSession.distance = 15;
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
        assertTrue(savedSession.distance == updatedSession.distance);
        assertTrue(savedSession.notes.equals(updatedSession.notes));
        assertTrue(savedSession.avgHrRate == updatedSession.avgHrRate);
        assertTrue(savedSession.location.equals(updatedSession.location));
        assertTrue(savedSession.caloriesBurnt == updatedSession.caloriesBurnt);
        assertTrue(savedSession.weight == updatedSession.weight);
        assertTrue(savedSession.raceName.equals(updatedSession.raceName));
        assertTrue(savedSession.trainingWeek == updatedSession.trainingWeek);
        assertTrue(savedSession.getSessionWeek() == updatedSession.getSessionWeek());
        assertTrue(savedSession.getSessionYear() == updatedSession.getSessionYear());

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
