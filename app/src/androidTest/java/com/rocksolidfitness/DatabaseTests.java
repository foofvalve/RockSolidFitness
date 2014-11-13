package com.rocksolidfitness;


import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

public class DatabaseTests extends AndroidTestCase
{
    private final String TAG = "UnitTest";
    private SessionsDataSource dataSource;

    public void setUp()
    {
        RenamingDelegatingContext context
                = new RenamingDelegatingContext(getContext(), "test_");
        dataSource = new SessionsDataSource(context);
        dataSource.open();
    }

    public void testSessionCRUD()
    {
        Session testSession = new Session(Session.State.PLANNED, "Running", "Unit Test: Easy fartlek run", 45);
        long recId = dataSource.createSession(testSession);
        Log.d(TAG, "Created session with Id: " + recId);
        assertTrue(recId != 0);
        Session savedSession = dataSource.getSessionById(recId);
        Log.d(TAG, "Saved session : " + savedSession.toString());
    }

    public void testSessionNotFound()
    {
        Session nonExistentSession = dataSource.getSessionById(10001);
        assertTrue(nonExistentSession == null);
    }

    public void testSportsCRU()
    {

    }

    public void tearDown() throws Exception
    {
        dataSource.close();
        super.tearDown();
    }
}
