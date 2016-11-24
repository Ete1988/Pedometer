/*
 * Copyright 2013 Leon Cheng
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mueller.mobileSports.pedometer;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.lylc.widget.circularprogressbar.CircularProgressBar;
import com.mueller.mobileSports.general.LoginActivity;
import com.mueller.mobileSports.general.SettingsActivity;
import com.mueller.mobileSports.general.StatisticsActivity;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.pedometerUtility.pedometerData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PedometerActivity extends AppCompatActivity implements SensorEventListener {

    private CircularProgressBar cBar;
    private SensorManager sensorManager;
    private SharedPreferences myData;
    private SharedPreferences.Editor editor;
    private int stepsOverWeek;
    private int stepsOverDay;
    private pedometerData data = new pedometerData();
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        cBar = (CircularProgressBar) findViewById(R.id.circularprogressbar3);
        cBar.setSubTitle("Steps today");
        cBar.setMax(7000);
        myData = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = myData.edit();
        updateAll();
    }

    private void updateAll() {
        updateDayCount();
        updateWeekCount();
    }

    private void updateDayCount() {
        try {
            boolean check;
            check = checkIfNewDay();
            isNewDay(check);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void isNewDay(boolean checkDay) {
        if (checkDay) {
            stepsOverDay = 0;
        } else {
            stepsOverDay = myData.getInt("dayCount", 0);
        }
    }

    private boolean checkIfNewDay() throws ParseException {

        TextView date = (TextView) findViewById(R.id.date);
        Date todayDate = new Date();
        SimpleDateFormat currDate = new SimpleDateFormat("EE dd MMM yyyy");
        String currentDate = currDate.format(todayDate);
        date.setText(currentDate);

        if (!(myData.contains("checkDate"))) {
            editor.putString("checkDate", currentDate);
            return true;
        } else {

            Date oldDate = currDate.parse((myData.getString("checkDate", null)));
            Date now = currDate.parse(currentDate);

            if (oldDate.before(now)) {
                editor.putInt("dayCount", 0);
                editor.putString("checkDate", currentDate);
                return true;
            } else if (oldDate.equals(now)) {
                editor.putInt("dayCount", stepsOverDay);
                return false;
            } else {
                //should never be reached
                //TODO test this out
                return true;
            }
        }
    }

    //TODO test this.
    private void updateWeekCount() {

        boolean checkWeek = checkIfNewWeek();
        if (checkWeek) {
            stepsOverWeek = 0;
        } else {
            stepsOverWeek = myData.getInt("weekCount", 0);
        }
    }

    private boolean checkIfNewWeek() {

        Calendar c = Calendar.getInstance();
        if (!myData.contains("weekOfYear")) {
            editor.putInt("weekOfYear", c.get(Calendar.WEEK_OF_YEAR));
            return true;
        } else {

            if ((c.get(Calendar.WEEK_OF_YEAR)) > (myData.getInt("weekOfYear", 0))) {
                editor.putInt("weekCount", 0);
                editor.putInt("weekOfYear", c.get(Calendar.WEEK_OF_YEAR));
                return true;
            } else if ((c.get(Calendar.WEEK_OF_YEAR)) == (myData.getInt("weekOfYear", 0))) {
                editor.putInt("weekCount", stepsOverWeek);
                return false;
            } else {
                //should never be reached
                //TODO test this out
                return false;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.statsBtn) {
            updateAll();
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
                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void aVoid) {
                        Toast.makeText(getBaseContext(), "You logged out!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PedometerActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(getBaseContext(), "Log Out failed!", Toast.LENGTH_LONG).show();
                    }
                });

            default:
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAll();
        editor.apply();
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
        updateAll();
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // if you unregister the last listener, the hardware will stop detecting step events
        //sensorManager.unregisterListener(this);
        editor.apply();
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





