package com.mueller.mobileSports.user;

import java.io.File;

/**
 * Created by Sandra on 24.11.2016.
 */

/**
 *
 */
public class UserData {

    private static UserData userData;
    //Personal Data
    private String username;
    private String email;
    private String gender;
    private int height;
    private int age;
    private int weight;
    private File uploadedFile;
    //HRM-Data
    private int heartRate;
    private int activityLevel;
    private int hRmax;
    //Pedometer-Data
    private int weeklyStepCount;
    private int monthlyStepCount;
    private int stepGoal;
    //Other
    private String objectId;

    public UserData() {
    }


    public UserData(String username, String email) {
        setUsername(username);
        setEmail(email);
    }

    public UserData(UserData data) {

        username = data.getUsername();
        email = data.getEmail();
        age = data.getAge();
        gender = data.getGender();
        weight = data.getWeight();
        height = data.getHeight();

        heartRate = data.getHeartRate();
        activityLevel = data.getActivityLevel();
        hRmax = data.gethRmax();

        weeklyStepCount = data.getWeeklyStepCount();
        monthlyStepCount = data.getMonthlyStepCount();
        stepGoal = data.getStepGoal();

        objectId = data.getObjectId();

    }

    public static UserData getInstance(String username, String email) {
        //if (userData == null) {
        userData = new UserData(username, email);
        //}
        return userData;
    }

    public static UserData getInstance(UserData data) {
        // if (userData == null) {
        userData = new UserData(data);
        //}
        return userData;
    }

    public static UserData getInstance() {
        // if (userData == null) {
        userData = new UserData();
        //}
        return userData;
    }

    public int gethRmax() {
        return hRmax;
    }

    public void sethRmax(int hRmax) {
        this.hRmax = hRmax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    private int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
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

    public File getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(File uploadedFile) {
        this.uploadedFile = uploadedFile;
    }


    public void deleteAll() {
        this.setAge(0);
        this.setHeight(0);
        this.setUsername("");
        this.setWeight(0);
        this.setStepGoal(0);
        this.setActivityLevel(0);
        this.setHeartRate(0);
        this.setMonthlyStepCount(0);
        this.setGender("");
        this.setEmail("");
        this.setObjectId(null);
        this.setUploadedFile(null);
        userData = null;
    }

    //Used for some testing only

}