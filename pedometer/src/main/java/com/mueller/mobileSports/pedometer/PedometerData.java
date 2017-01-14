package com.mueller.mobileSports.pedometer;

/**
 * Created by Ete on 22/12/2016.
 * Data object for PedometerData
 */

public class PedometerData {

    private int dailyStepCount;
    private int weeklyStepCount;
    private int monthlyStepCount;
    private double distance;
    private int energyExpenditureSteps;
    private String objectId;

    public String getObjectId() {
        return objectId;
    }

    private void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getDailyStepCount() {
        return dailyStepCount;
    }

    public void setDailyStepCount(int dailyStepCount) {
        this.dailyStepCount = dailyStepCount;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getEnergyExpenditureSteps() {
        return energyExpenditureSteps;
    }

    public void setEnergyExpenditureSteps(int energyExpenditureSteps) {
        this.energyExpenditureSteps = energyExpenditureSteps;
    }

    @Override
    public String toString() {
        return "PedometerData{" +
                "dailyStepCount=" + dailyStepCount +
                ", weeklyStepCount=" + weeklyStepCount +
                ", monthlyStepCount=" + monthlyStepCount +
                ", distance=" + distance +
                ", energyExpenditureSteps=" + energyExpenditureSteps +
                ", objectId='" + objectId + '\'' +
                '}';
    }
}
