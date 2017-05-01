package com.aic.android.aicmobile;

import android.app.Activity;
import android.app.FragmentManager;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aic.android.aicmobile.backend.aicDataAPI.model.CustomerContacts;
import com.aic.android.aicmobile.backend.aicDataAPI.AicDataAPI;
import com.aic.android.aicmobile.backend.aicDataAPI.model.Customers;
import com.aic.android.aicmobile.backend.aicDataAPI.model.NewProject;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RFQAddActivity extends AppCompatActivity {

    private static final String TAG = "RFQAddActivity";
    private static final int REQUEST_DATE = 0;
    private static final String DIALOG_DATE = "DialogDate";

    private List<String> mCustomerArray = new ArrayList<>();
    private List<String> mContactArray = new ArrayList<>();

    private Spinner mCustomerSpinner;
    private ArrayAdapter<String> mCustomerAdapter;
    private ArrayAdapter<String> mContactAdapter;
    private EditText mDescription;
    private AutoCompleteTextView mCustomerContact;
    private Context mContext = this;
    private Button mSubmitButton;
    private Button mDateButton;
    private NewProject mNewProject = new NewProject();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfqadd);

        // Get firebase authentication instance
        mAuth = FirebaseAuth.getInstance();

        // Set up customer spinner
        mCustomerSpinner = (Spinner) findViewById(R.id.rfq_add_customer_dropdown);
        mCustomerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, mCustomerArray);
        mCustomerSpinner.setAdapter(mCustomerAdapter);

        // Set up description text box
        mDescription = (EditText) findViewById(R.id.rfq_add_edit_description);

        // Set up customer contact autocomplete box
        mCustomerContact = (AutoCompleteTextView) findViewById(R.id.rfq_add_edit_customer_contact);
        mContactAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, mContactArray);
        mCustomerContact.setAdapter(mContactAdapter);


        // Set up submit button
        mSubmitButton = (Button) findViewById(R.id.rfq_add_submit_button);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewProject.setCustomer(mCustomerSpinner.getSelectedItem().toString());
                mNewProject.setDescription(mDescription.getText().toString());
                mNewProject.setCustomerContact(mCustomerContact.getText().toString());
                FirebaseUser user = mAuth.getCurrentUser();
                mNewProject.setAicContactInfo(user.getEmail());
                mNewProject.setAicContact(user.getDisplayName());
                new submitRFQ().execute(new Pair<>(mContext, mNewProject));
            }
        });

//        mDateButton = (Button) findViewById(R.id.rfq_add_due_date_button);
//        mDateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Date date = new Date();
//                FragmentManager manager = getFragmentManager();
//                DatePickerFragment dialog = DatePickerFragment.newInstance(date);
//                dialog.setTargetFragment(dialog, REQUEST_DATE);
//                dialog.show(mContext, DIALOG_DATE);
//            }
//        });

        // Call async task to load customer list dropdown
        new getCustomerList().execute();

        // Call async task to load contacts list
        new getContactList().execute();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode != Activity.RESULT_OK) {
//            return;
//        }
//
//        if (requestCode == REQUEST_DATE) {
//            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
//            String formattedDate = dateFormat.format(date);
//            mNewProject.setRfqDueDate(formattedDate);
//
//        }
//    }

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
                return parseCustomers(raw);
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

    // Get customer list for Add RFQ Page
    public class getContactList extends AsyncTask<Void, Void, List<CustomerContacts>> {
        private AicDataAPI myApiService = null;
        private ProgressDialog dialog = new ProgressDialog(mContext);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Loading customers");
            this.dialog.show();
        }

        @Override
        protected List<CustomerContacts> doInBackground(Void... params) {
            if (myApiService == null) {
                AicDataAPI.Builder builder = new AicDataAPI.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        //Root url of google cloud project
                        .setRootUrl("https://i-melody-158021.appspot.com/_ah/api/");
                myApiService = builder.build();
            }

            try {
                List<CustomerContacts> raw = myApiService.getContactList().execute().getItems();
                return parseContacts(raw);
            } catch (IOException e) {
                Log.i(TAG, "IO Exception", e);
                List<CustomerContacts> fail = new ArrayList<>();
                return fail;
            }
        }

        @Override
        protected void onPostExecute(List<CustomerContacts> contacts) {
            // Dismiss loading dialog
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            // Loop through results and add to customer array for spinner
            for (int i = 0; i < contacts.size(); i++) {
                mContactArray.add(contacts.get(i).getCustomerContact());
            }

            Log.i(TAG, "Response from endpoint: " + contacts);
            // Notify spinner of updated information
            mContactAdapter.notifyDataSetChanged();
        }
    }

    // Clean up response from google endpoints for customer list
    private List<Customers> parseCustomers(List<Customers> raw) {

        List<Customers> customers = new ArrayList<>();

        for (int i = 0; i < raw.size(); i++) {

            Customers customer = new Customers();
            customer.setId(raw.get(i).getId());
            customer.setName(raw.get(i).getName());

            customers.add(customer);
        }
        return customers;
    }

    // Clean up response from google endpoints for contacts autocomplete
    private List<CustomerContacts> parseContacts(List<CustomerContacts> raw) {

        List<CustomerContacts> contacts = new ArrayList<>();

        for (int i = 0; i < raw.size(); i++) {

            CustomerContacts contact = new CustomerContacts();
            contact.setCustomerContact(raw.get(i).getCustomerContact());

            contacts.add(contact);
        }
        return contacts;
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
