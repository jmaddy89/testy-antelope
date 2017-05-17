package com.aic.android.aicmobile;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aic.android.aicmobile.backend.aicDataAPI.model.TimeEntryRequestDayInfo;
import com.aic.android.aicmobile.backend.aicDataAPI.AicDataAPI;
import com.aic.android.aicmobile.backend.aicDataAPI.model.TimeEntryDay;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by JLM on 4/9/2017.
 *
 * For reference:
 * Time refers to the whole list view
 * Week refers to the horizontal scrolling view
 * Day refers to the card view
 */

public class TimeEntryFragment extends Fragment {

    private static final String TAG = "TimeEntryFragment";
    private static final String ARG_WEEK_NUBMER = "week_number";
    private static final String ARG_YEAR = "year_number";

    private static final String APP_URL = "https://aic-mobile-5fdf1.appspot.com/_ah/api/";

    private int mWeekNumber;
    private int mYearNumber;

    // Day labels
    private TextView mSundayLabel;
    private TextView mMondayLabel;
    private TextView mTuesdayLabel;
    private TextView mWednesdayLabel;
    private TextView mThursdayLabel;
    private TextView mFridayLabel;
    private TextView mSaturdayLabel;

    // Day total labels
    private TextView mSundayTotal;
    private TextView mMondayTotal;
    private TextView mTuesdayTotal;
    private TextView mWednesdayTotal;
    private TextView mThursdayTotal;
    private TextView mFridayTotal;
    private TextView mSaturdayTotal;

    // Day recyclerviews
    private RecyclerView mSundayRecyclerView;
    private RecyclerView mMondayRecyclerView;
    private RecyclerView mTuesdayRecyclerView;
    private RecyclerView mWednesdayRecyclerView;
    private RecyclerView mThursdayRecyclerView;
    private RecyclerView mFridayRecyclerView;
    private RecyclerView mSaturdayRecyclerView;

    // Day Adapters
    private TimeAdapter mSundayAdapter;
    private TimeAdapter mMondayAdapter;
    private TimeAdapter mTuesdayAdapter;
    private TimeAdapter mWednesdayAdapter;
    private TimeAdapter mThursdayAdapter;
    private TimeAdapter mFridayAdapter;
    private TimeAdapter mSaturdayAdapter;

    // Views
    private View mSundayCard;
    private View mMondayCard;
    private View mTuesdayCard;
    private View mWednesdayCard;
    private View mThursdayCard;
    private View mFridayCard;
    private View mSaturdayCard;

    // Add New Buttons
    private Button mSundayAddNew;
    private Button mMondayAddNew;
    private Button mTuesdayAddNew;
    private Button mWednesdayAddNew;
    private Button mThursdayAddNew;
    private Button mFridayAddNew;
    private Button mSaturdayAddNew;

    // Day list of info for whole week
    private List<TimeEntryDay> mDay = new ArrayList<>();

    // Private adapter list setup
    private List<TimeEntryDay> mSunday = new ArrayList<>();
    private List<TimeEntryDay> mMonday = new ArrayList<>();
    private List<TimeEntryDay> mTuesday = new ArrayList<>();
    private List<TimeEntryDay> mWednesday = new ArrayList<>();
    private List<TimeEntryDay> mThursday = new ArrayList<>();
    private List<TimeEntryDay> mFriday = new ArrayList<>();
    private List<TimeEntryDay> mSaturday = new ArrayList<>();

    // Total at bottom of activity
    private TextView mWeekTotal;

    private Resources mResource;

    // Create new instance of fragment, called from parent view pager found in Time Entry Activity
    public static TimeEntryFragment newInstance(int week, int year) {
        // Create bundle and put arguements of week and year number from above time entry activity
        Bundle args = new Bundle();
        args.putSerializable(ARG_WEEK_NUBMER, week);
        args.putSerializable(ARG_YEAR, year);

        // Create fragment, add arguements and return
        TimeEntryFragment fragment = new TimeEntryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get week and year from view pager in above time entry activity
        mWeekNumber = (int) getArguments().getSerializable(ARG_WEEK_NUBMER);
        mYearNumber = (int) getArguments().getSerializable(ARG_YEAR);
        Log.i(TAG, "Week Number during create is: " + mWeekNumber);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_time_entry, container, false);

        // Set title
        getActivity().setTitle("Time Entry");

        mResource = getResources();

        // Create views for each day
        mSundayCard = view.findViewById(R.id.time_entry_sunday_card);
        mMondayCard = view.findViewById(R.id.time_entry_monday_card);
        mTuesdayCard = view.findViewById(R.id.time_entry_tuesday_card);
        mWednesdayCard = view.findViewById(R.id.time_entry_wednesday_card);
        mThursdayCard = view.findViewById(R.id.time_entry_thursday_card);
        mFridayCard = view.findViewById(R.id.time_entry_friday_card);
        mSaturdayCard = view.findViewById(R.id.time_entry_saturday_card);

        // Initialize day label for each day
        mSundayLabel = (TextView) mSundayCard.findViewById(R.id.time_entry_day_label);
        mMondayLabel = (TextView) mMondayCard.findViewById(R.id.time_entry_day_label);
        mTuesdayLabel = (TextView) mTuesdayCard.findViewById(R.id.time_entry_day_label);
        mWednesdayLabel = (TextView) mWednesdayCard.findViewById(R.id.time_entry_day_label);
        mThursdayLabel = (TextView) mThursdayCard.findViewById(R.id.time_entry_day_label);
        mFridayLabel = (TextView) mFridayCard.findViewById(R.id.time_entry_day_label);
        mSaturdayLabel = (TextView) mSaturdayCard.findViewById(R.id.time_entry_day_label);

        // Initialize total label for each day
        mSundayTotal = (TextView) mSundayCard.findViewById(R.id.time_entry_hours_total);
        mMondayTotal = (TextView) mMondayCard.findViewById(R.id.time_entry_hours_total);
        mTuesdayTotal = (TextView) mTuesdayCard.findViewById(R.id.time_entry_hours_total);
        mWednesdayTotal = (TextView) mWednesdayCard.findViewById(R.id.time_entry_hours_total);
        mThursdayTotal = (TextView) mThursdayCard.findViewById(R.id.time_entry_hours_total);
        mFridayTotal = (TextView) mFridayCard.findViewById(R.id.time_entry_hours_total);
        mSaturdayTotal = (TextView) mSaturdayCard.findViewById(R.id.time_entry_hours_total);

        // Initialize add new buttons for each day
        mSundayAddNew = (Button) mSundayCard.findViewById(R.id.time_entry_add_new_button);
        mMondayAddNew = (Button) mMondayCard.findViewById(R.id.time_entry_add_new_button);
        mTuesdayAddNew = (Button) mTuesdayCard.findViewById(R.id.time_entry_add_new_button);
        mWednesdayAddNew = (Button) mWednesdayCard.findViewById(R.id.time_entry_add_new_button);
        mThursdayAddNew = (Button) mThursdayCard.findViewById(R.id.time_entry_add_new_button);
        mFridayAddNew = (Button) mFridayCard.findViewById(R.id.time_entry_add_new_button);
        mSaturdayAddNew = (Button) mSaturdayCard.findViewById(R.id.time_entry_add_new_button);

        // Initialize on click listeners for buttons
        mSundayAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTimeEntry(1);
            }
        });
        mMondayAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTimeEntry(2);
            }
        });
        mTuesdayAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTimeEntry(3);
            }
        });
        mWednesdayAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTimeEntry(4);
            }
        });
        mThursdayAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTimeEntry(5);
            }
        });
        mFridayAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTimeEntry(6);
            }
        });
        mSaturdayAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTimeEntry(7);
            }
        });

        // Initialize recycler views for each day
        mSundayRecyclerView = (RecyclerView) mSundayCard.findViewById(R.id.time_entry_recycler_view);
        mMondayRecyclerView = (RecyclerView) mMondayCard.findViewById(R.id.time_entry_recycler_view);
        mTuesdayRecyclerView = (RecyclerView) mTuesdayCard.findViewById(R.id.time_entry_recycler_view);
        mWednesdayRecyclerView = (RecyclerView) mWednesdayCard.findViewById(R.id.time_entry_recycler_view);
        mThursdayRecyclerView = (RecyclerView) mThursdayCard.findViewById(R.id.time_entry_recycler_view);
        mFridayRecyclerView = (RecyclerView) mFridayCard.findViewById(R.id.time_entry_recycler_view);
        mSaturdayRecyclerView = (RecyclerView) mSaturdayCard.findViewById(R.id.time_entry_recycler_view);

        // Set nested scrolling to false to allow smooth scrolling within the nested scroll view
        mSundayRecyclerView.setNestedScrollingEnabled(false);
        mMondayRecyclerView.setNestedScrollingEnabled(false);
        mTuesdayRecyclerView.setNestedScrollingEnabled(false);
        mWednesdayRecyclerView.setNestedScrollingEnabled(false);
        mThursdayRecyclerView.setNestedScrollingEnabled(false);
        mFridayRecyclerView.setNestedScrollingEnabled(false);
        mSaturdayRecyclerView.setNestedScrollingEnabled(false);

        // Set layout managers
        mSundayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMondayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTuesdayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWednesdayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mThursdayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFridayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSaturdayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize week total
        mWeekTotal = (TextView) view.findViewById(R.id.time_entry_week_total);

        // Set labels
        updateCardLabels(mWeekNumber);

        // Download time
        new DownloadTime().execute();

        // Return view to above activity to use
        return view;
    }

    public void setupAdapters() {
        // Build sunday day list and set adapter
        mSunday = TimeEntryWeekLoader.initializeDayList(mWeekNumber, mYearNumber, 1, mDay);
        mSundayAdapter = new TimeAdapter(mSunday);
        mSundayRecyclerView.setAdapter(mSundayAdapter);

        // Get hour totals for sunday
        float sundayTotal = updateDayTotals(mSunday);
        mSundayTotal.setText("Total: " + sundayTotal);

        // Build monday day list and set adapter
        mMonday = TimeEntryWeekLoader.initializeDayList(mWeekNumber, mYearNumber, 2, mDay);
        mMondayAdapter = new TimeAdapter(mMonday);
        mMondayRecyclerView.setAdapter(mMondayAdapter);

        // Get hour totals for monday
        float mondayTotal = updateDayTotals(mMonday);
        mMondayTotal.setText("Total: " + mondayTotal);

        // Build tuesday day list and set adapter
        mTuesday = TimeEntryWeekLoader.initializeDayList(mWeekNumber, mYearNumber, 3, mDay);
        mTuesdayAdapter = new TimeAdapter(mTuesday);
        mTuesdayRecyclerView.setAdapter(mTuesdayAdapter);

        // Get hour totals for tuesday
        float tuesdayTotal = updateDayTotals(mTuesday);
        mTuesdayTotal.setText("Total: " + tuesdayTotal);

        // Build wednesday day list and set adapter
        mWednesday = TimeEntryWeekLoader.initializeDayList(mWeekNumber, mYearNumber, 4, mDay);
        mWednesdayAdapter = new TimeAdapter(mWednesday);
        mWednesdayRecyclerView.setAdapter(mWednesdayAdapter);

        // Get hour totals for wednesday
        float wednesdayTotal = updateDayTotals(mWednesday);
        mWednesdayTotal.setText("Total: " + wednesdayTotal);

        // Build thursday day list and set adapter
        mThursday = TimeEntryWeekLoader.initializeDayList(mWeekNumber, mYearNumber, 5, mDay);
        mThursdayAdapter = new TimeAdapter(mThursday);
        mThursdayRecyclerView.setAdapter(mThursdayAdapter);

        // Get hour totals for thursday
        float thursdayTotal = updateDayTotals(mThursday);
        mThursdayTotal.setText("Total: " + thursdayTotal);

        // Build friday day list and set adapter
        mFriday = TimeEntryWeekLoader.initializeDayList(mWeekNumber, mYearNumber, 6, mDay);
        mFridayAdapter = new TimeAdapter(mFriday);
        mFridayRecyclerView.setAdapter(mFridayAdapter);

        // Get hour totals for friday
        float fridayTotal = updateDayTotals(mFriday);
        mFridayTotal.setText("Total: " + fridayTotal);

        // Build saturday day list and set adapter
        mSaturday = TimeEntryWeekLoader.initializeDayList(mWeekNumber, mYearNumber, 0, mDay);
        mSaturdayAdapter = new TimeAdapter(mSaturday);
        mSaturdayRecyclerView.setAdapter(mSaturdayAdapter);

        // Get hour totals for saturday
        float saturdayTotal = updateDayTotals(mSaturday);
        mSaturdayTotal.setText("Total: " + saturdayTotal);
    }

    public void updateCardLabels(int weekNumber) {
        // Get calendar instance and set current week number from parent viewpager
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.WEEK_OF_YEAR, weekNumber);
        cal.setFirstDayOfWeek(Calendar.SUNDAY);

        // Get current day of week
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        // Set date format
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d", Locale.US);

        // Normalize day, adding 1 - day of week gives sunday, update label
        cal.add(Calendar.DAY_OF_WEEK, 1 - dayOfWeek);
        mSundayLabel.setText(sdf.format(cal.getTime()));

        cal.add(Calendar.DAY_OF_WEEK, 1);
        mMondayLabel.setText(sdf.format(cal.getTime()));

        cal.add(Calendar.DAY_OF_WEEK, 1);
        mTuesdayLabel.setText(sdf.format(cal.getTime()));

        cal.add(Calendar.DAY_OF_WEEK, 1);
        mWednesdayLabel.setText(sdf.format(cal.getTime()));

        cal.add(Calendar.DAY_OF_WEEK, 1);
        mThursdayLabel.setText(sdf.format(cal.getTime()));

        cal.add(Calendar.DAY_OF_WEEK, 1);
        mFridayLabel.setText(sdf.format(cal.getTime()));

        cal.add(Calendar.DAY_OF_WEEK, 1);
        mSaturdayLabel.setText(sdf.format(cal.getTime()));

    }

    public float updateDayTotals(List<TimeEntryDay> day) {

        // If list is null, return a 0
        if (day == null) {
            return 0.0f;
        }
        // Initialize day total at 0
        float dayTotal = 0.0f;

        // Loop through list of items for that day, sum up total hours
        for (int i = 0; i < day.size(); i++) {
            dayTotal = dayTotal + day.get(i).getTime();
        }

        return dayTotal;
    }

    public void updateWeekTotals(List<TimeEntryDay> day) {

        if (day != null) {
            float total = 0.0f;

            for (int i = 0; i < day.size(); i++) {
                if (day.get(i).getTime() != null) {
                    total = total + day.get(i).getTime();
                }
            }

            mWeekTotal.setText(String.valueOf(total));
        }
    }

    public void addNewTimeEntry(int dayNumber) {
        // Set calendar object to get correct date for time entry dialog
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, dayNumber);
        cal.set(Calendar.WEEK_OF_YEAR, mWeekNumber);
        cal.set(Calendar.YEAR, mYearNumber);
        Date date = cal.getTime();

        // Create time entry dialog fragment
        DialogFragment fragment = TimeEntryAddFragment.newInstance(date);
        fragment.show(getFragmentManager(), "Add New Time Entry Dialog");
    }

    /*
        Time holder and adapter, recyclerview for entire page which holds horizontal recyclerviews
     */
    private class TimeHolder extends RecyclerView.ViewHolder {

        private TextView mDetail;
        private TextView mHours;


        public TimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_time_entry, parent, false));

            mDetail = (TextView) itemView.findViewById(R.id.time_entry_job_detail);
            mHours = (TextView) itemView.findViewById(R.id.time_entry_job_hours);


        }

        public void bind(TimeEntryDay time) {
            mDetail.setText(time.getProjectNumber() + " - " + time.getCustomer() + " - " + time.getDescription());
            mHours.setText(time.getTime().toString() + " hours");

            // If time is submitted, change text appearance
            if (time.getSubmitted()) {
                mDetail.setTextColor(mResource.getColor(R.color.submitted));
                mHours.setTextColor(mResource.getColor(R.color.submitted));
            } else {
                mDetail.setTextColor(mResource.getColor(R.color.unsubmitted));
                mHours.setTextColor(mResource.getColor(R.color.unsubmitted));
            }

        }
    }

    private class TimeAdapter extends RecyclerView.Adapter<TimeHolder> {

        private List<TimeEntryDay> mDayInfo;

        public TimeAdapter(List<TimeEntryDay> day) {
            mDayInfo = day;
        }

        @Override
        public TimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = parent.getRootView();
            return new TimeHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(TimeHolder holder, int position) {

            TimeEntryDay day = mDayInfo.get(position);
            holder.bind(day);
        }

        @Override
        public int getItemCount() {
            return mDayInfo.size();
        }
    }

    /*
Asychronous task to download project list
 */
    private class DownloadTime extends AsyncTask<Void, Void, List<TimeEntryDay>> {
        private AicDataAPI myApiService = null;
        private static final String TAG = "EndpointsAsyncTask";
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Loading time", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
//            this.dialog.setMessage("Loading time");
//            this.dialog.show();
        }

        @Override
        protected List<TimeEntryDay> doInBackground(Void... params) {
            if (myApiService == null) {
                AicDataAPI.Builder builder = new AicDataAPI.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        //Root url of google cloud project
                        .setRootUrl(APP_URL);
                myApiService = builder.build();
            }

            // Get logged in users firebase id
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            // Setup request info to download time
            TimeEntryRequestDayInfo requestInfo = new TimeEntryRequestDayInfo();
            requestInfo.setUserId(user.getUid());
            requestInfo.setWeekNumber(mWeekNumber);
            requestInfo.setYear(2017);


            try {
                List<TimeEntryDay> raw = myApiService.downloadTime(requestInfo).execute().getItems();
                return parseItems(raw);
            } catch (IOException e) {
                Log.i(TAG, "IO Exception", e);
                List<TimeEntryDay> fail = new ArrayList<>();
                return fail;
            }
        }

        @Override
        protected void onPostExecute(List<TimeEntryDay> s) {
            mDay.addAll(s);
            setupAdapters();
            updateWeekTotals(mDay);

            // Dismiss loading dialog
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }

            try {
                Log.i(TAG, "Response from google endpoint: " + s.toString());
            } catch (Exception e) {
                Log.i(TAG, "Response from google endpoint is null: " + e.toString());
            }
        }
    }

    /*
    Used for to clean up response from google endpoint
     */
    private List<TimeEntryDay> parseItems(List<TimeEntryDay> raw) {

        List<TimeEntryDay> times = new ArrayList<>();

        // If list is null, then return an empty list instead of a null one
        if (raw == null) {
            TimeEntryDay emptyDay = new TimeEntryDay();
            times.add(emptyDay);
            return times;
        }

        for (int i = 0; i < raw.size(); i++) {

            TimeEntryDay time = new TimeEntryDay();
            time.setProjectNumber(raw.get(i).getProjectNumber());
            time.setCustomer(raw.get(i).getCustomer());
            time.setDescription(raw.get(i).getDescription());
            time.setBillable(raw.get(i).getBillable());
            time.setSubmitted(raw.get(i).getSubmitted());
            time.setDate(raw.get(i).getDate());
            time.setNote(raw.get(i).getNote());
            time.setTime(raw.get(i).getTime());

            times.add(time);
        }
        return times;
    }
}