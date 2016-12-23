package com.mueller.mobileSports.heartRate;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mueller.mobileSports.general.GenericActivity;
import com.mueller.mobileSports.general.SharedValues;
import com.mueller.mobileSports.heartRate.hR_Monitor.HeartRateMonitor;
import com.mueller.mobileSports.heartRate.hR_Monitor.SimulationHRM;
import com.mueller.mobileSports.heartRate.hR_Utility.HeartRateMonitorUtility;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.user.SessionManager;

import java.util.Locale;

/**
 * Created by Ete
 * <p>
 * Activity meant for the heart rate monitoring app mode
 */

public class HeartRateActivity extends GenericActivity {

    public Runnable mTimerRunnable;
    public Runnable mHeartRateSimulation;
    long paused;
    private SharedValues sharedValues;
    private ImageView iv_start, iv_restart;
    private TextView mTextTime, mHeartRate, mAverageHeartRate, mMaxHearRate, mMinHeartRate;
    private int btnState, time_seconds, time_minutes, time_milliseconds;
    private int maxHeartRate, minHeartRate, averageHeartRate;
    private int updateCounter, averageHeartRateCalculationCounter;
    private long start_time, timeInMilliseconds, time_update, time_swapBuff;
    private Handler mHandler;
    private HeartRateMonitor hRM;
    private int[] heartRateDataArray, averageHeartRateArray;
    private SessionManager sessionManager;
    private HeartRateMonitorUtility heartRateMonitorUtility;

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
                    mHandler.postDelayed(mHeartRateSimulation, 10L);
                    btnState = 0;
                } else {
                    iv_start.setImageResource(R.mipmap.ic_start);
                    time_swapBuff += timeInMilliseconds;
                    mHandler.removeCallbacks(mTimerRunnable);
                    mHandler.removeCallbacks(mHeartRateSimulation);
                    btnState = 1;
                }
            }
        });

        iv_restart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                start_time = 0L;
                timeInMilliseconds = 0L;
                time_swapBuff = 0L;
                btnState = 1;
                time_seconds = 0;
                time_milliseconds = 0;
                time_minutes = 0;
                mHandler.removeCallbacks(mTimerRunnable);
                mTextTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", 0, 0, 0));

            }
        });
    }

    private void init() {

        updateCounter = 0;
        averageHeartRateCalculationCounter = 0;

        sharedValues = SharedValues.getInstance(this);
        heartRateMonitorUtility = new HeartRateMonitorUtility(this);
        sessionManager = new SessionManager(this);
        mTextTime = (TextView) findViewById(R.id.timeTextView);
        mHeartRate = (TextView) findViewById(R.id.txtHeartRateView);
        mAverageHeartRate = (TextView) findViewById(R.id.txtAvrHRView);
        mMaxHearRate = (TextView) findViewById(R.id.txtMaxHRView);
        mMinHeartRate = (TextView) findViewById(R.id.txtMinHRView);
        iv_start = (ImageView) findViewById(R.id.iv_start_stop);
        iv_restart = (ImageView) findViewById(R.id.iv_reset);
        averageHeartRateArray = new int[3];
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

        mHeartRateSimulation = new Runnable() {
            @Override
            public void run() {
                if (updateCounter == heartRateDataArray.length) {
                    updateCounter = 0;
                }
                averageHeartRateArray[averageHeartRateCalculationCounter++] = heartRateDataArray[updateCounter];

                if (averageHeartRateCalculationCounter == 3) {
                    heartRateMonitorUtility.calculateAverageHeartRate(averageHeartRateArray);
                    mAverageHeartRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("averageHeartRate")));
                    averageHeartRateCalculationCounter = 0;
                }
                heartRateMonitorUtility.storeMinAndMaxHeartRate(heartRateDataArray[updateCounter]);
                mMaxHearRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("maxHeartRate")));
                mMinHeartRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("minHeartRate")));
                mHeartRate.setText(String.format(Locale.getDefault(), "%03d", heartRateDataArray[updateCounter++]));
                mHandler.postDelayed(this, 4000);
            }
        };

        mappingWidgets();
        getHeartRateData();

    }

    @Override
    protected void mappingWidgets() {
        super.mappingWidgets();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }



    public void getHeartRateData() {
        hRM = new SimulationHRM(this);
        heartRateDataArray = hRM.getHeartRate();
        System.out.println(heartRateDataArray.length);
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionManager.checkUserState();
        start_time += System.currentTimeMillis() - paused;
    }

}
