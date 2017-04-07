package com.aic.android.aicmobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.aic.android.aicmobile.backend.aicDataAPI.model.Customers;
import com.aic.android.aicmobile.backend.aicDataAPI.AicDataAPI;
import com.aic.android.aicmobile.backend.aicDataAPI.model.Projects;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jordan on 4/2/2017.
 */

public class AsyncTasks {





    // Get project information for Project Detail Activity
    public class getProjectDetails extends AsyncTask<Void, Void, List<Projects>> {
        private AicDataAPI myApiService = null;
        private static final String TAG = "EndpointsAsyncTask";
//        private ProgressDialog dialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
//            this.dialog.setMessage("Loading projects");
//            this.dialog.show();
        }

        @Override
        protected List<Projects> doInBackground(Void... params) {
            if (myApiService == null) {
                AicDataAPI.Builder builder = new AicDataAPI.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        //Root url of google cloud project
                        .setRootUrl("https://i-melody-158021.appspot.com/_ah/api/");
                myApiService = builder.build();
            }


            try {
                Log.i(TAG, "made it to the async task bit");
                List<Projects> raw = myApiService.projectQuery().execute().getItems();
                return raw;
            } catch (IOException e) {
                Log.i(TAG, "IO Exception", e);
                List<Projects> fail = new ArrayList<>();
                return fail;
            }
        }

        @Override
        protected void onPostExecute(List<Projects> s) {
//            mOriginalProjects = s;
//            mProjects = s;
//            setupAdapter();

            // Dismiss loading dialog
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }



            try {
                Log.i(TAG, "Response from google endpoint: " + s.toString());
            } catch (Exception e) {
                Log.i(TAG, "Response from google endpoint is null: " + e.toString());
            }
        }
    }

}
