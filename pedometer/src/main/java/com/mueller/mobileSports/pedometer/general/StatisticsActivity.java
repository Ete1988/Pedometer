package com.mueller.mobileSports.pedometer.general;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.sharedValues;

/**
 * Created by Ete on 8/10/2016.
 */

public class StatisticsActivity extends AppCompatActivity {

    private sharedValues values;
    private TextView dayCount,weekCount;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        values = sharedValues.getInstance(this);
        dayCount = (TextView) findViewById(R.id.dayCountView);
        weekCount = (TextView) findViewById(R.id.weekCountView);
        getData();
    }

    private void getData() {

        dayCount.setText(Integer.toString(values.getInt("dayCount")));
        weekCount.setText(Integer.toString(values.getInt("weekCount")));


    }
}
