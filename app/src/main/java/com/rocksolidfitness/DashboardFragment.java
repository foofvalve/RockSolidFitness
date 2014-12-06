package com.rocksolidfitness;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.List;


public class DashboardFragment extends Fragment implements ImpExpManager.AsyncResponse
{
    View mView;
    double mCellWidth;
    Intent i;

    String mimeType = "text/html";
    String encoding = "utf-8";

    public DashboardFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mCellWidth = (Utils.getScreenWidthInPx(getActivity()) * 0.95) / 3;

        initButton("btnDashBoardAdd");
        initButton("btnDashBoardReport");
        initButton("btnDashboardPlan");
        initButton("btnDashboardImpExp");
        initButton("btnDashboardDownloadPrograms");
        initButton("btnDashboardWeekView");

        WebView webLeftView = (WebView) mView.findViewById(R.id.webViewLeftPane);
        webLeftView.loadData(generateHtmlLeftPane(), mimeType, encoding);

        WebView webRightView = (WebView) mView.findViewById(R.id.webViewRightPane);
        webRightView.loadData(generateHtmlRightPane(), mimeType, encoding);

        return mView;
    }


    private String generateHtmlRightPane()
    {
        String htmlRightPane;

        SessionsDataSource dataSource = new SessionsDataSource(getActivity());
        dataSource.open();
        DateTime now = new DateTime();
        String filter = now.toString("dd") + String.valueOf(now.getMonthOfYear())
                + String.valueOf(now.getYear());
        List<Session> sessionsForToday = dataSource.getAllSessionsForDay(filter);
        dataSource.close();

        if (sessionsForToday == null)
        {
            htmlRightPane = "<h4>Today</h4>"
                    + "<div>No workouts planned for today</div>";
        } else
        {
            htmlRightPane = "<h4>Today</h4>";
            for (Session s : sessionsForToday)
                htmlRightPane += s.toHtmlSnippet();
        }

        return htmlRightPane;
    }

    private String generateHtmlLeftPane()
    {
        String htmlLeftPane;

        SessionsDataSource dataSource = new SessionsDataSource(getActivity());
        dataSource.open();
        DateTime now = new DateTime();
        String totalDuration = dataSource.getTotalDurationForCurrentWeek();
        dataSource.close();

        int currentWeek = new DateTime().getWeekOfWeekyear();
        if (totalDuration.equals(""))
        {
            htmlLeftPane = "<h4>Summary Week " + currentWeek + "</h4>"
                    + "<div></div>";
        } else
        {
            //TODO: SOftify this...
            htmlLeftPane = "<h4>Summary Week " + currentWeek + "</h4>"
                    + "<div><b>Bike&nbsp;&nbsp;</b><small>3:15&nbsp;&nbsp;</small><small>150 KM</small></div>"
                    + "<div><b>Run&nbsp;&nbsp;</b><small>1:25&nbsp;&nbsp;</small><small>50 KM</small></div>"
                    + "<h2>88 %</h2>"
                    + "<div>Complete 5 out of 8<div>";
        }

        return htmlLeftPane;
    }

    public void initButton(String buttonId)   //resize the buttons relative to the screenwidth
    {
        String packageName = this.getActivity().getApplicationContext().getPackageName();
        Button button = (Button) mView.findViewById(this.getActivity().getResources().getIdentifier(buttonId, "id", packageName));
        button.setWidth((int) mCellWidth);
        button.setHeight((int) mCellWidth);


        if (buttonId.equals("btnDashBoardAdd"))
        {
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    i = new Intent(getActivity(), SessionDetails.class);
                    i.putExtra("SessionId", Consts.ADD_MODE);
                    i.putExtra("DesiredDateOfSession", new DateTime().getMillis());
                    startActivityForResult(i, 1);
                }
            });
        } else if (buttonId.equals("btnDashBoardReport"))
        {
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    i = new Intent(getActivity(), ListViewMultiChartActivity.class);
                    startActivity(i);
                }
            });
        } else if (buttonId.equals("btnDashboardPlan"))
        {
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    MainViewFragment newFragment = new MainViewFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    transaction.add(R.id.container, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        } else if (buttonId.equals("btnDashboardImpExp"))
        {
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    DialogFragment newFragment = ExportSessDialogFragment.newInstance();
                    newFragment.show(getFragmentManager(), "dialog");
                }
            });
        } else if (buttonId.equals("btnDashboardWeekView"))
        {
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    i = new Intent(getActivity(), WeekViewActivity.class);
                    startActivityForResult(i, 1);
                }
            });
        }
    }

    @Override
    public void processFinish(boolean passed)
    {
        if (passed)
            Toast.makeText(getActivity(), "Completed export to SD Card", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getActivity(), "Something bad happened- export failed", Toast.LENGTH_SHORT).show();
    }

    public static class ExportSessDialogFragment extends DialogFragment
    {

        public static ExportSessDialogFragment newInstance()
        {
            return new ExportSessDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.export_type_title)
                    .setItems(R.array.export_type, new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch (which)
                            {
                                case Consts.EXPORT_CSV:
                                    doExportCsv();
                                    break;
                                case Consts.EXPORT_EXCEL:
                                    doExportExcel();
                                    break;
                                case Consts.EXPORT_XML:
                                    doExportXML();
                                    break;
                            }
                        }
                    });


            return builder.create();
        }

        void doExportCsv()
        {
            ImpExpManager impExpManage = new ImpExpManager(Consts.EXPORT_CSV);
            impExpManage.delegate = (MainActivity) getActivity();
            impExpManage.execute(getActivity());
        }

        void doExportExcel()
        {
            ImpExpManager impExpManage = new ImpExpManager(Consts.EXPORT_EXCEL);
            impExpManage.delegate = (MainActivity) getActivity();
            impExpManage.execute(getActivity());
        }

        void doExportXML()
        {
            ImpExpManager impExpManage = new ImpExpManager(Consts.EXPORT_XML);
            impExpManage.delegate = (MainActivity) getActivity();
            impExpManage.execute(getActivity());
        }
    }
}
