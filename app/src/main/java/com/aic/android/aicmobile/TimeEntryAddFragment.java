package com.aic.android.aicmobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aic.android.aicmobile.backend.aicDataAPI.model.NewTimeEntry;
import com.aic.android.aicmobile.backend.aicDataAPI.model.TimeEntryRoles;
import com.aic.android.aicmobile.backend.aicDataAPI.model.Deliverables;
import com.aic.android.aicmobile.backend.aicDataAPI.AicDataAPI;
import com.aic.android.aicmobile.backend.aicDataAPI.model.Projects;
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
 * Created by JLM on 5/12/2017.
 * Time Entry Dialog - Entry form used to submit new time entries. Called from Time Entry Fragment and Main Page
 */

public class TimeEntryAddFragment extends DialogFragment {
    private DialogInterface.OnDismissListener onDismissListener;

    private static final String TAG = "TimeEntryAddFragment";

    private static final String ARG_DATE = "date";
    private static final String APP_URL = "https://aic-mobile-5fdf1.appspot.com/_ah/api/";
    private static final String DATE_PICKER_HEADER = "Time Entry Date";

    private static final int REQUEST_DATE = 0;

    private Date mDate;

    private Spinner mProjectSpinner;
    private Spinner mDeliverableSpinner;
    private Spinner mRoleSpinner;

    private TextView mDateText;
    private TextView mProjectLoadingText;
    private TextView mDeliverableLoadingText;
    private TextView mRoleLoadingText;

    private ProgressBar mProjectLoadingProgress;
    private ProgressBar mDeliverableLoadingProgress;
    private ProgressBar mRoleLoadingProgress;

    private EditText mHourText;
    private EditText mNoteText;

    private Button mSubmit;

    private List<Projects> mProjects;
    private List<Deliverables> mDeliverables;
    private List<TimeEntryRoles> mRoles;

    private List<String> mProjectArray = new ArrayList<>();
    private List<String> mDeliverableArray = new ArrayList<>();
    private List<String> mRolesArray = new ArrayList<>();

    private ArrayAdapter<String> mProjectAdapter;
    private ArrayAdapter<String> mDeliverableAdapter;
    private ArrayAdapter<String> mRolesAdapter;

    // Interface to use onDismissListener, used by TimeEntryFragment for refresh after submission
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        // Retrieve date from calling button found in bundle
        mDate = (Date) getArguments().getSerializable(ARG_DATE);

        // Create calendar object and set to date found in bundle
        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);

        // Set Simple Date Format object
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d", Locale.US);

        // Create view to inflate dialog
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time_entry_form, container, false);

        // Initialize spinners
        mProjectSpinner = (Spinner) v.findViewById(R.id.time_entry_dialog_project_select);
        mDeliverableSpinner = (Spinner) v.findViewById(R.id.time_entry_dialog_deliverable_select);
        mRoleSpinner = (Spinner) v.findViewById(R.id.time_entry_dialog_role_select);

        // Initialize text view and edit views
        mDateText = (TextView) v.findViewById(R.id.time_entry_dialog_date_select);
        mHourText = (EditText) v.findViewById(R.id.time_entry_dialog_hours_select);
        mNoteText = (EditText) v.findViewById(R.id.time_entry_dialog_note_text);
        mProjectLoadingText = (TextView) v.findViewById(R.id.time_entry_dialog_loading_projects_label);
        mDeliverableLoadingText = (TextView) v.findViewById(R.id.time_entry_dialog_loading_deliverables_label);
        mRoleLoadingText = (TextView) v.findViewById(R.id.time_entry_dialog_loading_role_label);

        // Initialize submit button and set disabled
        mSubmit = (Button) v.findViewById(R.id.time_entry_dialog_submit);
        mSubmit.setEnabled(false);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitTime();
            }
        });

        // Text change listeners to change state of submit button once data is entered
        mHourText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableSubmit();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mNoteText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableSubmit();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Initialize loading progress spinner
        mProjectLoadingProgress = (ProgressBar) v.findViewById(R.id.time_entry_dialog_loading_projects_progress);
        mDeliverableLoadingProgress = (ProgressBar) v.findViewById(R.id.time_entry_dialog_loading_deliverables_progress);
        mRoleLoadingProgress = (ProgressBar) v.findViewById(R.id.time_entry_dialog_loading_role_progress);

        // Set on item selected listener to request deliverables for selected project
        mProjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Item position is: " + mProjectSpinner.getSelectedItemPosition());

                // If select project header is selected again, clear out deliverables list
                if (position == 0) {
                    mDeliverableArray.clear();
                    mDeliverableAdapter.notifyDataSetChanged();
                }

                // Position 0 is the select projects header, -1 because first in the list is select project
                if (position > 0) {
                    new updateDeliverableList().execute(mProjects.get(position - 1).getProjectNum());
                }

                enableSubmit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mDeliverableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Selected deliverable is: " + mDeliverableArray.get(position));
                enableSubmit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set date text to date passed in from time entry fragment
        formatDateText(mDate);
        mDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        // Setup spinner adapters
        mProjectAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, mProjectArray);
        mProjectSpinner.setAdapter(mProjectAdapter);

        mDeliverableAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, mDeliverableArray);
        mDeliverableSpinner.setAdapter(mDeliverableAdapter);

        mRolesAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, mRolesArray);
        mRoleSpinner.setAdapter(mRolesAdapter);

        // Set visibility to gone to start
        mProjectLoadingText.setVisibility(View.GONE);
        mProjectLoadingProgress.setVisibility(View.GONE);

        mDeliverableLoadingText.setVisibility(View.GONE);
        mDeliverableLoadingProgress.setVisibility(View.GONE);

        mRoleLoadingText.setVisibility(View.GONE);
        mRoleLoadingProgress.setVisibility(View.GONE);


        // Call update to projects list
        new updateProjectList().execute();

        // Call update to roles list
        new updateRoleList().execute();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            // Get date from popup calendar
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);

            // Update date
            mDate = date;

            // Update date text
            formatDateText(date);
        }
    }

    public void formatDateText(Date date) {
        // Set Simple Date Format object
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d", Locale.US);
        mDateText.setText(sdf.format(date));
    }

    // Runs when dialog is dismissed, doesn't run when back button is pressed
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    // Checks to see if it is okay to enable submit button
    public void enableSubmit() {
        if (mProjectSpinner.getSelectedItemPosition() > 0 &&
                mDeliverableSpinner.getSelectedItemPosition() > 0 &&
                (!mHourText.getText().toString().matches("")) &&
                (!mNoteText.getText().toString().matches(""))) {
            mSubmit.setEnabled(true);
        } else {
            mSubmit.setEnabled(false);
        }
    }

    public void submitTime() {
        int roleSpinnerIndex = mRoleSpinner.getSelectedItemPosition();
        int deliverableSpinnerIndex = mDeliverableSpinner.getSelectedItemPosition();
        int projectSpinnerIndex = mProjectSpinner.getSelectedItemPosition();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        NewTimeEntry timeEntry = new NewTimeEntry();
        // Set info from roles array
        timeEntry.setRoleId(mRoles.get(roleSpinnerIndex).getRoleId());
        timeEntry.setUserId(mRoles.get(roleSpinnerIndex).getUserId());

        // Set info from deliverable array, - 1 because of added select deliverable added on front of array
        timeEntry.setDeliverableId(mDeliverables.get(deliverableSpinnerIndex - 1).getDeliverableId());
        timeEntry.setCoNumber(mDeliverables.get(deliverableSpinnerIndex - 1).getCoNumber());
        timeEntry.setOptionNumber(mDeliverables.get(deliverableSpinnerIndex - 1).getOptionNumber());

        // Set info from projects array, - 1 because of added select project added on front of array
        timeEntry.setProjectNumber(mProjects.get(projectSpinnerIndex - 1).getProjectNum());
        timeEntry.setProjectId(mProjects.get(projectSpinnerIndex - 1).getProjectId());

        // Set info from input boxes
        timeEntry.setTime(Float.valueOf(mHourText.getText().toString()));
        timeEntry.setDate(sdf.format(mDate));
        timeEntry.setNote(mNoteText.getText().toString());

        new submitTime().execute(timeEntry);
    }

    public void openDatePicker() {
        DatePickerFragment dp = DatePickerFragment.newInstance(mDate, DATE_PICKER_HEADER);
        dp.setTargetFragment(TimeEntryAddFragment.this, REQUEST_DATE);
        dp.show(getFragmentManager(), "Date Picker");
    }


    private class updateProjectList extends AsyncTask<Void, Void, List<Projects>> {
        private AicDataAPI myApiService = null;

        @Override
        protected void onPreExecute() {

            // Change visibility to show that data is loading
            mProjectLoadingProgress.setVisibility(View.VISIBLE);
            mProjectLoadingText.setVisibility(View.VISIBLE);
            mProjectSpinner.setVisibility(View.INVISIBLE);

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
                List<Projects> fail = new ArrayList<>();
                return fail;
            }
        }

        @Override
        protected void onPostExecute(List<Projects> s) {
            mProjects = s;

            // Change visibility to show that data is done loading
            mProjectLoadingProgress.setVisibility(View.GONE);
            mProjectLoadingText.setVisibility(View.GONE);
            mProjectSpinner.setVisibility(View.VISIBLE);

            // First entry is default select
            mProjectArray.add("Select Project");

            // Loop through results and add to customer array for spinner
            for (int i = 0; i < s.size(); i++) {
                mProjectArray.add(s.get(i).getProjectNum() + " - " + s.get(i).getCustomer() + " - " + s.get(i).getProjectDesc());
            }
            // Notify spinner of updated information
            mProjectAdapter.notifyDataSetChanged();

        }
    }

    /*
    Used for to clean up response from google endpoint
     */
    private List<Projects> parseProjects(List<Projects> raw) {

        List<Projects> projects = new ArrayList<>();

        for (int i = 0; i < raw.size(); i++) {

            Projects proj = new Projects();
            proj.setProjectId(raw.get(i).getProjectId());
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

        @Override
        protected void onPreExecute() {

            // Change visibility to show that data is loading
            mDeliverableLoadingProgress.setVisibility(View.VISIBLE);
            mDeliverableLoadingText.setVisibility(View.VISIBLE);
            mDeliverableSpinner.setVisibility(View.INVISIBLE);
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
                List<Deliverables> fail = new ArrayList<>();
                return fail;
            }
        }

        @Override
        protected void onPostExecute(List<Deliverables> s) {
            mDeliverables = s;

            // Change visibility to show that data is done loading
            mDeliverableLoadingProgress.setVisibility(View.GONE);
            mDeliverableLoadingText.setVisibility(View.GONE);
            mDeliverableSpinner.setVisibility(View.VISIBLE);

            // Clear deliverable list to start so when changing project selection, deliverables don't stack
            mDeliverableArray.clear();

            // Add first entry to deliverable array
            mDeliverableArray.add("Select Deliverable");

            // Loop through results and add to customer array for spinner
            for (int i = 0; i < s.size(); i++) {
                mDeliverableArray.add(s.get(i).getDeliverableDesc());
            }

            // Notify spinner of updated information
            mDeliverableAdapter.notifyDataSetChanged();

        }
    }

    /*
    Used for to clean up response from google endpoint
     */
    private List<Deliverables> parseDeliverables(List<Deliverables> raw) {

        List<Deliverables> deliverables = new ArrayList<>();

        // If deliverables returned are null, then return empty array
        if (raw == null) {
            return deliverables;
        }

        // Loop through results to clean up endpoint response
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

    private class updateRoleList extends AsyncTask<Integer, Void, List<TimeEntryRoles>> {
        private AicDataAPI myApiService = null;

        @Override
        protected void onPreExecute() {
            // Change visibility to show that data is loading
            mRoleLoadingProgress.setVisibility(View.VISIBLE);
            mRoleLoadingText.setVisibility(View.VISIBLE);
            mRoleSpinner.setVisibility(View.INVISIBLE);
        }

        @Override
        protected List<TimeEntryRoles> doInBackground(Integer... params) {
            if (myApiService == null) {
                AicDataAPI.Builder builder = new AicDataAPI.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        //Root url of google cloud project
                        .setRootUrl(APP_URL);
                myApiService = builder.build();
            }

            // Get logged in users firebase id
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userId = user.getUid();

            try {
                List<TimeEntryRoles> raw = myApiService.getRoles(userId).execute().getItems();
                return parseRoles(raw);
            } catch (IOException e) {
                List<TimeEntryRoles> fail = new ArrayList<>();
                return fail;
            }
        }

        @Override
        protected void onPostExecute(List<TimeEntryRoles> s) {
            mRoles = s;

            // Change visibility to show that data is done loading
            mRoleLoadingProgress.setVisibility(View.GONE);
            mRoleLoadingText.setVisibility(View.GONE);
            mRoleSpinner.setVisibility(View.VISIBLE);

            // Clear deliverable list to start so when changing project selection, deliverables don't stack
            mRolesArray.clear();

            // Loop through results and add to customer array for spinner
            for (int i = 0; i < s.size(); i++) {
                mRolesArray.add(s.get(i).getRoleDesc());
            }

            // Notify spinner of updated information
            mRolesAdapter.notifyDataSetChanged();
        }
    }

    /*
    Used for to clean up response from google endpoint
     */
    private List<TimeEntryRoles> parseRoles(List<TimeEntryRoles> raw) {

        List<TimeEntryRoles> roles = new ArrayList<>();

        // If roles returned are null, then return empty array
        if (raw == null) {
            return roles;
        }

        // Loop through results to clean up endpoint response
        for (int i = 0; i < raw.size(); i++) {

            TimeEntryRoles role = new TimeEntryRoles();
            role.setUserId(raw.get(i).getUserId());
            role.setRoleId(raw.get(i).getRoleId());
            role.setRoleDesc(raw.get(i).getRoleDesc());

            roles.add(role);
        }
        return roles;
    }

    // Get customer list for Add RFQ Page
    public class submitTime extends AsyncTask<NewTimeEntry, Void, String> {
        private AicDataAPI myApiService = null;
        private ProgressDialog dialog = new ProgressDialog(getActivity());
//        private Context context;

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Submitting Time Entry");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(NewTimeEntry... params) {
            if (myApiService == null) {
                AicDataAPI.Builder builder = new AicDataAPI.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        //Root url of google cloud project
                        .setRootUrl(APP_URL);
                myApiService = builder.build();
            }

//            context = params[0].first;
            NewTimeEntry timeInfo = params[0];

            try {
                return myApiService.submitTime(timeInfo).execute().getData();
            } catch (IOException e) {
                Log.i(TAG, "IO Exception", e);

                return "fail";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Dismiss loading dialog
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (result.matches("Time entry added successfully!")) {
                dismiss();
            }

            Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
        }
    }
}
