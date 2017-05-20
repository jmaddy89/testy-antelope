package com.aic.android.aicmobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aic.android.aicmobile.backend.aicDataAPI.AicDataAPI;
import com.aic.android.aicmobile.backend.aicDataAPI.model.MainPage;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Page";

    private static final String APP_URL = "https://aic-mobile-5fdf1.appspot.com/_ah/api/";
    private static final String ARG_DATE = "date";

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    private TextView mActiveProjectCount;
    private TextView mCompleteProjectCount;
    private TextView mOpenRFQCount;
    private CardView mProjectCard;
    private CardView mTimeEntryCard;
    private CardView mRFQCard;
    private Button mAddTimeEntry;
    private Button mAddRFQ;

    private MainPage mMainPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Projects card, select leads to project list recycler view
        mProjectCard = (CardView) findViewById(R.id.project_card_view);
        mProjectCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProjects();
            }
        });

        //Time Entry card, select leads to time entry activity
        mTimeEntryCard = (CardView) findViewById(R.id.time_entry_card_view);
        mTimeEntryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimeEntry();
            }
        });

        mAddTimeEntry = (Button) findViewById(R.id.time_entry_add_new_button);
        mAddTimeEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTimeEntry();
            }
        });

        //RFQ card
        mRFQCard = (CardView) findViewById(R.id.rfq_card_view);

        // Add new RFQ button goes to form to add a new RFQ
        mAddRFQ = (Button) findViewById(R.id.rfq_add_new_button);
        mAddRFQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddRFQ();
            }
        });

        //Initialize text views
        mActiveProjectCount = (TextView) findViewById(R.id.project_content_active_count);
        mCompleteProjectCount = (TextView) findViewById(R.id.project_content_complete_count);
        mOpenRFQCount = (TextView) findViewById(R.id.rfq_content_incomplete_count);

        //Start async task to get project count
        new getMainPage().execute();

    }

    public void addNewTimeEntry() {
        // Set calendar object to get correct date for time entry dialog
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();

        // Create bundle and put date into bundle
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        // Create fragment and bundle date into fragment
        FragmentManager fm = getSupportFragmentManager();
        TimeEntryAddFragment fragment = new TimeEntryAddFragment();

        fragment.setArguments(args);
        fragment.show(fm, "Add");
        }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    //Start projects starts the project list activity
    public void startProjects() {
        Intent i = new Intent(this, ProjectsActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    //Start time entry activty
    public void startTimeEntry() {
        Intent i = new Intent(this, TimeEntryActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    //Start Add RFQ activity
    public void startAddRFQ() {
        Intent i = new Intent(this, RFQAddActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    public void updateCards() {
        mActiveProjectCount.setText(getString(R.string.project_content_active_count, mMainPage.getActiveProjects()));
        mCompleteProjectCount.setText(getString(R.string.project_content_complete_count, mMainPage.getCompleteProjects()));
        mOpenRFQCount.setText(getString(R.string.rfq_content_incomplete_count, mMainPage.getIncompleteRFQs()));
    }

    private class getMainPage extends AsyncTask<Void, Void, MainPage> {
        private AicDataAPI myApiService = null;

        @Override
        protected void onPreExecute() {
            Snackbar.make(findViewById(android.R.id.content), "Refreshing Data", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();

        }

        @Override
        protected MainPage doInBackground(Void... params) {
            if (myApiService == null) {
                AicDataAPI.Builder builder = new AicDataAPI.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        //Root url of google cloud project
                        .setRootUrl(APP_URL);
                myApiService = builder.build();
            }

            try {
                return myApiService.getMainPage().execute();
            } catch (IOException e) {

                return new MainPage();
            }
        }

        @Override
        protected void onPostExecute(MainPage s) {
            mMainPage = s;
            updateCards();
        }
    }


}
