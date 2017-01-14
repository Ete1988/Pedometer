package com.mueller.mobileSports.pedometer;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lylc.widget.circularprogressbar.CircularProgressBar;
import com.mueller.mobileSports.general.GenericActivity;
import com.mueller.mobileSports.general.SharedValues;
import com.mueller.mobileSports.heartRate.HeartRateSensorService;
import com.mueller.mobileSports.heartRate.HeartRateSensorSimulationService;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.user.UserSessionManager;

import java.util.Locale;

public class PedometerActivity extends GenericActivity {

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
        setContentView(R.layout.generic_layout);
        init();
        setUpNavigation();
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.getMenu().findItem(R.id.PedometerBtn).setChecked(true);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View childLayout = inflater.inflate(R.layout.pedometer_view,
                (ViewGroup) findViewById(R.id.myPedometerView));
        frameLayout.addView(childLayout);
        initializeViews();

        registerReceiver(mUpdateReceiver, updateIntentFilter());
        if (!isMyServiceRunning(PedometerService.class)) {
            Intent intent = new Intent(PedometerActivity.this, PedometerService.class);
            startService(intent);

        }

    }

    @Override
    protected void setUpNavigation() {
        super.setUpNavigation();
    }

    @Override
    protected void init() {
        super.init();
    }

    /**
     * Initializes most fields of activity
     */
    private void initializeViews() {
        userSessionManager = new UserSessionManager(this);
        sharedValues = SharedValues.getInstance(this);
        mDate = (TextView) findViewById(R.id.PF_DateView);
        mCadence = (TextView) findViewById(R.id.PF_CadenceView);
        mSpeed = (TextView) findViewById(R.id.PF_SpeedView);
        mDistance = (TextView) findViewById(R.id.PF_DistanceView);
        mEnergyExpenditure = (TextView) findViewById(R.id.PF_EnergyExpenditure);
        cBar = (CircularProgressBar) findViewById(R.id.circularprogressbar3);
        cBar.setSubTitle("Steps");
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
        userSessionManager.uploadUserData(this, false, false, false);
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

    /**
     * Maps userdata to widgets in view
     */
    private void mapDataToView() {
        mDate.setText(sharedValues.getString("sessionDay"));
        cBar.setTitle(Integer.toString(sharedValues.getInt("stepsOverDay")));
        cBar.setProgress(sharedValues.getInt("stepsOverDay"));
        cBar.setMax(UserSessionManager.getUserData().getStepGoal());
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
        stopService(new Intent(this, PedometerService.class));
        stopService(new Intent(this, HeartRateSensorService.class));
        stopService(new Intent(this, HeartRateSensorSimulationService.class));
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.logOutBtn) {
            UserSessionManager userSessionManager = new UserSessionManager(this);
            userSessionManager.uploadUserData(this, true, true, true);
        }
    }
}


