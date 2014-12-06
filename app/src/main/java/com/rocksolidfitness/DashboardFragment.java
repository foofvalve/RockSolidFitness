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
import android.widget.Button;
import android.widget.Toast;

import org.joda.time.DateTime;


public class DashboardFragment extends Fragment implements ImpExpManager.AsyncResponse
{
    private static final int ALERT_DIALOG1 = 1;
    View mView;
    double mCellWidth;
    Intent i;

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


        return mView;
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
