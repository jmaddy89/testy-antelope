package com.aic.android.aicmobile.backend;

/**
 * Created by JLM on 5/16/2017.
 */

public class Deliverables {

    private int projectNumber;
    private int deliverableId;
    private int coNumber;
    private int optionNumber;
    private String deliverableDesc;

    public int getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(int projectNumber) {
        this.projectNumber = projectNumber;
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

    public String getDeliverableDesc() {
        return deliverableDesc;
    }

    public void setDeliverableDesc(String deliverableDesc) {
        this.deliverableDesc = deliverableDesc;
    }
}
