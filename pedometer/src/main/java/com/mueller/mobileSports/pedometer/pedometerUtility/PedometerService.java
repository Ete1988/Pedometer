package com.mueller.mobileSports.pedometer.pedometerUtility;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.lylc.widget.circularprogressbar.CircularProgressBar;
import com.mueller.mobileSports.general.SharedValues;
import com.mueller.mobileSports.user.SessionManager;

/**
 * Created by Ete on 22/12/2016.
 */

public class PedometerService extends Service implements SensorEventListener {

    static final public String STEP_MESSAGE = "com.mueller.mobileSPorts.pedometer..COPAService.STEP_VALUE";
    SessionManager sessionManager;
    PedometerData userData;
    private CircularProgressBar cBar;
    private SensorManager sensorManager;
    private Sensor mSensor;
    private int mCounterSteps;
    private int mStepsDay, mStepsWeek;
    private SharedValues sharedValues;

    @Override
    public void onCreate() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sessionManager = new SessionManager(this);
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
        Log.d("sensorService", "onSensorChanged.");

        sendMessage("");
    }

    public void sendMessage(String msg) {
        sharedValues.saveInt("stepsOverWeek", mStepsWeek++);
        sharedValues.saveInt("stepsOverDay", mStepsDay++);
        Intent intent = new Intent();
        intent.setAction(STEP_MESSAGE);
        intent.putExtra("DATAPASSED", msg);
        sendBroadcast(intent);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);          // get an instance of the SensorManager class, lets us access sensors.
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);    // get StepCounter sensor from the list of sensors.

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
