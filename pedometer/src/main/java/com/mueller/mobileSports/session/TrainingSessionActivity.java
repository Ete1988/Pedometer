package com.mueller.mobileSports.session;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mueller.mobileSports.heartRate.HeartRateSensorService;
import com.mueller.mobileSports.heartRate.HeartRateSensorSimulationService;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.utility.SharedValues;

import java.util.Locale;

/**
 * Activity mean to display data during a session.
 * Offers methods to start stop or pause a session.
 */
public class TrainingSessionActivity extends AppCompatActivity {

    TrainingSessionCalculations trainingSessionCalculations;
    private TextView mTextTime, mHeartRate, mTotalEnergyExpenditure, mAverageHeartRate, mMaxHearRate, mMinHeartRate, mTrimpScore, mPercentageOfHrMax, mEnergyExpenditure;
    private int time_seconds, time_minutes, time_milliseconds;
    private long paused, start_time, timeInMilliseconds, time_update, time_swapBuff;
    private Handler mHandler;
    private final Runnable mTimerRunnable = new Runnable() {

        @Override
        public void run() {
            timerCalculations();
            mHandler.postDelayed(this, 30);
        }
    };
    private boolean timerRunning;
    private SharedValues sharedValues;
    private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (HeartRateSensorService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //TODO implement this
                Log.d("Session: ", "Device Disconnected.");
            } else if (HeartRateSensorService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(HeartRateSensorService.EXTRA_DATA));

            } else if (HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_HEART_RATE_DETECTED.equals(action)) {
                displayData(intent.getStringExtra(HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_HEART_RATE_DETECTED));
            }
        }
    };

    private static IntentFilter updateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_HEART_RATE_DETECTED);
        intentFilter.addAction(HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_CONNECTED);
        intentFilter.addAction(HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_DISCONNECTED);
        intentFilter.addAction(HeartRateSensorService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(HeartRateSensorService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(HeartRateSensorService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(HeartRateSensorService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    //HeartIcon Animation
    private void anim(boolean check) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        ImageView myButton = (ImageView) findViewById(R.id.heartBtn);

        if (check) {
            myButton.setAnimation(shake);
        } else {
            myButton.clearAnimation();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_session);
        init();
        if (timerRunning) {
            registerReceiver(mUpdateReceiver, updateIntentFilter());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (timerRunning) {
            registerReceiver(mUpdateReceiver, updateIntentFilter());

        }
        start_time += System.currentTimeMillis() - paused;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timerRunning) {
            tryToUnregisterReceiver(mUpdateReceiver);
        }

        paused = System.currentTimeMillis();
    }

    /**
     * Initializes most fields of activity
     */
    private void init() {
        sharedValues = SharedValues.getInstance(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mHandler = new Handler();
        mTextTime = (TextView) findViewById(R.id.TS_TimerView);
        mHeartRate = (TextView) findViewById(R.id.TS_HeartRateView);
        mAverageHeartRate = (TextView) findViewById(R.id.TS_AvrHRView);
        mMaxHearRate = (TextView) findViewById(R.id.TS_MaxHRView);
        mMinHeartRate = (TextView) findViewById(R.id.TS_MinHRView);
        mPercentageOfHrMax = (TextView) findViewById(R.id.TS_PercentageOfHrMaxView);
        mTrimpScore = (TextView) findViewById(R.id.TS_TrimpScoreView);
        mEnergyExpenditure = (TextView) findViewById(R.id.TS_EnergyExpenditureView);
        mTotalEnergyExpenditure = (TextView) findViewById(R.id.TS_TotalEnergyView);
    }

    /**
     * small calculations for display of session timer.
     */
    private void timerCalculations() {
        timeInMilliseconds = SystemClock.uptimeMillis() - start_time;
        time_update = time_swapBuff + timeInMilliseconds;
        time_seconds = (int) (time_update / 1000);
        time_minutes = time_seconds / 60;
        time_seconds = time_seconds % 60;
        time_milliseconds = (int) (time_update % 1000);
        mTextTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", time_minutes, time_seconds, time_milliseconds % 100));
    }

    public void onClickTrainingSessionActivity(View v) {
        Button myButton;
        if (v.getId() == R.id.TS_StartSessionBtn) {
            resetAllValuesOnTimerStart();
            registerReceiver(mUpdateReceiver, updateIntentFilter());
            start_time = SystemClock.uptimeMillis();
            mHandler.postDelayed(mTimerRunnable, 0);
            timerRunning = true;
            myButton = (Button) findViewById(R.id.TS_StartSessionBtn);
            myButton.setVisibility(View.GONE);
            anim(true);
        } else if (v.getId() == R.id.TS_PauseSessionBtn) {
            myButton = (Button) findViewById(R.id.TS_PauseSessionBtn);
            if (timerRunning) {
                time_swapBuff += timeInMilliseconds;
                tryToUnregisterReceiver(mUpdateReceiver);
                mHandler.removeCallbacks(mTimerRunnable);
                myButton.setText(R.string.resume);
                timerRunning = false;
                anim(false);
            } else {
                start_time = SystemClock.uptimeMillis();
                registerReceiver(mUpdateReceiver, updateIntentFilter());
                mHandler.postDelayed(mTimerRunnable, 0);
                timerRunning = true;
                anim(true);
                myButton.setText(R.string.pause);
            }
        } else if (v.getId() == R.id.TS_StopSessionBtn) {
            timerRunning = false;
            anim(false);
            mHandler.removeCallbacks(mTimerRunnable);
            trainingSessionCalculations = new TrainingSessionCalculations(this);
            int trimp = trainingSessionCalculations.calculateTRIMP(time_minutes);
            float sessionDuration = time_minutes + (time_seconds / 100);
            float totalEnergyExpenditure = trainingSessionCalculations.calculateTotalEnergyExpenditureDuringSession(sessionDuration);
            trainingSessionCalculations.calculatePerformanceFitnessFatigue();

            mTotalEnergyExpenditure.setText(String.format(Locale.getDefault(), "%.2f", totalEnergyExpenditure));
            mTrimpScore.setText(String.format(Locale.getDefault(), "%d", trimp));

            sharedValues.saveFloat("totalEnergyExpenditureDuringSession", totalEnergyExpenditure);
            sharedValues.saveFloat("sessionDuration", sessionDuration);

            tryToUnregisterReceiver(mUpdateReceiver);
            myButton = (Button) findViewById(R.id.TS_StartSessionBtn);
            myButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Method to map data to widgets in view
     *
     * @param heartRate data received through intent;
     */
    private void displayData(String heartRate) {
        if (heartRate != null) {
            mHeartRate.setText(heartRate);
            trainingSessionCalculations.calculateEnergyExpenditure(sharedValues.getInt("currentHeartRate"));
            mMaxHearRate.setText(String.format(Locale.getDefault(), "%3d", sharedValues.getInt("maxHeartRate")));
            mMinHeartRate.setText(String.format(Locale.getDefault(), "%3d", sharedValues.getInt("minHeartRate")));
            mHeartRate.setText(String.format(Locale.getDefault(), "%3d", sharedValues.getInt("currentHeartRate")));
            mAverageHeartRate.setText(String.format(Locale.getDefault(), "%3d", sharedValues.getInt("averageHeartRate")));
            mEnergyExpenditure.setText(String.format(Locale.getDefault(), "%3d", sharedValues.getInt("energyExpenditureHR")));
            mPercentageOfHrMax.setText(String.format(Locale.getDefault(), "%2d", sharedValues.getInt("percentOfHRMax")));
        }
    }

    /**
     * Reset all values on start of a new session
     */
    private void resetAllValuesOnTimerStart() {

        start_time = 0L;
        timeInMilliseconds = 0L;
        time_swapBuff = 0L;
        time_update = 0L;
        time_seconds = 0;
        time_minutes = 0;
        time_milliseconds = 0;
        sharedValues.saveInt("averageHeartRate", 0);
        sharedValues.saveInt("maxHeartRate", 0);
        sharedValues.saveInt("minHeartRate", 0);
        sharedValues.saveInt("energyExpenditureHR", 0);
        sharedValues.saveInt("percentOfHRmax", 0);
        mTrimpScore.setText(R.string.emptyString);
        mAverageHeartRate.setText(R.string.emptyString);
        mPercentageOfHrMax.setText(R.string.emptyString);
        mMaxHearRate.setText(R.string.emptyString);
        mMinHeartRate.setText(R.string.emptyString);
        mHeartRate.setText(R.string.emptyString);
        mEnergyExpenditure.setText(R.string.emptyString);
        mTotalEnergyExpenditure.setText("");

    }

    private void tryToUnregisterReceiver(BroadcastReceiver myReceiver) {
        try {
            unregisterReceiver(myReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

}
