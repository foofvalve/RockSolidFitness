package com.rocksolidfitness;


import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseTests extends AndroidTestCase
{
    private final String TAG = "UnitTest";
    private SessionsDataSource dataSource;

    public void setUp()
    {
        RenamingDelegatingContext context
                = new RenamingDelegatingContext(getContext(), "test_v2_");
        dataSource = new SessionsDataSource(context);
        dataSource.open();
    }

    public void testSessionCRUD()
    {
        //Create
        Session testSession = new Session(Session.State.PLANNED, "Running", "Unit Test: Easy fartlek run", 45, new Date());
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
        savedSession.dateOfSession = Utils.getNextDay();
        savedSession.duration = 122;
        savedSession.distance = 15;
        savedSession.notes = "Lorem Ipsum is simply's dummy, text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typeset";
        savedSession.avgHrRate = 78;
        savedSession.location = "Brisbane";
        savedSession.caloriesBurnt = 1080;
        savedSession.weight = 60.25;
        savedSession.raceName = "Ironman NZ";
        savedSession.trainingWeek = 13;
        savedSession.sessionWeek = 53;
        savedSession.sessionYear = 2100;
        int updatedRecId = dataSource.updateSession(savedSession);

        //Read
        Session updatedSession = dataSource.getSessionById(updatedRecId);
        Log.d(TAG, "Updated session : " + updatedSession.toString());
        assertTrue(savedSession.sessionState.equals(updatedSession.sessionState));
        assertTrue(savedSession.sport.equals(updatedSession.sport));
        assertTrue(savedSession.dateOfSession.compareTo(updatedSession.dateOfSession) == 0);
        assertTrue(savedSession.duration == updatedSession.duration);
        assertTrue(savedSession.distance == updatedSession.distance);
        assertTrue(savedSession.notes.equals(updatedSession.notes));
        assertTrue(savedSession.avgHrRate == updatedSession.avgHrRate);
        assertTrue(savedSession.location.equals(updatedSession.location));
        assertTrue(savedSession.caloriesBurnt == updatedSession.caloriesBurnt);
        assertTrue(savedSession.weight == updatedSession.weight);
        assertTrue(savedSession.raceName.equals(updatedSession.raceName));
        assertTrue(savedSession.trainingWeek == updatedSession.trainingWeek);
        assertTrue(savedSession.sessionWeek == updatedSession.sessionWeek);
        assertTrue(savedSession.sessionYear == updatedSession.sessionYear);

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
        long recId = dataSource.createSession(new Session(Session.State.PLANNED, "Cycling", "Big Bike", 180, Utils.convertSQLiteDate("2014-05-05 00:00:00")));
        Log.d(TAG, "Created session with Id: " + recId);
        assertTrue(recId != 0);
    }

    public void testLoadingTestData()
    {
        dataSource.loadSmallTestDataSet();
        dataSource.loadDynamicTestData();
        dataSource.loadLargeDataSet();
    }

    public void testDateConverter()
    {
        String dateString = "2014-04-05 00:00:00";
        Date date = null;
        try
        {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(dateString);
            Log.d(TAG, date.toString());
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

    }

    public void testSportsCRU()
    {
        //Create
        String oldSport = "Kayaking";
        long recId = dataSource.createSport(oldSport);
        assertTrue(recId != 0);

        //Read
        List<String> sports = dataSource.getSports();
        assertTrue(sports.contains(oldSport));
        Log.d(TAG, "testSportsCRU - Number of sport records : " + sports.size());

        //Update
        Session testSession = new Session(Session.State.PLANNED, oldSport, "Unit Test: Easy fartlek run", 45, new Date());
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

    public void tearDown() throws Exception
    {
        dataSource.close();
        super.tearDown();
    }
}
