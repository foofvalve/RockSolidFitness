package com.rocksolidfitness;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import java.text.MessageFormat;


public final class Session implements Parcelable
{
    //Fields
    public long id;

    public State sessionState;
    public String sport;
    public String description;
    public int duration;
    public double distance;
    public String notes;
    public int avgHrRate;
    public String location;
    public int caloriesBurnt;
    public double weight;
    public String raceName;
    public int trainingWeek;   //http://en.wikipedia.org/wiki/ISO_week_date
    public DateTime dateCreated;
    public DateTime dateModified;
    private DateTime dateOfSession;
    private int sessionWeek;
    private int sessionYear;

    public Session()
    {
    }

    public Session(State state, String sport, String description, int duration, DateTime dateOfSession)
    {
        this.sessionState = state;
        this.sport = sport;
        this.description = description;
        this.duration = duration;
        this.dateOfSession = dateOfSession;
        setDateOfSession(dateOfSession);
    }

    public DateTime getDateOfSession()
    {
        return dateOfSession;
    }

    public void setDateOfSession(DateTime sessionDate)
    {
        dateOfSession = sessionDate;
        setSessionWeek();
        setSessionYear();
    }

    private void setSessionWeek()
    {
        sessionWeek = dateOfSession.getWeekOfWeekyear();
    }

    private void setSessionYear()
    {
        sessionYear = dateOfSession.getYear();
    }

    public int getSessionWeek()
    {
        return sessionWeek;
    }

    public int getSessionYear()
    {
        return sessionYear;
    }

    public String toString()
    {
        String stringedUp = MessageFormat.format("id=[{0}]|" +
                        "sessionState=[{1}]|" +
                        "sport=[{2}]|" +
                        "description=[{3}]|" +
                        "dateOfSession=[{4}]|" +
                        "duration=[{5}]|" +
                        "distance=[{6}]|" +
                        "notes=[{7}]|" +
                        "avgHrRate=[{8}]|" +
                        "location=[{9}]|" +
                        "caloriesBurnt=[{10}]|" +
                        "weight=[{11}]|" +
                        "raceName=[{12}]|" +
                        "trainingWeek=[{13}]|" +
                        "dateCreated=[{14}]|" +
                        "dateModified=[{15}]|" +
                        "sessionWeek=[{16}]|" +
                        "sessionYear=[{17}]\n",
                id, sessionState, sport, description, dateOfSession, duration, distance,
                notes, avgHrRate, location, caloriesBurnt, weight, raceName, trainingWeek,
                dateCreated, dateModified, sessionWeek, sessionYear);

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
