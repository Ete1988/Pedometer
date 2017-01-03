package com.mueller.mobileSports.pedometer;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lylc.widget.circularprogressbar.CircularProgressBar;
import com.mueller.mobileSports.general.SettingsActivity;
import com.mueller.mobileSports.general.SharedValues;
import com.mueller.mobileSports.general.TimeManager;
import com.mueller.mobileSports.heartRate.HeartRateActivity;
import com.mueller.mobileSports.heartRate.heartRateServices.HeartRateSensorService;
import com.mueller.mobileSports.heartRate.heartRateServices.HeartRateSensorSimulationService;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.pedometerService.PedometerService;
import com.mueller.mobileSports.user.ProfileActivity;
import com.mueller.mobileSports.user.SessionManager;

/**
 * Created by Ete
 * <p>
 * Activity meant for the pedometer app mode
 */
public class PedometerActivity extends AppCompatActivity {

    private final static String TAG = PedometerActivity.class.getSimpleName();
    boolean doubleBackToExitPressedOnce = false;
    private SessionManager sessionManager;
    private CircularProgressBar cBar;
    private TextView date;
    private SharedValues sharedValues;
    private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (PedometerService.STEP_MESSAGE.equals(action)) {
                cBar.setProgress(sharedValues.getInt("stepsOverDay"));
                cBar.setTitle(Integer.toString(sharedValues.getInt("stepsOverDay")));
            } else if (PedometerService.CADENCE_MESSAGE.equals(action)) {
                calculateAndSetCadence();
            }
        }
    };
    private PedometerService pedometerService;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            pedometerService = ((PedometerService.LocalBinder) service).getService();
            if (!pedometerService.initialize()) {
                Log.e(TAG, "Unable to initialize StepCounter");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            pedometerService.stopSelf();
            pedometerService = null;
        }
    };

    private static IntentFilter updateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PedometerService.STEP_MESSAGE);
        intentFilter.addAction(PedometerService.CADENCE_MESSAGE);
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
        sessionManager.checkUserState();
        mapDataToView();
        registerReceiver(mUpdateReceiver, updateIntentFilter());
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "Paused");
        sessionManager.uploadUserData(this, false, false);
        tryToUnregisterReceiver(mUpdateReceiver);
        super.onPause();
    }

    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true);
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press Back again to minimize the App", Toast.LENGTH_LONG).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
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
                sessionManager.uploadUserData(this, true, true);
                break;
            case R.id.menu_profile:
                Intent i2 = new Intent(this, ProfileActivity.class);
                startActivity(i2);
                break;
        }
        return true;
    }

    private void init() {
        sessionManager = new SessionManager(this);
        sharedValues = SharedValues.getInstance(this);
        TimeManager timeManager = new TimeManager(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        date = (TextView) findViewById(R.id.date);
        cBar = (CircularProgressBar) findViewById(R.id.circularprogressbar3);
        cBar.setSubTitle("Steps");
        timeManager.checkTime();
    }

    private void mapDataToView() {
        date.setText(sharedValues.getString("sessionDay"));
        cBar.setTitle(Integer.toString(sharedValues.getInt("stepsOverDay")));
        cBar.setProgress(sharedValues.getInt("stepsOverDay"));
        cBar.setMax(sharedValues.getInt("stepGoal"));
    }

    public void onClickPedometerActivity(View v) {
        if (v == null) {
            throw new NullPointerException(
                    "You are referring null object. "
                            + "Please check weather you had called super class method mappingWidgets() or not");
        } else if (v.getId() == R.id.PM_HeartRateBtn) {
            Intent i = new Intent(this, HeartRateActivity.class);
            startActivity(i);
        }
    }

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

    private void calculateAndSetCadence() {
        int cadence = sharedValues.getInt("cadence");
        cadence = cadence * 12;
        System.out.println("Cadence: " + cadence);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PedometerService.class));
        stopService(new Intent(this, HeartRateSensorService.class));
        stopService(new Intent(this, HeartRateSensorSimulationService.class));
        super.onDestroy();
    }
}





