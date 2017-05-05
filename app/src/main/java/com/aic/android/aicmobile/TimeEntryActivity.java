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
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.WEEK_OF_YEAR, mTime.get(position).getWeekNumber());

                mWeekLabel.setText(sdf.format(cal.getTime()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}
