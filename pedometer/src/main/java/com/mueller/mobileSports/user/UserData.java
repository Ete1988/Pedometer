package com.mueller.mobileSports.user;

import com.mueller.mobileSports.heartRate.hR_Utility.HeartRateData;
import com.mueller.mobileSports.pedometer.pedometerUtility.PedometerData;

/**
 * Created by Sandra on 24.11.2016.
 */

/**
 *
 */
public class UserData {


    private static PedometerData pedometerData;
    private static HeartRateData heartRateData;

    private String username;
    private String email;
    private String gender;
    private int height;
    private int age;
    private int weight;
    private int stepGoal;
    private int activityLevel;
    private int heartRateMax;
    private int restingHeartRate;
    private String objectId;

    UserData() {
        pedometerData = new PedometerData();
        heartRateData = new HeartRateData();
        this.gender = "Male";
    }


    UserData(UserData data) {
        username = data.getUsername();
        email = data.getEmail();
        age = data.getAge();
        gender = data.getGender();
        weight = data.getWeight();
        height = data.getHeight();
        restingHeartRate = data.getRestingHeartRate();
        heartRateMax = data.getHeartRateMax();
        activityLevel = data.getActivityLevel();
        stepGoal = data.getStepGoal();
        heartRateData = data.getHeartRateData();
        pedometerData = data.getPedometerData();
        objectId = data.getObjectId();
    }

    PedometerData getPedometerData() {
        if (pedometerData == null) {
            pedometerData = new PedometerData();
            return pedometerData;
        } else {
            return pedometerData;
        }

    }

    void setPedometerData(PedometerData pedometerData) {
        UserData.pedometerData = pedometerData;
    }

    HeartRateData getHeartRateData() {
        if (heartRateData == null) {
            heartRateData = new HeartRateData();
            return heartRateData;
        }
        return heartRateData;
    }

    void setHeartRateData(HeartRateData heartRateData) {
        UserData.heartRateData = heartRateData;
    }

    int getRestingHeartRate() {
        return restingHeartRate;
    }

    void setRestingHeartRate(int restingHeartRate) {
        this.restingHeartRate = restingHeartRate;
    }

    int getHeartRateMax() {
        return heartRateMax;
    }

    void setHeartRateMax(int heartRateMax) {
        this.heartRateMax = heartRateMax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    String getObjectId() {
        return objectId;
    }

    private void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    String getUsername() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    int getAge() {
        return age;
    }

    void setAge(int age) {
        this.age = age;
    }

    String getGender() {
        return gender;
    }

    void setGender(String gender) {
        this.gender = gender;
    }

    int getWeight() {
        return weight;
    }

    void setWeight(int weight) {
        this.weight = weight;
    }

    int getStepGoal() {
        return stepGoal;
    }

    void setStepGoal(int stepGoal) {
        this.stepGoal = stepGoal;
    }

    int getActivityLevel() {
        return activityLevel;
    }

    void setActivityLevel(int activityLevel) {
        this.activityLevel = activityLevel;
    }

    public void deleteAll() {
        setAge(0);
        setHeight(0);
        setUsername("");
        setWeight(0);
        setGender("");
        setEmail("");
        setObjectId(null);
        setActivityLevel(0);
        setHeartRateMax(0);
        setRestingHeartRate(0);
        setHeartRateData(null);
        setPedometerData(null);
    }

}