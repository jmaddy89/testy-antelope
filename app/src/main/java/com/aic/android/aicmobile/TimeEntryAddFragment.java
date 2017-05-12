package com.aic.android.aicmobile;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by JLM on 5/12/2017.
 */

public class TimeEntryAddFragment extends DialogFragment {


    public static TimeEntryAddFragment newInstance() {
        TimeEntryAddFragment fragment = new TimeEntryAddFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {


        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time_entry_form, null);

        // Set title for this dialog
        getDialog().setTitle("Add New Time Entry");

        return v;
    }
}
