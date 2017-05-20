package com.aic.android.aicmobile.backend;

/**
 * Created by JLM on 5/19/2017.
 */

public class MainPage {
    private int activeProjects;
    private int completeProjects;
    private int incompleteRFQs;
    private int completeRFQs;

    public int getActiveProjects() {
        return activeProjects;
    }

    public void setActiveProjects(int activeProjects) {
        this.activeProjects = activeProjects;
    }

    public int getCompleteProjects() {
        return completeProjects;
    }

    public void setCompleteProjects(int completeProjects) {
        this.completeProjects = completeProjects;
    }

    public int getIncompleteRFQs() {
        return incompleteRFQs;
    }

    public void setIncompleteRFQs(int incompleteRFQs) {
        this.incompleteRFQs = incompleteRFQs;
    }

    public int getCompleteRFQs() {
        return completeRFQs;
    }

    public void setCompleteRFQs(int completeRFQs) {
        this.completeRFQs = completeRFQs;
    }
}
