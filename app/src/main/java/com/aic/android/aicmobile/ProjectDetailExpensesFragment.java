package com.aic.android.aicmobile;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aic.android.aicmobile.backend.aicDataAPI.AicDataAPI;
import com.aic.android.aicmobile.backend.aicDataAPI.model.Projects;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JLM on 5/22/2017.
 * Fragment to display expenses on a project. Will have the ability to add new expenses as well.
 */

public class ProjectDetailExpensesFragment extends Fragment {

    private static final String APP_URL = "https://aic-mobile-5fdf1.appspot.com/_ah/api/";
    private static final String PROJ_NUMBER = "0";

    private Projects mProjects = new Projects();

    private RecyclerView mExpenseRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateExpenses();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_project_detail_expenses, container, false);

        mProjects.setProjectNum((int) getArguments().getSerializable(PROJ_NUMBER));

        mExpenseRecyclerView = (RecyclerView) v.findViewById(R.id.project_detail_expenses_recycler_view);
        mExpenseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return v;
    }

    public void updateExpenses() {

    }

    /*
    Asychrounous task to download project list
     */
    /*
    private class DownloadExpenses extends AsyncTask<Void, Void, List<Projects>> {
        private AicDataAPI myApiService = null;
        private static final String TAG = "EndpointsAsyncTask";
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading projects");
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
                Log.i(TAG, "made it to the async task bit");
                List<Projects> raw = myApiService.projectQuery().execute().getItems();
                return parseItems(raw);
            } catch (IOException e) {
                Log.i(TAG, "IO Exception", e);
                List<Projects> fail = new ArrayList<>();
                return fail;
            }
        }

        @Override
        protected void onPostExecute(List<Projects> s) {
            mOriginalProjects = s;
            mProjects = s;
            setupAdapter();

            // Dismiss loading dialog
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            try {
                Log.i(TAG, "Response from google endpoint: " + s.toString());
            } catch (Exception e) {
                Log.i(TAG, "Response from google endpoint is null: " + e.toString());
            }
        }
    }
    *;
    /*
    Used for search button on menu bar
     */
    private List<Projects> parseItems(List<Projects> raw) {

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
}
