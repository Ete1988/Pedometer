package com.mueller.mobileSports.pedometer.pedometerService;


import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.mueller.mobileSports.general.SharedValues;


public class PedometerService extends Service implements SensorEventListener {

    public final static String STEP_MESSAGE = "com.mueller.mobileSPorts.pedometer.pedometerUtility.PedometerService.STEP_VALUE";
    private final static String TAG = PedometerService.class.getSimpleName();
    private final IBinder mBinder = new LocalBinder();
    private SensorManager sensorManager;
    private int mStepsDay, mStepsWeek;
    private SharedValues sharedValues;
    private Sensor mSensor;

    @Override
    public void onCreate() {
        sharedValues = SharedValues.getInstance(this);
        mStepsDay = sharedValues.getInt("stepsOverDay");
        mStepsWeek = sharedValues.getInt("stepsOverWeek");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sendMessage();
        Log.e(TAG, "Sensor Listener Change.");
    }

    private void sendMessage() {
        sharedValues.saveInt("stepsOverWeek", mStepsWeek++);
        sharedValues.saveInt("stepsOverDay", mStepsDay++);
        Intent intent = new Intent();
        intent.setAction(STEP_MESSAGE);
        intent.putExtra("DATA_PASSED", "");
        sendBroadcast(intent);
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
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (mSensor == null) {
                Log.e(TAG, "Unable to initialize Sensor.");
                return false;
            }
        }

        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.e(TAG, "Sensor Listener Started.");
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Not Implemented
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);          // get an instance of the SensorManager class, lets us access sensors.
        Sensor mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);    // get StepCounter sensor from the list of sensors.

        if (mSensor == null)
            stopSelf();
        else
            sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    public class LocalBinder extends Binder {
        public PedometerService getService() {
            return PedometerService.this;
        }
    }
}
