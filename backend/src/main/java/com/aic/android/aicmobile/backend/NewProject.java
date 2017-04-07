package com.aic.android.aicmobile.backend;

import java.util.Date;

/**
 * Created by jordan on 4/5/2017.
 * Used by RFQ Add Activity to pass information to Google Endpoints to create a new project.
 */

public class NewProject {
    private String customer;
    private String description;
    private String customerContact;
    private Date rfqDueDate;

    public String getCustomer() {
        return customer;
    }

    public String getDescription() {
        return description;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public Date getRfqDueDate() {
        return rfqDueDate;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public void setRfqDueDate(Date rfqDueDate) {
        this.rfqDueDate = rfqDueDate;
    }
}
