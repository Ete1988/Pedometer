package com.mueller.mobileSports.pedometer;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lylc.widget.circularprogressbar.CircularProgressBar;
import com.mueller.mobileSports.general.SettingsActivity;
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
public class PedometerActivity extends AppCompatActivity {

    MyReceiver myReceiver;
    private SessionManager sessionManager;
    //TODO test this
    private boolean service_started = false;
    private CircularProgressBar cBar;
    private TextView date;
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

    private void init() {

        myReceiver = new MyReceiver();
        TimeManager timeManager = new TimeManager(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        date = (TextView) findViewById(R.id.date);
        cBar = (CircularProgressBar) findViewById(R.id.circularprogressbar3);
        cBar.setSubTitle("Steps");
        cBar.setMax(sharedValues.getInt("stepGoal"));
        timeManager.checkTime();
        getData();

    }

    private void startService() {
        if (!service_started) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(PedometerService.STEP_MESSAGE);
            registerReceiver(myReceiver, intentFilter);
            startService(new Intent(PedometerActivity.this, PedometerService.class));
        }
    }

    private void getData() {

        date.setText(sharedValues.getString("sessionDay"));
        cBar.setMax(sharedValues.getInt("stepGoal"));
        cBar.setTitle(Integer.toString(sharedValues.getInt("stepsOverDay")));
        cBar.setProgress(sharedValues.getInt("stepsOverDay"));

    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionManager.isLoginValid();
    }

    @Override
    protected void onDestroy() {
        sessionManager.uploadUserData(this, null, false);

        if (service_started) {
            unregisterReceiver(myReceiver);
            stopService(new Intent(this, PedometerService.class));
        }
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        if (sessionManager.isUserTokenAvailable()) {
            sessionManager.uploadUserData(this, null, false);
        }
        super.onPause();
    }

    public void onClickPedometer(View v) {
        if (v == null) {
            throw new NullPointerException(
                    "You are referring null object. "
                            + "Please check weather you had called super class method mappingWidgets() or not");
        } else if (v.getId() == R.id.PM_ProfileBtn) {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.PM_HeartRateBtn) {
            Intent i = new Intent(this, HeartRateActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Only one button for now.
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.menu_logout:
                sessionManager.logoutUser();
                break;
        }
        return true;
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            cBar.setProgress(sharedValues.getInt("stepsOverDay"));
            cBar.setTitle(Integer.toString(sharedValues.getInt("stepsOverDay")));
        }
    }

}





