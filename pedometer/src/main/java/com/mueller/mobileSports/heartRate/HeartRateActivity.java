package com.mueller.mobileSports.heartRate;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mueller.mobileSports.pedometer.MainActivity.R;

public class HeartRateActivity extends AppCompatActivity {

    long init, paused;
    private ImageView iv_start, iv_restart;
    private TextView mTextTime;
    private int btnState, time_seconds, time_minutes, time_milliseconds;
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
            mTextTime.setText(String.format("%02d:%02d:%02d", time_minutes, time_seconds, time_milliseconds));
            mHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mTextTime = (TextView) findViewById(R.id.timeTextView);
        iv_start = (ImageView) findViewById(R.id.iv_start_stop);
        iv_restart = (ImageView) findViewById(R.id.iv_reset);
        iv_start.setEnabled(true);
        btnState = 1;
        mHandler = new Handler();

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
                mTextTime.setText(String.format("%02d:%02d:%02d", 0, 0, 0));

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start_time += System.currentTimeMillis() - paused;
    }

}
