package com.rocksolidfitness;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;


public class WeekViewActivity extends Activity
{

    SessionsDataSource dataSource;
    DateTime mDayOfWeek;
    double mScreenWidth;
    double mScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weekly_layout_v2);

        mScreenWidth = (Utils.getScreenWidthInPx(this) * 0.93) / 3;
        mScreenHeight = (Utils.getScreenHeightInPx(this) * 0.98) / 7;
        mDayOfWeek = Utils.getFirstDayOfWeek();
        setWeekHeaders();
        setMainContent();
    }

    void setMainContent()
    {
        dataSource = new SessionsDataSource(this);
        if (!dataSource.isOpen())
            dataSource.open();

        for (int i = 1; i <= 21; i++)
        {
            String filter = mDayOfWeek.toString("dd") + String.valueOf(mDayOfWeek.getMonthOfYear())
                    + String.valueOf(mDayOfWeek.getYear());

            List<Session> listOfSessionsForDay = dataSource.getAllSessionsForDay(filter);

            TextView dayTextView = (TextView) findViewById(getResources().getIdentifier("tv" + i, "id", getPackageName()));

            dayTextView.clearComposingText();

            ViewGroup.LayoutParams params = dayTextView.getLayoutParams();
            params.height = (int) mScreenHeight;
            params.width = (int) mScreenWidth;
            dayTextView.setLayoutParams(params);

            dayTextView.setText(String.valueOf(mDayOfWeek.getDayOfMonth()));
            if (listOfSessionsForDay != null)
            {
                for (Session session : listOfSessionsForDay)
                {
                    String currentText = dayTextView.getText().toString();

                    if (listOfSessionsForDay.size() == 1)
                        dayTextView.setText(currentText + "\n" + session.getFullSessionDescription());
                    else if (listOfSessionsForDay.size() == 2)
                        dayTextView.setText(currentText + "\n" + session.getMeduimSessionDescription());
                    else
                        dayTextView.setText(currentText + "\n" + session.getShortSessionDescription());
                }
            }

            mDayOfWeek = mDayOfWeek.plusDays(1);
        }

        dataSource.close();
    }

    void setWeekHeaders()
    {
        TextView tvFirstWeek = (TextView) findViewById(R.id.tvFirstWeek);
        TextView tvSecondWeek = (TextView) findViewById(R.id.tvSecondWeek);
        TextView tvThirdWeek = (TextView) findViewById(R.id.tvThirdWeek);
        ViewGroup.LayoutParams params = tvFirstWeek.getLayoutParams();
        params.width = (int) mScreenWidth;
        tvFirstWeek.setLayoutParams(params);
        tvFirstWeek.clearComposingText();
        String firstWeek = String.valueOf(mDayOfWeek.getWeekOfWeekyear())
                + " " + mDayOfWeek.toString("MMM");
        tvFirstWeek.setText(getString(R.string.week) + " - " + firstWeek);

        params = tvSecondWeek.getLayoutParams();
        params.width = (int) mScreenWidth;
        tvSecondWeek.setLayoutParams(params);
        tvSecondWeek.clearComposingText();
        String secondWeek = String.valueOf(mDayOfWeek.plusDays(7).getWeekOfWeekyear())
                + " " + mDayOfWeek.toString("MMM");
        tvSecondWeek.setText(getString(R.string.week) + " - " + secondWeek);

        params = tvThirdWeek.getLayoutParams();
        params.width = (int) mScreenWidth;
        tvThirdWeek.setLayoutParams(params);
        tvThirdWeek.clearComposingText();
        String thirdWeek = String.valueOf(mDayOfWeek.plusDays(14).getWeekOfWeekyear())
                + " " + mDayOfWeek.toString("MMM");
        tvThirdWeek.setText(getString(R.string.week) + " - " + thirdWeek);
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

