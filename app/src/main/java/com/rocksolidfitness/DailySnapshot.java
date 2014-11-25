package com.rocksolidfitness;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class DailySnapshot
{
    public int mBelongsToWeek;
    public int mBelongsToDayOfWeek;
    List<Session> mSessionsForDay;
    private boolean mIsPlaceholder;

    public DailySnapshot(int belongsToWeek)
    {
        mSessionsForDay = new ArrayList<Session>();
        mIsPlaceholder = false;
        this.mBelongsToWeek = belongsToWeek;
    }

    public DailySnapshot flagAsPlaceholder()
    {
        mIsPlaceholder = true;
        mSessionsForDay = null;
        mBelongsToDayOfWeek = 0;
        return this;
    }

    public boolean isIsPlaceholder()
    {
        return mIsPlaceholder;
    }

    public void addSession(Session session)
    {
        mSessionsForDay.add(session);
    }

    public boolean hasNoSessions()
    {
        return mSessionsForDay != null && mSessionsForDay.size() == 0 ? true : false;
    }

    public boolean hasManySessions()
    {
        return mSessionsForDay != null && mSessionsForDay.size() > 1 ? true : false;
    }

    public boolean hasOneSession()
    {
        return mSessionsForDay != null && mSessionsForDay.size() == 1 ? true : false;
    }

    @Override
    public String toString()
    {
        if (hasNoSessions())
            return MessageFormat.format("Week {0} Day {1} no sessions", mBelongsToWeek, mBelongsToDayOfWeek);

        if (isIsPlaceholder()) return MessageFormat.format("Week {0}", mBelongsToWeek);

        if (hasOneSession()) return MessageFormat.format("Week {0} Day {1} {2} {3} {4}",
                mBelongsToWeek, mBelongsToDayOfWeek, mSessionsForDay.get(0).sport,
                mSessionsForDay.get(0).getFormattedDurationHour() + ":" + mSessionsForDay.get(0).getFormattedDurationMinute(),
                mSessionsForDay.get(0).description);

        if (hasManySessions())
        {
            String msg = "";
            for (Session sport : mSessionsForDay)
            {
                msg += "\n" + MessageFormat.format("Week {0} Day {1} {2} {3} {4}",
                        mBelongsToWeek, mBelongsToDayOfWeek, sport.sport,
                        sport.getFormattedDurationHour() + ":" + sport.getFormattedDurationMinute(),
                        sport.description);
            }
            return msg;
        }

        return "";
    }
}
