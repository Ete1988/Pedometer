package com.mueller.mobileSports.heartRate;

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
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.Objects;

/**
 * Created by Ete
 * <p>
 * Activity meant for the heart rate monitoring app mode
 */

public class HeartRateActivity extends AppCompatActivity {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_BIND_SERVICE = "BIND_SERVICE";
    private final static String TAG = HeartRateActivity.class.getSimpleName();
    MyReceiver myReceiver;
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean bindSensorService = false;
    private boolean bindSimulationHeartRate;
    private Runnable mTimerRunnable;
    private boolean mConnected;
    private long paused;
    private SharedValues sharedValues;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (HeartRateSensorService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
            } else if (HeartRateSensorService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
            } else if (HeartRateSensorService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(HeartRateSensorService.EXTRA_DATA));
            } else if (HeartRateSensorSimulationService.HRM_SIMULATION_MESSAGE.equals(action)) {

            }
        }
    };
    private ImageView iv_start, iv_restart;
    private TextView mTextTime, mHeartRate, mAverageHeartRate, mMaxHearRate, mMinHeartRate, mConnectionState;
    private int time_seconds, time_minutes, time_milliseconds;
    private long start_time, timeInMilliseconds, time_update, time_swapBuff;
    private Handler mHandler;
    private HeartRateSensorService heartRateSensorService;
    private HeartRateSensorSimulationService heartRateSensorSimulationService;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            heartRateSensorService = ((HeartRateSensorService.LocalBinder) service).getService();
            if (!heartRateSensorService.initialize()) {
                Log.e(TAG, "Unable to initialize HeartRateSensorService");
                finish();
            }
            heartRateSensorService.connect(mDeviceAddress);
           /* if(bindSimulationHeartRate){
                heartRateSensorSimulationService = ((HeartRateSensorSimulationService.LocalBinder) service).getService();
                if (!heartRateSensorSimulationService.initialize()) {
                    Log.e(TAG, "Unable to initialize HeartRateSimulationService");
                    finish();
                }
            } else {
                System.out.println("Here !!!!!!!!!!!!!!!!!!!!!!!!Fuuuunot");
                System.out.println(2);
                heartRateSensorService = ((HeartRateSensorService.LocalBinder) service).getService();
                if(!heartRateSensorService.initialize()) {
                    Log.e(TAG, "Unable to initialize HeartRateSensorService");
                    finish();
                }
                heartRateSensorService.connect(mDeviceAddress);
            }*/
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            heartRateSensorSimulationService = null;
            heartRateSensorService = null;
        }
    };
    private SessionManager sessionManager;

    private static IntentFilter updateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HeartRateSensorSimulationService.HRM_SIMULATION_MESSAGE);
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
        iv_start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startHRM();
                bindSimulationHeartRate = true;
                /*
                if (btnState == 1) {
                    iv_start.setImageResource(R.mipmap.ic_stop);
                    start_time = System.currentTimeMillis();
                    mHandler.postDelayed(mTimerRunnable, 10L);
                    btnState = 0;
                } else {
                    iv_start.setImageResource(R.mipmap.ic_start);
                    time_swapBuff += timeInMilliseconds;
                    mHandler.removeCallbacks(mTimerRunnable);
                    btnState = 1;
                }
                */
            }
        });

        iv_restart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(HeartRateActivity.this, BluetoothScanActivity.class);
                startActivity(i);
                bindSimulationHeartRate = false;
                // startHRM();
                /*
                start_time = 0L;
                timeInMilliseconds = 0L;
                time_swapBuff = 0L;
                btnState = 1;
                time_seconds = 0;
                time_milliseconds = 0;
                time_minutes = 0;
                mHandler.removeCallbacks(mTimerRunnable);
                mTextTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", 0, 0, 0));
            */

            }
        });
    }

    private void init() {
        myReceiver = new MyReceiver();
        sharedValues = SharedValues.getInstance(this);
        mConnectionState = (TextView) findViewById(R.id.txtConnectionStatus);
        sessionManager = new SessionManager(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mTextTime = (TextView) findViewById(R.id.timeTextView);
        mHeartRate = (TextView) findViewById(R.id.txtHeartRateView);
        mAverageHeartRate = (TextView) findViewById(R.id.txtAvrHRView);
        mMaxHearRate = (TextView) findViewById(R.id.txtMaxHRView);
        mMinHeartRate = (TextView) findViewById(R.id.txtMinHRView);
        iv_start = (ImageView) findViewById(R.id.iv_start_stop);
        iv_restart = (ImageView) findViewById(R.id.iv_reset);
        mHandler = new Handler();

        mTimerRunnable = new Runnable() {

            @Override
            public void run() {

                timeInMilliseconds = System.currentTimeMillis() - start_time;
                time_update = time_swapBuff + timeInMilliseconds;
                time_seconds = (int) (time_update / 1000);
                time_minutes = time_seconds / 60;
                time_milliseconds = (int) (time_update % 1000);
                mTextTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", time_minutes, time_seconds % 60, time_milliseconds % 100));
                mHandler.postDelayed(this, 30);
            }
        };
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
        }
    }

    private void startHRM() {

        Intent intent = new Intent(HeartRateActivity.this, HeartRateSensorSimulationService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void startRealHRM() {
        Intent intent = new Intent(HeartRateActivity.this, HeartRateSensorService.class);
        bindSimulationHeartRate = false;
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

    }

    public void calculateTRIMP() {
        double duration = time_minutes;
        double x, y, b;
        double trimp;

        x = ((sharedValues.getInt("averageHeartRate") - sharedValues.getInt("minHeartRate") /
                (sharedValues.getInt("maxHeartRate") - sharedValues.getInt("minHeartRate"))));

        if (Objects.equals(sharedValues.getString("gender"), "Female")) {
            b = 1.67;
        } else {
            b = 1.92;
        }

        y = Math.exp(b * x);

        trimp = duration * x * y;
    }

    public void onClickHeartRateMeter(View v) {
        if (v == null) {
            throw new NullPointerException(
                    "You are referring null object. "
                            + "Please check weather you had called super class method mappingWidgets() or not");
        } else if (v.getId() == R.id.HRM_ProfileBtn) {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.HRM_PedometerBtn) {
            Intent i = new Intent(this, PedometerActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onDestroy() {
        tryToUnregisterReceiver(myReceiver);
        tryToUnregisterReceiver(mGattUpdateReceiver);
        if (!(heartRateSensorService == null) || !(heartRateSensorSimulationService == null)) {
            unbindService(mServiceConnection);
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tryToUnregisterReceiver(myReceiver);
        tryToUnregisterReceiver(mGattUpdateReceiver);
        paused = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myReceiver, updateIntentFilter());
        registerReceiver(mGattUpdateReceiver, updateIntentFilter());
        final Intent receiveIntent = getIntent();
        mDeviceName = receiveIntent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = receiveIntent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        bindSensorService = receiveIntent.getBooleanExtra(EXTRAS_BIND_SERVICE, false);
        if (bindSensorService) {
            startRealHRM();
        }

        sessionManager.isLoginValid();
        start_time += System.currentTimeMillis() - paused;
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

    private void tryToUnregisterReceiver(BroadcastReceiver myReceiver) {
        try {
            unregisterReceiver(myReceiver);
        } catch (IllegalArgumentException e) {
            System.err.println(e);
        }
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            mMaxHearRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("maxHeartRate")));
            mMinHeartRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("minHeartRate")));
            mHeartRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("currentHeartRate")));
            mAverageHeartRate.setText(String.format(Locale.getDefault(), "%03d", sharedValues.getInt("averageHeartRate")));

        }
    }

}

