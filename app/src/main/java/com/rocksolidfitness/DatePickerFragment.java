package com.rocksolidfitness;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import org.joda.time.DateTime;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener
{

    private OnCompleteListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String strDateOfSessionMillis = getArguments().getString("dateOfSession");
        DateTime dateToDisplay = new DateTime(strDateOfSessionMillis);

        return new DatePickerDialog(getActivity(), this, dateToDisplay.getYear(),
                dateToDisplay.getMonthOfYear(), dateToDisplay.getDayOfMonth());
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        month++;
        this.mListener.onComplete(new DateTime(year + "-" + month + "-" + day));
    }

    public void onAttach(Activity activity)
    {
        try
        {
            super.onAttach(activity);
            this.mListener = (OnCompleteListener) activity;
        } catch (final ClassCastException e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    public static interface OnCompleteListener
    {
        public abstract void onComplete(DateTime dateOfSession);
    }
}
