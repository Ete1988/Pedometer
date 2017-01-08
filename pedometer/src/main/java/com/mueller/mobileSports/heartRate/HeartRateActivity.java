package com.mueller.mobileSports.heartRate;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mueller.mobileSports.general.BluetoothScanActivity;
import com.mueller.mobileSports.general.SettingsActivity;
import com.mueller.mobileSports.general.SharedValues;
import com.mueller.mobileSports.heartRate.heartRateServices.HeartRateSensorService;
import com.mueller.mobileSports.heartRate.heartRateServices.HeartRateSensorSimulationService;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.PedometerActivity;
import com.mueller.mobileSports.user.ProfileActivity;
import com.mueller.mobileSports.user.SessionManager;

import java.util.Locale;

/**
 * Created by Ete
 * <p>
 * Activity meant for the heart rate monitoring app mode
 */

public class HeartRateActivity extends AppCompatActivity {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_START_SERVICE = "BIND_SERVICE";
    public static final String EXTRAS_START_SIMULATION_SERVICE = "BIND_SIMULATION_SERVICE";
    private final static String TAG = HeartRateActivity.class.getSimpleName();
    private String mDeviceAddress;
    private String mDeviceName;

    private boolean startSimulationSensorService = false;
    private boolean startRealSensorService = false;
    private SharedValues sharedValues;
    private TextView mHeartRate, mAverageHeartRate, mMaxHearRate, mMinHeartRate, mConnectionState, mConnectedDeviceName;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (HeartRateSensorService.ACTION_GATT_CONNECTED.equals(action)) {
                updateConnectionState(R.string.connected);
            } else if (HeartRateSensorService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnectedDeviceName.setText(R.string.emptyString);
                updateConnectionState(R.string.disconnected);
            } else if (HeartRateSensorService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(HeartRateSensorService.EXTRA_DATA));
            } else if (HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_STEP_DETECTED.equals(action)) {
                displayData(intent.getStringExtra(HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_STEP_DETECTED));
            } else if (HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_CONNECTED.equals(action)) {
                updateConnectionState(R.string.connected);
            } else if (HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_DISCONNECTED.equals(action)) {
                updateConnectionState(R.string.disconnected);
            }
        }
    };
    private SessionManager sessionManager;

    private static IntentFilter updateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_STEP_DETECTED);
        intentFilter.addAction(HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_CONNECTED);
        intentFilter.addAction(HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_DISCONNECTED);
        intentFilter.addAction(HeartRateSensorService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(HeartRateSensorService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(HeartRateSensorService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(HeartRateSensorService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tryToUnregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionManager.checkUserState();
        registerReceiver(mGattUpdateReceiver, updateIntentFilter());
        final Intent receiveIntent = getIntent();
        if (!isMyServiceRunning(HeartRateSensorService.class) && !isMyServiceRunning(HeartRateSensorSimulationService.class)) {
            mDeviceAddress = receiveIntent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
            mDeviceName = receiveIntent.getStringExtra(EXTRAS_DEVICE_NAME);
            sharedValues.saveString("deviceName", mDeviceName);
            startRealSensorService = receiveIntent.getBooleanExtra(EXTRAS_START_SERVICE, false);
            startSimulationSensorService = receiveIntent.getBooleanExtra(EXTRAS_START_SIMULATION_SERVICE, false);
            startHRMService();
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
            case R.id.menu_profile:
                Intent i2 = new Intent(this, ProfileActivity.class);
                startActivity(i2);
                break;
        }
        return true;
    }

    private void init() {
        sharedValues = SharedValues.getInstance(this);

        sessionManager = new SessionManager(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mHeartRate = (TextView) findViewById(R.id.HR_heartRateView);
        mAverageHeartRate = (TextView) findViewById(R.id.HR_averageHeartRateView);
        mMaxHearRate = (TextView) findViewById(R.id.HR_maxHeartRateView);
        mMinHeartRate = (TextView) findViewById(R.id.HR_minHeartRateView);
        mConnectionState = (TextView) findViewById(R.id.HR_connectionStateView);
        mConnectedDeviceName = (TextView) findViewById(R.id.HR_connectedDeviceName);
    }

    private void startHRMService() {

        if (startRealSensorService) {
            Intent intent = new Intent(HeartRateActivity.this, HeartRateSensorService.class);
            intent.putExtra(EXTRAS_DEVICE_NAME, mDeviceName);
            intent.putExtra(EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
            startService(intent);
        }
        if (startSimulationSensorService) {
            Intent intent = new Intent(HeartRateActivity.this, HeartRateSensorSimulationService.class);
            startService(intent);
        }

    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mHeartRate.setText(data);
            mMaxHearRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("maxHeartRate")));
            mMinHeartRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("minHeartRate")));
            mHeartRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("currentHeartRate")));
            mAverageHeartRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("averageHeartRate")));
            mConnectedDeviceName.setText(sharedValues.getString("deviceName"));

        }
    }

    public void onClickHeartRateActivity(View v) {
        Intent i;
        if (v.getId() == R.id.HR_searchDeviceBtn) {
            i = new Intent(this, BluetoothScanActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.HR_startSessionBtn) {
            if (checkIfSessionCanBeStarted()) {
                i = new Intent(this, TrainingSessionActivity.class);
                startActivity(i);
            } else {
                Toast.makeText(this, "No Heart Rate Sensor connected!", Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.HRM_PedometerBtn) {
            i = new Intent(this, PedometerActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.HR_disconnectDeviceBtn) {
            if (isMyServiceRunning(HeartRateSensorService.class)) {
                stopService(new Intent(this, HeartRateSensorService.class));
                sharedValues.removeEntry("deviceName");
            } else if (isMyServiceRunning(HeartRateSensorSimulationService.class)) {
                stopService(new Intent(this, HeartRateSensorSimulationService.class));
                sharedValues.removeEntry("deviceName");
            } else {
                Toast.makeText(this, "No Heart Rate Sensor connected!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void tryToUnregisterReceiver(BroadcastReceiver myReceiver) {
        try {
            unregisterReceiver(myReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
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

    /**
     * Small check if training session can be started
     *
     * @return true iff all necessary data is available
     */
    private boolean checkIfSessionCanBeStarted() {

        if (sharedValues.getInt("age") == 0) {
            if (sharedValues.getInt("heartRateMax") == 0) {
                Toast.makeText(this, "Please set either your age(Profile) or HRmax(Settings)!", Toast.LENGTH_LONG).show();
                return false;
            }

        } else if (sharedValues.getInt("heartRateMax") == 0) {
            int heartRateMax;
            heartRateMax = (int) (208 - (0.7 * (sharedValues.getInt("age"))));
            sharedValues.saveInt("heartRateMax", heartRateMax);
        }

        if (sharedValues.getInt("weight") == 0) {
            Toast.makeText(this, "Please set your weight(Profile)!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (sharedValues.getInt("height") == 0) {
            Toast.makeText(this, "Please set your height(Profile)!", Toast.LENGTH_LONG).show();
            return false;
        }

        return isMyServiceRunning(HeartRateSensorService.class) || isMyServiceRunning(HeartRateSensorSimulationService.class);

    }
}

