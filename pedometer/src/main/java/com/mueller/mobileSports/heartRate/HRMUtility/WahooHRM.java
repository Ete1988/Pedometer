package com.mueller.mobileSports.heartRate.HRMUtility;

/**
 * Created by Ete on 15/12/2016.
 */

public class WahooHRM implements HeartRateMonitor {


    @Override
    public int[] getHeartRate() {


        return new int[]{0};
    }

    @Override
    public int getAverageHeartRate() {
        return 0;
    }

    @Override
    public int getMaxHeartRate() {
        return 0;
    }

    @Override
    public int getMinHeartRate() {
        return 0;
    }
}
