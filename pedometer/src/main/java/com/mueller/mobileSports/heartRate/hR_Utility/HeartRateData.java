package com.mueller.mobileSports.heartRate.hR_Utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ete on 22/12/2016.
 */

public class HeartRateData {

    private int maxHeartRate;
    private int minHeartRate;
    private int averageHeartRate;
    private int activityLevel;
    private int hRmax;
    private String sessionDay;
    private String objectId;

    public HeartRateData() {
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

    public int gethRmax() {
        return hRmax;
    }

    public void sethRmax(int hRmax) {
        this.hRmax = hRmax;
    }

    public int getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(int activityLevel) {
        this.activityLevel = activityLevel;
    }

    public int getMaxHeartRate() {
        return maxHeartRate;
    }

    public void setMaxHeartRate(int maxHeartRate) {
        this.maxHeartRate = maxHeartRate;
    }

    public int getMinHeartRate() {
        return minHeartRate;
    }

    public void setMinHeartRate(int minHeartRate) {
        this.minHeartRate = minHeartRate;
    }

    public int getAverageHeartRate() {
        return averageHeartRate;
    }

    public void setAverageHeartRate(int averageHeartRate) {
        this.averageHeartRate = averageHeartRate;
    }
}
