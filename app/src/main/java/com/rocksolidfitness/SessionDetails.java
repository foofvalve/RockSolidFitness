package com.rocksolidfitness;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

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
    AutoCompleteTextView mAutoTvSessDesc;
    AutoCompleteTextView mAutoTvSport;
    DateTime mSessionDate;
    Button mBtnLookupSessDesc;

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
        mBtnCancelSessionEdit = (Button) findViewById(R.id.btnCancelSessionDetail);
        mBtnLookupSessDesc = (Button) findViewById(R.id.btnSearchSessionDetail);

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
