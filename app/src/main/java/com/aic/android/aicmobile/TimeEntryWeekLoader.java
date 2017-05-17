package com.aic.android.aicmobile;

import android.text.format.DateUtils;
import android.util.Log;

import com.aic.android.aicmobile.backend.aicDataAPI.model.TimeEntryDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by JLM on 4/11/2017.
 */

public class TimeEntryWeekLoader {
    private static final String TAG = "TimeEntryWeekLoader";

    public static List<TimeEntryTime> initializeTimeList() {
        List<TimeEntryTime> weeks = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());

        int currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        int currentYear = cal.get(Calendar.YEAR);

        // Used if week span goes over a year change
        int yearChange = 0;

        // Start at i - 6 to go back 6 weeks, then go to load future 6 weeks
        for (int i = currentWeek - 6; i < currentWeek + 6; i++ ) {
            TimeEntryTime week = new TimeEntryTime();
            week.setWeekNumber(i);

            if (i < 0 && yearChange == 0) {
                currentYear--;
                yearChange = 1;
            }
            if (i > 52 && yearChange == 0) {
                currentYear++;
                yearChange = 1;
            }

            week.setWeekYear(currentYear);

            // Offset to start at 0 index
            weeks.add(i - currentWeek + 6, week);
        }

        return weeks;
    }

    public static List<TimeEntryTime> updateTimeList(List<TimeEntryTime> timeList, int weekNumber, int year) {

        return timeList;
    }

    /*
        initializeDayList called by recyclerview Adapters on TimeEntryFragment to load data for each day.
        The week and day is passed in from the fragment, and the list of data from the database is loaded in.
        Only returns information from the list if it matches the correct day and week
     */
    public static List<TimeEntryDay> initializeDayList(int week, int year, int day, List<TimeEntryDay> dayList) {

        // Initialize return list
        List<TimeEntryDay> returnList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        // Get calendar instance and set to week and day of the time request
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, week);
        cal.set(Calendar.DAY_OF_WEEK, day);

        Log.i(TAG, "week number here is: " + week + " Year here is: " + day);

        // Format date to get a date easier to work with
        String weekDate = sdf.format(cal.getTime());
        Log.i(TAG, "The week date calculated here is: " + weekDate + " Week number: " + week);

        // Loop through results to find matches of data to return
        for (int i = 0; i < dayList.size(); i++) {

            // Try to extract date from daylist. If null, then nothing happens and app doesn't crash
            if (dayList.get(i).getDate() != null) {
                // Convert strange endpoint datetime object to string, and split off date only
                String dayDate = dayList.get(i).getDate().toString().split("T")[0];

                Log.i(TAG, "Date from google: " + dayDate + "     Date to compare: " + weekDate);
                // Check to see if there is a match for the day
                if (weekDate.equals(dayDate)) {

                    Log.i(TAG, "Match found");

                    // Set dayInfo equal to entry found from database if dates match
                    TimeEntryDay dayInfo = dayList.get(i);

                    // Add dayinfo to return list
                    returnList.add(dayInfo);
                }
            }


        }
        return returnList;
    }
}

