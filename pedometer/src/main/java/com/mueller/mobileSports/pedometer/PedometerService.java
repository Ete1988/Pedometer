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

import com.mueller.mobileSports.general.SharedValues;
import com.mueller.mobileSports.user.UserSessionManager;

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
    private Sensor mSensor;
    private Handler mHandler;

    /*
        Timer for calculating distance, cadence, speed, totelEnergyExpenditure.
        Speed and cadence will be updated every 5 seconds
        Energy every second
     */
    private Runnable mTimer = new Runnable() {
        @Override
        public void run() {

            Intent i = new Intent(VALUES_CHANGED);
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);

            double cadence = calculateCadence(secondaryStepCount, 5);
            double speed = calculateSpeed(stride, cadence);
            double energy = calculateEnergyExpenditureForOneStep(cadence);
            double distance = calculateDistance(stride, stepsOverDay);

            energy = energy * (double) secondaryStepCount;

            //formatting
            cadence = Double.parseDouble(df.format(cadence));
            speed = Double.parseDouble(df.format(speed));
            totalExpenditureOverDay += Math.round(energy);
            distance = Double.parseDouble(df.format(distance));

            i.putExtra("cadenceValue", cadence);
            i.putExtra("speedValue", speed);
            i.putExtra("distanceValue", distance);
            i.putExtra("energyExpenditureSteps", totalExpenditureOverDay);

            sharedValues.saveFloat("distance", (float) distance);
            sharedValues.saveInt("energyExpenditureSteps", totalExpenditureOverDay);

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
        sharedValues.saveInt("stepsOverDay", stepsOverDay++);
        sharedValues.saveInt("stepsOverWeek", stepsOverWeek++);
        secondaryStepCount++;

        Intent intent = new Intent(STEP_MESSAGE);
        intent.putExtra("steps", stepsOverDay);
        sendBroadcast(intent);

    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mTimer);
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
        if (mSensor == null) {
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            if (mSensor == null) {
                Log.e(TAG, "Unable to initialize Sensor.");
            }
        }

        stepsOverDay = sharedValues.getInt("stepsOverDay");
        stepsOverWeek = sharedValues.getInt("stepsOverWeek");
        totalExpenditureOverDay = sharedValues.getInt("energyExpenditureSteps");

        mHandler.postDelayed(mTimer, 1000);
        stride = calculateStrideLength();
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
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

    @Contract(pure = true)
    private double calculateSpeed(double strideLength, double cadence) {
        return strideLength * cadence * 3.6; //  km/h
    }

    @Contract(pure = true)
    private double calculateDistance(double strideLength, int steps) {
        return (((steps / 2) * strideLength) / 1000); //km
    }

    private double calculateEnergyExpenditureForOneStep(double cadence) {
        double energyExpenditure;
        double gravity = 9.81;
        double weight = UserSessionManager.getUserData().getWeight();

        /*
            This is a rather trivial implemententaion based on the energy needed to do one step
            This result is based on the assumption that 1 step takes 1 second and
            decides with help of the candence if the user is running or walking.
            But again, this way of calculating is by no means accurate.
            If you walk really fast, you cadence can easily be over 3.0
            For this prototype this calculation is sufficient, but not for the real thing.
         */
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
}
