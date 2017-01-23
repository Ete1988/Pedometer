package com.mueller.mobileSports.pedometer;


import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.mueller.mobileSports.user.UserSessionManager;
import com.mueller.mobileSports.utility.SharedValues;

import org.jetbrains.annotations.Contract;

import java.math.RoundingMode;
import java.text.DecimalFormat;


public class PedometerService extends Service implements SensorEventListener {

    public final static String STEP_MESSAGE = "com.mueller.mobileSPorts.pedometer.pedometerUtility.PedometerService.STEP_VALUE";
    public final static String VALUES_CHANGED = "com.mueller.mobileSPorts.pedometer.pedometerUtility.PedometerService.CADENCE_VALUE";

    private final static String TAG = PedometerService.class.getSimpleName();
    private SensorManager sensorManager;
    private int stepsOverDay, stepsOverWeek, secondaryStepCount, totalExpenditureOverDay;
    private double stride;
    private SharedValues sharedValues;
    private Sensor stepSensor;
    private Sensor altitudeSensor;
    private Handler mHandler;
    private double oldHeight, height;


    /*
        Timer for calculating distance, cadence, speed, totalEnergyExpenditure.
     */
    private Runnable mTimer = new Runnable() {
        @Override
        public void run() {

            Intent i = new Intent(VALUES_CHANGED);
            double energy;
            double cadence = calculateCadence(secondaryStepCount, 5);
            double speed = calculateSpeed(stride, cadence);
            double distance = calculateDistance(stride, stepsOverDay);

            //formatting
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
            cadence = Double.parseDouble(df.format(cadence));
            speed = Double.parseDouble(df.format(speed));
            distance = Double.parseDouble(df.format(distance));

            if (altitudeSensor == null) {
                energy = calculateEnergyExpenditureSteps(cadence);
                energy = energy * (double) secondaryStepCount;
                totalExpenditureOverDay += Math.round(energy);
            } else {
                if (height > -1.0 && height < 1.0) {
                    energy = calculateEnergyExpenditureAltitude(4.1, calculateDistance(stride, secondaryStepCount));
                    totalExpenditureOverDay += Math.round(energy);
                } else if (height <= -1.0) {
                    energy = calculateEnergyExpenditureAltitude(1.93, calculateDistance(stride, secondaryStepCount));
                    totalExpenditureOverDay += Math.round(energy);
                } else if (height > 1.0) {
                    energy = calculateEnergyExpenditureAltitude(5.77, calculateDistance(stride, secondaryStepCount));
                    totalExpenditureOverDay += Math.round(energy);
                }
            }

            i.putExtra("cadenceValue", cadence);
            i.putExtra("speedValue", speed);
            i.putExtra("distanceValue", distance);
            i.putExtra("energyExpenditureSteps", totalExpenditureOverDay);

            sharedValues.saveInt("energyExpenditureSteps", totalExpenditureOverDay);
            sharedValues.saveFloat("distance", (float) distance);
            sendBroadcast(i);
            secondaryStepCount = 0;
            mHandler.postDelayed(this, 5000);
        }

    };

    @Override
    public void onCreate() {
        sharedValues = SharedValues.getInstance(this);
        mHandler = new Handler();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (Sensor.TYPE_STEP_DETECTOR == event.sensor.getType()) {
            sharedValues.saveInt("stepsOverDay", stepsOverDay++);
            sharedValues.saveInt("stepsOverWeek", stepsOverWeek++);
            secondaryStepCount++;

            Intent intent = new Intent(STEP_MESSAGE);
            intent.putExtra("steps", stepsOverDay);
            sendBroadcast(intent);

        }
        if (Sensor.TYPE_PRESSURE == event.sensor.getType()) {
            float pressure_value = event.values[0];
            double currentHeight = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure_value);
            if (!(currentHeight == 0) && !(oldHeight == 0)) {
                height = currentHeight - oldHeight;
            }
            oldHeight = currentHeight;
        }
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mTimer);
        sensorManager.unregisterListener(this);
        Log.w(TAG, "Pedometer destroyed.");
        super.onDestroy();
    }

    public void initialize() {

        if (sensorManager == null) {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            if (sensorManager == null) {
                Log.e(TAG, "Unable to initialize SensorManager.");
            }
        }

        if (stepSensor == null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            if (stepSensor == null) {
                Log.e(TAG, "Unable to initialize step sensor.");
            } else {
                sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
        }

        if (altitudeSensor == null) {
            altitudeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            if (altitudeSensor == null) {
                Log.e(TAG, "Unable to initialize altitude sensor.");
            } else {
                sensorManager.registerListener(this, altitudeSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
        stepsOverDay = sharedValues.getInt("stepsOverDay");
        stepsOverWeek = sharedValues.getInt("stepsOverWeek");
        totalExpenditureOverDay = sharedValues.getInt("energyExpenditureSteps");
        mHandler.postDelayed(mTimer, 1000);
        stride = calculateStrideLength();
        Log.i(TAG, "Sensor Listener Started.");

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Not Implemented
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        initialize();
        return START_STICKY;
    }

    @Contract(pure = true)
    private double calculateCadence(double stepCount, double timeWindow) {
        return stepCount / timeWindow;
    }

    /**
     * Calculates stride length based on age, height, weight.
     *
     * @return stride length in m
     */
    private double calculateStrideLength() {
        double strideLength;
        double age = UserSessionManager.getUserData().getAge();
        double height = UserSessionManager.getUserData().getHeight();
        double weight = UserSessionManager.getUserData().getWeight();
        if (UserSessionManager.getUserData().getGender().equals("Male")) {
            strideLength = (-0.002 * age) + (0.760 * (height / 100.0)) - (0.001 * weight) + 0.327;
        } else {
            strideLength = (-0.001 * age) + (1.058 * height / 100.0) - (0.002 * weight) - 0.129;
        }
        return strideLength;
    }

    /**
     * Calculates speed based on stride length and cadence
     *
     * @param strideLength in m
     * @param cadence      steps/s
     * @return speed in km/h
     */
    private double calculateSpeed(double strideLength, double cadence) {
        return strideLength * cadence * 3.6; //  km/h
    }


    /**
     * Calculates distance based on stride length and steps
     *
     * @param strideLength in m
     * @param steps        steps
     * @return distance in km
     */
    private double calculateDistance(double strideLength, int steps) {
        return (((steps / 2) * strideLength) / 1000); //km
    }

    /**
     * Method to calculate the energy expenditure based on the energy needed to to lift the body for one step.
     * This calculation is by no means accurate!
     *
     * @param cadence Step frequency to determine whether the user is running or walking (not accurate!).
     * @return energy expenditure for one step in calories.
     */
    private double calculateEnergyExpenditureSteps(double cadence) {
        double energyExpenditure;
        double gravity = 9.81;
        double weight = UserSessionManager.getUserData().getWeight();

        if (cadence <= 3.0) {
            //walking
            energyExpenditure = weight * gravity * 0.03;
        } else {
            //running
            energyExpenditure = weight * gravity * 0.07;
        }
        energyExpenditure = (energyExpenditure / 4.184);
        return energyExpenditure;
    }

    /**
     * Method to calculate the energy expenditure based on distance and altitude.
     *
     * @param cR       Factor for running uphill, downhill or flat terrain.
     * @param distance Distance traveled.
     * @return energy expenditure for travelled distance based on altitude.
     */
    private double calculateEnergyExpenditureAltitude(double cR, double distance) {
        double weight = UserSessionManager.getUserData().getWeight();
        double energyExpenditure;
        energyExpenditure = weight * cR * distance;
        return (energyExpenditure / 4.184);
    }
}
