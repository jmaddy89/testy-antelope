package com.aic.android.aicmobile;

import java.util.Date;

/**
 * Created by JLM on 4/11/2017.
 */

public class TimeEntryWeek {
    private String dayName;
    private String date;
    private float hours;

    public String getDayName() {
        return dayName;
    }

    public String getDate() {
        return date;
    }

    public float getHours() {
        return hours;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHours(float hours) {
        this.hours = hours;
    }
}
