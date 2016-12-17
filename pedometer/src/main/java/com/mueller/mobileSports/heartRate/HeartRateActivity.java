package com.mueller.mobileSports.heartRate;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mueller.mobileSports.account.SessionManager;
import com.mueller.mobileSports.general.BottomBarButtonManager;
import com.mueller.mobileSports.heartRate.HRMUtility.HeartRateMonitor;
import com.mueller.mobileSports.heartRate.HRMUtility.SimulationHRM;
import com.mueller.mobileSports.pedometer.MainActivity.R;

import java.util.Locale;

public class HeartRateActivity extends BottomBarButtonManager {

    long init, paused;
    private ImageView iv_start, iv_restart;
    private TextView mTextTime, mHeartRate, mAverageHeartRate, mMaxHearRate, mMinHeartRate;
    private int btnState, time_seconds, time_minutes, time_milliseconds;
    private int maxHeartRate = 0, minHeartRate = 0, averageHeartRate = 0;
    private int updateCounter = 0, averageHeartRateCalculationCounter = 0;
    private long start_time, timeInMilliseconds, time_update, time_swapBuff;
    private Handler mHandler;
    public Runnable mTimerRunnable = new Runnable() {

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
    private SessionManager sessionManager;
    private int[] heartRateDataArray, averageHeartRateArray;
    public Runnable mHeartRateSimulation = new Runnable() {
        @Override
        public void run() {
            if (updateCounter == heartRateDataArray.length) {
                updateCounter = 0;
            }
            System.out.println("" + updateCounter);
            averageHeartRateArray[averageHeartRateCalculationCounter++] = heartRateDataArray[updateCounter];

            if (averageHeartRateCalculationCounter == 3) {
                mAverageHeartRate.setText(String.format(Locale.getDefault(), "%03d", calculateAverageHeartRate()));
                averageHeartRateCalculationCounter = 0;
            }
            mMaxHearRate.setText(String.format(Locale.getDefault(), "%03d", heartRateDataArray[updateCounter]));
            mMinHeartRate.setText(String.format(Locale.getDefault(), "%03d", heartRateDataArray[updateCounter]));
            mHeartRate.setText(String.format(Locale.getDefault(), "%03d", heartRateDataArray[updateCounter++]));
            mHandler.postDelayed(this, 4000);
        }
    };
    private HeartRateMonitor hRM;
    private Button test_Btn;
    private boolean hRM_active = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mappingWidgets();
        sessionManager = new SessionManager(this);
        test_Btn = (Button) findViewById(R.id.test_btn);
        mTextTime = (TextView) findViewById(R.id.timeTextView);
        mHeartRate = (TextView) findViewById(R.id.HeartRate);
        mAverageHeartRate = (TextView) findViewById(R.id.txtAvrHRView);
        mMaxHearRate = (TextView) findViewById(R.id.txtMaxHRView);
        mMinHeartRate = (TextView) findViewById(R.id.txtMinHRView);
        iv_start = (ImageView) findViewById(R.id.iv_start_stop);
        iv_restart = (ImageView) findViewById(R.id.iv_reset);
        averageHeartRateArray = new int[3];
        //iv_start.setEnabled(true);
        btnState = 1;
        mHandler = new Handler();
        getHeartRateData();

        test_Btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (hRM_active) {
                    test_Btn.setText(R.string.stop_monitoring);
                    mHandler.postDelayed(mHeartRateSimulation, 10L);
                    hRM_active = false;
                } else {
                    test_Btn.setText(R.string.start_monitoring);
                    mHandler.removeCallbacks(mHeartRateSimulation);
                    hRM_active = true;
                    updateCounter = 0;
                    averageHeartRateCalculationCounter = 0;
                }
            }
        });

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

    @Override
    protected void mappingWidgets() {
        super.mappingWidgets();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    private int calculateAverageHeartRate() {
        int sum = 0;
        for (int i : averageHeartRateArray) {
            sum += i;
        }
        averageHeartRate = sum / 3;
        return averageHeartRate;
    }

    private int setMaxHeartRate(int heartRate) {
        if (heartRate > maxHeartRate) {
            maxHeartRate = heartRate;
        }
        return maxHeartRate;
    }

    private int setMinHeartRate(int heartRate) {
        if (heartRate < minHeartRate || minHeartRate == 0) {
            minHeartRate = heartRate;
        }
        return minHeartRate;

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
        sessionManager.checkLogin();
        start_time += System.currentTimeMillis() - paused;
    }

}
