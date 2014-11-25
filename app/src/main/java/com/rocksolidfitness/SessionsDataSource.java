package com.rocksolidfitness;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;


class SessionsDataSource
{
    private final DbHelper mDbHelper;
    Context mContext;
    // Database fields
    private SQLiteDatabase mDatabase;

    public SessionsDataSource(Context context)
    {
        mDbHelper = new DbHelper(context);
        mContext = context;
    }

    public void open() throws SQLException
    {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void openReadOnly() throws SQLException
    {
        mDatabase = mDbHelper.getReadableDatabase();
    }

    public void close()
    {
        mDbHelper.close();
    }

    public long createSession(Session session)
    {
        ContentValues values = new ContentValues();

        values.put(SessionColumns.STATE, session.sessionState.ordinal());
        values.put(SessionColumns.SPORT, session.sport);
        values.put(SessionColumns.DESCRIPTION, session.description);
        values.put(SessionColumns.DATE_OF_SESSION, Utils.convertDateToSQLiteCompliant(session.getDateOfSession()));
        values.put(SessionColumns.DURATION, session.duration);
        values.put(SessionColumns.DISTANCE, session.getDistance());
        values.put(SessionColumns.NOTES, session.notes);
        values.put(SessionColumns.AVG_HR_RATE, session.avgHrRate);
        values.put(SessionColumns.LOCATION, session.location);
        values.put(SessionColumns.CALORIES_BURNT, session.caloriesBurnt);
        values.put(SessionColumns.WEIGHT, session.weight);
        values.put(SessionColumns.RACE_NAME, session.raceName);
        values.put(SessionColumns.TRAINING_WEEK, session.trainingWeek);
        values.put(SessionColumns.DATE_CREATED, Utils.getTodayDatesSQLiteCompliant());
        values.put(SessionColumns.SESSION_WEEK, session.getSessionWeek());
        values.put(SessionColumns.SESSION_YEAR, session.getSessionYear());
        values.put(SessionColumns.UOM, session.getUomAsString());

        return mDatabase.insert(DbHelper.TABLE_SESSIONS, null, values);
    }

    public int updateSession(Session session)
    {
        ContentValues values = new ContentValues();

        values.put(SessionColumns.STATE, session.sessionState.ordinal());
        values.put(SessionColumns.SPORT, session.sport);
        values.put(SessionColumns.DESCRIPTION, session.description);
        values.put(SessionColumns.DATE_OF_SESSION, Utils.convertDateToSQLiteCompliant(session.getDateOfSession()));
        values.put(SessionColumns.DURATION, session.duration);
        values.put(SessionColumns.DISTANCE, session.getDistance());
        values.put(SessionColumns.NOTES, session.notes);
        values.put(SessionColumns.AVG_HR_RATE, session.avgHrRate);
        values.put(SessionColumns.LOCATION, session.location);
        values.put(SessionColumns.CALORIES_BURNT, session.caloriesBurnt);
        values.put(SessionColumns.WEIGHT, session.weight);
        values.put(SessionColumns.RACE_NAME, session.raceName);
        values.put(SessionColumns.TRAINING_WEEK, session.trainingWeek);
        values.put(SessionColumns.DATE_MODIFIED, Utils.getTodayDatesSQLiteCompliant());
        values.put(SessionColumns.SESSION_WEEK, session.getSessionWeek());
        values.put(SessionColumns.SESSION_YEAR, session.getSessionYear());
        values.put(SessionColumns.UOM, session.getUomAsString());

        return mDatabase.update(DbHelper.TABLE_SESSIONS, values, "_id = ?", new String[]{"" + session.id});
    }

    public void deleteSession(Session session)
    {
        long id = session.id;
        mDatabase.delete(DbHelper.TABLE_SESSIONS, SessionColumns.SESSION_ID + "=" + id, null);
    }

    public List<Session> getAllSessionsBasedOnDate(DateTime baseLineDate)
    {
        return getAllSessionsForWeek(baseLineDate.getWeekOfWeekyear(), baseLineDate.getYear());
    }

    public List<Session> getAllSessionsForCurrentWeek()
    {
        return getAllSessionsForWeek(DateTime.now().getWeekOfWeekyear(), DateTime.now().getYear());
    }

    List<Session> getAllSessions()
    {
        List<Session> sessionsForWeek = new ArrayList<Session>();

        Cursor cursor = mDatabase.query(DbHelper.TABLE_SESSIONS,
                SessionColumns.allColumns, null,
                null, null, null, null);

        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            sessionsForWeek.add(cursorToSession(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return sessionsForWeek;
    }

    List<Session> getAllPlannedSessions()
    {
        List<Session> sessionsForWeek = new ArrayList<Session>();

        Cursor cursor = mDatabase.query(DbHelper.TABLE_SESSIONS,
                SessionColumns.allColumns, null,
                null, null, null, null);

        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            sessionsForWeek.add(cursorToSession(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return sessionsForWeek;
    }

    List<Session> getAllSessionsForWeek(int week, int year)
    {
        List<Session> sessionsForWeek = new ArrayList<Session>();

        Cursor cursor = mDatabase.query(DbHelper.TABLE_SESSIONS,
                SessionColumns.allColumns, SessionColumns.SESSION_WEEK + " = ? and " + SessionColumns.SESSION_YEAR + " = ?",
                new String[]{"" + week, "" + year}, null, null, null);

        if (cursor.getCount() == 0) return null;

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            sessionsForWeek.add(cursorToSession(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return sessionsForWeek;
    }

    public Session getSessionById(long recId)
    {
        Session session = null;

        Cursor cursor = mDatabase.query(DbHelper.TABLE_SESSIONS,
                SessionColumns.allColumns, SessionColumns.SESSION_ID + " = ?", new String[]{"" + recId}, null, null, null);

        if (cursor.getCount() == 0) return null;

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
        if (sportExists(sport)) return -1;

        ContentValues values = new ContentValues();
        values.put(SportColumns.SPORT, sport);
        long recId;
        try
        {
            recId = mDatabase.insert(DbHelper.TABLE_SPORTS, null, values);
        } catch (SQLiteConstraintException e)
        {
            return -1;
        }
        return recId;
    }

    public int renameSport(String oldSportLabel, String newSportLabel)
    {
        if (!sportExists(oldSportLabel)) return -1;

        ContentValues values = new ContentValues();

        values.put(SportColumns.SPORT, newSportLabel);
        String updateQuery = "update " + DbHelper.TABLE_SESSIONS +
                " set " + SportColumns.SPORT + "='" + newSportLabel + "'" +
                " where " + SportColumns.SPORT + "='" + oldSportLabel + "'";
        mDatabase.execSQL(updateQuery);
        return mDatabase.update(DbHelper.TABLE_SPORTS, values, SportColumns.SPORT + " = ?", new String[]{"" + oldSportLabel});
    }

    boolean sportExists(String sport)
    {
        return getSports().contains(sport);
    }

    public List<String> getSports()
    {
        List<String> sports = new ArrayList<String>();

        Cursor cursor = mDatabase.query(DbHelper.TABLE_SPORTS,
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


    public String[] getSessDescSpinner(String filterBy)
    {
        List<String> sessDescList = new ArrayList<String>();

        Cursor cursor;
        if (filterBy.length() == 0)
            cursor = mDatabase.query(
                    DbHelper.TABLE_SESSIONS, new String[]{SessionColumns.DESCRIPTION, "count(*)"}, null, null, SessionColumns.DESCRIPTION, null, "2 desc");
        else
        {
            cursor = mDatabase.query(
                    DbHelper.TABLE_SESSIONS, new String[]{SessionColumns.DESCRIPTION, "count(*)"}, SessionColumns.SPORT + " = ?", new String[]{filterBy}, SessionColumns.DESCRIPTION, null, "2 desc");

            if (cursor.getCount() == 0) //fall back and show full list of sports instead
                cursor = mDatabase.query(
                        DbHelper.TABLE_SESSIONS, new String[]{SessionColumns.DESCRIPTION, "count(*)"}, null, null, SessionColumns.DESCRIPTION, null, "2 desc");
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            sessDescList.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();

        return sessDescList.toArray(new String[sessDescList.size()]);
    }

    public String[] getSportsForSpinner()
    {
        List<String> sportsList = new ArrayList<String>();
        sportsList.add(mContext.getString(R.string.sess_details_add_new));
        for (String sport : getSports())
            sportsList.add(sport);

        return sportsList.toArray(new String[sportsList.size()]);
    }


    private Session cursorToSession(Cursor cursor)
    {
        Session session = new Session();
        session.id = cursor.getLong(SessionColumns.SESSION_ID_INDEX);
        session.sessionState = Session.State.values()[cursor.getInt(SessionColumns.SESSION_STATE_INDEX)];
        session.sport = cursor.getString(SessionColumns.SESSION_SPORT_INDEX);
        session.description = cursor.getString(SessionColumns.SESSION_DESCRIPTION_INDEX);

        String rawDateTime = cursor.getString(SessionColumns.SESSION_DATE_OF_SESSION_INDEX);
        session.setDateOfSession(Utils.convertSQLiteDate(rawDateTime));
        session.duration = cursor.getInt(SessionColumns.SESSION_DURATION_INDEX);
        session.setDistance(mContext, cursor.getDouble(SessionColumns.SESSION_DISTANCE_INDEX));
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
        truncateSessionTable();
        createSession(new Session(Session.State.COMPLETE, "Running", "Easy run", 120, new DateTime()));
        createSession(new Session(Session.State.PLANNED, "Swimming", "Drills", 50, new DateTime()));
        createSession(new Session(Session.State.PLANNED, "Cycling", "Big Bike", 180, Utils.convertSQLiteDate("2014-05-05")));
        createSession(new Session(Session.State.PLANNED, "Running", "Long Run", 110, Utils.getNextDay()));
    }

    public void loadDynamicTestData()
    {
        truncateSessionTable();
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 58, Utils.getDateOffsetByNDays(-14)));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 62, Utils.getDateOffsetByNDays(-13)));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 120, Utils.getDateOffsetByNDays(-13)));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.getDateOffsetByNDays(-13)));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 49, Utils.getDateOffsetByNDays(-12)));
        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 30, Utils.getDateOffsetByNDays(-11)));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.getDateOffsetByNDays(-10)));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 57, Utils.getDateOffsetByNDays(-9)));
        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 30, Utils.getDateOffsetByNDays(-7)));
        createSession(new Session(Session.State.PLANNED, "Cycling", "roadie", 230, Utils.getDateOffsetByNDays(-6)));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 120, Utils.getDateOffsetByNDays(-6)));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 57, Utils.getDateOffsetByNDays(-4)));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 45, Utils.getDateOffsetByNDays(-3)));
        createSession(new Session(Session.State.COMPLETE, "Strength and Conditioning", "plank", 14, Utils.getDateOffsetByNDays(-1)));
        createSession(new Session(Session.State.COMPLETE, "Strength and Conditioning", "legs", 17, Utils.getDateOffsetByNDays(-1)));

        //aka Today
        Session specialFullyDefinedSess = new Session(Session.State.PLANNED, "Running", "easy with a hint of blah blah this session has lots of word and stuff with newline \n" +
                "       moreshit", 157, Utils.getDateOffsetByNDays(0));
        specialFullyDefinedSess.setDistance(mContext, 21.26);
        specialFullyDefinedSess.notes = "Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum";
        createSession(specialFullyDefinedSess);

        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 30, Utils.getDateOffsetByNDays(1)));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 56, Utils.getDateOffsetByNDays(2)));
        createSession(new Session(Session.State.COMPLETE, "Cycling", "big bike", 230, Utils.getDateOffsetByNDays(2)));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.getDateOffsetByNDays(3)));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 57, Utils.getDateOffsetByNDays(5)));
        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 30, Utils.getDateOffsetByNDays(5)));
        createSession(new Session(Session.State.PLANNED, "Running", "fartlek", 60, Utils.getDateOffsetByNDays(6)));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 114, Utils.getDateOffsetByNDays(7)));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "upper", 19, Utils.getDateOffsetByNDays(8)));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.getDateOffsetByNDays(9)));
        createSession(new Session(Session.State.PLANNED, "Running", "fartlek", 60, Utils.getDateOffsetByNDays(11)));
        createSession(new Session(Session.State.PLANNED, "Cycling", "roadie", 230, Utils.getDateOffsetByNDays(12)));
        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 30, Utils.getDateOffsetByNDays(12)));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.getDateOffsetByNDays(13)));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 50, Utils.getDateOffsetByNDays(14)));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 109, Utils.getDateOffsetByNDays(14)));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.getDateOffsetByNDays(14)));
    }

    public void loadLargeDataSet()
    {
        truncateSessionTable();
        createSession(new Session(Session.State.PLANNED, "Cycling", "big bike", 257, Utils.convertSQLiteDate("2014-05-04")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-05-05")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 70, Utils.convertSQLiteDate("2014-05-05")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-05-07")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 61, Utils.convertSQLiteDate("2014-05-07")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 19, Utils.convertSQLiteDate("2014-05-07")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 64, Utils.convertSQLiteDate("2014-05-08")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-05-09")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 68, Utils.convertSQLiteDate("2014-05-09")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "upper", 20, Utils.convertSQLiteDate("2014-05-10")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 153, Utils.convertSQLiteDate("2014-05-11")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-05-11")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 44, Utils.convertSQLiteDate("2014-05-11")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "open h2o", 58, Utils.convertSQLiteDate("2014-05-12")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-05-13")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 61, Utils.convertSQLiteDate("2014-05-13")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 15, Utils.convertSQLiteDate("2014-05-13")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "2k+drills", 60, Utils.convertSQLiteDate("2014-05-14")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 62, Utils.convertSQLiteDate("2014-05-15")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 118, Utils.convertSQLiteDate("2014-05-16")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 15, Utils.convertSQLiteDate("2014-05-16")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-05-19")));
        createSession(new Session(Session.State.PLANNED, "Running", "middy", 89, Utils.convertSQLiteDate("2014-05-19")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 58, Utils.convertSQLiteDate("2014-05-20")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-05-22")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "open h2o", 29, Utils.convertSQLiteDate("2014-05-22")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 58, Utils.convertSQLiteDate("2014-05-22")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 68, Utils.convertSQLiteDate("2014-05-23")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "big bike", 255, Utils.convertSQLiteDate("2014-05-24")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-05-25")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 148, Utils.convertSQLiteDate("2014-05-25")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 15, Utils.convertSQLiteDate("2014-05-25")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 61, Utils.convertSQLiteDate("2014-05-25")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "upper", 20, Utils.convertSQLiteDate("2014-05-26")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "2k+drills", 60, Utils.convertSQLiteDate("2014-05-27")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 59, Utils.convertSQLiteDate("2014-05-28")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-05-30")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 60, Utils.convertSQLiteDate("2014-05-30")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 29, Utils.convertSQLiteDate("2014-05-30")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 120, Utils.convertSQLiteDate("2014-05-31")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-06-01")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "open h2o", 45, Utils.convertSQLiteDate("2014-06-01")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 120, Utils.convertSQLiteDate("2014-06-01")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 61, Utils.convertSQLiteDate("2014-06-02")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 62, Utils.convertSQLiteDate("2014-06-03")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 131, Utils.convertSQLiteDate("2014-06-04")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-06-05")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 60, Utils.convertSQLiteDate("2014-06-05")));
        createSession(new Session(Session.State.PLANNED, "Running", "middy", 89, Utils.convertSQLiteDate("2014-06-06")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "upper", 15, Utils.convertSQLiteDate("2014-06-07")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-06-08")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 166, Utils.convertSQLiteDate("2014-06-08")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "big bike", 246, Utils.convertSQLiteDate("2014-06-09")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-06-11")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 15, Utils.convertSQLiteDate("2014-06-11")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 60, Utils.convertSQLiteDate("2014-06-11")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 62, Utils.convertSQLiteDate("2014-06-12")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 63, Utils.convertSQLiteDate("2014-06-13")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-06-14")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 60, Utils.convertSQLiteDate("2014-06-14")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "open h2o", 30, Utils.convertSQLiteDate("2014-06-15")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "roadie", 150, Utils.convertSQLiteDate("2014-06-15")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 59, Utils.convertSQLiteDate("2014-06-15")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-06-17")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 60, Utils.convertSQLiteDate("2014-06-17")));
        createSession(new Session(Session.State.PLANNED, "Running", "middy", 89, Utils.convertSQLiteDate("2014-06-18")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 61, Utils.convertSQLiteDate("2014-06-19")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 124, Utils.convertSQLiteDate("2014-06-20")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-06-21")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "big bike", 255, Utils.convertSQLiteDate("2014-06-22")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-06-23")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 14, Utils.convertSQLiteDate("2014-06-23")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 58, Utils.convertSQLiteDate("2014-06-23")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 62, Utils.convertSQLiteDate("2014-06-24")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-06-25")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 58, Utils.convertSQLiteDate("2014-06-25")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 64, Utils.convertSQLiteDate("2014-06-26")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-06-27")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 45, Utils.convertSQLiteDate("2014-06-27")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 145, Utils.convertSQLiteDate("2014-06-28")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "big bike", 236, Utils.convertSQLiteDate("2014-06-29")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 64, Utils.convertSQLiteDate("2014-06-29")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-07-01")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 15, Utils.convertSQLiteDate("2014-07-01")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 58, Utils.convertSQLiteDate("2014-07-01")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 61, Utils.convertSQLiteDate("2014-07-02")));
        createSession(new Session(Session.State.PLANNED, "Running", "middy", 88, Utils.convertSQLiteDate("2014-07-03")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-07-04")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 45, Utils.convertSQLiteDate("2014-07-04")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 61, Utils.convertSQLiteDate("2014-07-05")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-07-06")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 177, Utils.convertSQLiteDate("2014-07-06")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "2k+drills", 63, Utils.convertSQLiteDate("2014-07-07")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-07-08")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 58, Utils.convertSQLiteDate("2014-07-08")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 15, Utils.convertSQLiteDate("2014-07-08")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 63, Utils.convertSQLiteDate("2014-07-09")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-07-11")));
        createSession(new Session(Session.State.PLANNED, "Running", "middy", 87, Utils.convertSQLiteDate("2014-07-11")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "bands", 3, Utils.convertSQLiteDate("2014-07-11")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 15, Utils.convertSQLiteDate("2014-07-12")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 77, Utils.convertSQLiteDate("2014-07-12")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 120, Utils.convertSQLiteDate("2014-07-13")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-07-15")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 64, Utils.convertSQLiteDate("2014-07-15")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 15, Utils.convertSQLiteDate("2014-07-15")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 61, Utils.convertSQLiteDate("2014-07-16")));
        createSession(new Session(Session.State.PLANNED, "Running", "middy", 95, Utils.convertSQLiteDate("2014-07-17")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 120, Utils.convertSQLiteDate("2014-07-18")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "roadie", 117, Utils.convertSQLiteDate("2014-07-19")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "big bike", 251, Utils.convertSQLiteDate("2014-07-20")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-07-21")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 61, Utils.convertSQLiteDate("2014-07-22")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 72, Utils.convertSQLiteDate("2014-07-24")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 61, Utils.convertSQLiteDate("2014-07-25")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 128, Utils.convertSQLiteDate("2014-07-26")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "big bike", 245, Utils.convertSQLiteDate("2014-07-27")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 63, Utils.convertSQLiteDate("2014-07-27")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 60, Utils.convertSQLiteDate("2014-07-29")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-07-30")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 14, Utils.convertSQLiteDate("2014-07-30")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 59, Utils.convertSQLiteDate("2014-07-30")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "upper", 22, Utils.convertSQLiteDate("2014-07-31")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-08-01")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 60, Utils.convertSQLiteDate("2014-08-01")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 53, Utils.convertSQLiteDate("2014-08-02")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-08-03")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 166, Utils.convertSQLiteDate("2014-08-03")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 59, Utils.convertSQLiteDate("2014-08-03")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 117, Utils.convertSQLiteDate("2014-08-05")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 61, Utils.convertSQLiteDate("2014-08-06")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 60, Utils.convertSQLiteDate("2014-08-07")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 119, Utils.convertSQLiteDate("2014-08-08")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-08-09")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "upper", 14, Utils.convertSQLiteDate("2014-08-09")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "big bike", 248, Utils.convertSQLiteDate("2014-08-10")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 60, Utils.convertSQLiteDate("2014-08-12")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 117, Utils.convertSQLiteDate("2014-08-13")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "2k+drills", 59, Utils.convertSQLiteDate("2014-08-14")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-08-15")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 59, Utils.convertSQLiteDate("2014-08-15")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 15, Utils.convertSQLiteDate("2014-08-15")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 29, Utils.convertSQLiteDate("2014-08-16")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-08-17")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 165, Utils.convertSQLiteDate("2014-08-17")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 59, Utils.convertSQLiteDate("2014-08-19")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 59, Utils.convertSQLiteDate("2014-08-19")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 120, Utils.convertSQLiteDate("2014-08-20")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-08-21")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 15, Utils.convertSQLiteDate("2014-08-21")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 58, Utils.convertSQLiteDate("2014-08-21")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 60, Utils.convertSQLiteDate("2014-08-22")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 16, Utils.convertSQLiteDate("2014-08-23")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 20, Utils.convertSQLiteDate("2014-08-23")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 175, Utils.convertSQLiteDate("2014-08-24")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "2k+drills", 60, Utils.convertSQLiteDate("2014-08-25")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-08-27")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 66, Utils.convertSQLiteDate("2014-08-27")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 65, Utils.convertSQLiteDate("2014-08-28")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 15, Utils.convertSQLiteDate("2014-08-28")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 61, Utils.convertSQLiteDate("2014-08-29")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-08-30")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "upper", 24, Utils.convertSQLiteDate("2014-08-30")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "2k+drills", 61, Utils.convertSQLiteDate("2014-08-31")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 62, Utils.convertSQLiteDate("2014-09-02")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-09-04")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 61, Utils.convertSQLiteDate("2014-09-04")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-09-06")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "east", 30, Utils.convertSQLiteDate("2014-09-06")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-09-08")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 64, Utils.convertSQLiteDate("2014-09-09")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 32, Utils.convertSQLiteDate("2014-09-10")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-09-11")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 44, Utils.convertSQLiteDate("2014-09-11")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 46, Utils.convertSQLiteDate("2014-09-12")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 57, Utils.convertSQLiteDate("2014-09-12")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "roadie", 120, Utils.convertSQLiteDate("2014-09-13")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 26, Utils.convertSQLiteDate("2014-09-13")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-09-14")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 99, Utils.convertSQLiteDate("2014-09-14")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 60, Utils.convertSQLiteDate("2014-09-15")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 51, Utils.convertSQLiteDate("2014-09-16")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-09-17")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 30, Utils.convertSQLiteDate("2014-09-17")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 45, Utils.convertSQLiteDate("2014-09-18")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 63, Utils.convertSQLiteDate("2014-09-19")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 117, Utils.convertSQLiteDate("2014-09-20")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-09-21")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "roadie", 180, Utils.convertSQLiteDate("2014-09-21")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 41, Utils.convertSQLiteDate("2014-09-21")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 45, Utils.convertSQLiteDate("2014-09-23")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 13, Utils.convertSQLiteDate("2014-09-23")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 32, Utils.convertSQLiteDate("2014-09-23")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-09-24")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 57, Utils.convertSQLiteDate("2014-09-25")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "upper", 15, Utils.convertSQLiteDate("2014-09-25")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 3, Utils.convertSQLiteDate("2014-09-25")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 115, Utils.convertSQLiteDate("2014-09-26")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-09-27")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 60, Utils.convertSQLiteDate("2014-09-27")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 60, Utils.convertSQLiteDate("2014-09-28")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 120, Utils.convertSQLiteDate("2014-09-29")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 51, Utils.convertSQLiteDate("2014-09-30")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 13, Utils.convertSQLiteDate("2014-09-30")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-10-01")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 60, Utils.convertSQLiteDate("2014-10-01")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "roadie", 170, Utils.convertSQLiteDate("2014-10-02")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-10-03")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 63, Utils.convertSQLiteDate("2014-10-03")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 34, Utils.convertSQLiteDate("2014-10-03")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 47, Utils.convertSQLiteDate("2014-10-04")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "upper", 17, Utils.convertSQLiteDate("2014-10-04")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-10-05")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 148, Utils.convertSQLiteDate("2014-10-05")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 58, Utils.convertSQLiteDate("2014-10-06")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 116, Utils.convertSQLiteDate("2014-10-07")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 46, Utils.convertSQLiteDate("2014-10-08")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 14, Utils.convertSQLiteDate("2014-10-08")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 60, Utils.convertSQLiteDate("2014-10-09")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-10-09")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 120, Utils.convertSQLiteDate("2014-10-10")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-10-12")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 150, Utils.convertSQLiteDate("2014-10-12")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "2k+drills", 61, Utils.convertSQLiteDate("2014-10-14")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-10-15")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 120, Utils.convertSQLiteDate("2014-10-16")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 30, Utils.convertSQLiteDate("2014-10-16")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 57, Utils.convertSQLiteDate("2014-10-17")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-10-18")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "roadie", 120, Utils.convertSQLiteDate("2014-10-18")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "roadie", 174, Utils.convertSQLiteDate("2014-10-19")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 58, Utils.convertSQLiteDate("2014-10-20")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 62, Utils.convertSQLiteDate("2014-10-21")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 120, Utils.convertSQLiteDate("2014-10-22")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-10-23")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 49, Utils.convertSQLiteDate("2014-10-23")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 30, Utils.convertSQLiteDate("2014-10-23")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-10-25")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 57, Utils.convertSQLiteDate("2014-10-25")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 30, Utils.convertSQLiteDate("2014-10-25")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "roadie", 230, Utils.convertSQLiteDate("2014-10-26")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 120, Utils.convertSQLiteDate("2014-10-27")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 57, Utils.convertSQLiteDate("2014-10-28")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 45, Utils.convertSQLiteDate("2014-10-28")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-10-28")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "legs", 17, Utils.convertSQLiteDate("2014-10-29")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 57, Utils.convertSQLiteDate("2014-10-30")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 30, Utils.convertSQLiteDate("2014-10-30")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 56, Utils.convertSQLiteDate("2014-10-31")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "big bike", 230, Utils.convertSQLiteDate("2014-11-02")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-11-04")));
        createSession(new Session(Session.State.PLANNED, "Running", "easy", 57, Utils.convertSQLiteDate("2014-11-05")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 30, Utils.convertSQLiteDate("2014-11-05")));
        createSession(new Session(Session.State.PLANNED, "Running", "fartlek", 60, Utils.convertSQLiteDate("2014-11-06")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 114, Utils.convertSQLiteDate("2014-11-07")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "upper", 19, Utils.convertSQLiteDate("2014-11-07")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-11-08")));
        createSession(new Session(Session.State.PLANNED, "Running", "fartlek", 60, Utils.convertSQLiteDate("2014-11-08")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "roadie", 230, Utils.convertSQLiteDate("2014-11-09")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "easy", 30, Utils.convertSQLiteDate("2014-11-09")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-11-10")));
        createSession(new Session(Session.State.PLANNED, "Swimming", "1k+stuff+pullbouy+wd", 50, Utils.convertSQLiteDate("2014-11-10")));
        createSession(new Session(Session.State.PLANNED, "Cycling", "mtb", 109, Utils.convertSQLiteDate("2014-11-11")));
        createSession(new Session(Session.State.PLANNED, "Strength and Conditioning", "plank", 14, Utils.convertSQLiteDate("2014-11-12")));
    }

    void truncateSessionTable()
    {
        String truncateSessionsSQL = "delete from sessions;";
        mDatabase.execSQL(truncateSessionsSQL);
    }
}
