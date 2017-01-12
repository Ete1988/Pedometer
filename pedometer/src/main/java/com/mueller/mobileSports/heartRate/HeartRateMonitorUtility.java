package com.mueller.mobileSports.heartRate;

import android.content.Context;

import com.mueller.mobileSports.general.SharedValues;
import com.mueller.mobileSports.user.UserSessionManager;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 * Created by Ete on 18/12/2016.
 * Utility class offering different calculations concerning the heart rate monitoring
 *
 */

class HeartRateMonitorUtility {

    private SharedValues sharedValues;

    HeartRateMonitorUtility(Context context) {
        this.sharedValues = SharedValues.getInstance(context);
    }

    void doCalculations(int heartRate) {
        calculateMaxHeartRate(heartRate);
        calculateMinHeartRate(heartRate);
        calculatePercentOfHrMax(heartRate);
        calculateEnergyExpenditure(heartRate);
    }

    void calculateAverageHeartRate(int[] averageHeartRateArray) {
        int sum = 0;
        for (int i : averageHeartRateArray) {
            sum += i;
        }
        int averageHeartRate = sum / 3;
        sharedValues.saveInt("averageHeartRate", averageHeartRate);
    }

    private double calculateVO2Max() {

        double age = UserSessionManager.getUserData().getAge();
        double height = UserSessionManager.getUserData().getHeight();
        double weight = UserSessionManager.getUserData().getWeight();
        double pal = UserSessionManager.getUserData().getActivityLevel();
        String gender = UserSessionManager.getUserData().getGender();

        height = height / 100; // Height in meter
        int gen;

        if (gender.equals("Female")) {
            gen = 0;
        } else {
            gen = 1;
        }

        return (0.133 * age) - (0.005 * Math.pow(age, 2)) + (11.403 * gen)
                + (1.463 * pal) + (9.17 * height) - (0.254 * weight) + 34.143;
    }

    private void calculateEnergyExpenditure(int heartRate) {
        double vo2 = calculateVO2Max();
        double age = UserSessionManager.getUserData().getAge();
        double weight = UserSessionManager.getUserData().getWeight();
        String gender = UserSessionManager.getUserData().getGender();
        int gen;

        if (gender.equals("Female")) {
            gen = 0;
        } else {
            gen = 1;
        }
        double part1 = -36.3781 + (0.271 * age) + (0.394 * weight) + (0.404 * vo2) + (0.634 * (double) heartRate);
        double part2 = -36.3781 + (0.274 * age) + (0.103 * weight) + (0.380 * vo2) + (0.450 * (double) heartRate);
        double ee = -59.3954 + (gen * part1) + ((1 - gen) * part2);

        sharedValues.saveInt("energyExpenditureHR", (int) Math.round(ee));
    }

    float calculateTotalEnergyExpenditureDuringSession(float minutes) {
        float tEe = (sharedValues.getInt("energyExpenditureHR") * minutes);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        tEe = Float.parseFloat(df.format(tEe));
        return tEe;
    }

    int calculateTRIMP(int minutes) {
        double x;
        double x1 = sharedValues.getInt("averageHeartRate") - sharedValues.getInt("minHeartRate");
        double x2 = sharedValues.getInt("maxHeartRate") - sharedValues.getInt("minHeartRate");
        if (!(x2 == 0)) {
            x = x1 / x2;
        } else {
            return 0;
        }

        double b;
        if (Objects.equals(UserSessionManager.getUserData().getGender(), "Female")) {
            b = 1.67;
        } else {
            b = 1.92;
        }

        double z;
        z = b * x;
        double y = Math.pow(Math.E, z);
        double trimp = (double) minutes * x * y;
        sharedValues.saveInt("trimp", (int) Math.round(trimp));
        return (int) Math.round(trimp);
    }

    private void calculatePercentOfHrMax(int heartRate) {

        int heartRateMax = sharedValues.getInt("heartRateMax");
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
