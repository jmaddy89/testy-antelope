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

    private RecyclerView mTimeRecyclerView;
    private RecyclerView mWeekRecyclerView;
    private RecyclerView mDayRecyclerView;

    private TimeAdapter mTimeAdapter;
    private WeekAdapter mWeekAdapter;
    private DayAdapter mDayAdapter;

    private List<TimeEntryTime> mTime = new ArrayList<>();
    private List<TimeEntryWeek> mWeek = new ArrayList<>();
    private List<TimeEntryDay> mDay = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_time_entry, container, false);

        mTimeRecyclerView = (RecyclerView) view.findViewById(R.id.time_entry_recycler_view);
        mTimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mTime = TimeEntryWeekLoader.initializeTimeList();

        mTimeAdapter = new TimeAdapter(mTime);
        mTimeRecyclerView.setAdapter(mTimeAdapter);

        getActivity().setTitle("Time Entry");

        new DownloadTime().execute();

        return view;
    }

    public void setupWeekAdapter() {
        mWeekAdapter = new WeekAdapter(mWeek);
        mWeekRecyclerView.setAdapter(mWeekAdapter);
    }

    public void setupDayAdapter() {
        mDayAdapter = new DayAdapter(mDay);
        mDayRecyclerView.setAdapter(mDayAdapter);
    }

    /*
        Time holder and adapter, recyclerview for entire page which holds horizontal recyclerviews
     */
    private class TimeHolder extends RecyclerView.ViewHolder {

        private TextView mTimeEntryWeekDate;
//        private RecyclerView mDayRecyclerView;
//        private WeekAdapter mWeekAdapter;
//        private List<TimeEntryWeek> mWeek = new ArrayList<>();


        public TimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_time_entry, parent, false));

            mTimeEntryWeekDate = (TextView) itemView.findViewById(R.id.time_entry_week_date);
            mWeekRecyclerView = (RecyclerView) itemView.findViewById(R.id.time_entry_week_recycler_view);

            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            // 0 = Horizontal orientation
            manager.setOrientation(0);
            mWeekRecyclerView.setLayoutManager(manager);

        }

        public void bind(TimeEntryTime week) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, week.getWeekYear());
            cal.set(Calendar.WEEK_OF_YEAR, week.getWeekNumber());

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
            mTimeEntryWeekDate.setText(sdf.format(cal.getTime()));
        }
    }

    private class TimeAdapter extends RecyclerView.Adapter<TimeHolder> {

        private List<TimeEntryTime> mTime;

        public TimeAdapter(List<TimeEntryTime> Time) {
            mTime = Time;
        }

        @Override
        public TimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new TimeHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(TimeHolder holder, int position) {
            mWeek = TimeEntryWeekLoader.initializeWeekList(mTime.get(position).getWeekNumber(),
                    mTime.get(position).getWeekYear());
            setupWeekAdapter();
            TimeEntryTime time = mTime.get(position);
            holder.bind(time);
        }

        @Override
        public int getItemCount() {
            return mTime.size();
        }
    }

    /*
        Week holder and adapter, horizontal scrolling recycler views for showing each day
     */
    private class WeekHolder extends RecyclerView.ViewHolder {
        private List<TimeEntryDay> mDay = new ArrayList<>();

        private TextView mDayText;
        private TextView mDateText;
        private Button mAddNew;
//        private RecyclerView mDayRecyclerView;
//        private DayAdapter mDayAdapter;


        public WeekHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_time_entry_day, parent, false));

            mDayText = (TextView) itemView.findViewById(R.id.time_entry_day_header);
            mDateText = (TextView) itemView.findViewById(R.id.time_entry_day_date);

            mDayRecyclerView = (RecyclerView) itemView.findViewById(R.id.time_entry_day_recycler_view);
            mDayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            mDayAdapter = new DayAdapter(mDay);

            mDayRecyclerView.setAdapter(mDayAdapter);
        }

        public void bind(TimeEntryWeek week) {
            mDayText.setText(week.getDayName());
            mDateText.setText(week.getDate());
        }
    }

    private class WeekAdapter extends RecyclerView.Adapter<WeekHolder> {

        private List<TimeEntryWeek> mWeek = new ArrayList<>();

        public WeekAdapter(List<TimeEntryWeek> week) {
            mWeek = week;
        }

        @Override
        public WeekHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new WeekHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(WeekHolder holder, int position) {
            TimeEntryWeek week = mWeek.get(position);
            holder.bind(week);
        }

        @Override
        public int getItemCount() {
            return mWeek.size();
        }
    }

    /*
    *   Day holder and adapter, for showing detailed time breakdown within the card
     */
    private class DayHolder extends RecyclerView.ViewHolder {

        private TextView mText;

        public DayHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_time_entry_detail, parent, false));

            mText = (TextView) itemView.findViewById(R.id.time_entry_project_description);
        }

        public void bind(TimeEntryDay day) {
            mText.setText(day.getCustomer());
        }
    }

    private class DayAdapter extends RecyclerView.Adapter<DayHolder> {

        public DayAdapter(List<TimeEntryDay> day) {
            mDay = day;
        }

        @Override
        public DayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new DayHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(DayHolder holder, int position) {

//            TimeEntryDay day = mDay.get(position);
//            holder.bind(day);
        }

        @Override
        public int getItemCount() {
            return mDay.size();
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
            mDay = s;
            setupDayAdapter();

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
