package com.mueller.mobileSports.heartRate;

/**
 * Created by Ete on 22/12/2016.
 * Data object for HeartRateData
 *
 */

public class HeartRateData {

    private int maxHeartRate;
    private int minHeartRate;
    private int averageHeartRate;
    private double sessionDuration;
    private int trimpScore;
    private float energyExpenditure;
    private String objectId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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

    public double getSessionDuration() {
        return sessionDuration;
    }

    public void setSessionDuration(double sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    public int getTrimpScore() {
        return trimpScore;
    }

    public void setTrimpScore(int trimpScore) {
        this.trimpScore = trimpScore;
    }

    public float getEnergyExpenditure() {
        return energyExpenditure;
    }

    public void setEnergyExpenditure(float energyExpenditure) {
        this.energyExpenditure = energyExpenditure;
    }

    @Override
    public String toString() {
        return "HeartRateData{" +
                "maxHeartRate=" + maxHeartRate +
                ", minHeartRate=" + minHeartRate +
                ", averageHeartRate=" + averageHeartRate +
                ", sessionDuration=" + sessionDuration +
                ", trimpScore=" + trimpScore +
                ", energyExpenditure=" + energyExpenditure +
                ", objectId='" + objectId + '\'' +
                '}';
    }
}
