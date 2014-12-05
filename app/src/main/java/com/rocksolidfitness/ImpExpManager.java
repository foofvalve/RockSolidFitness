package com.rocksolidfitness;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class ImpExpManager extends AsyncTask<Context, Integer, Boolean>
{
    public AsyncResponse delegate = null;
    Context mContext;

    @Override
    protected Boolean doInBackground(Context... context)
    {
        try
        {
            mContext = context[0];
            SessionsDataSource dataSource = new SessionsDataSource(mContext);
            dataSource.open();
            dataSource.loadDynamicTestData();
            List<Session> listOfAllSessions = dataSource.getAllSessions();
            dataSource.close();

            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/rocksolidfitnessexport");

            dir.mkdirs();
            DateTimeFormatter isoFormat = DateTimeFormat.forPattern("yyyy-MM-dd HHmm");

            File wbfile = new File(dir, "workout_export_" + new DateTime().toString(isoFormat) + ".csv");

            FileWriter writer = new FileWriter(wbfile);
            writer.append(Session.getCSVHeaders());
            int countExported = 1;
            int numSessionsForExport = listOfAllSessions.size();
            for (Session s : listOfAllSessions)
            {
                publishProgress((int) ((countExported / (float) numSessionsForExport) * 100));
                Log.d("", s.toCSV());
                writer.append(s.toCSV());
                countExported++;
            }
            writer.flush();
            writer.close();
            return true;
        } catch (Exception e)
        {
            Log.e("ImpExpManager.exportAllSessions", e.getMessage());
            return false;
        }
    }

    protected void onProgressUpdate(Integer... progress)
    {
        Log.i("", "Progress:" + progress[0]);
    }

    protected void onPostExecute(boolean result)
    {
        delegate.processFinish(result);
    }

    public interface AsyncResponse
    {
        void processFinish(boolean output);
    }
}
