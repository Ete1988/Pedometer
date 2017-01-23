package com.mueller.mobileSports.heartRate;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mueller.mobileSports.general.BluetoothScanActivity;
import com.mueller.mobileSports.general.GenericActivity;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.session.TrainingSessionActivity;
import com.mueller.mobileSports.user.UserData;
import com.mueller.mobileSports.user.UserSessionManager;
import com.mueller.mobileSports.utility.SharedValues;

import java.util.Locale;

/**
 * Created by Ete
 * <p>
 * Activity meant for the heart rate monitoring app mode
 */

public class HeartRateActivity extends GenericActivity {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_START_SERVICE = "BIND_SERVICE";
    public static final String EXTRAS_START_SIMULATION_SERVICE = "BIND_SIMULATION_SERVICE";
    private String mDeviceAddress, mDeviceName;
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
            } else if (HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_HEART_RATE_DETECTED.equals(action)) {
                displayData(intent.getStringExtra(HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_HEART_RATE_DETECTED));
            } else if (HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_CONNECTED.equals(action)) {
                updateConnectionState(R.string.connected);
            } else if (HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_DISCONNECTED.equals(action)) {
                updateConnectionState(R.string.disconnected);
            }
        }
    };
    private UserSessionManager userSessionManager;

    private static IntentFilter updateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_HEART_RATE_DETECTED);
        intentFilter.addAction(HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_CONNECTED);
        intentFilter.addAction(HeartRateSensorSimulationService.ACTION_HRM_SIMULATION_DISCONNECTED);
        intentFilter.addAction(HeartRateSensorService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(HeartRateSensorService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(HeartRateSensorService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(HeartRateSensorService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void setUpNavigation() {
        super.setUpNavigation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_layout);
        init();
        setUpNavigation();
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.getMenu().findItem(R.id.HeartRateBtn).setChecked(true);
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View childLayout = inflater.inflate(R.layout.heart_rate_view,
                (ViewGroup) findViewById(R.id.myHeartRateView));
        frameLayout.addView(childLayout);
        initializeViews();


    }

    @Override
    protected void onPause() {
        super.onPause();
        tryToUnregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userSessionManager.checkUserState();
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
    protected void init() {
        super.init();
    }

    /**
     * Initializes most fields of activity
     */
    private void initializeViews() {
        sharedValues = SharedValues.getInstance(this);
        userSessionManager = new UserSessionManager(this);
        mHeartRate = (TextView) findViewById(R.id.HR_heartRateView);
        mAverageHeartRate = (TextView) findViewById(R.id.HR_averageHeartRateView);
        mMaxHearRate = (TextView) findViewById(R.id.HR_maxHeartRateView);
        mMinHeartRate = (TextView) findViewById(R.id.HR_minHeartRateView);
        mConnectionState = (TextView) findViewById(R.id.HR_connectionStateView);
        mConnectedDeviceName = (TextView) findViewById(R.id.HR_connectedDeviceName);
    }

    /**
     * Method to start HRM service
     */
    private void startHRMService() {

        //True iff real sensor was choosen in BluetoothScanActivity
        if (startRealSensorService) {
            Intent intent = new Intent(HeartRateActivity.this, HeartRateSensorService.class);
            intent.putExtra(EXTRAS_DEVICE_NAME, mDeviceName);
            intent.putExtra(EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
            startService(intent);
        }
        //True iff simulation service was choosen in BluetoothScanActivity
        if (startSimulationSensorService) {
            Intent intent = new Intent(HeartRateActivity.this, HeartRateSensorSimulationService.class);
            startService(intent);
        }

    }

    //Thread to update the connection state to the connected service/device
    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    /**
     * Method to map data to widgets in view
     *
     * @param data received data
     */
    private void displayData(String data) {
        if (data != null) {
            mHeartRate.setText(data);
            mMaxHearRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("maxHeartRate")));
            mMinHeartRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("minHeartRate")));
            mHeartRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("currentHeartRate")));
            mAverageHeartRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("averageHeartRateOverDay")));
            mConnectedDeviceName.setText(sharedValues.getString("deviceName"));

        }
    }

    private void tryToUnregisterReceiver(BroadcastReceiver myReceiver) {
        try {
            unregisterReceiver(myReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
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

    /**
     * Quick check if training session can be started
     *
     * @return true iff all necessary data is available
     */
    private boolean checkIfSessionCanBeStarted() {
        UserData userData = UserSessionManager.getUserData();
        if (userData.getAge() == 0) {
            if (userData.getHeartRateMax() == 0) {
                Toast.makeText(this, "Please set either your age(Profile) or HRMax(Settings)!", Toast.LENGTH_LONG).show();
                return false;
            }

        } else if (userData.getHeartRateMax() == 0) {
            int heartRateMax;
            heartRateMax = (int) (208 - (0.7 * (sharedValues.getInt("age"))));
            userData.setHeartRateMax(heartRateMax);
            UserSessionManager.setUserData(userData);
        }

        if (userData.getWeight() == 0) {
            Toast.makeText(this, "Please set your weight(Profile)!", Toast.LENGTH_LONG).show();
            return false;
        }

        if (userData.getHeight() == 0) {
            Toast.makeText(this, "Please set your height(Profile)!", Toast.LENGTH_LONG).show();
            return false;
        }

        return isMyServiceRunning(HeartRateSensorService.class) || isMyServiceRunning(HeartRateSensorSimulationService.class);

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


}

