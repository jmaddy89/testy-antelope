package com.aic.android.aicmobile.backend;

import java.util.Date;

/**
 * Created by JLM on 4/11/2017.
 */

public class TimeEntryDay {
    private int projectNumber;
    private String customer;
    private String description;
    private float time;
    private Date date;
    private boolean billable;
    private String note;

    public int getProjectNumber() {
        return projectNumber;
    }

    public String getCustomer() {
        return customer;
    }

    public String getDescription() {
        return description;
    }

    public float getTime() {
        return time;
    }

    public Date getDate() {
        return date;
    }

    public boolean isBillable() {
        return billable;
    }

    public String getNote() {
        return note;
    }

    public void setProjectNumber(int projectNumber) {
        this.projectNumber = projectNumber;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setBillable(boolean billable) {
        this.billable = billable;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
