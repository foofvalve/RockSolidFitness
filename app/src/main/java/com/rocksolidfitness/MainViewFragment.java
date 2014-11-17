package com.rocksolidfitness;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainViewFragment extends Fragment
{
    ExpandableListAdapter mListAdapter;
    ExpandableListView mExpListView;
    List<String> mListDataHeader;
    HashMap<String, List<Session>> mListDataChild;

    public MainViewFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mExpListView = (ExpandableListView) rootView.findViewById(R.id.expandableListViewSessions);

        // preparing list data
        prepareListData();

        mListAdapter = new ExpandableListAdapter(getActivity(), mListDataHeader, mListDataChild);

        // setting list adapter
        mExpListView.setAdapter(mListAdapter);

        //auto expand and highlight today  :: TODO highlight it!
        int count = mListAdapter.getGroupCount();
        for (int position = 1; position <= count; position++)
        {
            if (mListAdapter.getGroup(position - 1).toString().startsWith("Today"))
                mExpListView.expandGroup(position - 1, true);
        }

        return rootView;
    }

    private void prepareListData()
    {
        SessionsDataSource dataSource = new SessionsDataSource(getActivity());
        dataSource.open();
        dataSource.loadDynamicTestData();
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

        HashMap<String, DateTime> dateFromWeekAndYear = Utils.getDateFromWeekAndYear(sessionsForThisWeek.get(0).getDateOfSession());

        // Weekday group header - format will be "Wednesday~12", this will be split on ~ in the Adapter
        mListDataHeader.add(getFormattedGroupHeaderLabel(getString(R.string.wk_mon), dateFromWeekAndYear.get(DateTimeConstants.MONDAY + "")));
        mListDataHeader.add(getFormattedGroupHeaderLabel(getString(R.string.wk_tue), dateFromWeekAndYear.get(DateTimeConstants.TUESDAY + "")));
        mListDataHeader.add(getFormattedGroupHeaderLabel(getString(R.string.wk_wed), dateFromWeekAndYear.get(DateTimeConstants.WEDNESDAY + "")));
        mListDataHeader.add(getFormattedGroupHeaderLabel(getString(R.string.wk_thu), dateFromWeekAndYear.get(DateTimeConstants.THURSDAY + "")));
        mListDataHeader.add(getFormattedGroupHeaderLabel(getString(R.string.wk_fri), dateFromWeekAndYear.get(DateTimeConstants.FRIDAY + "")));
        mListDataHeader.add(getFormattedGroupHeaderLabel(getString(R.string.wk_sat), dateFromWeekAndYear.get(DateTimeConstants.SATURDAY + "")));
        mListDataHeader.add(getFormattedGroupHeaderLabel(getString(R.string.wk_sun), dateFromWeekAndYear.get(DateTimeConstants.SUNDAY + "")));

        mListDataChild.put(mListDataHeader.get(0), monSessions); // Header, Child data
        mListDataChild.put(mListDataHeader.get(1), tueSessions);
        mListDataChild.put(mListDataHeader.get(2), wedSessions);
        mListDataChild.put(mListDataHeader.get(3), thuSessions);
        mListDataChild.put(mListDataHeader.get(4), friSessions);
        mListDataChild.put(mListDataHeader.get(5), satSessions);
        mListDataChild.put(mListDataHeader.get(6), sunSessions);
        dataSource.close();
    }

    public String getFormattedGroupHeaderLabel(String dayOfWeek, DateTime correspondingDate)
    {
        //necessary to find the current day
        DateTime today = new DateTime();
        if (today.dayOfMonth().getAsShortText().equals(correspondingDate.dayOfMonth().getAsShortText()) &&
                today.getMonthOfYear() == correspondingDate.getMonthOfYear() &&
                today.getYear() == correspondingDate.getYear())
            return "Today~" + correspondingDate.dayOfMonth().getAsShortText();
        else
            return dayOfWeek + "~" + correspondingDate.dayOfMonth().getAsShortText();
    }
}