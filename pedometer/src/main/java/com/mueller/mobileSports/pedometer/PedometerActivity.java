package com.mueller.mobileSports.pedometer;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.lylc.widget.circularprogressbar.CircularProgressBar;
import com.mueller.mobileSports.general.SettingsActivity;
import com.mueller.mobileSports.general.SharedValues;
import com.mueller.mobileSports.heartRate.HeartRateActivity;
import com.mueller.mobileSports.heartRate.HeartRateSensorService;
import com.mueller.mobileSports.heartRate.HeartRateSensorSimulationService;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.user.ProfileActivity;
import com.mueller.mobileSports.user.UserSessionManager;

import java.util.Locale;

/**
 * Created by Ete
 * <p>
 * Activity meant for the pedometer app mode
 */
public class PedometerActivity extends AppCompatActivity {


    boolean doubleBackToExitPressedOnce = false;
    private UserSessionManager userSessionManager;
    private CircularProgressBar cBar;
    private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (PedometerService.STEP_MESSAGE.equals(action)) {
                int steps = intent.getIntExtra("steps", 0);
                cBar.setProgress(steps);
                cBar.setTitle(Integer.toString(steps));

            } else if (PedometerService.VALUES_CHANGED.equals(action)) {
                //TODO format all
                mCadence.setText(String.format(Locale.getDefault(), "%.2f", intent.getDoubleExtra("cadenceValue", 0.0)));
                mDistance.setText(String.format(Locale.getDefault(), "%.2f", intent.getDoubleExtra("distanceValue", 0.0)));
                mSpeed.setText(String.format(Locale.getDefault(), "%.2f", intent.getDoubleExtra("speedValue", 0.0)));
                mEnergyExpenditure.setText(String.format(Locale.getDefault(), "%d", intent.getIntExtra("energyExpenditureSteps", 0)));

            }
        }
    };
    private TextView mDate, mCadence, mSpeed, mDistance, mEnergyExpenditure;
    private SharedValues sharedValues;

    private static IntentFilter updateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PedometerService.STEP_MESSAGE);
        intentFilter.addAction(PedometerService.VALUES_CHANGED);
        return intentFilter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        init();
        registerReceiver(mUpdateReceiver, updateIntentFilter());
        if (!isMyServiceRunning(PedometerService.class)) {
            Intent intent = new Intent(PedometerActivity.this, PedometerService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        userSessionManager.checkUserState();
        mapDataToView();
        registerReceiver(mUpdateReceiver, updateIntentFilter());
    }


    @Override
    protected void onPause() {
        tryToUnregisterReceiver(mUpdateReceiver);
        super.onPause();
    }

    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true);
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press Back again to leave", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 5000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.menu_logout:
                userSessionManager.uploadUserData(this, true, true, true);
                break;
            case R.id.menu_profile:
                Intent i2 = new Intent(this, ProfileActivity.class);
                startActivity(i2);
                break;
        }
        return true;
    }

    /**
     * Initializes most fields of activity
     */
    private void init() {
        userSessionManager = new UserSessionManager(this);
        sharedValues = SharedValues.getInstance(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Pedometer");
        mDate = (TextView) findViewById(R.id.PF_DateView);
        mCadence = (TextView) findViewById(R.id.PF_CadenceView);
        mSpeed = (TextView) findViewById(R.id.PF_SpeedView);
        mDistance = (TextView) findViewById(R.id.PF_DistanceView);
        mEnergyExpenditure = (TextView) findViewById(R.id.PF_EnergyExpenditure);
        cBar = (CircularProgressBar) findViewById(R.id.circularprogressbar3);
        cBar.setSubTitle("Steps");
    }

    /**
     * Maps userdata to widgets in view
     */
    private void mapDataToView() {
        mDate.setText(sharedValues.getString("sessionDay"));
        cBar.setTitle(Integer.toString(sharedValues.getInt("stepsOverDay")));
        cBar.setProgress(sharedValues.getInt("stepsOverDay"));
        cBar.setMax(UserSessionManager.getUserData().getStepGoal());
    }

    public void onClickPedometerActivity(View v) {
        if (v.getId() == R.id.PM_HeartRateBtn) {
            Intent i = new Intent(this, HeartRateActivity.class);
            startActivity(i);
        }
    }

    /**
     * Quick check if the given Service class is already running
     *
     * @param serviceClass to be checked service class
     * @return true iff given service class is already running
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void tryToUnregisterReceiver(BroadcastReceiver myReceiver) {
        try {
            unregisterReceiver(myReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        userSessionManager.uploadUserData(this, false, false, true);
        stopService(new Intent(this, PedometerService.class));
        stopService(new Intent(this, HeartRateSensorService.class));
        stopService(new Intent(this, HeartRateSensorSimulationService.class));
        super.onDestroy();
    }
}





