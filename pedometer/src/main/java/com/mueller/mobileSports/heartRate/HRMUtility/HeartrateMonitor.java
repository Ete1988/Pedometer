package com.mueller.mobileSports.heartRate.HRMUtility;

/**
 * Created by Ete on 17/11/2016.
 */

public interface HeartRateMonitor {


    int[] getHeartRate();

    int getAverageHeartRate();

    int getMaxHeartRate();

    int getMinHeartRate();

}
