package com.aic.android.aicmobile.backend;

import java.util.Date;

/**
 * Created by JLM on 5/17/2017.
 */

public class NewTimeEntry {
    private int projectId;
    private int projectNumber;
    private int userId;
    private int roleId;
    private int deliverableId;
    private int coNumber;
    private int optionNumber;
    private float time;
    private String date;
    private boolean billable;
    private String note;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(int projectNumber) {
        this.projectNumber = projectNumber;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getDeliverableId() {
        return deliverableId;
    }

    public void setDeliverableId(int deliverableId) {
        this.deliverableId = deliverableId;
    }

    public int getCoNumber() {
        return coNumber;
    }

    public void setCoNumber(int coNumber) {
        this.coNumber = coNumber;
    }

    public int getOptionNumber() {
        return optionNumber;
    }

    public void setOptionNumber(int optionNumber) {
        this.optionNumber = optionNumber;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isBillable() {
        return billable;
    }

    public void setBillable(boolean billable) {
        this.billable = billable;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
