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
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.pedometerUtility.PedometerData;
import com.mueller.mobileSports.pedometer.pedometerUtility.PedometerService;
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
    private TimeManager timeManager;
    private PedometerData userData;
    private SharedValues sharedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        sharedValues = SharedValues.getInstance(this);
        init();
        sessionManager = new SessionManager(this);
        if (sessionManager.checkUserState()) {
            invalidateOptionsMenu();
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
        timeManager = new TimeManager(this);

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

    private void updateData() {
        invalidateOptionsMenu();
        sharedValues.saveInt("stepsOverDay", stepsOverDay);
        sharedValues.saveInt("stepsOverWeek", stepsOverWeek);

        sessionManager.uploadUserData(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateData();
        unregisterReceiver(myReceiver);
        stopService(new Intent(this, PedometerService.class));
    }

    @Override
    protected void onPause() {
        updateData();
        invalidateOptionsMenu();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
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





