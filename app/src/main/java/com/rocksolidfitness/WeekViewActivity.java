package com.rocksolidfitness;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.List;


public class WeekViewActivity extends Activity implements SwipeInterface
{

    SessionsDataSource dataSource;
    DateTime mDayOfWeek;
    double mCellWidth;
    double mCellHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weekly_layout_v2);

        ActivitySwipeDetector swipe = new ActivitySwipeDetector(this, WeekViewActivity.this);
        LinearLayout swipe_layout = (LinearLayout) findViewById(R.id.weeklyLayoutMainContainer);
        swipe_layout.setOnTouchListener(swipe);

        mCellWidth = (Utils.getScreenWidthInPx(this) * 0.93) / 3;
        mCellHeight = (Utils.getScreenHeightInPx(this) * 0.98) / 7;
        mDayOfWeek = Utils.getFirstDayOfWeek();
        setWeekHeaders();
        setMainContent();
    }

    @Override
    public void onLeftToRight(View v)
    {
        mDayOfWeek = mDayOfWeek.minusDays(7);
        setWeekHeaders();
        setMainContent();
    }

    @Override
    public void onRightToLeft(View v)
    {
        mDayOfWeek = mDayOfWeek.plusDays(7);
        setWeekHeaders();
        setMainContent();
    }

    @SuppressLint("NewApi")
    void setMainContent()
    {
        dataSource = new SessionsDataSource(this);
        if (!dataSource.isOpen())
            dataSource.open();

        ViewGroup vg = (ViewGroup) findViewById(R.id.weeklyLayoutMainContainer);
        vg.invalidate();  //force repaint the view

        DateTime dateRange = mDayOfWeek;
        for (int i = 1; i <= 21; i++)
        {
            String filter = dateRange.toString("dd") + String.valueOf(dateRange.getMonthOfYear())
                    + String.valueOf(dateRange.getYear());

            List<Session> listOfSessionsForDay = dataSource.getAllSessionsForDay(filter);

            TextView dayTextView = (TextView) findViewById(getResources().getIdentifier("tv" + i, "id", getPackageName()));

            dayTextView.clearComposingText();
            dayTextView.setText("");
            ViewGroup.LayoutParams params = dayTextView.getLayoutParams();
            params.height = (int) mCellHeight;
            params.width = (int) mCellWidth;
            dayTextView.setLayoutParams(params);

            int bgColor;
            if (dateRange.getWeekOfWeekyear() == new DateTime().getWeekOfWeekyear())  //highlight the current week
                bgColor = Color.parseColor("#d4d446");
            else
                bgColor = Color.parseColor("#eaeaea");

            String lableDayOfMonth = String.valueOf(dateRange.getDayOfMonth());
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN)
            {
                dayTextView.setBackgroundDrawable(writeOnDrawable(bgColor, lableDayOfMonth));
            } else
            {
                dayTextView.setBackground(writeOnDrawable(bgColor, lableDayOfMonth));
            }

            if (listOfSessionsForDay != null)
            {
                for (Session session : listOfSessionsForDay)
                {
                    String currentText = dayTextView.getText().toString();

                    if (listOfSessionsForDay.size() == 1)
                        dayTextView.setText(currentText + session.getFullSessionDescription() + "\n");
                    else if (listOfSessionsForDay.size() == 2)
                        dayTextView.setText(currentText + session.getMeduimSessionDescription() + "\n");
                    else
                        dayTextView.setText(currentText + session.getShortSessionDescription() + "\n");
                }
            }

            dateRange = dateRange.plusDays(1);
        }
        if (dataSource != null && dataSource.isOpen())
            dataSource.close();
    }

    void setWeekHeaders()
    {
        TextView tvFirstWeek = (TextView) findViewById(R.id.tvFirstWeek);
        TextView tvSecondWeek = (TextView) findViewById(R.id.tvSecondWeek);
        TextView tvThirdWeek = (TextView) findViewById(R.id.tvThirdWeek);
        ViewGroup.LayoutParams params = tvFirstWeek.getLayoutParams();
        params.width = (int) mCellWidth;
        tvFirstWeek.setLayoutParams(params);
        tvFirstWeek.clearComposingText();

        String firstWeek = String.valueOf(mDayOfWeek.getWeekOfWeekyear())
                + " " + mDayOfWeek.toString("MMM");
        tvFirstWeek.setText(getString(R.string.week) + " - " + firstWeek);

        params = tvSecondWeek.getLayoutParams();
        params.width = (int) mCellWidth;
        tvSecondWeek.setLayoutParams(params);
        tvSecondWeek.clearComposingText();

        String secondWeek;
        secondWeek = String.valueOf(mDayOfWeek.plusDays(7).getWeekOfWeekyear())
                + " " + mDayOfWeek.plusDays(7).toString("MMM");

        tvSecondWeek.setText(getString(R.string.week) + " - " + secondWeek);

        params = tvThirdWeek.getLayoutParams();
        params.width = (int) mCellWidth;
        tvThirdWeek.setLayoutParams(params);
        tvThirdWeek.clearComposingText();

        String thirdWeek;
        thirdWeek = String.valueOf(mDayOfWeek.plusDays(14).getWeekOfWeekyear())
                + " " + mDayOfWeek.plusDays(14).toString("MMM");

        tvThirdWeek.setText(getString(R.string.week) + " - " + thirdWeek);
    }

    public BitmapDrawable writeOnDrawable(int color, String text)
    {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bm = Bitmap.createBitmap((int) mCellWidth, (int) mCellHeight, conf);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(Utils.convertDpToPixel(24, this));

        Canvas canvas = new Canvas(bm);
        canvas.drawColor(color);
        float relativePos = Utils.convertDpToPixel(28, this);
        canvas.drawText(text, bm.getWidth() - relativePos, bm.getHeight() - relativePos, paint);

        return new BitmapDrawable(getResources(), bm);
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

