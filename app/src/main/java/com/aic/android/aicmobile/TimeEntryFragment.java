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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    private List<TimeEntryDay> mTime = new ArrayList<>();
    private List<TimeEntryDay> mRawDay = new ArrayList<>();

    private List<TimeEntryWeek> mWeek = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_time_entry, container, false);

//        mTimeRecyclerView = (RecyclerView) view.findViewById(R.id.time_entry_recycler_view);
//        mTimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        mTime = TimeEntryWeekLoader.initializeTimeList();
//
//        mTimeAdapter = new TimeAdapter(mTime);
//        mTimeRecyclerView.setAdapter(mTimeAdapter);
        mWeek = TimeEntryWeekLoader.initializeWeekList(18,2017);
//        mTimeEntryWeekDate = (TextView) view.findViewById(R.id.time_entry_week_date);
//        mWeekRecyclerView = (RecyclerView) view.findViewById(R.id.time_entry_week_recycler_view);
//
//        mWeekRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        setupWeekAdapter();
        getActivity().setTitle("Time Entry");

        // Create views for each day
        View sundayCard = view.findViewById(R.id.time_entry_sunday_card);
        View mondayCard = view.findViewById(R.id.time_entry_monday_card);
        View tuesdayCard = view.findViewById(R.id.time_entry_tuesday_card);
        View wednesdayCard = view.findViewById(R.id.time_entry_wednesday_card);
        View thursdayCard = view.findViewById(R.id.time_entry_thursday_card);
        View fridayCard = view.findViewById(R.id.time_entry_friday_card);
        View saturdayCard = view.findViewById(R.id.time_entry_saturday_card);

        // Initialize label for each day
        mSundayLabel = (TextView) sundayCard.findViewById(R.id.time_entry_day_label);
        mMondayLabel = (TextView) mondayCard.findViewById(R.id.time_entry_day_label);
        mTuesdayLabel = (TextView) tuesdayCard.findViewById(R.id.time_entry_day_label);
        mWednesdayLabel = (TextView) wednesdayCard.findViewById(R.id.time_entry_day_label);
        mThursdayLabel = (TextView) thursdayCard.findViewById(R.id.time_entry_day_label);
        mFridayLabel = (TextView) fridayCard.findViewById(R.id.time_entry_day_label);
        mSaturdayLabel = (TextView) saturdayCard.findViewById(R.id.time_entry_day_label);

        // Initialize recycler views for each day
        mSundayRecyclerView = (RecyclerView) sundayCard.findViewById(R.id.time_entry_recycler_view);
        mMondayRecyclerView = (RecyclerView) mondayCard.findViewById(R.id.time_entry_recycler_view);

        // Set layout managers
        mSundayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMondayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Set labels
        mSundayLabel.setText("Sunday");
        mMondayLabel.setText("Monday");
        mTuesdayLabel.setText("Tuesday");
        mWednesdayLabel.setText("Wednesday");
        mThursdayLabel.setText("Thursday");
        mFridayLabel.setText("Friday");
        mSaturdayLabel.setText("Saturday");




        new DownloadTime().execute();

        return view;
    }

    public void setupAdapters() {
        TimeAdapter mSundayAdapter = new TimeAdapter(mTime);
        mSundayRecyclerView.setAdapter(mSundayAdapter);

        TimeAdapter mMondayAdapter = new TimeAdapter(mTime);
        mMondayRecyclerView.setAdapter(mMondayAdapter);

    }

    /*
        Time holder and adapter, recyclerview for entire page which holds horizontal recyclerviews
     */
    private class TimeHolder extends RecyclerView.ViewHolder {


        public TimeHolder(LayoutInflater inflater, ViewGroup parent, View view) {
            super(inflater.inflate(R.layout.list_item_time_entry, parent, false));

        }

        public void bind(TimeEntryDay time) {
            mSundayLabel.setText("There is stuff happenening");
            mMondayLabel.setText(time.getCustomer());
        }
    }

    private class TimeAdapter extends RecyclerView.Adapter<TimeHolder> {

        private List<TimeEntryDay> mTime;

        public TimeAdapter(List<TimeEntryDay> time) {
            mTime = time;
        }

        @Override
        public TimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = parent.getRootView();
            return new TimeHolder(inflater, parent, view);
        }

        @Override
        public void onBindViewHolder(TimeHolder holder, int position) {

            TimeEntryDay time = TimeEntryWeekLoader.initializeDayList(18, mTime);
            holder.bind(time);
        }

        @Override
        public int getItemCount() {
            Log.i(TAG, "Size of: " + mTime.size());
            return mTime.size();
        }
    }

    /*
        Week holder and adapter, horizontal scrolling recycler views for showing each day
     */
//    private class WeekHolder extends RecyclerView.ViewHolder {
//        private List<TimeEntryDay> mDay = new ArrayList<>();
//
//        private TextView mDayText;
//        private TextView mDateText;
//        private Button mAddNew;
//        private RecyclerView mDayRecyclerView;
//        private DayAdapter mDayAdapter;


//        public WeekHolder(LayoutInflater inflater, ViewGroup parent) {
//            super(inflater.inflate(R.layout.list_item_time_entry_day, parent, false));
//
//            mDayText = (TextView) itemView.findViewById(R.id.time_entry_day_header);
//            mDateText = (TextView) itemView.findViewById(R.id.time_entry_day_date);
//
//            mDayRecyclerView = (RecyclerView) itemView.findViewById(R.id.time_entry_day_recycler_view);
//            mDayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//            mDayAdapter = new DayAdapter(mDay);

//            mDayRecyclerView.setAdapter(mDayAdapter);
//        }
//
//        public void bind(TimeEntryWeek week) {
//
//
//            mDayText.setText(week.getDayName());
//            mDateText.setText(week.getDate());
//        }
//    }
//
//    private class WeekAdapter extends RecyclerView.Adapter<WeekHolder> {
//
//        private List<TimeEntryWeek> mWeek = new ArrayList<>();
//
//
//        public WeekAdapter(List<TimeEntryWeek> week) {
//            mWeek = week;
//        }
//
//        @Override
//        public WeekHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            LayoutInflater inflater = LayoutInflater.from(getActivity());
//            return new WeekHolder(inflater, parent);
//        }
//
//        @Override
//        public void onBindViewHolder(WeekHolder holder, int position) {
//            Log.i(TAG, "Calling up week holder position: " + position);
//            TimeEntryWeek week = mWeek.get(position);
//            mDay = TimeEntryWeekLoader.initializeDayList(mWeek.get(position), mRawDay);
//
//            mDayAdapter = new DayAdapter(mDay);
//            mDayRecyclerView.setAdapter(mDayAdapter);
//
//
//
//            holder.bind(week);
//        }
//
//        @Override
//        public int getItemCount() {
//            return mWeek.size();
//        }
//    }

    /*
    *   Day holder and adapter, for showing detailed time breakdown within the card
     */
//    private class DayHolder extends RecyclerView.ViewHolder {
//
//        private TextView mText;
//
//        public DayHolder(LayoutInflater inflater, ViewGroup parent) {
//            super(inflater.inflate(R.layout.list_item_time_entry_detail, parent, false));
//
//            mText = (TextView) itemView.findViewById(R.id.time_entry_project_description);
//        }
//
//        public void bind(TimeEntryDay day) {
//            Log.i(TAG, "Setting customer: " + day.getCustomer());
//            mText.setText(day.getCustomer());
//        }
//    }
//
//    private class DayAdapter extends RecyclerView.Adapter<DayHolder> {
//
//        public DayAdapter(List<TimeEntryDay> day) {
//            mDay = day;
//        }
//
//        @Override
//        public DayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            LayoutInflater inflater = LayoutInflater.from(getActivity());
//            return new DayHolder(inflater, parent);
//        }
//
//        @Override
//        public void onBindViewHolder(DayHolder holder, int position) {
//
//            Log.i(TAG, "Calling up day holder position: " + position);
//            mDay = TimeEntryWeekLoader.initializeDayList(mWeek.get(position), mDay);
//            holder.bind(mDay.get(position));
//        }
//
//        @Override
//        public int getItemCount() {
//            return mDay.size();
//        }
//    }

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
            mTime = s;
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
