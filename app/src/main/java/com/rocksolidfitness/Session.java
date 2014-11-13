package com.rocksolidfitness;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.MessageFormat;
import java.util.Date;


public final class Session implements Parcelable
{
    //Fields
    public long id;

    public State sessionState;
    public String sport;
    public String description;
    public Date dateOfSession;
    public int duration;
    public double distance;
    public String notes;
    public int avgHrRate;
    public String location;
    public int caloriesBurnt;
    public double weight;
    public String raceName;
    public int trainingWeek;   //http://en.wikipedia.org/wiki/ISO_week_date
    public Date dateCreated;
    public Date dateModified;

    public Session()
    {
    }

    public Session(State state, String sport, String description, int duration, Date dateOfSession)
    {
        this.sessionState = state;
        this.sport = sport;
        this.description = description;
        this.duration = duration;
        this.dateOfSession = dateOfSession;
    }

    public Session(Cursor c)
    {
        /*
        id = c.getLong(Columns.SESSION_ID_INDEX);
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
        */
    }

    public Session(Parcel p)
    {
    /*
        id = p.readLong();
        sessionState = State.values()[p.readInt()];
        sport = p.readString();
        description = p.readString();
        dateOfSession = p.read readLong();
        duration = p.readInt();
        distance = p.readDouble();
        notes = p.readString();
        avgHrRate = p.readInt();
        location = p.readString();
        caloriesBurnt = p.readInt();
        weight = p.readDouble();
        raceName = p.readString();
        trainingWeek = p.readInt();
        */
    }

    public String toString()
    {
        String stringedUp = MessageFormat.format("id=[{0}]\n" +
                        "sessionState=[{1}]\n" +
                        "sport=[{2}]\n" +
                        "description=[{3}]\n" +
                        "dateOfSession=[{4}]\n" +
                        "duration=[{5}]\n" +
                        "distance=[{6}]\n" +
                        "notes=[{7}]\n" +
                        "avgHrRate=[{8}]\n" +
                        "location=[{9}]\n" +
                        "caloriesBurnt=[{10}]\n" +
                        "weight=[{11}]\n" +
                        "raceName=[{12}]\n" +
                        "trainingWeek=[{13}]\n" +
                        "dateCreated=[{14}]\n" +
                        "dateModified=[{15}]\n",
                id, sessionState, sport, description, dateOfSession, duration, distance, notes, avgHrRate, location, caloriesBurnt, weight, raceName, trainingWeek, dateCreated, dateModified);

        return stringedUp;
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

    public enum State
    {
        PLANNED, COMPLETE, SKIPPED
    }
}
