package com.aic.android.aicmobile;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by JLM on 4/11/2017.
 */

public class TimeEntryWeekLoader {
    private static final String TAG = "TimeEntryWeekLoader";

    public static List<TimeEntryTime> initializeTimeList() {
        List<TimeEntryTime> weeks = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);

        int currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        int currentYear = cal.get(Calendar.YEAR);

        for (int i = currentWeek - 4; i < currentWeek + 2; i++ ) {
            TimeEntryTime week = new TimeEntryTime();
            week.setWeekNumber(i);
            week.setWeekYear(currentYear);
            weeks.add(i - currentWeek + 4, week);
        }

//        TimeEntryTime week = new TimeEntryTime();
//        week.setWeekNumber(cal.get(Calendar.WEEK_OF_YEAR));
//        week.setWeekYear(cal.get(Calendar.YEAR));
//
//        Log.i(TAG, "Date set as :" + week.getWeekNumber() + " week number. " + week.getWeekYear() + " as week year.");
//        weeks.add(0, week);
        return weeks;
    }

    public static List<TimeEntryWeek> initializeWeekList(int week, int year) {
        List<TimeEntryWeek> days = new ArrayList<>();

        // Get calendar reference and set to week and year
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, week);

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd");

        for (int i = 0; i < 7; i++) {
            TimeEntryWeek day = new TimeEntryWeek();

            // switch sets day of week, and updates calendar obj with day of week to get date of that day
            switch (i) {
                case 1:
                    day.setDayName("Sunday");
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    day.setDate(sdf.format(cal.getTime()));
                    break;
                case 2:
                    day.setDayName("Monday");
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    day.setDate(sdf.format(cal.getTime()));
                    break;
                case 3:
                    day.setDayName("Tuesday");
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    day.setDate(sdf.format(cal.getTime()));
                    break;
                case 4:
                    day.setDayName("Wednesday");
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    day.setDate(sdf.format(cal.getTime()));
                    break;
                case 5:
                    day.setDayName("Thursday");
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    day.setDate(sdf.format(cal.getTime()));
                    break;
                case 6:
                    day.setDayName("Friday");
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    day.setDate(sdf.format(cal.getTime()));
                    break;
                case 0:
                    day.setDayName("Saturday");
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    day.setDate(sdf.format(cal.getTime()));
                    break;
                default:
                    day.setDayName("Error");
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    day.setDate(sdf.format(cal.getTime()));
            }

            // This is done weird because saturday is the first day of the week
            // and this puts saturday last in the list
            if (i==0) {
                days.add(i, day);
            } else {
                days.add(i-1, day);
            }
        }

        return days;
    }
}

