package com.mueller.mobileSports.pedometer.user;

import java.io.File;

/**
 * Created by Sandra on 24.11.2016.
 */

public class saveProfileChanges {
    private String username, gender;
    private int height, age,weight, heartRate, weeklyStepCount, monthlyStepCount;
    private File uploadedFile;

    public saveProfileChanges(){}

    public saveProfileChanges(String username, int height,
                              int age, String gender, int weight, int heartRate, int weeklyStepCount,
                              int monthlyStepCount) {

        this.username = username;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.heartRate = heartRate;
        this.weeklyStepCount = weeklyStepCount;
        this.monthlyStepCount = monthlyStepCount;
        this.height = height;


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

    public int getHeartRate() {
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

    public int getMonthlyStepCount() {
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
}
