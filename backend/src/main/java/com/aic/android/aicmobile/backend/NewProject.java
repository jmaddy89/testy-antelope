package com.aic.android.aicmobile.backend;

/**
 * Created by jordan on 4/5/2017.
 * Used by RFQ Add Activity to pass information to Google Endpoints to create a new project.
 */

public class NewProject {
    private String customer;
    private String description;
    private String aicContact;
    private String aicContactInfo;
    private String customerContact;
    private String rfqDueDate;

    public String getCustomer() {
        return customer;
    }

    public String getDescription() {
        return description;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public String getRfqDueDate() {
        return rfqDueDate;
    }

    public String getAicContact() {
        return aicContact;
    }

    public String getAicContactInfo() {
        return aicContactInfo;
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

    public void setRfqDueDate(String rfqDueDate) {
        this.rfqDueDate = rfqDueDate;
    }

    public void setAicContact(String aicContact) {
        this.aicContact = aicContact;
    }

    public void setAicContactInfo(String aicContactInfo) {
        this.aicContactInfo = aicContactInfo;
    }
}
