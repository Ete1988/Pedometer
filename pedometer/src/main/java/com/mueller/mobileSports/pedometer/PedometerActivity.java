package com.mueller.mobileSports.pedometer;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lylc.widget.circularprogressbar.CircularProgressBar;
import com.mueller.mobileSports.general.BottomBarButtonManager;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.user.SessionManager;
import com.mueller.mobileSports.user.UserData;

/**
 * Created by Ete
 * <p>
 * Activity meant for the pedometer app mode
 */
public class PedometerActivity extends BottomBarButtonManager implements SensorEventListener {

    // Session Manager Class
    SessionManager sessionManager;
    private CircularProgressBar cBar;
    private SensorManager sensorManager;
    private TextView date;
    private int stepsOverWeek, stepsOverDay;
    private PedometerUtility timeManager;
    private SharedValues sharedValues;
    private UserData myData;

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        date = (TextView) findViewById(R.id.date);
        cBar = (CircularProgressBar) findViewById(R.id.circularprogressbar3);
        cBar.setSubTitle("Steps");
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();
        myData = sessionManager.getUserData();
        timeManager = new PedometerUtility(this);
        timeManager.checkTime();
        sharedValues = SharedValues.getInstance(this);
        getData();
        mappingWidgets();
    }

    @Override
    protected void mappingWidgets() {
        super.mappingWidgets();
    }

    private void getData() {
        stepsOverDay = sharedValues.getInt("dayCount");
        stepsOverWeek = myData.getWeeklyStepCount();
        date.setText(sharedValues.getString("checkDate"));
        cBar.setMax(myData.getStepGoal());
    }

    private void updateData() {

        myData.setWeeklyStepCount(stepsOverWeek);
        sharedValues.saveInt("dayCount", stepsOverDay);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionManager.checkLogin();
        getData();
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateData();


    }

    @Override
    protected void onPause() {
        super.onPause();
        updateData();
        // if you unregister the last listener, the hardware will stop detecting step events
        //sensorManager.unregisterListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        stepsOverDay++;
        stepsOverWeek++;
        cBar.setProgress(stepsOverDay);
        cBar.setTitle(Integer.toString(stepsOverDay));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}





