package com.aic.android.aicmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aic.android.aicmobile.backend.aicDataAPI.model.TimeEntryDay;

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
    private TimeAdapter mTimeAdapter;
    private List<TimeEntryTime> mWeek = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_time_entry, container, false);

        mTimeRecyclerView = (RecyclerView) view.findViewById(R.id.time_entry_recycler_view);
        mTimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mWeek = TimeEntryWeekLoader.initializeTimeList();

        mTimeAdapter = new TimeAdapter(mWeek);
        mTimeRecyclerView.setAdapter(mTimeAdapter);

        getActivity().setTitle("Time Entry");

        return view;
    }

    /*
        Time holder and adapter, recyclerview for entire page which holds horizontal recyclerviews
     */
    private class TimeHolder extends RecyclerView.ViewHolder {

        private TextView mTimeEntryWeekDate;
        private RecyclerView mDayRecyclerView;
        private WeekAdapter mWeekAdapter;
        private List<TimeEntryWeek> mWeek = new ArrayList<>();


        public TimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_time_entry, parent, false));

            mTimeEntryWeekDate = (TextView) itemView.findViewById(R.id.time_entry_week_date);
            mDayRecyclerView = (RecyclerView) itemView.findViewById(R.id.time_entry_week_recycler_view);

            LinearLayoutManager manager = new LinearLayoutManager(getActivity());
            // 0 = Horizontal orientation
            manager.setOrientation(0);
            mDayRecyclerView.setLayoutManager(manager);
            mWeek = TimeEntryWeekLoader.initializeWeekList();
            mWeekAdapter = new WeekAdapter(mWeek);
            mDayRecyclerView.setAdapter(mWeekAdapter);
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
        private Button mAddNew;
        private RecyclerView mDayRecyclerView;
        private DayAdapter mDayAdapter;


        public WeekHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_time_entry_day, parent, false));

            mDayText = (TextView) itemView.findViewById(R.id.time_entry_day_header);

            mDayRecyclerView = (RecyclerView) itemView.findViewById(R.id.time_entry_day_recycler_view);
            mDayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            mDayAdapter = new DayAdapter(mDay);

            mDayRecyclerView.setAdapter(mDayAdapter);
        }

        public void bind(TimeEntryWeek week) {
            mDayText.setText(week.getDayName());
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
            super(inflater.inflate(R.layout.list_item_time_entry_day, parent, false));

            mText = (TextView) itemView.findViewById(R.id.time_entry_day_header);
        }

        public void bind(TimeEntryDay day) {
            mText.setText(day.getCustomer());
        }
    }

    private class DayAdapter extends RecyclerView.Adapter<DayHolder> {

        private List<TimeEntryDay> mDay = new ArrayList<>();

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
            TimeEntryDay day = mDay.get(position);
            holder.bind(day);
        }

        @Override
        public int getItemCount() {
            return mDay.size();
        }
    }
}
