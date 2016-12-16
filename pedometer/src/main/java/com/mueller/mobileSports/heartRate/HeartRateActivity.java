package com.mueller.mobileSports.heartRate;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mueller.mobileSports.pedometer.MainActivity.R;

public class HeartRateActivity extends AppCompatActivity {

    long init, now, paused;
    private TextView mTextTime;
    private ToggleButton mToggleButton;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mTextTime = (TextView) findViewById(R.id.timeTextView);
        mToggleButton = (ToggleButton) findViewById(R.id.stopWatchToggleButton);
        handler = new Handler();

        //Timer
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mToggleButton.isChecked()) {
                    now = System.currentTimeMillis();
                    long millis = now - init;
                    long seconds = millis / 1000;
                    mTextTime.setText(String.format("%02d:%02d:%02d", seconds / 60, seconds % 60, millis % 100));
                    handler.postDelayed(this, 30);
                }
            }
        };

        mToggleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                init = System.currentTimeMillis();
                handler.post(runnable);
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
        init += System.currentTimeMillis() - paused;
    }

}
