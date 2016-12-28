package com.mueller.mobileSports.pedometer.pedometerUtility;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import com.mueller.mobileSports.general.SharedValues;

/**
 * Created by Ete on 22/12/2016.
 */

public class PedometerService extends Service implements SensorEventListener {

    static final public String STEP_MESSAGE = "com.mueller.mobileSPorts.pedometer.pedometerUtility.PedometerService.STEP_VALUE";
    private SensorManager sensorManager;

    private int mStepsDay, mStepsWeek;
    private SharedValues sharedValues;

    @Override
    public void onCreate() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sharedValues = SharedValues.getInstance(this);
        mStepsDay = sharedValues.getInt("stepsOverDay");
        mStepsWeek = sharedValues.getInt("stepsOverWeek");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Stop Service
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sendMessage();
    }

    private void sendMessage() {
        sharedValues.saveInt("stepsOverWeek", mStepsWeek++);
        sharedValues.saveInt("stepsOverDay", mStepsDay++);
        Intent intent = new Intent();
        intent.setAction(STEP_MESSAGE);
        intent.putExtra("DATA_PASSED", "");
        sendBroadcast(intent);
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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Not Implemented
    }
}
