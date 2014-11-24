package com.rocksolidfitness;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Ryan on 22/11/2014.
 */
public class SessionDetails extends Activity
        implements DatePickerFragment.OnCompleteListener,
        SessionDescListFragment.OnCompleteListener,
        SportsListFragment.OnCompleteListener
{
    Button mBtnSetDateOfSession;
    Button mBtnCancelSessionEdit;
    Button mBtnSearchForSport;
    Button mBtnSaveSession;
    Button mBtnLookupSessDesc;
    AutoCompleteTextView mAutoTvSessDesc;
    AutoCompleteTextView mAutoTvSport;
    TextView mTvDurationHours;
    TextView mTvDurationMinutes;
    DateTime mSessionDate;
    String mErrorMessages;


    public SessionDetails()
    {
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);

        SessionsDataSource dataSource = new SessionsDataSource(this);
        dataSource.openReadOnly();
        String[] autoCompleteListOfSessDesc = dataSource.getSessDescSpinner("");
        String[] autoCompleteListOfSports = dataSource.getSportsForSpinner();
        dataSource.close();

        mBtnSetDateOfSession = (Button) findViewById(R.id.btnSetSessDate);
        mBtnSaveSession = (Button) findViewById(R.id.btnSaveSession);
        mBtnCancelSessionEdit = (Button) findViewById(R.id.btnCancelSessionDetail);
        mBtnLookupSessDesc = (Button) findViewById(R.id.btnSearchSessionDetail);

        mTvDurationHours = (TextView) findViewById(R.id.editTextDurationHours);
        mTvDurationMinutes = (TextView) findViewById(R.id.editTextDurationMins);

        mAutoTvSessDesc = (AutoCompleteTextView) findViewById(R.id.editTextDescription);
        if (autoCompleteListOfSessDesc != null && autoCompleteListOfSports.length > 0)  //attach autocomplete entries
        {
            ArrayAdapter sessDescAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, autoCompleteListOfSessDesc);
            mAutoTvSessDesc.setAdapter(sessDescAdapter);
        }

        mAutoTvSport = (AutoCompleteTextView) findViewById(R.id.editTextSport);
        if (autoCompleteListOfSports != null && autoCompleteListOfSports.length > 0)
        {
            ArrayAdapter sportsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, autoCompleteListOfSports);
            mAutoTvSport.setAdapter(sportsAdapter);
        }

        mBtnSearchForSport = (Button) findViewById(R.id.btnSearchForSport);
        addSessDateButtonListener();
        addCloseButtonListner();
        addLookupSessDescBtnListener();
        addLookupSportBtnListener();
        addSaveBtnListener();
    }

    void addSaveBtnListener()
    {
        mBtnSaveSession.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                /*
                FragmentManager fm = getFragmentManager();

                if (fm.findFragmentById(android.R.id.content) == null)
                {
                    SportsListFragment sportListFragment = new SportsListFragment();
                    FragmentTransaction transaction = fm.beginTransaction();

                    transaction.addToBackStack(null);
                    transaction.add(android.R.id.content, sportListFragment);
                    transaction.commit();
                } */

                if (!isFormValid())
                    displayErrorMessages();
                else
                {
                    //persist session to db


                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED, returnIntent);
                    finish();
                    // --> notify dataset change !!! here.
                }
            }
        });
    }

    boolean isFormValid()
    {
        mErrorMessages = "";
        if (mAutoTvSport.getText().toString().length() == 0)
            mErrorMessages = getString(R.string.validation_sport_error) + "\n";

        if (mAutoTvSessDesc.getText().toString().length() == 0)
            mErrorMessages = mErrorMessages + getString(R.string.validation_description_error) + "\n";

        int durationHoursLength = mTvDurationHours.getText().toString().length();
        int durationMinutesLength = mTvDurationMinutes.getText().toString().length();

        if (durationHoursLength == 0 && durationMinutesLength == 0)
        {
            if (durationHoursLength == 0)
                mErrorMessages = mErrorMessages + getString(R.string.validation_duration_hours_error) + "\n";

            if (durationMinutesLength == 0)
                mErrorMessages = mErrorMessages + getString(R.string.validation_duration_minutes_error) + "\n";
        }
        return mErrorMessages.equals("") ? true : false;
    }

    void displayErrorMessages()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(getString(R.string.validation_errors_title));

        alertDialogBuilder
                .setMessage(mErrorMessages)
                .setCancelable(false)
                .setNeutralButton(getString(R.string.validation_button_ok), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    void addLookupSportBtnListener()
    {
        mBtnSearchForSport.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentManager fm = getFragmentManager();

                if (fm.findFragmentById(android.R.id.content) == null)
                {
                    SportsListFragment sportListFragment = new SportsListFragment();
                    FragmentTransaction transaction = fm.beginTransaction();

                    transaction.addToBackStack(null);
                    transaction.add(android.R.id.content, sportListFragment);
                    transaction.commit();
                }
            }
        });
    }


    void addLookupSessDescBtnListener()
    {
        mBtnLookupSessDesc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentManager fm = getFragmentManager();

                if (fm.findFragmentById(android.R.id.content) == null)
                {
                    SessionDescListFragment sessionDescListFragment = new SessionDescListFragment();
                    FragmentTransaction transaction = fm.beginTransaction();

                    Bundle bundle = new Bundle();
                    String sportSelected = mAutoTvSport.getText().toString();
                    if (sportSelected.equals(getString(R.string.sess_details_add_new)))
                        bundle.putString("filterBySport", "");
                    else
                        bundle.putString("filterBySport", sportSelected);

                    sessionDescListFragment.setArguments(bundle);
                    transaction.addToBackStack(null);
                    transaction.add(android.R.id.content, sessionDescListFragment);
                    transaction.commit();
                }
            }
        });
    }

    void addCloseButtonListner()
    {
        mBtnCancelSessionEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
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
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                Bundle bundle = new Bundle();
                if (mBtnSetDateOfSession.getText().equals("Set Date"))
                    mSessionDate = new DateTime().minusMonths(1);
                else
                    mSessionDate = mSessionDate.minusMonths(1);

                bundle.putString("dateOfSession", mSessionDate.toString());
                datePickerFragment.setArguments(bundle);
                datePickerFragment.show(getFragmentManager(), "datePicker");
            }
        });
    }

    @Override
    public void onComplete(DateTime sessionDate)
    {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("E d MMMM yyyy");
        mSessionDate = sessionDate;
        mBtnSetDateOfSession.setText(mSessionDate.toString(formatter));
    }


    @Override
    public void onComplete(String itemSelected)
    {
        mAutoTvSessDesc.setText(itemSelected);
    }


    @Override
    public void onSportSelected(String sportSelected)
    {
        mAutoTvSport.setText(sportSelected);
    }
}
