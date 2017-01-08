package com.mueller.mobileSports.heartRate.hR_Utility;

import android.content.Context;

import com.mueller.mobileSports.general.SharedValues;

import java.util.Objects;

/**
 * Created by Ete on 18/12/2016.
 */

public class HeartRateMonitorUtility {

    private SharedValues sharedValues;

    public HeartRateMonitorUtility(Context context) {
        this.sharedValues = SharedValues.getInstance(context);
    }

    public void doCalculations(int heartRate) {
        calculateMaxHeartRate(heartRate);
        calculateMinHeartRate(heartRate);
        calculatePercentOfHrMax(heartRate);
        calculateEnergyExpenditure(heartRate);
    }

    public void calculateAverageHeartRate(int[] averageHeartRateArray) {
        int sum = 0;
        for (int i : averageHeartRateArray) {
            sum += i;
        }
        int averageHeartRate = sum / 3;
        sharedValues.saveInt("averageHeartRate", averageHeartRate);
    }

    private double calculateVO2Max() {
        double age = sharedValues.getInt("age");
        double pal = sharedValues.getInt("physicalActivityLevel");
        double height = sharedValues.getInt("height");
        double weight = sharedValues.getInt("weight");
        height = height / 100; // Height in meter
        String gender = sharedValues.getString("gender");
        int gen;

        if (gender.equals("Female")) {
            gen = 0;
        } else {
            gen = 1;
        }

        return (0.133 * age) - (0.005 * Math.pow(age, 2)) + (11.403 * gen)
                + (1.463 * pal) + (9.17 * height) - (0.254 * weight) + 34.143;
    }

    public void calculateEnergyExpenditure(int heartRate) {

        double vo2 = calculateVO2Max();
        double age = sharedValues.getInt("age");
        double height = sharedValues.getInt("height");
        double weight = sharedValues.getInt("weight");
        String gender = sharedValues.getString("gender");
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

    public int calculateTRIMP(int minutes) {
        double x;
        double x1 = sharedValues.getInt("averageHeartRate") - sharedValues.getInt("minHeartRate");
        double x2 = sharedValues.getInt("maxHeartRate") - sharedValues.getInt("minHeartRate");
        if (!(x2 == 0)) {
            x = x1 / x2;
        } else {
            return 0;
        }

        double b;

        if (Objects.equals(sharedValues.getString("gender"), "Female")) {
            b = 1.67;
        } else {
            b = 1.92;
        }

        double z;
        z = b * x;
        double y = Math.pow(Math.E, z);
        double trimpD = (double) minutes * x * y;
        sharedValues.saveInt("trimp", (int) Math.round(trimpD));
        return (int) Math.round(trimpD);
    }

    private void calculatePercentOfHrMax(int heartRate) {

        int hRmax = sharedValues.getInt("heartRateMax");
        sharedValues.saveInt("percentOfHRmax", ((heartRate * 100) / hRmax));

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
