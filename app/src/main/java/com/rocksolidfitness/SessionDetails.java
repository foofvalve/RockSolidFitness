package com.rocksolidfitness;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

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
    TextView mTvPrepopForm;
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
        addDeleteSessListener();

        //debug only!!!
        mTvPrepopForm = (TextView) findViewById(R.id.textViewSessDone);
        addDebugPrefillForm();

        mSessionComplete = (ImageView) findViewById(R.id.imgMarkAsComplete);
        addMarksAsCompleteListener();

        if (getIntent().getExtras().getLong("SessionId") == Consts.ADD_MODE)
        {
            mSessionComplete.setBackgroundColor(Color.rgb(0, 0, 0)); //in ADD_MODE display as unticked.
            mBtnDeleteSession.setVisibility(View.INVISIBLE);

            long desiredDateOfSessMillis = getIntent().getExtras().getLong("DesiredDateOfSession");
            mSessionDate = new DateTime(desiredDateOfSessMillis);
            mBtnSetDateOfSession.setText(Utils.formatDateForDisplay(mSessionDate));
        } else
        {
            retrieveAndDisplaySession();
            mBtnDeleteSession.setVisibility(View.VISIBLE);
        }

    }

    //TEsting
    void addDebugPrefillForm()
    {
        mTvPrepopForm.setOnLongClickListener(new View.OnLongClickListener()
        {
            public boolean onLongClick(View v)
            {
                mTvDurationMinutes.setText("11");
                mTxtNotes.setText("some notes blah blah \n blah blah ... stuff");
                mTvDurationHours.setText("1");
                mAutoTvSessDesc.setText("Really Long Run");
                mAutoTvSport.setText("Running");
                mTxtDistance.setText("50");
                return true;
            }
        });
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
        mSessionDate = sessionForDisplay.getDateOfSession();
        mTxtNotes.setText(sessionForDisplay.notes);
    }

    void addDeleteSessListener()
    {
        mBtnDeleteSession.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());

                alertDialogBuilder.setTitle(getString(R.string.confirm_delete_title));

                alertDialogBuilder
                        .setMessage(getString(R.string.confirm_delete_msg))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.confirm_delete_yes), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                deleteSession();
                                saveSessionDateToPrefs();
                                Intent returnIntent = new Intent();
                                setResult(RESULT_OK, returnIntent);
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.confirm_delete_no), new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
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
                if (!isFormValid())
                    displayErrorMessages();
                else
                {
                    if (getIntent().getExtras().getLong("SessionId") == Consts.ADD_MODE)
                        persistNewSession();
                    else
                        persistUpdatedSession();

                    saveSessionDateToPrefs();
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    void saveSessionDateToPrefs()
    {
        SharedPreferences settings = getSharedPreferences(Consts.PREFS_NAME, 0);
        SharedPreferences.Editor edit = settings.edit();
        edit.clear();
        edit.putLong("global_dashboard_date", mSessionDate.getMillis());
        edit.commit();
    }

    void deleteSession()
    {
        SessionsDataSource dataSource = new SessionsDataSource(this);
        long sessionId = getIntent().getExtras().getLong("SessionId");
        dataSource.open();
        Session sessionForDeletion = dataSource.getSessionById(sessionId);
        mSessionDate = sessionForDeletion.getDateOfSession();
        dataSource.deleteSession(sessionForDeletion);
        dataSource.close();
        Toast.makeText(this, "Session deleted", Toast.LENGTH_SHORT).show();
    }

    void persistNewSession()
    {
        SessionsDataSource dataSource = new SessionsDataSource(this);
        dataSource.open();

        Session.State initialState;
        ColorDrawable drawable = (ColorDrawable) mSessionComplete.getBackground();
        if (drawable.getColor() == Color.rgb(47, 255, 64))
            initialState = Session.State.COMPLETE;
        else
            initialState = Session.State.PLANNED;

        Session newSession = new Session(initialState,
                mAutoTvSport.getText().toString(),
                mAutoTvSessDesc.getText().toString(),
                getTotalDurationFromUi(),
                mSessionDate);

        newSession.notes = mTxtNotes.getText().toString();
        newSession.setDistance(this, safelyGetDistanceFromUi());
        dataSource.createSession(newSession);
        dataSource.close();
        Toast.makeText(this, "Session saved", Toast.LENGTH_SHORT).show();
    }

    void persistUpdatedSession()
    {
        SessionsDataSource dataSource = new SessionsDataSource(this);
        dataSource.open();
        long recordId = getIntent().getExtras().getLong("SessionId");
        Session sessionUpdated = dataSource.getSessionById(recordId);
        sessionUpdated.description = mAutoTvSessDesc.getText().toString();
        sessionUpdated.sport = mAutoTvSport.getText().toString();
        sessionUpdated.notes = mTxtNotes.getText().toString();
        sessionUpdated.duration = getTotalDurationFromUi();
        sessionUpdated.setDistance(this, safelyGetDistanceFromUi());
        sessionUpdated.setDateOfSession(mSessionDate);

        ColorDrawable drawable = (ColorDrawable) mSessionComplete.getBackground();
        if (drawable.getColor() == Color.rgb(47, 255, 64))
            sessionUpdated.sessionState = Session.State.COMPLETE;
        else
            sessionUpdated.sessionState = Session.State.PLANNED;

        dataSource.updateSession(sessionUpdated);
        dataSource.close();
        Toast.makeText(this, "Session updated", Toast.LENGTH_SHORT).show();
    }

    double safelyGetDistanceFromUi()
    {
        if (mTxtDistance.getText().toString().length() == 0)
            return 0;

        return Double.parseDouble(mTxtDistance.getText().toString());
    }

    int getTotalDurationFromUi()
    {
        int totalMinutes = 0;
        if (mTvDurationHours.getText().toString().length() > 0)
            totalMinutes = 60 * Integer.parseInt(mTvDurationHours.getText().toString());

        if (mTvDurationMinutes.getText().toString().length() > 0)
            totalMinutes += Integer.parseInt(mTvDurationMinutes.getText().toString());

        return totalMinutes;
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

            if (durationMinutesLength > 59)
                mErrorMessages = mErrorMessages + getString(R.string.validation_duration_minutes_length_error) + "\n";
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
