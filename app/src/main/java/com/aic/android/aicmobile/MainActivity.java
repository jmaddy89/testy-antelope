package com.aic.android.aicmobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.aic.android.aicmobile.ProjectsActivity;
import com.aic.android.aicmobile.backend.aicDataAPI.AicDataAPI;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    private TextView mActiveProjectCount;
    private CardView mProjectCard;
    private CardView mTimeEntryCard;
    private CardView mRFQCard;
    private Button mAddTimeEntry;
    private Button mAddRFQ;

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

        mAddTimeEntry = (Button) findViewById(R.id.time_entry_add_new_button);

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

        //Initialize active project count button on project card
        mActiveProjectCount = (TextView) findViewById(R.id.project_active_count);

        //Start async task to get project count
        new getActiveProjectCount().execute();

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

    //Start Add RFQ activity
    public void startAddRFQ() {
        Intent i = new Intent(this, RFQAddActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
    }

    public void updateProjectCard(String active) {
        mActiveProjectCount.setText(active);
    }

    private class getActiveProjectCount extends AsyncTask<Void, Void, String> {
        private AicDataAPI myApiService = null;
        private static final String TAG = "MainActivityAsyncTask";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            if (myApiService == null) {
                AicDataAPI.Builder builder = new AicDataAPI.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        //Root url of google cloud project
                        .setRootUrl("https://i-melody-158021.appspot.com/_ah/api/");
                myApiService = builder.build();
            }


            try {
                Log.i(TAG, "made it to the async task bit");
                return myApiService.activeProjectCount().execute().getData();
            } catch (IOException e) {
                Log.i(TAG, "IO Exception", e);
                String fail = "fail";
                return fail;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            updateProjectCard(s + " active projects");
        }
    }


}
