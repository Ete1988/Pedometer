package com.mueller.mobileSports.general;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.SharedValues;
import com.mueller.mobileSports.user.UserProfileData;

import java.util.Locale;

/**
 * Created by Ete on 8/10/2016.
 */

public class StatisticsActivity extends AppCompatActivity {

    private SharedValues values;
    private TextView dayCount, weekCount;
    private UserProfileData myData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myData = new UserProfileData();
        values = SharedValues.getInstance(this);
        dayCount = (TextView) findViewById(R.id.dayCountView);
        weekCount = (TextView) findViewById(R.id.weekCountView);
        getData();
    }

    private void getData() {

        dayCount.setText(String.format(Locale.getDefault(), "%05d", values.getInt("dayCount")));
        weekCount.setText(String.format(Locale.getDefault(), "%05d", myData.getWeeklyStepCount()));


    }
}
