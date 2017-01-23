package com.mueller.mobileSports.heartRate;

import android.content.Context;

import com.mueller.mobileSports.user.UserData;
import com.mueller.mobileSports.user.UserSessionManager;
import com.mueller.mobileSports.utility.SharedValues;

import java.util.ArrayList;

/**
 * Created by Ete on 18/12/2016.
 * Utility class offering different calculations concerning the heart rate monitoring
 */

public class HeartRateMonitorUtility {

    private SharedValues sharedValues;

    public HeartRateMonitorUtility(Context context) {
        this.sharedValues = SharedValues.getInstance(context);
    }

    void doCalculations(int heartRate) {
        calculateMaxHeartRate(heartRate);
        calculateMinHeartRate(heartRate);
        calculatePercentOfHrMax(heartRate);
    }

    void calculateAverageHeartRateOverDay(ArrayList<Integer> heartRateArray) {
        int sum = 0;
        for (int i : heartRateArray) {
            sum += i;
        }
        int averageHeartRate = sum / heartRateArray.size();
        sharedValues.saveInt("averageHeartRateOverDay", averageHeartRate);
    }


    private void calculatePercentOfHrMax(int heartRate) {
        UserData userData = UserSessionManager.getUserData();
        int heartRateMax;

        if (!(userData.getHeartRateMax() == 0)) {
            heartRateMax = userData.getHeartRateMax();
        } else if (!(userData.getAge() == 0)) {
            heartRateMax = (int) (208 - (0.7 * (userData.getAge())));
        } else {
            heartRateMax = 0;
        }
        sharedValues.saveInt("percentOfHRMax", ((heartRate * 100) / heartRateMax));
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
