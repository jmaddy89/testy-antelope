package com.aic.android.aicmobile;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.aic.android.aicmobile.backend.aicDataAPI.model.Deliverables;
import com.aic.android.aicmobile.backend.aicDataAPI.AicDataAPI;
import com.aic.android.aicmobile.backend.aicDataAPI.model.Projects;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by JLM on 5/12/2017.
 */

public class TimeEntryAddFragment extends DialogFragment {

    private static final String TAG = "TimeEntryAddFragment";

    private static final String ARG_DATE = "date";

    private static final String APP_URL = "https://aic-mobile-5fdf1.appspot.com/_ah/api/";

    private Spinner mProjectSpinner;
    private Spinner mDeliverableSpinner;
    private Spinner mRoleSpinner;

    private TextView mDateText;

    private EditText mHourText;
    private EditText mNoteText;

    private List<Projects> mProjects;

    private List<String> mProjectArray = new ArrayList<>();
    private List<String> mDeliverableArray = new ArrayList<>();

    private ArrayAdapter<String> mProjectAdapter;
    private ArrayAdapter<String> mDeliverableAdapter;


    // Create new instance from Time Entry Fragment
    public static TimeEntryAddFragment newInstance(Date date) {

        // Create bundle and put date into bundle
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        // Create fragment and bundle date into fragment
        TimeEntryAddFragment fragment = new TimeEntryAddFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        // Retrieve date from calling button found in bundle
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        // Create calendar object and set to date found in bundle
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        // Set Simple Date Format object
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d", Locale.US);

        // Create view to inflate dialog
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time_entry_form, null);

        // Initialize spinners
        mProjectSpinner = (Spinner) v.findViewById(R.id.time_entry_dialog_project_select);
        mDeliverableSpinner = (Spinner) v.findViewById(R.id.time_entry_dialog_deliverable_select);
        mRoleSpinner = (Spinner) v.findViewById(R.id.time_entry_dialog_role_select);

        // Initialize text view and edit views
        mDateText = (TextView) v.findViewById(R.id.time_entry_dialog_date_select);
        mHourText = (EditText) v.findViewById(R.id.time_entry_dialog_hours_select);
        mNoteText = (EditText) v.findViewById(R.id.time_entry_dialog_note_text);

        // Set on item selected listener to request deliverables for selected project
        mProjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // If select project header is selected again, clear out deliverables list
                if (position == 0) {
                    mDeliverableArray.clear();
                    mDeliverableAdapter.notifyDataSetChanged();
                }

                // Position 0 is the select projects header, -1 because first in the list is select project
                if (position > 0) {
                    new updateDeliverableList().execute(mProjects.get(position - 1).getProjectNum());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mDeliverableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Selected deliverable is: " + mDeliverableArray.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set date text to date passed in from time entry fragment
        mDateText.setText(sdf.format(date));

        mProjectAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, mProjectArray);
        mProjectSpinner.setAdapter(mProjectAdapter);

        mDeliverableAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, mDeliverableArray);
        mDeliverableSpinner.setAdapter(mDeliverableAdapter);

        new updateProjectList().execute();


        return v;
    }

    private class updateProjectList extends AsyncTask<Void, Void, List<Projects>> {
        private AicDataAPI myApiService = null;
        private static final String TAG = "EndpointsAsyncTask";
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
//            Snackbar.make(getActivity().findViewById(android.R.id.content), "Loading time", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null)
//                    .show();
            this.dialog.setMessage("Loading project list");
            this.dialog.show();
        }

        @Override
        protected List<Projects> doInBackground(Void... params) {
            if (myApiService == null) {
                AicDataAPI.Builder builder = new AicDataAPI.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        //Root url of google cloud project
                        .setRootUrl(APP_URL);
                myApiService = builder.build();
            }

            try {
                List<Projects> raw = myApiService.projectQuery().execute().getItems();
                return parseProjects(raw);
            } catch (IOException e) {
                Log.i(TAG, "IO Exception", e);
                List<Projects> fail = new ArrayList<>();
                return fail;
            }
        }

        @Override
        protected void onPostExecute(List<Projects> s) {
            mProjects = s;
//            setupAdapters();
//            updateWeekTotals(mDay);

            // Dismiss loading dialog
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            // First entry is default select
            mProjectArray.add("Select Project");

            // Loop through results and add to customer array for spinner
            for (int i = 0; i < s.size(); i++) {
                mProjectArray.add(s.get(i).getProjectNum() + " - " + s.get(i).getCustomer() + " - " + s.get(i).getProjectDesc());
            }

            Log.i(TAG, "Response from endpoint: " + s);
            // Notify spinner of updated information
            mProjectAdapter.notifyDataSetChanged();

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
    private List<Projects> parseProjects(List<Projects> raw) {

        List<Projects> projects = new ArrayList<>();

        for (int i = 0; i < raw.size(); i++) {

            Projects proj = new Projects();
            proj.setProjectNum(raw.get(i).getProjectNum());
            proj.setCustomer(raw.get(i).getCustomer());
            proj.setProjectDesc(raw.get(i).getProjectDesc());
            proj.setBudget(raw.get(i).getBudget());
            proj.setBurn(raw.get(i).getBurn());
            proj.setProjectStatus(raw.get(i).getProjectStatus());
            proj.setAicContact(raw.get(i).getAicContact());
            proj.setCustomerContact(raw.get(i).getCustomerContact());

            projects.add(proj);
        }
        return projects;
    }


    private class updateDeliverableList extends AsyncTask<Integer, Void, List<Deliverables>> {
        private AicDataAPI myApiService = null;
        private static final String TAG = "EndpointsAsyncTask";
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
//            Snackbar.make(getActivity().findViewById(android.R.id.content), "Loading time", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null)
//                    .show();
            this.dialog.setMessage("Loading project list");
            this.dialog.show();
        }

        @Override
        protected List<Deliverables> doInBackground(Integer... params) {
            if (myApiService == null) {
                AicDataAPI.Builder builder = new AicDataAPI.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        //Root url of google cloud project
                        .setRootUrl(APP_URL);
                myApiService = builder.build();
            }

            int projectNumber = params[0];
            try {
                List<Deliverables> raw = myApiService.getDeliverables(projectNumber).execute().getItems();
                return parseDeliverables(raw);
            } catch (IOException e) {
                Log.i(TAG, "IO Exception", e);
                List<Deliverables> fail = new ArrayList<>();
                return fail;
            }
        }

        @Override
        protected void onPostExecute(List<Deliverables> s) {

            // Dismiss loading dialog
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            // Clear deliverable list to start so when changing project selection, deliverables don't stack
            mDeliverableArray.clear();

            // Add first entry to deliverable array
            mDeliverableArray.add("Select Deliverable");

            // Loop through results and add to customer array for spinner
            for (int i = 0; i < s.size(); i++) {
                mDeliverableArray.add(s.get(i).getDeliverableDesc());
            }

            Log.i(TAG, "Response from endpoint: " + s);
            // Notify spinner of updated information
            mDeliverableAdapter.notifyDataSetChanged();

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
    private List<Deliverables> parseDeliverables(List<Deliverables> raw) {

        List<Deliverables> deliverables = new ArrayList<>();

        for (int i = 0; i < raw.size(); i++) {

            Deliverables deliverable = new Deliverables();
            deliverable.setProjectNumber(raw.get(i).getProjectNumber());
            deliverable.setDeliverableId(raw.get(i).getDeliverableId());
            deliverable.setCoNumber(raw.get(i).getCoNumber());
            deliverable.setOptionNumber(raw.get(i).getOptionNumber());
            deliverable.setDeliverableDesc(raw.get(i).getDeliverableDesc());

            deliverables.add(deliverable);
        }
        return deliverables;
    }
}
