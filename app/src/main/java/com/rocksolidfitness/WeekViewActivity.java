package com.rocksolidfitness;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;


public class WeekViewActivity extends Activity
{
    private ArrayList<DailySnapshot> listDailySnapshots;
    private GridviewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.weekly_layout);

        prepareList();
        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setNumColumns(8);
        mAdapter = new GridviewAdapter(this, listDailySnapshots);
        gridView.setAdapter(mAdapter);
    }

    public void prepareList()
    {
        listDailySnapshots = new ArrayList<DailySnapshot>();

        SessionsDataSource dataSource = new SessionsDataSource(this);
        dataSource.openReadOnly();
        ...fix this shit
        for (int week = 46; week <= 49; week++)
        {
            listDailySnapshots.add(new DailySnapshot(week).flagAsPlaceholder());

            List<Session> sessionsForWeek = dataSource.getAllSessionsForWeek(week, 2014);
            for (int dayOfWeek = 1; dayOfWeek <= 7; dayOfWeek++)
            {
                DailySnapshot dailySnapshot = new DailySnapshot(week);
                dailySnapshot.mBelongsToDayOfWeek = dayOfWeek;
                for (Session session : sessionsForWeek)
                {
                    if (session.getDateOfSession().getDayOfWeek() == dayOfWeek)
                        dailySnapshot.addSession(session);
                }
                listDailySnapshots.add(dailySnapshot);
            }
        }

        dataSource.close();
    }
}
