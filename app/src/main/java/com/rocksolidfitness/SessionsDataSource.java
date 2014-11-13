package com.rocksolidfitness;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SessionsDataSource
{
    // Database fields
    private SQLiteDatabase database;
    private DbHelper dbHelper;
    private String[] allColumns = {
            SessionColumns.SESSION_ID,
            SessionColumns.STATE,
            SessionColumns.SPORT,
            SessionColumns.DESCRIPTION,
            SessionColumns.DATE_OF_SESSION,
            SessionColumns.DURATION,
            SessionColumns.DISTANCE,
            SessionColumns.NOTES,
            SessionColumns.AVG_HR_RATE,
            SessionColumns.LOCATION,
            SessionColumns.CALORIES_BURNT,
            SessionColumns.WEIGHT,
            SessionColumns.RACE_NAME,
            SessionColumns.TRAINING_WEEK,
            SessionColumns.DATE_CREATED,
            SessionColumns.DATE_MODIFIED
    };

    public SessionsDataSource(Context context)
    {
        dbHelper = new DbHelper(context);
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    public long createSession(Session session)
    {
        ContentValues values = new ContentValues();

        values.put(SessionColumns.STATE, session.sessionState.ordinal());
        values.put(SessionColumns.SPORT, session.sport);
        values.put(SessionColumns.DESCRIPTION, session.description);
        values.put(SessionColumns.DATE_OF_SESSION, Utils.convertDateToSQLiteCompliant(session.dateOfSession));
        values.put(SessionColumns.DURATION, session.duration);
        values.put(SessionColumns.DISTANCE, session.distance);
        values.put(SessionColumns.NOTES, session.notes);
        values.put(SessionColumns.AVG_HR_RATE, session.avgHrRate);
        values.put(SessionColumns.LOCATION, session.location);
        values.put(SessionColumns.CALORIES_BURNT, session.caloriesBurnt);
        values.put(SessionColumns.WEIGHT, session.weight);
        values.put(SessionColumns.RACE_NAME, session.raceName);
        values.put(SessionColumns.TRAINING_WEEK, session.trainingWeek);
        values.put(SessionColumns.DATE_CREATED, Utils.getTodayDatesSQLiteCompliant());

        return database.insert(DbHelper.TABLE_SESSIONS, null, values);
    }

    public int updateSession(Session session)
    {
        ContentValues values = new ContentValues();

        values.put(SessionColumns.STATE, session.sessionState.ordinal());
        values.put(SessionColumns.SPORT, session.sport);
        values.put(SessionColumns.DESCRIPTION, session.description);
        values.put(SessionColumns.DATE_OF_SESSION, Utils.convertDateToSQLiteCompliant(session.dateOfSession));
        values.put(SessionColumns.DURATION, session.duration);
        values.put(SessionColumns.DISTANCE, session.distance);
        values.put(SessionColumns.NOTES, session.notes);
        values.put(SessionColumns.AVG_HR_RATE, session.avgHrRate);
        values.put(SessionColumns.LOCATION, session.location);
        values.put(SessionColumns.CALORIES_BURNT, session.caloriesBurnt);
        values.put(SessionColumns.WEIGHT, session.weight);
        values.put(SessionColumns.RACE_NAME, session.raceName);
        values.put(SessionColumns.TRAINING_WEEK, session.trainingWeek);
        values.put(SessionColumns.DATE_MODIFIED, Utils.getTodayDatesSQLiteCompliant());

        return database.update(DbHelper.TABLE_SESSIONS, values, "_id = ?", new String[]{"" + session.id});
    }

    public void deleteSession(Session session)
    {
        long id = session.id;
        database.delete(DbHelper.TABLE_SESSIONS, SessionColumns.SESSION_ID + "=" + id, null);
    }

    public List<Session> getAllSessions()
    {
        throw new UnsupportedOperationException("Not implemented yet");
        /*
        List<Comment> comments = new ArrayList<Comment>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Comment comment = cursorToComment(cursor);
            comments.add(comment);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return comments;
        */
    }

    public Session getSessionById(long recId)
    {
        Session session = null;

        Cursor cursor = database.query(DbHelper.TABLE_SESSIONS,
                allColumns, SessionColumns.SESSION_ID + " = ?", new String[]{"" + recId}, null, null, null);

        if (cursor.getCount() == 0) return session;

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            session = cursorToSession(cursor);
            cursor.moveToNext();
        }
        cursor.close();
        return session;
    }

    public long createSport(String sport)
    {
        ContentValues values = new ContentValues();
        values.put(SportColumns.SPORT, sport);
        long recId;
        try
        {
            recId = database.insert(DbHelper.TABLE_SPORTS, null, values);
        } catch (SQLiteConstraintException e)
        {
            return -1;
        }
        return recId;
    }

    public int renameSport(String oldSportLabel, String newSportLabel)
    {
        ContentValues values = new ContentValues();

        values.put(SportColumns.SPORT, newSportLabel);
        String updateQuery = "update " + DbHelper.TABLE_SESSIONS +
                " set " + SportColumns.SPORT + "='" + newSportLabel + "'" +
                " where " + SportColumns.SPORT + "='" + oldSportLabel + "'";
        database.execSQL(updateQuery);
        return database.update(DbHelper.TABLE_SPORTS, values, SportColumns.SPORT + " = ?", new String[]{"" + oldSportLabel});
    }

    public List<String> getSports()
    {
        List<String> sports = new ArrayList<String>();


        Cursor cursor = database.query(DbHelper.TABLE_SPORTS,
                new String[]{SportColumns.SPORT}, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            sports.add(cursor.getString(0));

            cursor.moveToNext();
        }
        cursor.close();

        return sports;
    }


    private Session cursorToSession(Cursor cursor)
    {
        Session session = new Session();
        session.id = cursor.getLong(SessionColumns.SESSION_ID_INDEX);
        session.sessionState = Session.State.values()[cursor.getInt(SessionColumns.SESSION_STATE_INDEX)];
        session.sport = cursor.getString(SessionColumns.SESSION_SPORT_INDEX);
        session.description = cursor.getString(SessionColumns.SESSION_DESCRIPTION_INDEX);

        String rawDateTime = cursor.getString(SessionColumns.SESSION_DATE_OF_SESSION_INDEX);
        session.dateOfSession = Utils.convertSQLiteDate(rawDateTime);
        session.duration = cursor.getInt(SessionColumns.SESSION_DURATION_INDEX);
        session.distance = cursor.getDouble(SessionColumns.SESSION_DISTANCE_INDEX);
        session.notes = cursor.getString(SessionColumns.SESSION_NOTES_INDEX);
        session.avgHrRate = cursor.getInt(SessionColumns.SESSION_AVG_HR_RATE_INDEX);
        session.location = cursor.getString(SessionColumns.SESSION_LOCATION_INDEX);
        session.caloriesBurnt = cursor.getInt(SessionColumns.SESSION_CALORIES_BURNT_INDEX);
        session.weight = cursor.getDouble(SessionColumns.SESSION_WEIGHT_INDEX);
        session.raceName = cursor.getString(SessionColumns.SESSION_RACE_NAME_INDEX);
        session.trainingWeek = cursor.getInt(SessionColumns.SESSION_TRAINING_WEEK_INDEX);

        rawDateTime = cursor.getString(SessionColumns.SESSION_DATE_CREATED_INDEX);
        session.dateCreated = Utils.convertSQLiteDate(rawDateTime);

        rawDateTime = cursor.getString(SessionColumns.SESSION_DATE_MODIFIED_INDEX);
        session.dateModified = Utils.convertSQLiteDate(rawDateTime);

        return session;
    }

    public void loadSmallTestDataSet()
    {
        createSession(new Session(Session.State.COMPLETE, "Running", "Easy run", 120, new Date()));
        createSession(new Session(Session.State.PLANNED, "Swimming", "Drills", 50, new Date()));
        createSession(new Session(Session.State.PLANNED, "Cycling", "Big Bike", 180, Utils.convertSQLiteDate("2014-05-05")));
        createSession(new Session(Session.State.PLANNED, "Running", "Long Run", 110, Utils.getNextDay()));
    }

    public void loadLargeDataSet()
    {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
