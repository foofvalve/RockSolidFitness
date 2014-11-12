package com.rocksolidfitness;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper
{
    public static final String TABLE_SESSIONS = "sessions";
    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_SESSIONS + "( " + Columns.SESSION_ID
            + " integer primary key autoincrement, " +
            Columns.STATE + " int not null," +
            Columns.SPORT + " text not null," +
            Columns.DESCRIPTION + " text not null," +
            Columns.DATE_OF_SESSION + " datetime default null," +
            Columns.DURATION + " int not null," +
            Columns.DISTANCE + " real," +
            Columns.NOTES + " text," +
            Columns.AVG_HR_RATE + " real," +
            Columns.LOCATION + " text," +
            Columns.CALORIES_BURNT + " int," +
            Columns.WEIGHT + " real," +
            Columns.RACE_NAME + " text," +
            Columns.TRAINING_WEEK + " int," +
            Columns.DATE_CREATED + " datetime default null," +
            Columns.DATE_MODIFIED + " datetime default null);";
    public static final String TABLE_SPORTS = "sports";
    private static final String DATABASE_CREATE_SPORT_TABLE = "create table if not exists "
            + TABLE_SPORTS + "(sport varchar(255) primary key);  ";
    //TODO: translate this string
    private static final String DATABASE_INSERT_SPORTS =
            "insert into " + TABLE_SPORTS + " (sport) values ";
    private static final int DATABASE_VERSION = 3;
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

