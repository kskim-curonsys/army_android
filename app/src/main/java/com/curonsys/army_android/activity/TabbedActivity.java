package com.curonsys.army_android.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.curonsys.army_android.R;
import com.curonsys.army_android.adapter.ContentsListRecyclerViewAdapter;
import com.curonsys.army_android.model.ContentModel;
import com.curonsys.army_android.model.TransferModel;
import com.curonsys.army_android.util.RequestManager;

import java.util.ArrayList;

public class TabbedActivity extends AppCompatActivity {
    private static final String TAG = TabbedActivity.class.getSimpleName();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This function is not implemented yet.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public Intent getParentActivityIntent() {
        Intent parentIntent= getIntent();
        String className = parentIntent.getStringExtra("ParentClassSource");

        Intent newIntent = null;
        try {
            newIntent = new Intent(TabbedActivity.this, Class.forName(className));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        private RecyclerView mRecyclerView;
        private ContentsListRecyclerViewAdapter mContentsListAdapter;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int index = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = inflater.inflate(R.layout.fragment_tabbed, container, false);

            if (index == 1) {
                rootView = inflater.inflate(R.layout.fragment_tabbed, container, false);

                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_contents_list);
                mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

                getContentsList(rootView);

            } else if (index == 2) {
                rootView = inflater.inflate(R.layout.fragment_tabbed2, container, false);

                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.store_contents_list);
                mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

                getContentsList(rootView);

            } else if (index == 3) {
                rootView = inflater.inflate(R.layout.fragment_tabbed3, container, false);

                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.published_list);
                mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));

                getContentsList(rootView);
            }

            return rootView;
        }

        private void getContentsList(View rootView) {
            ArrayList<ContentModel> items = new ArrayList<ContentModel>();

            RequestManager rm = RequestManager.getInstance();
            rm.requestGetAllContents(new RequestManager.ContentsListCallback() {
                @Override
                public void onResponse(ArrayList<ContentModel> contents) {
                    mContentsListAdapter = new ContentsListRecyclerViewAdapter(null, contents, false);
                    mRecyclerView.setAdapter(mContentsListAdapter);

                    getThumbnails(contents);
                }
            });
        }

        private void getThumbnails(ArrayList<ContentModel> contents) {
            for (int i = 0; i < contents.size(); i++) {
                String thumbname = contents.get(i).getContentName();
                String thumbpath = contents.get(i).getThumb();
                String thumbsuffix = thumbpath.substring(thumbpath.indexOf('.'), thumbpath.length());

                final int index = i;
                RequestManager rm = RequestManager.getInstance();
                rm.requestDownloadFileFromStorage(thumbname, thumbpath, thumbsuffix, new RequestManager.TransferCallback() {
                    @Override
                    public void onResponse(TransferModel download) {
                        ContentModel model = contents.get(index);
                        model.setThumb(download.getPath());
                        contents.set(index, model);

                        mContentsListAdapter.notifyItemChanged(index);
                    }
                });
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Log.d(TAG, "TimePickerFragment:onTimeSet");
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Log.d(TAG, "DatePickerFragment:onDateSet");
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}
