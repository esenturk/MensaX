package edu.kit.psegruppe3.mensax;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.kit.psegruppe3.mensax.sync.MensaXSyncAdapter;


public class MainActivity extends ActionBarActivity {

    static final int NUM_TABS = 5;
    static final String ARG_DATE = "date";

    private TabAdapter mAdapter;
    private ViewPager mPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        mAdapter = new TabAdapter(getSupportFragmentManager(), this);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mPager);

        MensaXSyncAdapter.initializeSyncAdapter(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;

        } else if (id == R.id.action_search) {
            onSearchRequested();
            return true;

        } else if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);

        } else if (id == R.id.action_contact) {
            Intent intent = new Intent(this, ContactActivity.class);
            startActivity(intent);

        }else if (id == R.id.action_liveCam) {
            Intent intent = new Intent(this, LiveCamsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public static class TabAdapter extends FragmentPagerAdapter {

        private Context context;

        public TabAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {
            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
            if (currentDay + position > Calendar.FRIDAY) {
                calendar.add(Calendar.DATE, position + 2);
            } else if (currentDay == Calendar.SUNDAY) {
                calendar.add(Calendar.DATE, position + 1);
            } else {
                calendar.add(Calendar.DATE, position);
            }

            Fragment f = new DailyMenuFragment();
            Bundle bundle = new Bundle();
            bundle.putLong(ARG_DATE, calendar.getTimeInMillis());
            f.setArguments(bundle);
            return f;
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Calendar calendar = Calendar.getInstance();
            String dayOfWeek = "";
            int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
            if (currentDay + position > Calendar.FRIDAY) {
                calendar.add(Calendar.DATE, position + 2); //skip weekend
            } else if (currentDay == Calendar.SUNDAY) {
                calendar.add(Calendar.DATE, position + 1);
            } else {
                calendar.add(Calendar.DATE, position);
            }
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            if (day == currentDay) {
                dayOfWeek = context.getString(R.string.today);
            } else if (day == currentDay + 1) {
                dayOfWeek = context.getString(R.string.tomorrow);
            } else {
                switch (day) {
                    case Calendar.MONDAY: dayOfWeek = context.getString(R.string.monday); break;
                    case Calendar.TUESDAY: dayOfWeek = context.getString(R.string.tuesday); break;
                    case Calendar.WEDNESDAY: dayOfWeek = context.getString(R.string.wednesday); break;
                    case Calendar.THURSDAY: dayOfWeek = context.getString(R.string.thursday); break;
                    case Calendar.FRIDAY: dayOfWeek = context.getString(R.string.friday);
                }
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM");
            Date currentDate = calendar.getTime();
            return dayOfWeek + " (" + dateFormat.format(currentDate) + ")";
        }
    }

}
