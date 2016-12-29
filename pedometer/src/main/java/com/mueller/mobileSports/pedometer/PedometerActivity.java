package com.mueller.mobileSports.pedometer;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lylc.widget.circularprogressbar.CircularProgressBar;
import com.mueller.mobileSports.general.SettingsActivity;
import com.mueller.mobileSports.general.SharedValues;
import com.mueller.mobileSports.general.TimeManager;
import com.mueller.mobileSports.heartRate.HeartRateActivity;
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
    MyReceiver myReceiver;
    private SessionManager sessionManager;
    private CircularProgressBar cBar;
    private TextView date;
    private SharedValues sharedValues;
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
        return intentFilter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        init();
        registerReceiver(myReceiver, updateIntentFilter());
        if (!isMyServiceRunning(PedometerService.class)) {
            Intent intent = new Intent(PedometerActivity.this, PedometerService.class);
            bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        }
    }

    private void init() {
        myReceiver = new MyReceiver();
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

    private void getData() {
        date.setText(sharedValues.getString("sessionDay"));
        cBar.setMax(sharedValues.getInt("stepGoal"));
        cBar.setTitle(Integer.toString(sharedValues.getInt("stepsOverDay")));
        cBar.setProgress(sharedValues.getInt("stepsOverDay"));
        cBar.setMax(sharedValues.getInt("stepGoal"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        registerReceiver(myReceiver, updateIntentFilter());
        sessionManager.isLoginValid();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "Destroyed!");
        if (sessionManager.isUserTokenAvailable() && sessionManager.checkIfUserDataAvailable()) {
            sessionManager.uploadUserData(this, false, false);
        }

        tryToUnregisterReceiver(myReceiver);
        if (isMyServiceRunning(PedometerService.class)) {
            System.out.println("Removed ALL");

            unbindService(mServiceConnection);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        tryToUnregisterReceiver(myReceiver);
        super.onPause();
    }

    public void onClickPedometer(View v) {
        if (v == null) {
            throw new NullPointerException(
                    "You are referring null object. "
                            + "Please check weather you had called super class method mappingWidgets() or not");
        } else if (v.getId() == R.id.PM_ProfileBtn) {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.PM_HeartRateBtn) {
            Intent i = new Intent(this, HeartRateActivity.class);
            startActivity(i);
        }
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
        }
        return true;
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

    private void tryToUnregisterReceiver(MyReceiver myReceiver) {
        try {
            unregisterReceiver(myReceiver);
        } catch (IllegalArgumentException e) {
            System.err.println(e);
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            System.out.println("Got message!");
            cBar.setProgress(sharedValues.getInt("stepsOverDay"));
            cBar.setTitle(Integer.toString(sharedValues.getInt("stepsOverDay")));
        }
    }
}





