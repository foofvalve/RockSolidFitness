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
    private final int mExportType;
    public AsyncResponse delegate = null;
    Context mContext;
    DateTimeFormatter isoFormat;
    File dir;

    public ImpExpManager(int exportType)
    {
        mExportType = exportType;
    }

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
            dir = new File(sdCard.getAbsolutePath() + "/rocksolidfitnessexport");

            dir.mkdirs();
            isoFormat = DateTimeFormat.forPattern("yyyy-MM-dd HHmm");

            switch (mExportType)
            {
                case Consts.EXPORT_CSV:
                    exportToCSV(listOfAllSessions);
                    break;
                case Consts.EXPORT_EXCEL:
                    exportToExcel(listOfAllSessions);
                    break;
                case Consts.EXPORT_XML:
                    exportToXML(listOfAllSessions);
                    break;
            }

            return true;
        } catch (Exception e)
        {
            Log.e("ImpExpManager.exportAllSessions", e.getMessage());
            return false;
        }
    }

    void exportToExcel(List<Session> listOfAllSessions)
    {
        throw new UnsupportedOperationException();
    }

    void exportToCSV(List<Session> listOfAllSessions)
    {
        File wbfile = new File(dir, "workout_export_" + new DateTime().toString(isoFormat) + ".csv");

        try
        {
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
        } catch (Exception e)
        {
            Log.e("ImpExpManager.exportToCSV", e.getMessage());
        }
    }

    void exportToXML(List<Session> listOfAllSessions)
    {
        File wbfile = new File(dir, "workout_export_" + new DateTime().toString(isoFormat) + ".xml");

        try
        {
            FileWriter writer = new FileWriter(wbfile);
            writer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            writer.append("<sessions>\n");
            int countExported = 1;
            int numSessionsForExport = listOfAllSessions.size();
            for (Session s : listOfAllSessions)
            {
                publishProgress((int) ((countExported / (float) numSessionsForExport) * 100));
                Log.d("", s.toXML());
                writer.append(s.toXML());
                countExported++;
            }
            writer.append("</sessions>");
            writer.flush();
            writer.close();
        } catch (Exception e)
        {
            Log.e("ImpExpManager.exportToCSV", e.getMessage());
        }
    }


    protected void onProgressUpdate(Integer... progress)
    {
        Log.i("", "Progress:" + progress[0]);
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        delegate.processFinish(result);
    }

    public interface AsyncResponse
    {
        void processFinish(boolean output);
    }
}
