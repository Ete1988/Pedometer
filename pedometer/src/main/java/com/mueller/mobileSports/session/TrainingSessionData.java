package com.mueller.mobileSports.session;

/**
 * Created by Ete on 23/01/2017.
 */

public class TrainingSessionData {

    private double sessionDuration;
    private int trimpScore;
    private float energyExpenditure;
    private int fitness;
    private int fatigue;
    private int performance;
    private String objectId;

    public double getSessionDuration() {
        return sessionDuration;
    }

    public void setSessionDuration(double sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    public float getEnergyExpenditure() {
        return energyExpenditure;
    }

    public void setEnergyExpenditure(float energyExpenditure) {
        this.energyExpenditure = energyExpenditure;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getTrimpScore() {
        return trimpScore;
    }

    public void setTrimpScore(int trimpScore) {
        this.trimpScore = trimpScore;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public int getFatigue() {
        return fatigue;
    }

    public void setFatigue(int fatigue) {
        this.fatigue = fatigue;
    }

    public int getPerformance() {
        return performance;
    }

    public void setPerformance(int performance) {
        this.performance = performance;
    }

    @Override
    public String toString() {
        return "TrainingSessionData{" +
                "sessionDuration=" + sessionDuration +
                ", trimpScore=" + trimpScore +
                ", energyExpenditure=" + energyExpenditure +
                ", fitness=" + fitness +
                ", fatigue=" + fatigue +
                ", performance=" + performance +
                ", objectId='" + objectId + '\'' +
                '}';
    }
}
