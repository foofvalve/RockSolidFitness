package com.rocksolidfitness;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import org.joda.time.DateTime;

import java.text.MessageFormat;


public final class Session
{
    //Fields
    public long id;

    public State sessionState;
    public String sport;
    public String description;
    public int duration;
    public String notes;
    public int avgHrRate;
    public String location;
    public int caloriesBurnt;
    public double weight;
    public String raceName;
    public int trainingWeek;   //http://en.wikipedia.org/wiki/ISO_week_date
    public DateTime dateCreated;
    public DateTime dateModified;
    private double distance;
    private DateTime dateOfSession;
    private int sessionWeek;
    private int sessionYear;
    private UOM uom;

    public Session()
    {
    }

    public Session(State state, String sport, String description, int duration, DateTime dateOfSession, double distance)
    {
        this.distance = distance;
        this.sessionState = state;
        this.sport = sport;
        this.description = description;
        this.duration = duration;
        this.dateOfSession = dateOfSession;
        setDateOfSession(dateOfSession);
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

    public boolean isComplete()
    {
        return sessionState == State.COMPLETE;
    }

    private void setSessionWeek()
    {
        sessionWeek = this.dateOfSession.getWeekOfWeekyear();
    }

    private void setSessionYear()
    {
        sessionYear = this.dateOfSession.getYear();
    }

    public int getSessionWeek()
    {
        return sessionWeek;
    }

    public int getSessionYear()
    {
        return sessionYear;
    }

    public String getFormattedDuration(Context context)
    {
        Resources resources = context.getResources();

        if (duration < 60)
            return duration + " " + resources.getString(R.string.time_minutes);

        if (duration == 60)
            return "1 " + resources.getString(R.string.time_hour);

        if (duration % 60 == 0)
            return duration / 60 + " " + resources.getString(R.string.time_hours);

        int minutes = duration % 60;
        int hours = (duration - minutes) / 60;

        return hours + " " + resources.getString(R.string.time_hours) + " " +
                minutes + " " + resources.getString(R.string.time_minutes);
    }

    public String getFormattedDurationHour()
    {
        if (duration < 60)
            return "0";

        if (duration == 60)
            return "1";

        if (duration % 60 == 0)
            return Integer.toString(duration / 60);

        int minutes = duration % 60;
        int hours = (duration - minutes) / 60;

        return Integer.toString(hours);
    }

    public String getFormattedDurationMinute()
    {
        if (duration < 60)
            return Integer.toString(duration);

        if (duration == 60)
            return "0";

        if (duration % 60 == 0)
            return "0";

        int minutes = duration % 60;

        return Integer.toString(minutes);
    }

    public String getFormattedCombinedDuration()
    {
        return getFormattedDurationHour() + ":" + getFormattedDurationMinute();
    }

    public String getSessionDateFormatted()
    {
        return Utils.getSessionDateFormatted(dateOfSession);
    }

    public String getFullSessionDescription()
    {
        return Utils.shortifyText(sport, 30) + "\t" + getFormattedCombinedDuration() + "\n" +
                Utils.shortifyText(description, 40);
    }

    public String getMeduimSessionDescription()
    {
        return Utils.shortifyText(sport, 13) + "\t" + getFormattedCombinedDuration() + "\n" +
                Utils.shortifyText(description, 25);
    }

    public String getShortSessionDescription()
    {
        return sport.charAt(0) + "\t" + getFormattedCombinedDuration() + "\t" +
                Utils.shortifyText(description, 12);
    }

    public String toString()
    {
        return MessageFormat.format("id=[{0}]|" +
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
                        "sessionYear=[{17}]|" +
                        "uom=[{17}]\n",
                id, sessionState, sport, description, dateOfSession, duration, getDistance(),
                notes, avgHrRate, location, caloriesBurnt, weight, raceName, trainingWeek,
                dateCreated, dateModified, sessionWeek, sessionYear, getUomAsString());

    }

    public double getDistance()
    {
        return distance;
    }

    public void setDistance(Context context, double distance)
    {
        this.distance = distance;
        if (distance != 0)
        {
            SharedPreferences settings = context.getSharedPreferences(Consts.PREFS_NAME, 0);
            this.uom = UOM.valueOf(settings.getString("uom", "KM"));
        }
    }

    public String getUomAsString()
    {
        if (uom == null)
            return "";
        else
            return uom.name();
    }

    public enum State
    {
        PLANNED, COMPLETE, SKIPPED
    }

    public enum UOM
    {
        KM, MILES
    }
}
