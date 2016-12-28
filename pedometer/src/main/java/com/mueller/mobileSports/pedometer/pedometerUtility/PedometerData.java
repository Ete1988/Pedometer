package com.mueller.mobileSports.pedometer.pedometerUtility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ete on 22/12/2016.
 */

public class PedometerData {

    private int daylyStepCount;
    private int weeklyStepCount;
    private int monthlyStepCount;

    private String sessionDay;

    private String objectId;

    public PedometerData() {
        SimpleDateFormat currDate = new SimpleDateFormat("EE dd MMM yyyy", Locale.getDefault());
        this.sessionDay = currDate.format(new Date());
    }

    public String getObjectId() {
        return objectId;
    }

    private void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getSessionDay() {
        return sessionDay;
    }

    public void setSessionDay(String sessionDay) {
        this.sessionDay = sessionDay;
    }

    public int getDaylyStepCount() {
        return daylyStepCount;
    }

    public void setDaylyStepCount(int daylyStepCount) {
        this.daylyStepCount = daylyStepCount;
    }

    public int getWeeklyStepCount() {
        return weeklyStepCount;
    }

    public void setWeeklyStepCount(int weeklyStepCount) {
        this.weeklyStepCount = weeklyStepCount;
    }

    private int getMonthlyStepCount() {
        return monthlyStepCount;
    }

    public void setMonthlyStepCount(int monthlyStepCount) {
        this.monthlyStepCount = monthlyStepCount;
    }
}
