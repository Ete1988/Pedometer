package com.mueller.mobileSports.pedometer;


import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lylc.widget.circularprogressbar.CircularProgressBar;
import com.mueller.mobileSports.account.SessionManager;
import com.mueller.mobileSports.general.SettingsActivity;
import com.mueller.mobileSports.general.StatisticsActivity;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.user.UserProfileData;


public class PedometerActivity extends AppCompatActivity implements SensorEventListener {

    // Session Manager Class
    SessionManager session;
    private CircularProgressBar cBar;
    private SensorManager sensorManager;
    private TextView date;
    private int stepsOverWeek, stepsOverDay;
    private sharedValues values;
    private UserProfileData myData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        values = sharedValues.getInstance(this);
        myData = new UserProfileData();
        date = (TextView) findViewById(R.id.date);
        cBar = (CircularProgressBar) findViewById(R.id.circularprogressbar3);
        cBar.setSubTitle("Steps");
        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        values.checkTime();
        getData();
    }



    private void getData() {
        stepsOverDay = values.getInt("dayCount");
        stepsOverWeek = myData.getWeeklyStepCount();
        //stepsOverWeek = values.getInt("weekCount");
        date.setText(values.getString("checkDate"));
        cBar.setMax(values.getInt("stepGoal"));
    }

    private void updateData() {

        myData.setWeeklyStepCount(stepsOverWeek);
        values.saveInt("dayCount", stepsOverDay);
        //values.saveInt("weekCount", stepsOverWeek);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.statsBtn) {
            updateData();
            Intent intent = new Intent(PedometerActivity.this, StatisticsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Only one button for now.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(PedometerActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.action_logout:
                session.logoutUser();
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateData();


    }

    @Override
    protected void onPause() {
        super.onPause();
        updateData();
        // if you unregister the last listener, the hardware will stop detecting step events
        //sensorManager.unregisterListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        stepsOverDay++;
        stepsOverWeek++;
        cBar.setProgress(stepsOverDay);
        cBar.setTitle(Integer.toString(stepsOverDay));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}





