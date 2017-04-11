package com.aic.android.aicmobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.aic.android.aicmobile.backend.aicDataAPI.model.Projects;
import com.aic.android.aicmobile.backend.aicDataAPI.AicDataAPI;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorda on 3/28/2017.
 */

public class ProjectsListFragment extends Fragment {

    private static final String TAG = "ProjectsListFragment";
    private static final String PROJ_NUMBER = "0";
    private static final String PROJ_CUSTOMER = "AIC";
    private static final String PROJ_DESCRIPTION = "Mobile";

    private RecyclerView mProjectRecyclerView;
    private ProjectAdapter mAdapter;
    private Callbacks mCallbacks;
    private List<Projects> mProjects = new ArrayList<>();
    //The unfiltered copy of the projects list
    private List<Projects> mOriginalProjects = new ArrayList<>();

    public interface Callbacks {
        void onProjectSelected(Projects projects);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //Enable options menu
        setHasOptionsMenu(true);

        updateItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_projects_list, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        mProjectRecyclerView = (RecyclerView) view.findViewById(R.id.project_recycler_view);
        mProjectRecyclerView.setLayoutManager(layoutManager);




        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_project_list, menu);

        MenuItem searchItem = menu.findItem(R.id.project_list_search_item);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "Query text submitted: " + query);

//                updateAdapter(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                updateAdapter(query);
                return true;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    public void updateItems() {
        //Call Async task here
        new DownloadProjectList().execute();
    }

    public void startProjectDetail(Projects project) {
        Intent i = new Intent(getActivity(), ProjectDetailActivity.class);
        i.putExtra(PROJ_NUMBER, project.getProjectNum().toString());
        i.putExtra(PROJ_CUSTOMER, project.getCustomer());
        i.putExtra(PROJ_DESCRIPTION, project.getProjectDesc());

        startActivity(i);
    }



    public void setupAdapter() {

        if (isAdded()) {
            Log.i(TAG, "adding adapter");
            mAdapter = new ProjectAdapter(mProjects);
            mProjectRecyclerView.setAdapter(mAdapter);
        }
    }

    public void updateAdapter(String query) {
        mProjects = filter(mOriginalProjects, query);
        Log.i(TAG, "Updated list is: " + mProjects);
        Log.i(TAG, "Query is: " + query);

//        mAdapter.notifyDataSetChanged();
        // This is currently working but rebuilds the adapter everytime any data is changed. Need
        // to get the notfydatasetchanged() to work
        setupAdapter();
    }

    private class ProjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mProjectNumber;
        private TextView mCustomer;
        private TextView mDescription;
        private TextView mBudget;
        private TextView mBurn;
        private TextView mBurnPercent;
        private TextView mProjectStatus;
        private ImageView mCustomerIcon;
        private ProgressBar mBurnProgress;
        private Projects mProject;

        public ProjectHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_projects_card, parent, false));
            itemView.setOnClickListener(this);

            mProjectNumber = (TextView) itemView.findViewById(R.id.project_list_number);
            mCustomer = (TextView) itemView.findViewById(R.id.project_list_customer);
            mDescription = (TextView) itemView.findViewById(R.id.project_list_description);

            mCustomerIcon = (ImageView) itemView.findViewById(R.id.project_customer_icon);

            mBudget = (TextView) itemView.findViewById(R.id.project_budget);
            mBurn = (TextView) itemView.findViewById(R.id.project_burn);
            mBurnPercent = (TextView) itemView.findViewById(R.id.project_burn_percent);
            mBurnProgress = (ProgressBar) itemView.findViewById(R.id.project_burn_progress);
            mProjectStatus = (TextView) itemView.findViewById(R.id.project_status);
        }

        public void bind(Projects project) {
            Resources res = getResources();

            mProject = project;

            mProjectNumber.setText(mProject.getProjectNum().toString());
            mCustomer.setText(mProject.getCustomer());
            mDescription.setText(mProject.getProjectDesc());

            mBudget.setText(res.getString(R.string.project_budget, mProject.getBudget().toString()));
            mBurn.setText(res.getString(R.string.project_burn, mProject.getBurn().toString()));

            if (mProject.getProjectStatus() == 1) {
                mProjectStatus.setText("Active");
            } else if (mProject.getProjectStatus() == 2) {
                mProjectStatus.setText("Complete");
            }

            try {
                String icon = "ic_" + mProject.getCustomer().replaceAll(" ", "_").toLowerCase();
                int resId = res.getIdentifier(icon, "drawable", "com.aic.android.aicmobile");
                mCustomerIcon.setImageResource(resId);
            } catch (Exception e){
                Log.e(TAG, "Failed to load icon: ", e);
            }

//            float burnPercent = mProject.getBurn() / mProject.getBudget();
//            mBurnPercent.setText(res.getString(R.string.project_burn_percent, burnPercent));

            mBurnProgress.setMax(mProject.getBudget().intValue());
            mBurnProgress.setProgress(mProject.getBurn().intValue());

//            if (mProject.getBurn() > mProject.getBudget()) {
//                int colorId = res.getColor(R.color.project_list_progress_over);
//                mBurnProgress.getProgressDrawable().setColorFilter(colorId, PorterDuff.Mode.SRC_IN);
//            }

        }

        @Override
        public void onClick(View v) {
            startProjectDetail(mProject);
        }
    }

    private class ProjectAdapter extends RecyclerView.Adapter<ProjectHolder> {

        private List<Projects> mProjects;


        public ProjectAdapter(List<Projects> projects) {

            mProjects = projects;
        }

        @Override
        public ProjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ProjectHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ProjectHolder holder, int position) {

            Projects project = mProjects.get(position);
            holder.bind(project);
        }

        @Override
        public int getItemCount() {
            return mProjects.size();
        }

    }

    private static List<Projects> filter(List<Projects> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Projects> filteredProjects = new ArrayList<>();
        for (Projects model : models) {
            final String customer = model.getCustomer().toLowerCase();
            final String projectNumber = String.valueOf(model.getProjectNum());

            if (customer.contains(lowerCaseQuery) || projectNumber.contains(lowerCaseQuery)) {
                filteredProjects.add(model);
            }
        }
        return filteredProjects;
    }

    /*
    Asychrounous task to download project list
     */
    private class DownloadProjectList extends AsyncTask<Void, Void, List<Projects>> {
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
                        .setRootUrl("https://i-melody-158021.appspot.com/_ah/api/");
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
