package com.rocksolidfitness;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainViewFragment extends Fragment
{
    DateTime mDashboardDate;
    ExpandableListView mExpListView;
    ExpandableListAdapter mListAdapter;
    HashMap<String, DateTime> mDateFromWeekAndYear;
    TextView currentWeekLabel;
    TextView sliderMonthName;
    private List<String> mListDataHeader;
    private HashMap<String, List<Session>> mListDataChild;

    public MainViewFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        SharedPreferences settings = getActivity().getSharedPreferences(Consts.PREFS_NAME, 0);
        settings.getString("uom", "KM");

        SessionsDataSource dataSource = new SessionsDataSource(getActivity());
        dataSource.open();
        dataSource.loadDynamicTestData();
        dataSource.close();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mExpListView = (ExpandableListView) rootView.findViewById(R.id.expandableListViewSessions);
        currentWeekLabel = (TextView) rootView.findViewById(R.id.lblCurrentWeek);
        sliderMonthName = (TextView) rootView.findViewById(R.id.lblCurrentWeekInfo);

        long initialDashDate = settings.getLong("global_dashboard_date", new DateTime().getMillis()); //if stored in prefs use it, otherwise fallback to today
        setDashboardDate(new DateTime(initialDashDate));

        Button btnPrev = (Button) rootView.findViewById(R.id.btnPreviousWeek);
        btnPrev.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                prepareListData(DIRECTION.BACKWARD);

                mListAdapter = new ExpandableListAdapter(getActivity(), mListDataHeader, mListDataChild);
                mExpListView.setAdapter(mListAdapter);

                mListAdapter.notifyDataSetChanged();
                mExpListView.invalidate();
                autoExpandAll();
            }
        });

        Button btnNext = (Button) rootView.findViewById(R.id.btnNexWeek);
        btnNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                prepareListData(DIRECTION.FORWARD);

                mListAdapter = new ExpandableListAdapter(getActivity(), mListDataHeader, mListDataChild);
                mExpListView.setAdapter(mListAdapter);

                mListAdapter.notifyDataSetChanged();
                mExpListView.invalidate();
                autoExpandAll();
            }
        });

        if (mDashboardDate.getMillis() == new DateTime().getMillis())
            prepareListData(DIRECTION.TODAY);
        else
            prepareListData(DIRECTION.DO_NOT_SLIDE);

        mListAdapter = new ExpandableListAdapter(getActivity(), mListDataHeader, mListDataChild);
        mExpListView.setAdapter(mListAdapter);
        if (mDashboardDate.getMillis() == new DateTime().getMillis())
            autoExpandToday();
        else
            autoExpandAll();

        return rootView;
    }


    void setDashboardDate(DateTime dashboardDate)
    {
        mDashboardDate = dashboardDate;
        currentWeekLabel.clearComposingText();

        if (mDashboardDate.getWeekOfWeekyear() == DateTime.now().getWeekOfWeekyear())
            currentWeekLabel.setText(getString(R.string.dashboard_header));
        else
            currentWeekLabel.setText(getString(R.string.week) + " " + mDashboardDate.getWeekOfWeekyear());

        sliderMonthName.clearComposingText();
        sliderMonthName.setText(mDashboardDate.monthOfYear().getAsText() + " " + mDashboardDate.getYear());

        SharedPreferences settings = getActivity().getSharedPreferences(Consts.PREFS_NAME, 0);
        SharedPreferences.Editor edit = settings.edit();
        edit.clear();
        edit.putLong("global_dashboard_date", mDashboardDate.getMillis());
        edit.commit();
    }


    void autoExpandAll()
    {
        int count = mListAdapter.getGroupCount();
        for (int position = count; position != 0; position--)  //go backwards to force scroll to top
            mExpListView.expandGroup(position - 1, true);
    }

    void autoExpandToday()
    {
        int count = mListAdapter.getGroupCount();
        for (int position = 1; position <= count; position++)
            if (mListAdapter.getGroup(position - 1).toString().startsWith(getString(R.string.today)))
            {
                mExpListView.expandGroup(position - 1, true);
                break;
            }
    }

    private void prepareListData(DIRECTION shiftDirection)
    {
        SessionsDataSource dataSource = new SessionsDataSource(getActivity());
        dataSource.openReadOnly();

        mListDataHeader = new ArrayList<String>();
        mListDataChild = new HashMap<String, List<Session>>();

        List<Session> monSessions = new ArrayList<Session>();
        List<Session> tueSessions = new ArrayList<Session>();
        List<Session> wedSessions = new ArrayList<Session>();
        List<Session> thuSessions = new ArrayList<Session>();
        List<Session> friSessions = new ArrayList<Session>();
        List<Session> satSessions = new ArrayList<Session>();
        List<Session> sunSessions = new ArrayList<Session>();

        List<Session> sessionsForDisplay;

        if (shiftDirection == DIRECTION.FORWARD)
        {
            setDashboardDate(mDashboardDate.plusDays(7));
            sessionsForDisplay = dataSource.getAllSessionsBasedOnDate(mDashboardDate);
        } else if (shiftDirection == DIRECTION.TODAY)
        {
            sessionsForDisplay = dataSource.getAllSessionsForCurrentWeek();
        } else if (shiftDirection == DIRECTION.BACKWARD)
        {
            setDashboardDate(mDashboardDate.minusDays(7));
            sessionsForDisplay = dataSource.getAllSessionsBasedOnDate(mDashboardDate);
        } else
        {
            sessionsForDisplay = dataSource.getAllSessionsBasedOnDate(mDashboardDate);
        }

        if (sessionsForDisplay == null || sessionsForDisplay.size() == 0) //no session data found for the given week
        {
            mDateFromWeekAndYear = Utils.getDateFromWeekAndYear(mDashboardDate);
        } else
        {
            for (Session sport : sessionsForDisplay)
            {
                DateTime.Property pDoW = sport.getDateOfSession().dayOfWeek();

                switch (pDoW.get())
                {
                    case 1:
                        monSessions.add(sport);
                        break;
                    case 2:
                        tueSessions.add(sport);
                        break;
                    case 3:
                        wedSessions.add(sport);
                        break;
                    case 4:
                        thuSessions.add(sport);
                        break;
                    case 5:
                        friSessions.add(sport);
                        break;
                    case 6:
                        satSessions.add(sport);
                        break;
                    case 7:
                        sunSessions.add(sport);
                        break;
                }
            }
            mDateFromWeekAndYear = Utils.getDateFromWeekAndYear(sessionsForDisplay.get(0).getDateOfSession());
        }


        // Weekday group header - format will be "Wednesday~12", this will be split on ~ in the Adapter
        mListDataHeader.add(getFormattedGroupHeaderLabel(getString(R.string.wk_mon), mDateFromWeekAndYear.get(DateTimeConstants.MONDAY + "")));
        mListDataHeader.add(getFormattedGroupHeaderLabel(getString(R.string.wk_tue), mDateFromWeekAndYear.get(DateTimeConstants.TUESDAY + "")));
        mListDataHeader.add(getFormattedGroupHeaderLabel(getString(R.string.wk_wed), mDateFromWeekAndYear.get(DateTimeConstants.WEDNESDAY + "")));
        mListDataHeader.add(getFormattedGroupHeaderLabel(getString(R.string.wk_thu), mDateFromWeekAndYear.get(DateTimeConstants.THURSDAY + "")));
        mListDataHeader.add(getFormattedGroupHeaderLabel(getString(R.string.wk_fri), mDateFromWeekAndYear.get(DateTimeConstants.FRIDAY + "")));
        mListDataHeader.add(getFormattedGroupHeaderLabel(getString(R.string.wk_sat), mDateFromWeekAndYear.get(DateTimeConstants.SATURDAY + "")));
        mListDataHeader.add(getFormattedGroupHeaderLabel(getString(R.string.wk_sun), mDateFromWeekAndYear.get(DateTimeConstants.SUNDAY + "")));


        mListDataChild.put(mListDataHeader.get(0), getSessionForDay(monSessions, DateTimeConstants.MONDAY)); // Header, Child data
        mListDataChild.put(mListDataHeader.get(1), getSessionForDay(tueSessions, DateTimeConstants.TUESDAY));
        mListDataChild.put(mListDataHeader.get(2), getSessionForDay(wedSessions, DateTimeConstants.WEDNESDAY));
        mListDataChild.put(mListDataHeader.get(3), getSessionForDay(thuSessions, DateTimeConstants.THURSDAY));
        mListDataChild.put(mListDataHeader.get(4), getSessionForDay(friSessions, DateTimeConstants.FRIDAY));
        mListDataChild.put(mListDataHeader.get(5), getSessionForDay(satSessions, DateTimeConstants.SATURDAY));
        mListDataChild.put(mListDataHeader.get(6), getSessionForDay(sunSessions, DateTimeConstants.SUNDAY));
        dataSource.close();
    }

    List<Session> getSessionForDay(List<Session> sessions, int dayOfWeek)
    {
        if (sessions.size() == 0) //insert a placeholder session if there are no sessions for the day
            sessions.add(new Session(Session.State.PLANNED, Consts.NO_SESSIONS_YET, Consts.NO_SESSIONS_YET, 99, mDateFromWeekAndYear.get(dayOfWeek + "")));

        return sessions;
    }

    String getFormattedGroupHeaderLabel(String dayOfWeek, DateTime correspondingDate)
    {
        //necessary to find the current day
        DateTime today = new DateTime();
        if (today.dayOfMonth().getAsShortText().equals(correspondingDate.dayOfMonth().getAsShortText()) &&
                today.getMonthOfYear() == correspondingDate.getMonthOfYear() &&
                today.getYear() == correspondingDate.getYear())
            return getString(R.string.today) + "~" + correspondingDate.dayOfMonth().getAsShortText();
        else
            return dayOfWeek + "~" + correspondingDate.dayOfMonth().getAsShortText();
    }

    public enum DIRECTION
    {
        FORWARD,
        BACKWARD,
        TODAY,
        DO_NOT_SLIDE
    }
}