package com.mueller.mobileSports.pedometer.pedometerService;


import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.mueller.mobileSports.general.SharedValues;


public class PedometerService extends Service implements SensorEventListener {

    public final static String STEP_MESSAGE = "com.mueller.mobileSPorts.pedometer.pedometerUtility.PedometerService.STEP_VALUE";
    public final static String CADENCE_MESSAGE = "com.mueller.mobileSPorts.pedometer.pedometerUtility.PedometerService.CADENCE_VALUE";

    private final static String TAG = PedometerService.class.getSimpleName();
    private final IBinder mBinder = new LocalBinder();
    private SensorManager sensorManager;
    private int mStepsDay, mStepsWeek, timeCount, mSecondaryStepCount;
    private SharedValues sharedValues;
    private Sensor mSensor;
    private Handler mHandler;
    private Runnable mTimer = new Runnable() {
        @Override
        public void run() {
            timeCount++;

            if (timeCount == 5) {
                sharedValues.saveInt("cadence", mSecondaryStepCount);
                mSecondaryStepCount = 0;
                timeCount = 0;
                broadcastUpdate(CADENCE_MESSAGE);
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
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sharedValues.saveInt("stepsOverWeek", mStepsWeek++);
        sharedValues.saveInt("stepsOverDay", mStepsDay++);
        mSecondaryStepCount++;
        broadcastUpdate(STEP_MESSAGE);

    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mTimer);
        Log.w(TAG, "Pedo destroyed.");
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
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
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

    public class LocalBinder extends Binder {
        public PedometerService getService() {
            return PedometerService.this;
        }
    }

}
