package com.rocksolidfitness;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;


import android.net.Uri;
import android.provider.BaseColumns;


public final class Session implements Parcelable
{
    //Fields
    public int id;
    public boolean isComplete;
    public String sport;
    public String description;
    public long dateOfSession;
    public int duration;
    public double distance;
    public String notes;
    public int avgHrRate;
    public String location;
    public int caloriesBurnt;
    public double weight;
    public String raceName;
    public int trainingWeek;   //http://en.wikipedia.org/wiki/ISO_week_date

    public Session(Cursor c)
    {
        id = c.getInt(Columns.SESSION_ID_INDEX);
        isComplete = c.getInt(Columns.SESSION_IS_COMPLETE_INDEX) == 1;
        sport = c.getString(Columns.SESSION_SPORT_INDEX);
        description = c.getString(Columns.SESSION_DESCRIPTION_INDEX);

        dateOfSession = c.getLong(Columns.SESSION_DATE_OF_SESSION_INDEX);
        duration = c.getInt(Columns.SESSION_DURATION_INDEX);
        distance = c.getDouble(Columns.SESSION_DISTANCE_INDEX);
        notes = c.getString(Columns.SESSION_NOTES_INDEX);
        avgHrRate = c.getInt(Columns.SESSION_AVG_HR_RATE_INDEX);
        location = c.getString(Columns.SESSION_LOCATION_INDEX);
        caloriesBurnt = c.getInt(Columns.SESSION_CALORIES_BURNT_INDEX);
        weight = c.getDouble(Columns.SESSION_WEIGHT_INDEX);
        raceName = c.getString(Columns.SESSION_RACE_NAME_INDEX);
        trainingWeek = c.getInt(Columns.SESSION_TRAINING_WEEK_INDEX);
    }

    public Session(Parcel p)
    {

        id = p.readInt();
        isComplete = p.readInt() == 1;
        sport = p.readString();
        description = p.readString();
        dateOfSession = p.readLong();
        duration = p.readInt();
        distance = p.readDouble();
        notes = p.readString();
        avgHrRate = p.readInt();
        location = p.readString();
        caloriesBurnt = p.readInt();
        weight = p.readDouble();
        raceName = p.readString();
        trainingWeek = p.readInt();
    }

    public static class Columns implements BaseColumns
    {
        public static final Uri CONTENT_URI =
                Uri.parse("content://com.rocksolidfitness.app.provider");

        public static final String IS_COMPLETE = "iscomplete";
        public static final String SPORT = "sport";
        public static final String DESCRIPTION = "description";
        public static final String DATE_OF_SESSION = "dateofsession";
        public static final String DURATION = "duration";
        public static final String DAYS_OF_WEEK = "daysofweek";
        public static final String DISTANCE = "distance";
        public static final String NOTES = "notes";
        public static final String AVG_HR_RATE = "avghrrate";
        public static final String LOCATION = "location";
        public static final String CALORIES_BURNT = "caloriesburnt";
        public static final String WEIGHT = "weight";
        public static final String RACE_NAME = "racename";
        public static final String TRAINING_WEEK = "trainingweek";


        public static final int SESSION_ID_INDEX = 0;
        public static final int SESSION_IS_COMPLETE_INDEX = 1;
        public static final int SESSION_SPORT_INDEX = 2;
        public static final int SESSION_DESCRIPTION_INDEX = 3;
        public static final int SESSION_DATE_OF_SESSION_INDEX = 4;
        public static final int SESSION_DURATION_INDEX = 5;
        public static final int SESSION_DISTANCE_INDEX = 6;
        public static final int SESSION_NOTES_INDEX = 7;
        public static final int SESSION_AVG_HR_RATE_INDEX = 8;
        public static final int SESSION_LOCATION_INDEX = 9;
        public static final int SESSION_CALORIES_BURNT_INDEX = 10;
        public static final int SESSION_WEIGHT_INDEX = 11;
        public static final int SESSION_RACE_NAME_INDEX = 12;
        public static final int SESSION_TRAINING_WEEK_INDEX = 13;

    }


    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {

    }
}
