package com.mueller.mobileSports.general;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.SharedValues;
import com.mueller.mobileSports.user.UserData;

import java.util.Locale;

/**
 * Created by Ete on 8/10/2016.
 * <p>
 * Activity to display app relevant statistics.
 */

public class StatisticsActivity extends AppCompatActivity {

    private SharedValues values;
    private TextView dayCount, weekCount;
    private UserData myData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
         /*   Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
      *  setSupportActionBar(myToolbar); */
        myData = new UserData();
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
