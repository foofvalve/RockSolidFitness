package com.rocksolidfitness;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.joda.time.DateTime;


public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        ImpExpManager.AsyncResponse

{
    private static final int ALERT_DIALOG1 = 1;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        if (savedInstanceState == null)
        {
            MainViewFragment newFragment = new MainViewFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.add(R.id.container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            finish();
            startActivity(getIntent());

            MainViewFragment newFragment = new MainViewFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }


    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
        Intent i;
        FragmentManager fragmentManager = getFragmentManager();
        if (position == 1)
        {
            i = new Intent(this, SessionDetails.class);
            i.putExtra("SessionId", Consts.ADD_MODE);
            i.putExtra("DesiredDateOfSession", new DateTime().getMillis());
            startActivityForResult(i, 1);
        } else if (position == 2)
        {
            i = new Intent(this, ListViewMultiChartActivity.class);
            startActivity(i);
        } else if (position == 3)
        {
            i = new Intent(this, WeekViewActivity.class);
            startActivityForResult(i, 1);
        } else if (position == 7)
        {

        } else if (position == 8)
        {
            showDialog(1);
        } else
        {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                    .commit();
        }
    }


    @Override
    public Dialog onCreateDialog(int id)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id)
        {
            case ALERT_DIALOG1:

                builder.setTitle(R.string.export_type_title)
                        .setItems(R.array.export_type, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                switch (which)
                                {
                                    case Consts.EXPORT_CSV:
                                        doExportCsv();
                                        break;
                                    case Consts.EXPORT_EXCEL:
                                        doExportExcel();
                                        break;
                                    case Consts.EXPORT_XML:
                                        doExportXML();
                                        break;
                                }
                            }
                        });
                break;
            default:
                return null;
        }
        return builder.create();
    }

    void doExportCsv()
    {
        ImpExpManager impExpManage = new ImpExpManager(Consts.EXPORT_CSV);
        impExpManage.delegate = this;
        impExpManage.execute(this);
    }

    void doExportExcel()
    {
        ImpExpManager impExpManage = new ImpExpManager(Consts.EXPORT_EXCEL);
        impExpManage.delegate = this;
        impExpManage.execute(this);
    }

    void doExportXML()
    {
        ImpExpManager impExpManage = new ImpExpManager(Consts.EXPORT_XML);
        impExpManage.delegate = this;
        impExpManage.execute(this);
    }

    void onSectionAttached(int number)
    {
        switch (number)
        {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
            case 4:
                mTitle = getString(R.string.title_section4);
            case 5:
                mTitle = getString(R.string.title_section5);
            case 6:
                mTitle = getString(R.string.title_section6);
            case 7:
                mTitle = getString(R.string.title_section7);
            case 8:
                mTitle = getString(R.string.title_section8);
            case 9:
                mTitle = getString(R.string.title_section9);
            case 10:
                mTitle = getString(R.string.title_section10);
            case 11:
                mTitle = getString(R.string.title_section11);
                break;
        }
    }

    void restoreActionBar()
    {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (!mNavigationDrawerFragment.isDrawerOpen())
        {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void processFinish(boolean passed)
    {
        if (passed)
            Toast.makeText(this, "Completed export to SD Card", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Something bad happened- export failed", Toast.LENGTH_SHORT).show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment()
        {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber)
        {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        @Override
        public void onAttach(Activity activity)
        {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
}

