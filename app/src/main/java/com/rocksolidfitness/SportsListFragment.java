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


public class SportsListFragment extends ListFragment
{
    String[] listOfSports = new String[]{};
    private OnCompleteListener mListener;

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        this.mListener.onSportSelected("" + listOfSports[(int) id]);
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
        SessionsDataSource dataSource = new SessionsDataSource(getActivity());
        dataSource.openReadOnly();
        listOfSports = dataSource.getSportsForSpinner();
        dataSource.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                inflater.getContext(), android.R.layout.simple_list_item_1,
                listOfSports);
        setListAdapter(adapter);
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(Color.rgb(100, 100, 100));
        return view;
    }

    public static interface OnCompleteListener
    {
        public abstract void onSportSelected(String sportSelected);
    }
}
