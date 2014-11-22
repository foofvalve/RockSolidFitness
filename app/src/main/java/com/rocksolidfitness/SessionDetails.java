package com.rocksolidfitness;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Ryan on 22/11/2014.
 */
public class SessionDetails extends FragmentActivity implements DatePickerFragment.OnCompleteListener
{
    Button mSetDateOfSession;
    TextView tvDateOfSession;
    DateTime mSessionDate;

    public SessionDetails()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);
        mSetDateOfSession = (Button) findViewById(R.id.btnSetSessDate);
        tvDateOfSession = (TextView) findViewById(R.id.textViewDateOfSession);
        addSessDateButtonListener();
    }


    void addSessDateButtonListener()
    {
        mSetDateOfSession.setOnClickListener(new View.OnClickListener()
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
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
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
