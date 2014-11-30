package com.rocksolidfitness;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;


public class WeekViewActivity extends Activity
{

    SessionsDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weekly_layout_v2);

        DateTime dayOfWeek = Utils.getFirstDayOfWeek();

        dataSource = new SessionsDataSource(this);
        if (!dataSource.isOpen())
            dataSource.open();

        for (int i = 1; i <= 21; i++)
        {
            String filter = String.valueOf(dayOfWeek.dayOfMonth().get()) + String.valueOf(dayOfWeek.getMonthOfYear())
                    + String.valueOf(dayOfWeek.getYear());

            List<Session> listOfSessionsForDay = dataSource.getAllSessionsForDay(filter);

            TextView dayTextView = (TextView) findViewById(getResources().getIdentifier("tv" + i, "id", getPackageName()));
            //TextView dayTextView = (TextView)findViewById(R.id.tv1);
            Log.d("", "finding :: tv" + i);
            dayTextView.clearComposingText();
            dayTextView.setText(String.valueOf(dayOfWeek.getDayOfMonth()));
            if (listOfSessionsForDay != null)
            {
                for (Session session : listOfSessionsForDay)
                {
                    String currentText = dayTextView.getText().toString();
                    dayTextView.setText(currentText + "\n" + session.getFullSessionDescription());
                }
            }

            dayOfWeek = dayOfWeek.plusDays(1);
        }

        dataSource.close();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (dataSource != null)
        {
            dataSource.close();
        }
    }
}
