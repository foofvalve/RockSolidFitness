package com.rocksolidfitness;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.joda.time.DateTime;

import java.util.List;


public class MainActivity extends Activity
{
    public SessionsDataSource dataSource;  //todo: get rid of this or pass it around or fragment blah

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        dataSource = new SessionsDataSource(this);
        dataSource.open();
        //dataSource.loadSmallTestDataSet();
        //dataSource.loadLargeDataSet();
        dataSource.loadDynamicTestData();
        Session testSession = new Session(Session.State.PLANNED, "Running", "Easy fartlek run", 45, new DateTime());
        long recId = dataSource.createSession(testSession);

        Session savedSession = dataSource.getSessionById(recId);
        List<String> sports = dataSource.getSports();

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null)
        {

            MainViewFragment newFragment = new MainViewFragment();
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
    protected void onDestroy()
    {

        if (dataSource != null) dataSource.close();
        super.onDestroy();

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


}
