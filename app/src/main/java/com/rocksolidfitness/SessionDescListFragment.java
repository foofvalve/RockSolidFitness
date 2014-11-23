package com.rocksolidfitness;

import android.app.Activity;
import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class SessionDescListFragment extends ListFragment
{
    String[] numbers_text = new String[]{"one", "two", "three", "four",
            "five", "six", "seven", "eight", "nine", "ten", "eleven",
            "twelve", "thirteen", "fourteen", "fifteen"};
    String[] numbers_digits = new String[]{"1", "2", "3", "4", "5", "6", "7",
            "8", "9", "10", "11", "12", "13", "14", "15"};
    private OnCompleteListener mListener;

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        this.mListener.onComplete("" + numbers_digits[(int) id]);
        getFragmentManager().popBackStack();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                inflater.getContext(), android.R.layout.simple_list_item_1,
                numbers_text);
        setListAdapter(adapter);
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(Color.rgb(100, 100, 100));
        return view;
    }

    public static interface OnCompleteListener
    {
        public abstract void onComplete(String itemSelected);
    }
}
