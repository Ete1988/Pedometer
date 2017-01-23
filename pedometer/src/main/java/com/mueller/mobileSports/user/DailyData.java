package com.mueller.mobileSports.user;

import com.mueller.mobileSports.heartRate.HeartRateData;
import com.mueller.mobileSports.pedometer.PedometerData;
import com.mueller.mobileSports.session.TrainingSessionData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ete on 11/01/2017.
 * <p>
 * Dataobject to store the pedometer and heartrate data for a specific day
 */

public class DailyData {

    private PedometerData pedometerData;
    private HeartRateData heartRateData;
    private TrainingSessionData trainingSessionData;
    private String sessionDay;
    private String objectId;

    DailyData() {
        SimpleDateFormat currDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        this.sessionDay = currDate.format(new Date());
    }


    public PedometerData getPedometerData() {
        if (pedometerData == null)
            return new PedometerData();
        return pedometerData;
    }

    void setPedometerData(PedometerData pedometerData) {
        this.pedometerData = pedometerData;
    }

    public HeartRateData getHeartRateData() {
        if (heartRateData == null)
            return new HeartRateData();
        return heartRateData;
    }

    void setHeartRateData(HeartRateData heartRateData) {
        this.heartRateData = heartRateData;
    }

    public TrainingSessionData getTrainingSessionData() {
        if (trainingSessionData == null) {
            return new TrainingSessionData();
        }
        return trainingSessionData;
    }

    public void setTrainingSessionData(TrainingSessionData trainingSessionData) {
        this.trainingSessionData = trainingSessionData;
    }

    public String getSessionDay() {
        return sessionDay;
    }

    void setSessionDay(String sessionDay) {
        this.sessionDay = sessionDay;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    @Override
    public String toString() {
        return "DailyData{" +
                "pedometerData=" + pedometerData +
                ", heartRateData=" + heartRateData +
                ", sessionDay='" + sessionDay + '\'' +
                ", objectId='" + objectId + '\'' +
                '}';
    }
}
