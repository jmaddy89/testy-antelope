package com.aic.android.aicmobile;

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
        Calendar cal = new GregorianCalendar();
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

    public static List<TimeEntryWeek> initializeWeekList() {
        List<TimeEntryWeek> days = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            TimeEntryWeek day = new TimeEntryWeek();
            switch (i) {
                case 0:
                    day.setDayName("Sunday");
                    break;
                case 1:
                    day.setDayName("Monday");
                    break;
                case 2:
                    day.setDayName("Tuesday");
                    break;
                case 3:
                    day.setDayName("Wednesday");
                    break;
                case 4:
                    day.setDayName("Thursday");
                    break;
                case 5:
                    day.setDayName("Friday");
                    break;
                case 6:
                    day.setDayName("Saturday");
                    break;
                default:
                    day.setDayName("Error");
            }
            days.add(i, day);

        }

        return days;
    }
}

