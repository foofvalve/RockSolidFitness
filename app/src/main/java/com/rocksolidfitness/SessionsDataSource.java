package com.rocksolidfitness;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class SessionsDataSource
{
    // Database fields
    private SQLiteDatabase database;
    private DbHelper dbHelper;
    private String[] allColumns = {
            Columns.SESSION_ID,
            Columns.STATE,
            Columns.SPORT,
            Columns.DESCRIPTION,
            Columns.DATE_OF_SESSION,
            Columns.DURATION,
            Columns.DISTANCE,
            Columns.NOTES,
            Columns.AVG_HR_RATE,
            Columns.LOCATION,
            Columns.CALORIES_BURNT,
            Columns.WEIGHT,
            Columns.RACE_NAME,
            Columns.TRAINING_WEEK,
            Columns.DATE_CREATED,
            Columns.DATE_MODIFIED
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

        values.put(Columns.STATE, Session.State.PLANNED.toString());
        values.put(Columns.SPORT, session.sport);
        values.put(Columns.DESCRIPTION, session.description);
        values.put(Columns.DURATION, session.duration);

        return database.insert(DbHelper.TABLE_SESSIONS, null, values);
        /*
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_COMMENT, comment);
        long insertId = database.insert(MySQLiteHelper.TABLE_COMMENTS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Comment newComment = cursorToComment(cursor);
        cursor.close();
        return newComment;
         */
    }

    public void deleteSession(Session session)
    {
        throw new UnsupportedOperationException("Not implemented yet");
        /*
        long id = comment.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_COMMENTS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
                */
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

    public Session getSession()
    {
        Session session = new Session();

        Cursor cursor = database.query(DbHelper.TABLE_SESSIONS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            session = cursorToSession(cursor);
            cursor.moveToNext();
        }
        cursor.close();

        return session;
    }

    public List<String> getSports()
    {
        List<String> sports = new ArrayList<String>();
        ;

        Cursor cursor = database.query(DbHelper.TABLE_SPORTS,
                new String[]{"sport"}, null, null, null, null, null);
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
        session.id = cursor.getLong(Columns.SESSION_ID_INDEX);
        session.sessionState = Session.State.values()[cursor.getInt(Columns.SESSION_STATE_INDEX)];
        session.sport = cursor.getString(Columns.SESSION_SPORT_INDEX);
        session.description = cursor.getString(Columns.SESSION_DESCRIPTION_INDEX);

        String rawDateTime = cursor.getString(Columns.SESSION_DATE_OF_SESSION_INDEX);
        session.dateOfSession = Utils.convertSQLiteDate(rawDateTime);
        session.duration = cursor.getInt(Columns.SESSION_DURATION_INDEX);
        session.distance = cursor.getDouble(Columns.SESSION_DISTANCE_INDEX);
        session.notes = cursor.getString(Columns.SESSION_NOTES_INDEX);
        session.avgHrRate = cursor.getInt(Columns.SESSION_AVG_HR_RATE_INDEX);
        session.location = cursor.getString(Columns.SESSION_LOCATION_INDEX);
        session.caloriesBurnt = cursor.getInt(Columns.SESSION_CALORIES_BURNT_INDEX);
        session.weight = cursor.getDouble(Columns.SESSION_WEIGHT_INDEX);
        session.raceName = cursor.getString(Columns.SESSION_RACE_NAME_INDEX);
        session.trainingWeek = cursor.getInt(Columns.SESSION_TRAINING_WEEK_INDEX);

        rawDateTime = cursor.getString(Columns.SESSION_DATE_CREATED_INDEX);
        session.dateCreated = Utils.convertSQLiteDate(rawDateTime);

        rawDateTime = cursor.getString(Columns.SESSION_DATE_MODIFIED_INDEX);
        session.dateModified = Utils.convertSQLiteDate(rawDateTime);

        return session;
    }

}
