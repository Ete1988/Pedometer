package com.mueller.mobileSports.user;

import java.util.ArrayList;

/**
 * Created by Sandra on 24.11.2016.
 * Data object for UserData
 */

public class UserData {

    private ArrayList<DailyData> dailyData;
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
        dailyData = new ArrayList<>();
        this.gender = "Male";
    }


    ArrayList<DailyData> getDailyData() {
        return dailyData;
    }

    void setDailyData(ArrayList<DailyData> dailyData) {
        this.dailyData = dailyData;
    }

    public int getRestingHeartRate() {
        return restingHeartRate;
    }

    public void setRestingHeartRate(int restingHeartRate) {
        this.restingHeartRate = restingHeartRate;
    }

    public int getHeartRateMax() {
        return heartRateMax;
    }

    public void setHeartRateMax(int heartRateMax) {
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

    public int getAge() {
        return age;
    }

    void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    void setGender(String gender) {
        this.gender = gender;
    }

    public int getWeight() {
        return weight;
    }

    void setWeight(int weight) {
        this.weight = weight;
    }

    public int getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(int stepGoal) {
        this.stepGoal = stepGoal;
    }

    public int getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(int activityLevel) {
        this.activityLevel = activityLevel;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "dailyData=" + dailyData +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", height=" + height +
                ", age=" + age +
                ", weight=" + weight +
                ", stepGoal=" + stepGoal +
                ", activityLevel=" + activityLevel +
                ", heartRateMax=" + heartRateMax +
                ", restingHeartRate=" + restingHeartRate +
                ", objectId='" + objectId + '\'' +
                '}';
    }
}