package com.rocksolidfitness;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;


public class WeekViewActivity extends Activity
{
    private ArrayList<DailySnapshot> listDailySnapshots;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weekly_layout);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setDomStorageEnabled(true);
        String customHtml = "<html><head><link href=\"bootstrap.min.css\" rel=\"stylesheet\" type=\"text/css\"/></head><body><h2>Greetings from JavaCodeGeeks</h2>\t<table class=\"table\">\n" +
                "\t\t<tr>\n" +
                "\t\t\t<th>Week 12</th>\n" +
                "\t\t\t<th>Week 13</th>\n" +
                "\t\t\t<th>Week 14</th>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t<td>asdfasf</td>\n" +
                "\t\t<td>asdfasf</td>\n" +
                "\t\t<td>asdfas</td>\n" +
                "\t\t</tr>\t\n" +
                "\t</table>\n</body></html>";
        //webView.loadData(customHtml, "text/html", "UTF-8");

        webView.loadDataWithBaseURL("file:///android_asset/", customHtml, "text/html", "UTF-8", "");
    }

    public void prepareList()
    {
        listDailySnapshots = new ArrayList<DailySnapshot>();

        SessionsDataSource dataSource = new SessionsDataSource(this);
        dataSource.openReadOnly();
        //...fix this shit
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
