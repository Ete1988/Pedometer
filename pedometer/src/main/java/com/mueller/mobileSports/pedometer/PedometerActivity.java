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

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.lylc.widget.circularprogressbar.CircularProgressBar;
import com.mueller.mobileSports.general.AlertDialogManager;
import com.mueller.mobileSports.account.LoginActivity;
import com.mueller.mobileSports.general.SettingsActivity;
import com.mueller.mobileSports.general.StatisticsActivity;
import com.mueller.mobileSports.account.SessionManager;
import com.mueller.mobileSports.pedometer.MainActivity.R;

import java.util.HashMap;


public class PedometerActivity extends AppCompatActivity implements SensorEventListener {

    private CircularProgressBar cBar;
    private SensorManager sensorManager;
    private TextView date;
    private int stepsOverWeek, stepsOverDay;
    private sharedValues values;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        values = sharedValues.getInstance(this);
        date = (TextView) findViewById(R.id.date);
        cBar = (CircularProgressBar) findViewById(R.id.circularprogressbar3);
        cBar.setSubTitle("Steps");
        values.checkTime();
        getData();

        // Session class instance
        session = new SessionManager(getApplicationContext());

        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();


        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name
        String name = user.get(SessionManager.KEY_NAME);

        // email
        String email = user.get(SessionManager.KEY_EMAIL);

        // displaying user data
        //cBar.setTitle("Name: <b>" + name + "</b>");
        //cBar.setSubTitle("Email: <b>" + email + "</b>");

    }


    private void getData() {
        stepsOverDay = values.getInt("dayCount");
        stepsOverWeek = values.getInt("weekCount");
        date.setText(values.getString("checkDate"));
        cBar.setMax(values.getInt("stepGoal"));
    }

    private void updateData() {
        values.saveInt("dayCount", stepsOverDay);
        values.saveInt("weekCount", stepsOverWeek);
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





