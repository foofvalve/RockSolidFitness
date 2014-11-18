package com.rocksolidfitness;

import android.provider.BaseColumns;

class SessionColumns implements BaseColumns
{

    public static final String SESSION_ID = "_id";
    public static final String STATE = "iscomplete";
    public static final String SPORT = "sport";
    public static final String DESCRIPTION = "description";
    public static final String DATE_OF_SESSION = "dateofsession";
    public static final String DURATION = "duration";
    public static final String DISTANCE = "distance";
    public static final String NOTES = "notes";
    public static final String AVG_HR_RATE = "avghrrate";
    public static final String LOCATION = "location";
    public static final String CALORIES_BURNT = "caloriesburnt";
    public static final String WEIGHT = "weight";
    public static final String RACE_NAME = "racename";
    public static final String TRAINING_WEEK = "trainingweek";
    public static final String DATE_CREATED = "datecreated";
    public static final String DATE_MODIFIED = "datemodified";
    public static final String SESSION_WEEK = "sessionweek";
    public static final String SESSION_YEAR = "sessionyear";

    public static final String[] allColumns = {
            SESSION_ID,
            STATE,
            SPORT,
            DESCRIPTION,
            DATE_OF_SESSION,
            DURATION,
            DISTANCE,
            NOTES,
            AVG_HR_RATE,
            LOCATION,
            CALORIES_BURNT,
            WEIGHT,
            RACE_NAME,
            TRAINING_WEEK,
            DATE_CREATED,
            DATE_MODIFIED,
            SESSION_WEEK,
            SESSION_YEAR
    };

    public static final int SESSION_ID_INDEX = 0;
    public static final int SESSION_STATE_INDEX = 1;
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
    public static final int SESSION_DATE_CREATED_INDEX = 14;
    public static final int SESSION_DATE_MODIFIED_INDEX = 15;

    //not required, set dynamically by Session.setDateOfSession()
    //public static final int SESSION_SESSION_WEEK_INDEX = 16;
    //public static final int SESSION_SESSION_YEAR_INDEX = 17;
}

