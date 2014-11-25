package com.rocksolidfitness;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ryan on 25/11/2014.
 */
public class GridviewAdapter extends BaseAdapter
{
    private ArrayList<DailySnapshot> listDailySnapshots;
    private Activity activity;

    public GridviewAdapter(Activity activity, ArrayList<DailySnapshot> listDailySnapshots)
    {
        super();
        this.listDailySnapshots = listDailySnapshots;
        this.activity = activity;
    }

    @Override
    public int getCount()
    {
        return listDailySnapshots.size();
    }

    @Override
    public Object getItem(int position)
    {
        return listDailySnapshots.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder view;
        LayoutInflater inflator = activity.getLayoutInflater();

        if (convertView == null)
        {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.gridview_row, null);

            view.txtViewTitle = (TextView) convertView.findViewById(R.id.textViewGridCell);
            convertView.setTag(view);
        } else
        {
            view = (ViewHolder) convertView.getTag();
        }

        String txtToDisplay = "";
        DailySnapshot dailySnapshot = listDailySnapshots.get(position);
        if (dailySnapshot.isIsPlaceholder())
            txtToDisplay = dailySnapshot.mBelongsToWeek + "";
        else if (dailySnapshot.hasNoSessions())
            txtToDisplay = "__";
        else if (dailySnapshot.hasOneSession())
            txtToDisplay = dailySnapshot.mSessionsForDay.get(0).sport.charAt(0) + "";
        else
        {
            for (Session session : dailySnapshot.mSessionsForDay)
                txtToDisplay += session.sport.charAt(0) + "\n";
        }

        view.txtViewTitle.setText(txtToDisplay);
        return convertView;
    }

    public static class ViewHolder
    {
        public TextView txtViewTitle;
    }
}
