package com.aic.android.aicmobile;

/**
 * Created by JLM on 5/19/2017.
 * Used to manage the tabs on the Project Detail activity
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;


public class SectionPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public SectionPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Overview";
            case 1:
                return "Chat";
            case 2:
                return "Expenses";
            case 3:
                return "Change Orders";
            case 4:
                return "Time Entries";
            default:
                return "Second Tab";
        }
    }
}
