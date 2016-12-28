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
    private int hRmax;
    private String objectId;

    public UserData() {
        pedometerData = new PedometerData();
        heartRateData = new HeartRateData();
        this.gender = "Male";
    }

    public UserData(UserData data) {
        username = data.getUsername();
        email = data.getEmail();
        age = data.getAge();
        gender = data.getGender();
        weight = data.getWeight();
        height = data.getHeight();
        heartRateData = data.getHeartRateData();
        pedometerData = data.getPedometerData();
        objectId = data.getObjectId();
    }

    public PedometerData getPedometerData() {
        if (pedometerData == null) {
            return new PedometerData();
        } else {
            return pedometerData;
        }

    }

    public void setPedometerData(PedometerData pedometerData) {
        UserData.pedometerData = pedometerData;
    }

    public HeartRateData getHeartRateData() {
        if (heartRateData == null) {
            return new HeartRateData();
        }
        return heartRateData;
    }

    public void setHeartRateData(HeartRateData heartRateData) {
        UserData.heartRateData = heartRateData;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getObjectId() {
        return objectId;
    }

    private void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(int stepGoal) {
        this.stepGoal = stepGoal;
    }

    public int gethRmax() {
        return hRmax;
    }

    public void sethRmax(int hRmax) {
        this.hRmax = hRmax;
    }

    public int getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(int activityLevel) {
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
        setStepGoal(0);
        setActivityLevel(0);
        sethRmax(0);
        setHeartRateData(null);
        setPedometerData(null);
    }

    //Used for some testing only

}