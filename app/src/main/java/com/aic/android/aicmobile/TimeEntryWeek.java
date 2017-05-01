package com.aic.android.aicmobile;

import com.aic.android.aicmobile.backend.aicDataAPI.model.TimeEntryDay;

import java.util.Date;
import java.util.List;

/**
 * Created by JLM on 4/11/2017.
 */

public class TimeEntryWeek {
    private String dayName;
    private String date;
    private Date realDate;
    private float hours;
    private List<TimeEntryDay> timeEntryDayList;

    public String getDayName() {
        return dayName;
    }

    public String getDate() {
        return date;
    }

    public Date getRealDate() {
        return realDate;
    }

    public float getHours() {
        return hours;
    }

    public List<TimeEntryDay> getTimeEntryDayList() {
        return timeEntryDayList;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRealDate(Date realDate) {
        this.realDate = realDate;
    }

    public void setHours(float hours) {
        this.hours = hours;
    }

    public void setTimeEntryDayList(List<TimeEntryDay> timeEntryDayList) {
        this.timeEntryDayList = timeEntryDayList;
    }
}
