package com.rocksolidfitness;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private List<String> mListDataHeader;
    private HashMap<String, List<Session>> mListDataChild;

    public MainViewFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        SessionsDataSource dataSource = new SessionsDataSource(getActivity());
        dataSource.open();
        //dataSource.loadDynamicTestData();
        dataSource.close();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mExpListView = (ExpandableListView) rootView.findViewById(R.id.expandableListViewSessions);

        TextView sliderMonthName = (TextView) rootView.findViewById(R.id.lblCurrentWeekInfo);
        mDashboardDate = new DateTime();
        sliderMonthName.clearComposingText();
        sliderMonthName.setText(mDashboardDate.monthOfYear().getAsText() + " " + mDashboardDate.getYear());

        // preparing list data
        prepareListData();

        mListAdapter = new ExpandableListAdapter(getActivity(), mListDataHeader, mListDataChild);

        // setting list adapter
        mExpListView.setAdapter(mListAdapter);
        autoExpandToday();

        return rootView;
    }

    //@Override
    public void onResumeZZZ()
    {
        Log.e("", ">>> onResume is called");
        super.onResume();
        prepareListData();

        final ExpandableListAdapter mListAdapter = new ExpandableListAdapter(getActivity(), mListDataHeader, mListDataChild);
        mExpListView.setAdapter(mListAdapter);
        //mExpListView.refreshDrawableState();
        autoExpandToday();
        // setting list adapter


        //if(mListAdapter== null){ //Adapter not set yet.
        //   mExpListView.setAdapter(mListAdapter);
        //   Log.e("",">>> onResume mExpListView.setAdapter(mListAdapter);");
        //}
        //else{ //Already has an adapter

        //    mListAdapter.notifyDataSetChanged();
        //    Log.e("",">>> onResume notifyDataSetChanged");
        //}
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

    private void prepareListData()
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


        List<Session> sessionsForThisWeek = dataSource.getAllSessionsForCurrentWeek();
        for (Session sport : sessionsForThisWeek)
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

        mDateFromWeekAndYear = Utils.getDateFromWeekAndYear(sessionsForThisWeek.get(0).getDateOfSession());

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
}