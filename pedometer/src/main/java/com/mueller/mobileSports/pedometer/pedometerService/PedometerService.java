package com.mueller.mobileSports.pedometer.pedometerService;


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


public class PedometerService extends Service implements SensorEventListener {

    public final static String STEP_MESSAGE = "com.mueller.mobileSPorts.pedometer.pedometerUtility.PedometerService.STEP_VALUE";
    public final static String VALUES_CHANGED = "com.mueller.mobileSPorts.pedometer.pedometerUtility.PedometerService.CADENCE_VALUE";

    private final static String TAG = PedometerService.class.getSimpleName();
    private SensorManager sensorManager;
    private int mStepsDay, mStepsWeek, timeCount, mCadenceStepCount;

    private SharedValues sharedValues;
    private Sensor mSensor;
    private Handler mHandler;
    private Runnable mTimer = new Runnable() {
        @Override
        public void run() {
            timeCount++;

            if (timeCount == 5) {
                double cadence = calculateCadence(mCadenceStepCount, 5);
                calculateEnergyExpenditure(cadence);
                double stride = calculateStrideLength();
                double distance = calculateDistance(stride, mStepsDay);
                double speed = calculateSpeed(stride, cadence);
                Intent i = new Intent(VALUES_CHANGED);
                i.putExtra("cadenceValue", cadence);
                i.putExtra("speedValue", speed);
                i.putExtra("distanceValue", distance);
                sendBroadcast(i);
                mCadenceStepCount = 0;
                timeCount = 0;
            }
            mHandler.postDelayed(this, 1000);
        }

    };

    @Override
    public void onCreate() {
        sharedValues = SharedValues.getInstance(this);
        mHandler = new Handler();
        mStepsDay = sharedValues.getInt("stepsOverDay");
        mStepsWeek = sharedValues.getInt("stepsOverWeek");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sharedValues.saveInt("stepsOverWeek", mStepsWeek++);
        sharedValues.saveInt("stepsOverDay", mStepsDay++);
        mCadenceStepCount++;

        broadcastUpdate(STEP_MESSAGE);

    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mTimer);
        Log.w(TAG, "Pedometer destroyed.");
        super.onDestroy();
    }

    public boolean initialize() {

        if (sensorManager == null) {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            if (sensorManager == null) {
                Log.e(TAG, "Unable to initialize SensorManager.");
                return false;
            }
        }
        if (mSensor == null) {
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            if (mSensor == null) {
                Log.e(TAG, "Unable to initialize Sensor.");
                return false;
            }
        }

        mHandler.postDelayed(mTimer, 1000);
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        Log.e(TAG, "Sensor Listener Started.");
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Not Implemented
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        initialize();
        return START_STICKY;
    }

    private double calculateCadence(double stepCount, int timeWindow) {
        return stepCount / (double) timeWindow;
    }

    private double calculateStrideLength() {
        double strideLength;
        double age = sharedValues.getInt("age");
        double height = sharedValues.getInt("height");
        double weight = sharedValues.getInt("weight");
        if (sharedValues.getString("gender").equals("Male")) {
            strideLength = (-0.002 * age) + (0.760 * (height / 100.0)) - (0.001 * weight) + 0.327;
        } else {
            strideLength = (-0.001 * age) + (1.058 * height / 100.0) - (0.002 * weight) - 0.129;
        }

        return strideLength;
    }

    private double calculateSpeed(double strideLength, double cadence) {
        return strideLength * cadence * 3.6; //  km/h
    }

    private double calculateDistance(double strideLength, int steps) {
        return ((((double) steps / 2) * strideLength) / 1000); //km
    }

    //TODO Increment through the day!
    private void calculateEnergyExpenditure(double cadence) {
        double energyExpenditure;
        double gravity = 9.81;
        double weight = sharedValues.getInt("weight");


        //Energy for lifting body in J
        if (cadence <= 3.0) {
            energyExpenditure = weight * gravity * 0.03 * cadence;
        } else {
            energyExpenditure = weight * gravity * 0.07 * cadence;
        }


    }
}
