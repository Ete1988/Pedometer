package com.mueller.mobileSports.general;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.mueller.mobileSports.pedometer.MainActivity.R;


public class StatisticsActivity extends AppCompatActivity {

    //TODO
    private TextView dayCount;
    private TextView weekCount;
    private SharedPreferences myData;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        dayCount = (TextView) findViewById(R.id.dayCountView);
        weekCount = (TextView) findViewById(R.id.weekCountView);
        myData = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        //TODO why -1??
        dayCount.setText(Integer.toString(myData.getInt("dayCount", 0)));
        weekCount.setText(Integer.toString(myData.getInt("weekCount", 0)));


    }
}
