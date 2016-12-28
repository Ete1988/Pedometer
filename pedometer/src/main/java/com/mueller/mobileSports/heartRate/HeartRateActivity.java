package com.mueller.mobileSports.heartRate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mueller.mobileSports.general.SettingsActivity;
import com.mueller.mobileSports.general.SharedValues;
import com.mueller.mobileSports.heartRate.hR_Monitor.HeartRateMonitor;
import com.mueller.mobileSports.heartRate.hR_Monitor.HeartRateSimulationService;
import com.mueller.mobileSports.heartRate.hR_Monitor.SensorFactory;
import com.mueller.mobileSports.heartRate.hR_Monitor.SimulationFactory;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.PedometerActivity;
import com.mueller.mobileSports.user.ProfileActivity;
import com.mueller.mobileSports.user.SessionManager;

import java.util.Locale;
import java.util.Objects;

/**
 * Created by Ete
 * <p>
 * Activity meant for the heart rate monitoring app mode
 */

public class HeartRateActivity extends AppCompatActivity {


    MyReceiver myReceiver;
    private Runnable mTimerRunnable;
    private long paused;
    private SharedValues sharedValues;
    private ImageView iv_start, iv_restart;
    private TextView mTextTime, mHeartRate, mAverageHeartRate, mMaxHearRate, mMinHeartRate;
    private int btnState, time_seconds, time_minutes, time_milliseconds;
    private long start_time, timeInMilliseconds, time_update, time_swapBuff;
    private Handler mHandler;
    private SessionManager sessionManager;
    private SensorFactory sensorFactory;
    private HeartRateMonitor heartRateMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        init();

        iv_start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (btnState == 1) {
                    iv_start.setImageResource(R.mipmap.ic_stop);
                    start_time = System.currentTimeMillis();
                    mHandler.postDelayed(mTimerRunnable, 10L);
                    btnState = 0;
                } else {
                    iv_start.setImageResource(R.mipmap.ic_start);
                    time_swapBuff += timeInMilliseconds;
                    mHandler.removeCallbacks(mTimerRunnable);
                    btnState = 1;
                }
            }
        });

        iv_restart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startHRM();
                /*
                start_time = 0L;
                timeInMilliseconds = 0L;
                time_swapBuff = 0L;
                btnState = 1;
                time_seconds = 0;
                time_milliseconds = 0;
                time_minutes = 0;
                mHandler.removeCallbacks(mTimerRunnable);
                mTextTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", 0, 0, 0));
            */

            }
        });
    }

    private void init() {
        myReceiver = new MyReceiver();
        sharedValues = SharedValues.getInstance(this);
        sessionManager = new SessionManager(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mTextTime = (TextView) findViewById(R.id.timeTextView);
        mHeartRate = (TextView) findViewById(R.id.txtHeartRateView);
        mAverageHeartRate = (TextView) findViewById(R.id.txtAvrHRView);
        mMaxHearRate = (TextView) findViewById(R.id.txtMaxHRView);
        mMinHeartRate = (TextView) findViewById(R.id.txtMinHRView);
        iv_start = (ImageView) findViewById(R.id.iv_start_stop);
        iv_restart = (ImageView) findViewById(R.id.iv_reset);
        btnState = 1;
        mHandler = new Handler();

        mTimerRunnable = new Runnable() {

            @Override
            public void run() {

                timeInMilliseconds = System.currentTimeMillis() - start_time;
                time_update = time_swapBuff + timeInMilliseconds;
                time_seconds = (int) (time_update / 1000);
                time_minutes = time_seconds / 60;
                time_milliseconds = (int) (time_update % 1000);
                mTextTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", time_minutes, time_seconds % 60, time_milliseconds % 100));
                mHandler.postDelayed(this, 30);
            }
        };
    }

    private void startHRM() {

        HeartRateMonitor heartRateMonitor;
        System.out.println("startHRM");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HeartRateSimulationService.HRM_SIMULATION_MESSAGE);
        registerReceiver(myReceiver, intentFilter);
        sensorFactory = new SimulationFactory(this);
        sensorFactory.createHRM();

        //startService(new Intent(HeartRateActivity.this, HeartRateSimulationService.class));
    }

    public void calculateTRIMP() {
        double duration = time_minutes;
        double x, y, b;
        double trimp;

        x = ((sharedValues.getInt("averageHeartRate") - sharedValues.getInt("minHeartRate") /
                (sharedValues.getInt("maxHeartRate") - sharedValues.getInt("minHeartRate"))));

        if (Objects.equals(sharedValues.getString("gender"), "Female")) {
            b = 1.67;
        } else {
            b = 1.92;
        }

        y = Math.exp(b * x);

        trimp = duration * x * y;
    }

    public void onClickHeartRateMeter(View v) {
        if (v == null) {
            throw new NullPointerException(
                    "You are refering null object. "
                            + "Please check weather you had called super class method mappingWidgets() or not");
        } else if (v.getId() == R.id.HRM_ProfileBtn) {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.HRM_PedometerBtn) {
            Intent i = new Intent(this, PedometerActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = System.currentTimeMillis();

    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionManager.isLoginValid();
        start_time += System.currentTimeMillis() - paused;
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
            mMaxHearRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("maxHeartRate")));
            mMinHeartRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("minHeartRate")));
            mHeartRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("currentHeartRate")));
            mAverageHeartRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("averageHeartRate")));

        }
    }

}

