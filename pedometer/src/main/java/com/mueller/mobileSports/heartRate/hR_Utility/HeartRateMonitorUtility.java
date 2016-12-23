package com.mueller.mobileSports.heartRate.hR_Utility;

import android.content.Context;

import com.mueller.mobileSports.general.SharedValues;

/**
 * Created by Ete on 18/12/2016.
 */

public class HeartRateMonitorUtility {

    private Context context;
    private SharedValues sharedValues;


    public HeartRateMonitorUtility(Context context) {
        this.sharedValues = SharedValues.getInstance(context);
    }

    public void storeMinAndMaxHeartRate(int heartRate) {
        calculateMaxHeartRate(heartRate);
        calculateMinHeartRate(heartRate);
    }

    public void calculateAverageHeartRate(int[] averageHeartRateArray) {
        int sum = 0;
        for (int i : averageHeartRateArray) {
            sum += i;
        }
        int averageHeartRate = sum / 3;
        sharedValues.saveInt("averageHeartRate", averageHeartRate);

    }

    public int calculateHRmax(int age) {
        double hRmaxD = 208 - (0.7 * age);
        return (int) hRmaxD;
    }

    private void calculateMaxHeartRate(int heartRate) {
        if (heartRate > sharedValues.getInt("maxHeartRate")) {
            sharedValues.saveInt("maxHeartRate", heartRate);
        }
    }

    private void calculateMinHeartRate(int heartRate) {
        if (heartRate < sharedValues.getInt("minHeartRate") || sharedValues.getInt("minHeartRate") == 0) {
            sharedValues.saveInt("minHeartRate", heartRate);
        }

    }


}
