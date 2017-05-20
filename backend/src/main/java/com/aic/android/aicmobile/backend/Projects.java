package com.aic.android.aicmobile.backend;

import java.io.Serializable;

/**
 * Created by jordan on 3/27/2017.
 */

public class Projects implements Serializable {

    private int projectId;
    private int projectNum;
    private String customer;
    private String projectDesc;
    private float budget;
    private float burn;
    private int projectStatus;
    private String aicContact;
    private String customerContact;

    public int getProjectId() {
        return projectId;
    }

    public int getProjectNum() {
        return projectNum;
    }

    public String getCustomer() {
        return customer;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public float getBudget() {
        return budget;
    }

    public float getBurn() {
        return burn;
    }

    public int getProjectStatus() {
        return projectStatus;
    }

    public String getAicContact() {
        return aicContact;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setProjectNum(int projectNum) {
        this.projectNum = projectNum;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    public void setBurn(float burn) {
        this.burn = burn;
    }

    public void setProjectStatus(int projectStatus) {
        this.projectStatus = projectStatus;
    }

    public void setAicContact(String aicContact) {
        this.aicContact = aicContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }
}

