package com.rocksolidfitness;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ListViewMultiChartActivity extends ChartBase
{
    List<String> mListOfLoggedSports;


    //DISTANCE:
    //group by week+year [DISTANCE]  >> Grand Totals per week
    //select sessionweek,sessionyear, round((sum(distance)*1.0)/60,2) from sessions group by 1,2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_listview_chart);
        populateSportList();
        ListView lv = (ListView) findViewById(R.id.listView1);

        ArrayList<ChartItem> list = new ArrayList<ChartItem>();
        list.add(new LineChartItem(generateTotDurationDataLine(), getApplicationContext()));

        for (String sport : mListOfLoggedSports)
        {
            list.add(new LineChartItem(generateSportDurationDataLine(sport, SessionColumns.DURATION), getApplicationContext()));
            list.add(new LineChartItem(generateSportDurationDataLine(sport, SessionColumns.DISTANCE), getApplicationContext()));
        }

        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(cda);
    }

    void populateSportList()
    {
        SessionsDataSource dataSource = new SessionsDataSource(this);
        dataSource.open();
        mListOfLoggedSports = dataSource.getUniqueSports();
        dataSource.close();
    }

    private LineData generateTotDurationDataLine()
    {
        SessionsDataSource dataSource = new SessionsDataSource(this);
        dataSource.open();
        LinkedHashMap<String, Float> resultSet = dataSource.getDurationPerWeek();
        dataSource.close();
        ArrayList<String> wkTicks = new ArrayList<String>();
        ArrayList<Entry> e1 = new ArrayList<Entry>();

        int index = 0;
        for (Map.Entry<String, Float> entry : resultSet.entrySet())
        {
            wkTicks.add(entry.getKey().toString());
            e1.add(new Entry(Float.parseFloat(String.valueOf(entry.getValue())), index));
            index++;
        }

        LineDataSet d1 = new LineDataSet(e1, "Total Duration(Hrs)/Week ");
        d1.setLineWidth(4f);
        d1.setCircleSize(5f);
        d1.setHighLightColor(Color.rgb(200, 100, 50));

        ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
        sets.add(d1);

        LineData cd = new LineData(wkTicks, sets);
        return cd;
    }

    private LineData generateSportDurationDataLine(String sport, String columnToSum)
    {
        SessionsDataSource dataSource = new SessionsDataSource(this);
        dataSource.open();
        LinkedHashMap<String, Float> resultSet = dataSource.getTotalsPerWeekPerSport(sport, columnToSum);
        dataSource.close();
        ArrayList<String> wkTicks = new ArrayList<String>();
        ArrayList<Entry> e1 = new ArrayList<Entry>();

        int index = 0;
        for (Map.Entry<String, Float> entry : resultSet.entrySet())
        {
            wkTicks.add(entry.getKey().toString());
            e1.add(new Entry(Float.parseFloat(String.valueOf(entry.getValue())), index));
            index++;
        }

        String graphLabel;

        if (columnToSum.equals(SessionColumns.DURATION))
            graphLabel = sport + " Total " + columnToSum + " (Hrs)/Week ";
        else
        {
            SharedPreferences settings = getSharedPreferences(Consts.PREFS_NAME, 0);
            graphLabel = sport + " Total " + columnToSum + " (" + settings.getString("uom", "KM") + ")/Week ";
        }


        LineDataSet d1 = new LineDataSet(e1, graphLabel);
        d1.setLineWidth(4f);
        d1.setCircleSize(5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));

        if (columnToSum.equals(SessionColumns.DISTANCE))
        {
            d1.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
            d1.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        }

        ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
        sets.add(d1);

        LineData cd = new LineData(wkTicks, sets);
        return cd;
    }


    /**
     * adapter that supports 3 different item types
     */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem>
    {

        public ChartDataAdapter(Context context, List<ChartItem> objects)
        {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position)
        {
            // return the views type
            return getItem(position).getItemType();
        }

        @Override
        public int getViewTypeCount()
        {
            return 3; // we have 3 different item-types
        }
    }
}
