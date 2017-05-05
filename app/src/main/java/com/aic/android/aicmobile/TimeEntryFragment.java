package com.aic.android.aicmobile;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aic.android.aicmobile.backend.aicDataAPI.AicDataAPI;
import com.aic.android.aicmobile.backend.aicDataAPI.model.TimeEntryDay;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    private int mWeekNumber;

    // Day labels
    private TextView mSundayLabel;
    private TextView mMondayLabel;
    private TextView mTuesdayLabel;
    private TextView mWednesdayLabel;
    private TextView mThursdayLabel;
    private TextView mFridayLabel;
    private TextView mSaturdayLabel;

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

    private List<TimeEntryDay> mDay = new ArrayList<>();

    // Private adapter list setup
    private List<TimeEntryDay> mSunday = new ArrayList<>();
    private List<TimeEntryDay> mMonday = new ArrayList<>();
    private List<TimeEntryDay> mTuesday = new ArrayList<>();
    private List<TimeEntryDay> mWednesday = new ArrayList<>();
    private List<TimeEntryDay> mThursday = new ArrayList<>();
    private List<TimeEntryDay> mFriday = new ArrayList<>();
    private List<TimeEntryDay> mSaturday = new ArrayList<>();

    // Create new instance of fragment, called from parent view pager found in Time Entry Activity
    public static TimeEntryFragment newInstance(int week) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_WEEK_NUBMER, week);

        TimeEntryFragment fragment = new TimeEntryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWeekNumber = (int) getArguments().getSerializable(ARG_WEEK_NUBMER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_time_entry, container, false);



//        mWeek = TimeEntryWeekLoader.initializeWeekList(18,2017);

        getActivity().setTitle("Time Entry");

        // Create views for each day
        mSundayCard = view.findViewById(R.id.time_entry_sunday_card);
        mMondayCard = view.findViewById(R.id.time_entry_monday_card);
        mTuesdayCard = view.findViewById(R.id.time_entry_tuesday_card);
        mWednesdayCard = view.findViewById(R.id.time_entry_wednesday_card);
        mThursdayCard = view.findViewById(R.id.time_entry_thursday_card);
        mFridayCard = view.findViewById(R.id.time_entry_friday_card);
        mSaturdayCard = view.findViewById(R.id.time_entry_saturday_card);

        // Initialize label for each day
        mSundayLabel = (TextView) mSundayCard.findViewById(R.id.time_entry_day_label);
        mMondayLabel = (TextView) mMondayCard.findViewById(R.id.time_entry_day_label);
        mTuesdayLabel = (TextView) mTuesdayCard.findViewById(R.id.time_entry_day_label);
        mWednesdayLabel = (TextView) mWednesdayCard.findViewById(R.id.time_entry_day_label);
        mThursdayLabel = (TextView) mThursdayCard.findViewById(R.id.time_entry_day_label);
        mFridayLabel = (TextView) mFridayCard.findViewById(R.id.time_entry_day_label);
        mSaturdayLabel = (TextView) mSaturdayCard.findViewById(R.id.time_entry_day_label);

        // Initialize recycler views for each day
        mSundayRecyclerView = (RecyclerView) mSundayCard.findViewById(R.id.time_entry_recycler_view);
        mMondayRecyclerView = (RecyclerView) mMondayCard.findViewById(R.id.time_entry_recycler_view);
        mTuesdayRecyclerView = (RecyclerView) mTuesdayCard.findViewById(R.id.time_entry_recycler_view);
        mWednesdayRecyclerView = (RecyclerView) mWednesdayCard.findViewById(R.id.time_entry_recycler_view);
        mThursdayRecyclerView = (RecyclerView) mThursdayCard.findViewById(R.id.time_entry_recycler_view);
        mFridayRecyclerView = (RecyclerView) mFridayCard.findViewById(R.id.time_entry_recycler_view);
        mSaturdayRecyclerView = (RecyclerView) mSaturdayCard.findViewById(R.id.time_entry_recycler_view);

        // Set layout managers
        mSundayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMondayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTuesdayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mWednesdayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mThursdayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFridayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSaturdayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set labels
        updateCardLabels(mWeekNumber);


        new DownloadTime().execute();

        return view;
    }

    public void setupAdapters() {
        mSunday = TimeEntryWeekLoader.initializeDayList(mWeekNumber, 1, mDay);
        mSundayAdapter = new TimeAdapter(mSunday);
        mSundayRecyclerView.setAdapter(mSundayAdapter);

        mMonday = TimeEntryWeekLoader.initializeDayList(mWeekNumber, 2, mDay);
        mMondayAdapter = new TimeAdapter(mMonday);
        mMondayRecyclerView.setAdapter(mMondayAdapter);

        mTuesday = TimeEntryWeekLoader.initializeDayList(mWeekNumber, 3, mDay);
        mTuesdayAdapter = new TimeAdapter(mTuesday);
        mTuesdayRecyclerView.setAdapter(mTuesdayAdapter);

        mWednesday = TimeEntryWeekLoader.initializeDayList(mWeekNumber, 4, mDay);
        mWednesdayAdapter = new TimeAdapter(mWednesday);
        mWednesdayRecyclerView.setAdapter(mWednesdayAdapter);

        mThursday = TimeEntryWeekLoader.initializeDayList(mWeekNumber, 5, mDay);
        mThursdayAdapter = new TimeAdapter(mThursday);
        mThursdayRecyclerView.setAdapter(mThursdayAdapter);

        mFriday = TimeEntryWeekLoader.initializeDayList(mWeekNumber, 6, mDay);
        mFridayAdapter = new TimeAdapter(mFriday);
        mFridayRecyclerView.setAdapter(mFridayAdapter);

        mSaturday = TimeEntryWeekLoader.initializeDayList(mWeekNumber, 0, mDay);
        mSaturdayAdapter = new TimeAdapter(mSaturday);
        mSaturdayRecyclerView.setAdapter(mSaturdayAdapter);
    }

    public void updateCardLabels(int weekNumber) {
        // Get calendar instance and set current week number from parent viewpager
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.WEEK_OF_YEAR, weekNumber);
        cal.setFirstDayOfWeek(Calendar.SUNDAY);

        // Get current day of week
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        // Set date format
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d");

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

    /*
        Time holder and adapter, recyclerview for entire page which holds horizontal recyclerviews
     */
    private class TimeHolder extends RecyclerView.ViewHolder {

        private TextView mDetail;


        public TimeHolder(LayoutInflater inflater, ViewGroup parent, View view) {
            super(inflater.inflate(R.layout.list_item_time_entry, parent, false));

            mDetail = (TextView) itemView.findViewById(R.id.time_entry_job_detail);

        }

        public void bind(TimeEntryDay time) {
            mDetail.setText(time.getCustomer() + " - " + time.getDescription());
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
            return new TimeHolder(inflater, parent, view);
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
Asychrounous task to download project list
 */
    private class DownloadTime extends AsyncTask<Void, Void, List<TimeEntryDay>> {
        private AicDataAPI myApiService = null;
        private static final String TAG = "EndpointsAsyncTask";
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading time");
            this.dialog.show();
        }

        @Override
        protected List<TimeEntryDay> doInBackground(Void... params) {
            if (myApiService == null) {
                AicDataAPI.Builder builder = new AicDataAPI.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        //Root url of google cloud project
                        .setRootUrl("https://i-melody-158021.appspot.com/_ah/api/");
                myApiService = builder.build();
            }


            try {
                Log.i(TAG, "made it to the async task bit");
                List<TimeEntryDay> raw = myApiService.getTime().execute().getItems();
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

            // Dismiss loading dialog
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

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

        for (int i = 0; i < raw.size(); i++) {

            TimeEntryDay time = new TimeEntryDay();
            time.setProjectNumber(raw.get(i).getProjectNumber());
            time.setCustomer(raw.get(i).getCustomer());
            time.setDescription(raw.get(i).getDescription());
            time.setBillable(raw.get(i).getBillable());
            time.setDate(raw.get(i).getDate());
            time.setNote(raw.get(i).getNote());

            times.add(time);
        }
        return times;
    }
}
