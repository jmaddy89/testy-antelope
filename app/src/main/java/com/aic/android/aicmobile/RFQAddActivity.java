package com.aic.android.aicmobile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aic.android.aicmobile.backend.aicDataAPI.AicDataAPI;
import com.aic.android.aicmobile.backend.aicDataAPI.model.Customers;
import com.aic.android.aicmobile.backend.aicDataAPI.model.NewProject;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class RFQAddActivity extends AppCompatActivity {

    private static final String TAG = "RFQAddActivity";
    private static final int REQUEST_DATE = 0;

    private List<String> mCustomerArray = new ArrayList<>();
    private Spinner mCustomerSpinner;
    private ArrayAdapter<String> mCustomerAdapter;
    private EditText mDescription;
    private EditText mCustomerContact;
    private Context mContext = this;
    private Button mSubmitButton;
    private NewProject mNewProject = new NewProject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfqadd);

        // Set up customer spinner
        mCustomerSpinner = (Spinner) findViewById(R.id.rfq_add_customer_dropdown);
        mCustomerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, mCustomerArray);
        mCustomerSpinner.setAdapter(mCustomerAdapter);

        mDescription = (EditText) findViewById(R.id.rfq_add_edit_description);
        mCustomerContact = (EditText) findViewById(R.id.rfq_add_edit_customer_contact);

        // Set up submit button
        mSubmitButton = (Button) findViewById(R.id.rfq_add_submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewProject.setCustomer(mCustomerSpinner.getSelectedItem().toString());
                mNewProject.setDescription(mDescription.getText().toString());
                mNewProject.setCustomerContact(mCustomerContact.getText().toString());
                new submitRFQ().execute(new Pair<>(mContext, mNewProject));
            }
        });

        // Call async task to load customer list dropdown
        new getCustomerList().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
//            mNewProject.setRfqDueDate(date);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }


    // Get customer list for Add RFQ Page
    public class getCustomerList extends AsyncTask<Void, Void, List<Customers>> {
        private AicDataAPI myApiService = null;
        private ProgressDialog dialog = new ProgressDialog(mContext);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading customers");
            this.dialog.show();
        }

        @Override
        protected List<Customers> doInBackground(Void... params) {
            if (myApiService == null) {
                AicDataAPI.Builder builder = new AicDataAPI.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        //Root url of google cloud project
                        .setRootUrl("https://i-melody-158021.appspot.com/_ah/api/");
                myApiService = builder.build();
            }

            try {
                List<Customers> raw = myApiService.getCustomerList().execute().getItems();
                return parseItems(raw);
            } catch (IOException e) {
                Log.i(TAG, "IO Exception", e);
                List<Customers> fail = new ArrayList<>();
                return fail;
            }
        }

        @Override
        protected void onPostExecute(List<Customers> customers) {
            // Dismiss loading dialog
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            // Loop through results and add to customer array for spinner
            for (int i = 0; i < customers.size(); i++) {
                mCustomerArray.add(customers.get(i).getName());
            }

            // Notify spinner of updated information
            mCustomerAdapter.notifyDataSetChanged();
        }
    }

    // Clean up response from google endpoints
    private List<Customers> parseItems(List<Customers> raw) {

        List<Customers> customers = new ArrayList<>();

        for (int i = 0; i < raw.size(); i++) {

            Customers customer = new Customers();
            customer.setId(raw.get(i).getId());
            customer.setName(raw.get(i).getName());

            customers.add(customer);
        }
        return customers;
    }

    // Get customer list for Add RFQ Page
    public class submitRFQ extends AsyncTask<Pair<Context, NewProject>, Void, String> {
        private AicDataAPI myApiService = null;
        private ProgressDialog dialog = new ProgressDialog(mContext);
        private Context context;

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Submitting RFQ");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(Pair<Context, NewProject>... params) {
            if (myApiService == null) {
                AicDataAPI.Builder builder = new AicDataAPI.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        //Root url of google cloud project
                        .setRootUrl("https://i-melody-158021.appspot.com/_ah/api/");
                myApiService = builder.build();
            }

            context = params[0].first;
            NewProject projectInfo = params[0].second;

            try {
                String response = myApiService.submitRFQ(projectInfo).execute().getData();
                return response;
            } catch (IOException e) {
                Log.i(TAG, "IO Exception", e);
                String fail = "fail";
                return fail;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Dismiss loading dialog
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            Log.i(TAG, "Response from google endpoint: " + result);

            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        }
    }
}
