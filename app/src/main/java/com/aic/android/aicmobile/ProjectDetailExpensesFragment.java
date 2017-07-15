package com.aic.android.aicmobile;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.res.Resources;
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
import android.widget.TextView;

import com.aic.android.aicmobile.backend.aicDataAPI.model.Expenses;
import com.aic.android.aicmobile.backend.aicDataAPI.AicDataAPI;
import com.aic.android.aicmobile.backend.aicDataAPI.model.Projects;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
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

    private ExpenseAdapter mExpenseAdapter;

    private List<Expenses> mExpenses;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProjects.setProjectNum((int) getArguments().getSerializable(PROJ_NUMBER));
        updateExpenses();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_project_detail_expenses, container, false);

        mExpenseRecyclerView = (RecyclerView) v.findViewById(R.id.project_detail_expenses_recycler_view);
        mExpenseRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;
    }

    public void updateExpenses() {
        //Call Async task here
        new DownloadExpenses().execute();
    }

    public void setupAdapter() {
        if (isAdded()) {
            mExpenseAdapter = new ExpenseAdapter(mExpenses);
            mExpenseRecyclerView.setAdapter(mExpenseAdapter);
        }
    }

    private class ExpenseHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mVendor;
        private TextView mCategory;
        private TextView mAmount;
        private TextView mNotes;

        DecimalFormat df = new DecimalFormat("#,###,###.00");

        public ExpenseHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_project_detail_expenses, parent, false));
            itemView.setOnClickListener(this);

            mVendor = (TextView) itemView.findViewById(R.id.project_detail_expenses_vendor);
            mCategory = (TextView) itemView.findViewById(R.id.project_detail_expenses_category);
            mAmount = (TextView) itemView.findViewById(R.id.project_detail_expenses_amount);
            mNotes = (TextView) itemView.findViewById(R.id.project_detail_expenses_notes);

        }

        public void bind(Expenses expense) {

            mVendor.setText(expense.getVendor());
            mCategory.setText(expense.getCategory());
            mAmount.setText(getResources().getString(R.string.project_detail_expenses_amount, df.format(expense.getAmount())));
            mNotes.setText(expense.getNotes());

        }

        @Override
        public void onClick(View v) {
//            startProjectDetail(mProject);
        }
    }

    private class ExpenseAdapter extends RecyclerView.Adapter<ExpenseHolder> {

        private List<Expenses> mExpenses;


        public ExpenseAdapter(List<Expenses> expense) {

            mExpenses = expense;
        }

        @Override
        public ExpenseHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ProjectDetailExpensesFragment.ExpenseHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ProjectDetailExpensesFragment.ExpenseHolder holder, int position) {

            Expenses expenses = mExpenses.get(position);
            holder.bind(expenses);
        }

        @Override
        public int getItemCount() {
            return mExpenses.size();
        }

    }

    /*
    Asychrounous task to download project list
     */

    private class DownloadExpenses extends AsyncTask<Void, Void, List<Expenses>> {
        private AicDataAPI myApiService = null;
        private static final String TAG = "EndpointsAsyncTask";
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
//            this.dialog.setMessage("Loading projects");
//            this.dialog.show();
        }

        @Override
        protected List<Expenses> doInBackground(Void... params) {
            if (myApiService == null) {
                AicDataAPI.Builder builder = new AicDataAPI.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        //Root url of google cloud project
                        .setRootUrl(APP_URL);
                myApiService = builder.build();
            }

            Log.i(TAG, "Project number is: " + mProjects.getProjectNum());

            try {
                List<Expenses> raw = myApiService.getExpenses(mProjects.getProjectNum()).execute().getItems();
                return parseItems(raw);
            } catch (IOException e) {
                Log.i(TAG, "IO Exception", e);
                List<Expenses> fail = new ArrayList<>();
                return fail;
            }
        }

        @Override
        protected void onPostExecute(List<Expenses> s) {
            mExpenses = s;
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
    private List<Expenses> parseItems(List<Expenses> raw) {

        List<Expenses> expenses = new ArrayList<>();

        for (int i = 0; i < raw.size(); i++) {

            Expenses expense = new Expenses();
            expense.setCategory(raw.get(i).getCategory());
            expense.setVendor(raw.get(i).getVendor());
            expense.setNotes(raw.get(i).getNotes());
            expense.setDate(raw.get(i).getDate());
            expense.setAmount(raw.get(i).getAmount());
            expense.setPoNumber(raw.get(i).getPoNumber());

            expenses.add(expense);
        }
        return expenses;
    }
}
