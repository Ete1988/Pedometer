package com.mueller.mobileSports.heartRate.HRMUtility;

/**
 * Created by Ete on 18/12/2016.
 */

public class HeartRateMonitorUtility {


    public int calculateHRmax(int age) {
        double hRmaxD = 208 - (0.7 * age);
        return (int) hRmaxD;
    }

}
