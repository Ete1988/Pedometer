package com.mueller.mobileSports.session;

import android.content.Context;

import com.mueller.mobileSports.user.DailyData;
import com.mueller.mobileSports.user.UserData;
import com.mueller.mobileSports.user.UserSessionManager;
import com.mueller.mobileSports.utility.SharedValues;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ete on 23/01/2017.
 */

public class TrainingSessionCalculations {


    private Context context;
    private SharedValues sharedValues;

    TrainingSessionCalculations(Context context) {
        this.sharedValues = SharedValues.getInstance(context);
    }


    public void calculatePerformanceFitnessFatigue() {
        calculateFitness();
        calculateFatigue();
        calculatePerformance();
    }


    private double calculateVO2Max() {

        double age = UserSessionManager.getUserData().getAge();
        double height = UserSessionManager.getUserData().getHeight();
        double weight = UserSessionManager.getUserData().getWeight();
        double pal = UserSessionManager.getUserData().getActivityLevel();
        String gender = UserSessionManager.getUserData().getGender();

        height = height / 100; // Height in meter
        int gen;

        if (gender.equals("Female")) {
            gen = 0;
        } else {
            gen = 1;
        }

        return (0.133 * age) - (0.005 * Math.pow(age, 2)) + (11.403 * gen)
                + (1.463 * pal) + (9.17 * height) - (0.254 * weight) + 34.143;
    }

    void calculateEnergyExpenditure(int heartRate) {
        double vo2 = calculateVO2Max();
        double age = UserSessionManager.getUserData().getAge();
        double weight = UserSessionManager.getUserData().getWeight();
        String gender = UserSessionManager.getUserData().getGender();
        int gen;

        if (gender.equals("Female")) {
            gen = 0;
        } else {
            gen = 1;
        }
        double part1 = -36.3781 + (0.271 * age) + (0.394 * weight) + (0.404 * vo2) + (0.634 * (double) heartRate);
        double part2 = -36.3781 + (0.274 * age) + (0.103 * weight) + (0.380 * vo2) + (0.450 * (double) heartRate);
        double ee = -59.3954 + (gen * part1) + ((1 - gen) * part2);
        sharedValues.saveInt("energyExpenditureHR", (int) Math.round(ee));
    }

    float calculateTotalEnergyExpenditureDuringSession(float minutes) {
        float energyExpenditure = (sharedValues.getInt("energyExpenditureHR") * minutes);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        energyExpenditure = ((energyExpenditure / 4.184f) / 1000);
        energyExpenditure = Float.parseFloat(df.format(energyExpenditure));
        return energyExpenditure;
    }

    int calculateTRIMP(int minutes) {
        UserData userData = UserSessionManager.getUserData();
        int restingHeartRate;
        double x;
        if (!(userData.getRestingHeartRate() == 0)) {
            restingHeartRate = userData.getRestingHeartRate();
        } else {
            restingHeartRate = 60;
        }

        double x1 = sharedValues.getInt("averageHeartRate") - restingHeartRate;
        double x2 = sharedValues.getInt("maxHeartRate") - restingHeartRate;
        if (!(x2 == 0)) {
            x = x1 / x2;
        } else {
            return 0;
        }

        double b;
        if (Objects.equals(UserSessionManager.getUserData().getGender(), "Female")) {
            b = 1.67;
        } else {
            b = 1.92;
        }

        double z;
        z = b * x;
        double y = Math.pow(Math.E, z);
        double trimp = (double) minutes * x * y;
        sharedValues.saveInt("trimp", (int) Math.round(trimp));
        return (int) Math.round(trimp);
    }

    private void calculateFitness() {
        UserData userData = UserSessionManager.getUserData();
        int fitness;
        float tempFitness;
        float oldFitness;
        ArrayList<DailyData> dailyDataList = userData.getDailyData();
        DailyData dailyData;
        if (dailyDataList.size() == 1) {
            fitness = 1000;
            sharedValues.saveInt("fitness", fitness);
        } else if (dailyDataList.size() > 1) {
            dailyData = dailyDataList.get(dailyDataList.size() - 2);
            oldFitness = dailyData.getTrainingSessionData().getFitness();
            float days = calculateDaysSinceTraining(dailyData.getSessionDay());
            tempFitness = (float) (oldFitness * Math.pow(Math.E, (-days / 40.0f)));
            fitness = Math.round(tempFitness) + dailyData.getTrainingSessionData().getTrimpScore();
            sharedValues.saveInt("fitness", fitness);
        }
    }

    private void calculateFatigue() {
        UserData userData = UserSessionManager.getUserData();
        int fatigue;
        float tempFatigue;
        float oldFatigue;
        ArrayList<DailyData> dailyDataList = userData.getDailyData();
        DailyData dailyData;
        if (dailyDataList.size() == 1) {
            fatigue = sharedValues.getInt("trimpScore");
        } else if (dailyDataList.size() > 1) {
            dailyData = dailyDataList.get(dailyDataList.size() - 2);
            oldFatigue = dailyData.getTrainingSessionData().getFatigue();
            float days = calculateDaysSinceTraining(dailyData.getSessionDay());
            tempFatigue = (float) (oldFatigue * Math.pow(Math.E, (-days / 40.0f)));
            fatigue = Math.round(tempFatigue) + dailyData.getTrainingSessionData().getTrimpScore();
            sharedValues.saveInt("fatigue", fatigue);
        }
    }

    private void calculatePerformance() {
        int fitness = sharedValues.getInt("fitness");
        int fatigue = sharedValues.getInt("fatigue");
        int performance = fitness - 2 * fatigue;
        sharedValues.saveInt("performance", performance);
    }


    private int calculateDaysSinceTraining(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            Calendar calCheck = Calendar.getInstance();
            Date testDate = dateFormat.parse(date);
            calCheck.setTime(testDate);
            long msDiff = Calendar.getInstance().getTimeInMillis() - calCheck.getTimeInMillis();
            long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);
            return (int) daysDiff;

        } catch (ParseException e) {
            e.printStackTrace();
            return 1;
        }
    }
}
