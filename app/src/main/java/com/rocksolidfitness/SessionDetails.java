package com.rocksolidfitness;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Ryan on 22/11/2014.
 */
public class SessionDetails extends Fragment implements DatePickerFragment.OnCompleteListener
{
    Button mBtnSetDateOfSession;
    Button mBtnCancelSessionEdit;
    TextView tvDateOfSession;
    DateTime mSessionDate;

    public SessionDetails()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.activity_session_detail, container, false);

        mBtnSetDateOfSession = (Button) rootView.findViewById(R.id.btnSetSessDate);
        tvDateOfSession = (TextView) rootView.findViewById(R.id.textViewDateOfSession);
        mBtnCancelSessionEdit = (Button) rootView.findViewById(R.id.btnCancelSessionDetail);
        addSessDateButtonListener();
        addCloseButtonListner();

        return rootView;
    }

    void addCloseButtonListner()
    {
        mBtnCancelSessionEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getFragmentManager().popBackStack();
            }
        });
    }

    void addSessDateButtonListener()
    {
        mBtnSetDateOfSession.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DialogFragment datePickerFragment = new DatePickerFragment();
                Bundle bundle = new Bundle();
                if (tvDateOfSession.getText().length() == 0)
                {
                    mSessionDate = new DateTime();
                    bundle.putString("dateOfSession", mSessionDate.toString());
                } else
                {
                    mSessionDate = mSessionDate.minusMonths(1);
                    bundle.putString("dateOfSession", mSessionDate.toString());
                }

                datePickerFragment.setArguments(bundle);
                //datePickerFragment.show(getFragmentManager(), "datePicker");
            }
        });
    }

    @Override
    public void onComplete(DateTime sessionDate)
    {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("E d MMMM yyyy");
        mSessionDate = sessionDate;
        tvDateOfSession.setText(mSessionDate.toString(formatter));
    }
}
