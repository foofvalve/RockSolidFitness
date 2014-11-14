package com.rocksolidfitness;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainViewFragment extends Fragment
{
    ExpandableListAdapter mListAdapter;
    ExpandableListView mExpListView;
    List<String> mListDataHeader;
    HashMap<String, List<String>> mListDataChild;

    public MainViewFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String msg = this.getArguments().getString("Sport") + "\t" +
                this.getArguments().getString("Desc") + "\n" +
                this.getArguments().getString("sport - Cycling") + "\n" +
                this.getArguments().getString("sport - Swimming") + "\n" +
                this.getArguments().getString("sport - Running");
        TextView mainContent = (TextView) rootView.findViewById(R.id.mainTextView);
        mainContent.setText(msg);

        mExpListView = (ExpandableListView) rootView.findViewById(R.id.expandableListViewSessions);

        // preparing list data
        prepareListData();

        mListAdapter = new ExpandableListAdapter(getActivity(), mListDataHeader, mListDataChild);

        // setting list adapter
        mExpListView.setAdapter(mListAdapter);
        return rootView;
    }

    private void prepareListData()
    {
        mListDataHeader = new ArrayList<String>();
        mListDataChild = new HashMap<String, List<String>>();

        // Adding child data
        mListDataHeader.add(getString(R.string.wk_mon));
        mListDataHeader.add(getString(R.string.wk_tue));
        mListDataHeader.add(getString(R.string.wk_wed));
        mListDataHeader.add(getString(R.string.wk_thu));
        mListDataHeader.add(getString(R.string.wk_fri));
        mListDataHeader.add(getString(R.string.wk_sat));
        mListDataHeader.add(getString(R.string.wk_sun));


        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");

        mListDataChild.put(mListDataHeader.get(0), top250); // Header, Child data
        mListDataChild.put(mListDataHeader.get(1), nowShowing);
        mListDataChild.put(mListDataHeader.get(2), comingSoon);
    }
}