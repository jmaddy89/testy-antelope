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

        // Start at i - 4 to go back 4 weeks, then go to load future 2 weeks
        for (int i = currentWeek - 4; i < currentWeek + 1; i++ ) {
            TimeEntryTime week = new TimeEntryTime();
            week.setWeekNumber(i);
            week.setWeekYear(currentYear);
            week.setWeekList(new ArrayList<TimeEntryWeek>());

            // Offset to start at 0 index
            weeks.add(i - currentWeek + 4, week);
        }

        return weeks;
    }

    public static List<TimeEntryWeek> initializeWeekList(int weekNum, int year) {
        List<TimeEntryWeek> week = new ArrayList<>();

        // Get calendar reference and set to week and year
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, weekNum);

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd");

        for (int i = 0; i < 7; i++) {
            TimeEntryWeek day = new TimeEntryWeek();

            // switch sets day of week, and updates calendar obj with day of week to get date of that day
            switch (i) {
                case 1:
                    day.setDayName("Sunday");
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    day.setDate(sdf.format(cal.getTime()));
                    day.setRealDate(cal.getTime());
                    break;
                case 2:
                    day.setDayName("Monday");
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    day.setDate(sdf.format(cal.getTime()));
                    day.setRealDate(cal.getTime());
                    break;
                case 3:
                    day.setDayName("Tuesday");
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    day.setDate(sdf.format(cal.getTime()));
                    day.setRealDate(cal.getTime());
                    break;
                case 4:
                    day.setDayName("Wednesday");
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    day.setDate(sdf.format(cal.getTime()));
                    day.setRealDate(cal.getTime());
                    break;
                case 5:
                    day.setDayName("Thursday");
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    day.setDate(sdf.format(cal.getTime()));
                    day.setRealDate(cal.getTime());
                    break;
                case 6:
                    day.setDayName("Friday");
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    day.setDate(sdf.format(cal.getTime()));
                    day.setRealDate(cal.getTime());
                    break;
                case 0:
                    day.setDayName("Saturday");
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    day.setDate(sdf.format(cal.getTime()));
                    day.setRealDate(cal.getTime());
                    break;
                default:
                    day.setDayName("Error");
                    cal.set(Calendar.DAY_OF_WEEK, i);
                    day.setDate(sdf.format(cal.getTime()));
                    day.setRealDate(cal.getTime());
            }

            // This is done weird because saturday is the first day of the week
            // and this puts saturday last in the list
            if (i==0) {
                week.add(i, day);
            } else {
                week.add(i-1, day);
            }
        }

        return week;
    }

    public static List<TimeEntryDay> initializeDayList(TimeEntryWeek week, List<TimeEntryDay> dayList) {
        List<TimeEntryDay> dayInfo = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String weekDate = sdf.format(week.getRealDate());

        Log.i(TAG, "Daylist size is: " + dayList.size());

        int index = 0;
        for (int i = 0; i < dayList.size(); i++) {

            TimeEntryDay day = new TimeEntryDay();

           String dayDate = null;
            try {
                // Convert strange endpoint datetime object to string, and split off date only
                Log.i(TAG, "Crashing on: " + dayList.get(i).getDate().toString());
                dayDate = dayList.get(i).getDate().toString().split("T")[0];
            } catch (Exception e) {
                Log.e(TAG, "Null date: ", e);
            }

            Log.i(TAG, "Week date is: " + weekDate);
            Log.i(TAG, "Day date is: " + dayDate);

            if (weekDate.equals(dayDate)) {
                day.setCustomer(dayList.get(i).getCustomer());
                Log.i(TAG, "This date equals: " + weekDate);

                dayInfo.add(index, day);
                index++;
            } else {
                Log.i(TAG, "No match found " + weekDate + " and " + dayDate);
            }
        }
        return dayInfo;
    }
}

