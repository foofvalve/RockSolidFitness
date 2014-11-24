package com.rocksolidfitness;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;

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
    Button mBtnDeleteSession;
    AutoCompleteTextView mAutoTvSessDesc;
    AutoCompleteTextView mAutoTvSport;
    TextView mTvDurationHours;
    TextView mTvDurationMinutes;
    DateTime mSessionDate;
    String mErrorMessages;
    ImageView mSessionComplete;
    EditText mTxtNotes;
    EditText mTxtDistance;

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

        mTxtNotes = (EditText) findViewById(R.id.editTextNotes);
        mTxtDistance = (EditText) findViewById(R.id.editTextDistance);
        mBtnSetDateOfSession = (Button) findViewById(R.id.btnSetSessDate);
        mBtnSaveSession = (Button) findViewById(R.id.btnSaveSession);
        mBtnCancelSessionEdit = (Button) findViewById(R.id.btnCancelSessionDetail);
        mBtnLookupSessDesc = (Button) findViewById(R.id.btnSearchSessionDetail);
        mBtnDeleteSession = (Button) findViewById(R.id.btnSessDetailDeleteSessiono);

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

        mSessionComplete = (ImageView) findViewById(R.id.imgMarkAsComplete);
        addMarksAsCompleteListener();

        if (getIntent().getExtras().getLong("SessionId") == Consts.ADD_MODE)
        {
            mSessionComplete.setBackgroundColor(Color.rgb(0, 0, 0)); //in ADD_MODE display as unticked.
            mBtnDeleteSession.setVisibility(View.INVISIBLE);
        } else
        {
            retrieveAndDisplaySession();
            mBtnDeleteSession.setVisibility(View.VISIBLE);
        }
    }

    void retrieveAndDisplaySession()
    {
        mAutoTvSessDesc.setText("Session id : " + getIntent().getExtras().getLong("SessionId"));
        long sessionId = getIntent().getExtras().getLong("SessionId");
        SessionsDataSource dataSource = new SessionsDataSource(this);
        dataSource.openReadOnly();
        Session sessionForDisplay = dataSource.getSessionById(sessionId);
        dataSource.close();

        if (sessionForDisplay.isComplete())
            mSessionComplete.setBackgroundColor(Color.rgb(47, 255, 64));
        else
            mSessionComplete.setBackgroundColor(Color.rgb(0, 0, 0));

        mAutoTvSport.setText(sessionForDisplay.sport);
        mAutoTvSessDesc.setText(sessionForDisplay.description);
        mTxtDistance.setText(Double.toString(sessionForDisplay.getDistance()));
        mTvDurationMinutes.setText(sessionForDisplay.getFormattedDurationMinute());
        mTvDurationHours.setText(sessionForDisplay.getFormattedDurationHour());
        mBtnSetDateOfSession.setText(Utils.formatDateForDisplay(sessionForDisplay.getDateOfSession()));
        mTxtNotes.setText(sessionForDisplay.notes);
    }

    void addMarksAsCompleteListener()
    {
        mSessionComplete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ColorDrawable drawable = (ColorDrawable) mSessionComplete.getBackground();
                if (drawable.getColor() == Color.rgb(47, 255, 64))
                    mSessionComplete.setBackgroundColor(Color.rgb(0, 0, 0));
                else
                    mSessionComplete.setBackgroundColor(Color.rgb(47, 255, 64));
            }
        });
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

                    if (getIntent().getExtras().getLong("SessionId") == Consts.ADD_MODE)
                        persistNewSession();
                    else
                        persistUpdatedSession();

                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                    // --> notify dataset change !!! here.
                }
            }
        });
    }

    void persistNewSession()
    {
        SessionsDataSource dataSource = new SessionsDataSource(this);
        dataSource.open();
        Session newSession = new Session(Session.State.PLANNED, "Cycling", "big bike", 257, Utils.getDateOffsetByNDays(0));

        dataSource.createSession(newSession);
        dataSource.close();
    }

    void persistUpdatedSession()
    {
        SessionsDataSource dataSource = new SessionsDataSource(this);
        dataSource.open();
        long recordId = getIntent().getExtras().getLong("SessionId");
        Session sessionUpdated = dataSource.getSessionById(recordId);
        sessionUpdated.description = mAutoTvSessDesc.getText().toString();

        //TODO.. fill other fields and shit

        dataSource.updateSession(sessionUpdated);
        dataSource.close();
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
        mSessionDate = sessionDate;
        mBtnSetDateOfSession.setText(Utils.formatDateForDisplay(mSessionDate));
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
