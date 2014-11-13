package com.rocksolidfitness;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends Activity
{
    public SessionsDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        dataSource = new SessionsDataSource(this);
        dataSource.open();

        Session testSession = new Session(Session.State.PLANNED, "Running", "Easy fartlek run", 45);
        long recId = dataSource.createSession(testSession);

        Session savedSession = dataSource.getSessionById(recId);
        List<String> sports = dataSource.getSports();

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null)
        {

            PlaceholderFragment newFragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString("Sport", savedSession.sport);
            args.putString("Desc", savedSession.description);
            for (String sport : sports)
                args.putString("sport - " + sport, "val: " + sport);

            newFragment.setArguments(args);

            getFragmentManager().beginTransaction()
                    .add(R.id.container, newFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
            return true;

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment
    {
        public PlaceholderFragment()
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
            return rootView;
        }
    }
}
