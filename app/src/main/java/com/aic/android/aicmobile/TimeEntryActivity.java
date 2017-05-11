package com.aic.android.aicmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by JLM on 4/9/2017.
 */

public class TimeEntryActivity extends AppCompatActivity /* SingleFragmentActivity*/ {

    private ViewPager mViewPager;
    private TextView mWeekLabel;
    private List<TimeEntryTime> mTime;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_entry);

        mTime = TimeEntryWeekLoader.initializeTimeList();

        mViewPager = (ViewPager) findViewById(R.id.time_entry_view_pager);
        mWeekLabel = (TextView) findViewById(R.id.time_entry_week_label);

        mViewPager.setPadding(16,16,16,16);

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                TimeEntryTime time = mTime.get(position);
                return TimeEntryFragment.newInstance(time.getWeekNumber());
            }

            @Override
            public int getCount() {
                return mTime.size();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateLabel(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        updateLabel(mViewPager.getCurrentItem());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    // Update label at top of page
    public void updateLabel(int position) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
        Calendar cal = Calendar.getInstance();

        // Set week from position
        cal.set(Calendar.WEEK_OF_YEAR, mTime.get(position).getWeekNumber());

        // Get day of week and find sunday
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DAY_OF_WEEK, 1 - dayOfWeek);

        String startDate = sdf.format(cal.getTime());

        cal.add(Calendar.DAY_OF_WEEK, 6);
        String endDate = sdf.format(cal.getTime());

        // Update label
        mWeekLabel.setText(startDate + " - " + endDate);
    }

}
