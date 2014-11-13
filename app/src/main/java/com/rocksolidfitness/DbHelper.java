package com.rocksolidfitness;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper
{
    public static final String TABLE_SESSIONS = "sessions";
    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_SESSIONS + "( " + SessionColumns.SESSION_ID
            + " integer primary key autoincrement, " +
            SessionColumns.STATE + " int not null," +
            SessionColumns.SPORT + " text not null," +
            SessionColumns.DESCRIPTION + " text not null," +
            SessionColumns.DATE_OF_SESSION + " datetime default null," +
            SessionColumns.DURATION + " int not null," +
            SessionColumns.DISTANCE + " real," +
            SessionColumns.NOTES + " text," +
            SessionColumns.AVG_HR_RATE + " real," +
            SessionColumns.LOCATION + " text," +
            SessionColumns.CALORIES_BURNT + " int," +
            SessionColumns.WEIGHT + " real," +
            SessionColumns.RACE_NAME + " text," +
            SessionColumns.TRAINING_WEEK + " int," +
            SessionColumns.DATE_CREATED + " datetime default null," +
            SessionColumns.DATE_MODIFIED + " datetime default null);";
    public static final String TABLE_SPORTS = "sports";
    private static final String DATABASE_CREATE_SPORT_TABLE = "create table if not exists "
            + TABLE_SPORTS + "(" + SportColumns.SPORT_ID + " integer primary key autoincrement, " +
            SportColumns.SPORT + " varchar(255) not null unique);  ";

    //TODO: translate this string
    private static final String DATABASE_INSERT_SPORTS =
            "insert into " + TABLE_SPORTS + " (" + SportColumns.SPORT + ") values ";
    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "sessions.db";


    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(DATABASE_CREATE);
        database.execSQL(DATABASE_CREATE_SPORT_TABLE);

        String[] sports = new String[]{"Cycling", "Running", "Swimming"};
        for (String sport : sports)
            database.execSQL(DATABASE_INSERT_SPORTS + " ('" + sport + "');");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(DbHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPORTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        onCreate(db);
    }


}

