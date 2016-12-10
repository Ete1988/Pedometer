package com.mueller.mobileSports.user;

import java.io.File;

/**
 * Created by Sandra on 24.11.2016.
 */

/**
 *
 */
public class UserProfileData {

    private static String username, gender, objectId;
    private static int height, age, weight, heartRate, weeklyStepCount, monthlyStepCount, activityLevel, stepGoal;
    private File uploadedFile;

    public UserProfileData() {
    }

    public UserProfileData(int height, int age, String gender, int weight, int heartRate, int weeklyStepCount, int monthlyStepCount) {

        UserProfileData.age = age;
        UserProfileData.gender = gender;
        UserProfileData.weight = weight;
        UserProfileData.heartRate = heartRate;
        UserProfileData.weeklyStepCount = weeklyStepCount;
        UserProfileData.monthlyStepCount = monthlyStepCount;
        UserProfileData.height = height;
    }

    public UserProfileData(UserProfileData data) {
        age = data.getAge();
        gender = data.getGender();
        weight = data.getWeight();
        height = data.getHeight();
        heartRate = data.getHeartRate();
        weeklyStepCount = data.getWeeklyStepCount();
        monthlyStepCount = data.getMonthlyStepCount();
        objectId = data.getObjectId();

    }

    public int getStepGoal() {
        return stepGoal;
    }

    public void setStepGoal(int stepGoal) {
        UserProfileData.stepGoal = stepGoal;
    }

    public int getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(int activityLevel) {
        UserProfileData.activityLevel = activityLevel;
    }


    private String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        UserProfileData.objectId = objectId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        UserProfileData.username = username;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        UserProfileData.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        UserProfileData.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        UserProfileData.gender = gender;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        UserProfileData.weight = weight;
    }

    private int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        UserProfileData.heartRate = heartRate;
    }

    public int getWeeklyStepCount() {
        return weeklyStepCount;
    }

    public void setWeeklyStepCount(int weeklyStepCount) {
        UserProfileData.weeklyStepCount = weeklyStepCount;
    }

    private int getMonthlyStepCount() {
        return monthlyStepCount;
    }

    public void setMonthlyStepCount(int monthlyStepCount) {
        UserProfileData.monthlyStepCount = monthlyStepCount;
    }

    public File getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(File uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    //Used for some testing only
    @Override
    public String toString() {
        return "UserProfileData{" +
                ", gender='" + gender + '\'' +
                ", height=" + height +
                ", age=" + age +
                ", weight=" + weight +
                ", heartRate=" + heartRate +
                ", weeklyStepCount=" + weeklyStepCount +
                ", monthlyStepCount=" + monthlyStepCount +
                ", ObjectID=" + objectId +
                '}';
    }
}