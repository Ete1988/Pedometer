package com.mueller.mobileSports.pedometer;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lylc.widget.circularprogressbar.CircularProgressBar;
import com.mueller.mobileSports.general.GenericActivity;
import com.mueller.mobileSports.general.SharedValues;
import com.mueller.mobileSports.general.TimeManager;
import com.mueller.mobileSports.heartRate.HeartRateActivity;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.pedometerUtility.PedometerService;
import com.mueller.mobileSports.user.ProfileActivity;
import com.mueller.mobileSports.user.SessionManager;

/**
 * Created by Ete
 * <p>
 * Activity meant for the pedometer app mode
 */
public class PedometerActivity extends GenericActivity {

    SessionManager sessionManager;
    MyReceiver myReceiver;
    private CircularProgressBar cBar;
    private TextView date;
    private int stepsOverWeek, stepsOverDay;
    private SharedValues sharedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        sharedValues = SharedValues.getInstance(this);
        init();
        sessionManager = new SessionManager(this);
        if (sessionManager.checkIfUserDataAvailable()) {
            sessionManager.checkUserState();
            startService();
        }

    }

    private void startService() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PedometerService.STEP_MESSAGE);
        registerReceiver(myReceiver, intentFilter);
        startService(new Intent(PedometerActivity.this, PedometerService.class));
    }

    private void init() {


        myReceiver = new MyReceiver();
        TimeManager timeManager = new TimeManager(this);
        date = (TextView) findViewById(R.id.date);
        cBar = (CircularProgressBar) findViewById(R.id.circularprogressbar3);
        cBar.setSubTitle("Steps");
        cBar.setMax(sharedValues.getInt("stepGoal"));
        timeManager.checkTime();
        getData();
        mappingWidgets();


    }

    private void getData() {

        stepsOverDay = sharedValues.getInt("stepsOverDay");
        stepsOverWeek = sharedValues.getInt("stepsOverWeek");
        date.setText(sharedValues.getString("sessionDay"));
        cBar.setMax(sharedValues.getInt("stepGoal"));
        cBar.setTitle(Integer.toString(sharedValues.getInt("stepsOverDay")));
        cBar.setProgress(sharedValues.getInt("stepsOverDay"));

    }

    private void updateData(Intent intent) {
        sharedValues.saveInt("stepsOverDay", stepsOverDay);
        sharedValues.saveInt("stepsOverWeek", stepsOverWeek);
        sessionManager.uploadUserData(this, intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    @Override
    protected void onDestroy() {
        sharedValues.saveBool("notFirstTimePedo", false);
        if (sessionManager.isUserTokenAvailable()) {
            updateData(null);
        }
        unregisterReceiver(myReceiver);
        stopService(new Intent(this, PedometerService.class));
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (v == null)
            throw new NullPointerException(
                    "You are refering null object. "
                            + "Please check weather you had called super class method mappingWidgets() or not");
        if (v.getId() == R.id.PedometerBtn) {
            updateData(new Intent(this, PedometerActivity.class));
        } else if (v.getId() == R.id.ProfileBtn) {
            updateData(new Intent(this, ProfileActivity.class));
        } else if (v.getId() == R.id.HeartRateBtn) {
            updateData(new Intent(this, HeartRateActivity.class));
        }
    }

    @Override
    protected void mappingWidgets() {
        super.mappingWidgets();
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            cBar.setProgress(sharedValues.getInt("stepsOverDay"));
            cBar.setTitle(Integer.toString(sharedValues.getInt("stepsOverDay")));
        }
    }

}





