package com.aic.android.aicmobile.backend;

/**
 * Created by JLM on 5/21/2017.
 */

public class ProjectBreakdown {

    private int projNumber;
    private float laborBudget;
    private float materialBudget;
    private float subcontractorBudget;
    private float contingencyBudget;

    public int getProjNumber() {
        return projNumber;
    }

    public void setProjNumber(int projNumber) {
        this.projNumber = projNumber;
    }

    public float getLaborBudget() {
        return laborBudget;
    }

    public void setLaborBudget(float laborBudget) {
        this.laborBudget = laborBudget;
    }

    public float getMaterialBudget() {
        return materialBudget;
    }

    public void setMaterialBudget(float materialBudget) {
        this.materialBudget = materialBudget;
    }

    public float getSubcontractorBudget() {
        return subcontractorBudget;
    }

    public void setSubcontractorBudget(float subcontractorBudget) {
        this.subcontractorBudget = subcontractorBudget;
    }

    public float getContingencyBudget() {
        return contingencyBudget;
    }

    public void setContingencyBudget(float contingencyBudget) {
        this.contingencyBudget = contingencyBudget;
    }
}
