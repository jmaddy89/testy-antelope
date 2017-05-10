package com.aic.android.aicmobile.backend;

/**
 * Created by jorda on 5/10/2017.
 * Class for app to create to initiate a request to download time
 */

public class TimeEntryRequestDayInfo {
    private int weekNumber;
    private int year;
    private int userId;

    public int getWeekNumber() {
        return weekNumber;
    }

    public int getYear() {
        return year;
    }

    public int getUserId() {
        return userId;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
