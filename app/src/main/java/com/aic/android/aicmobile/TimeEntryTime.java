package com.aic.android.aicmobile;

import java.util.List;

/**
 * Created by JLM on 4/11/2017.
 */

public class TimeEntryTime {

    private int weekNumber;
    private int weekYear;
    private List<TimeEntryWeek> weekList;

    public int getWeekNumber() {
        return weekNumber;
    }

    public int getWeekYear() {
        return weekYear;
    }

    public List<TimeEntryWeek> getWeekList() {
        return weekList;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public void setWeekYear(int weekYear) {
        this.weekYear = weekYear;
    }

    public void setWeekList(List<TimeEntryWeek> weekList) {
        this.weekList = weekList;
    }
}
