package com.mueller.mobileSports.heartRate;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.mueller.mobileSports.utility.SharedValues;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Ete on 15/12/2016.
 * <p>
 * Class to simulate an connected BLE HeartRate device
 */

public class HeartRateSensorSimulationService extends Service {

    public final static String ACTION_HRM_SIMULATION_HEART_RATE_DETECTED = "com.mueller.mobileSPorts.HeartRateSimulation.STEP_VALUE";
    public final static String ACTION_HRM_SIMULATION_CONNECTED =
            "com.mueller.mobileSPorts.HeartRateSimulation.CONNECTED";
    public final static String ACTION_HRM_SIMULATION_DISCONNECTED =
            "com.mueller.mobileSPorts.HeartRateSimulation.DISCONNECTED";

    private final static String TAG = HeartRateSensorSimulationService.class.getSimpleName();
    private final static int SIZE_OF_SIMULATION_DATA = 541;
    private final static int TIME_DELAY = 3000;
    private int[] arraySimulationData;
    private ArrayList<Integer> array_HeartRate;
    private int updateCounter;
    private Handler mHandler;
    private HeartRateMonitorUtility heartRateMonitorUtility;
    private SharedValues sharedValues;
    private Runnable mHeartRateSimulation = new Runnable() {
        @Override
        public void run() {
            broadcastUpdate(ACTION_HRM_SIMULATION_CONNECTED);

            //Let the simulation run forever
            if (updateCounter == arraySimulationData.length) {
                updateCounter = 0;
            }

            array_HeartRate.add(arraySimulationData[updateCounter]);
            heartRateMonitorUtility.doCalculations(arraySimulationData[updateCounter]);
            heartRateMonitorUtility.calculateAverageHeartRateOverDay(array_HeartRate);
            sharedValues.saveInt("currentHeartRate", arraySimulationData[updateCounter]);

            //Send Data to activity
            broadcastUpdate(ACTION_HRM_SIMULATION_HEART_RATE_DETECTED, arraySimulationData[updateCounter++]);
            mHandler.postDelayed(this, TIME_DELAY);

        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 int heartRate) {
        final Intent intent = new Intent(action);
        intent.putExtra(ACTION_HRM_SIMULATION_HEART_RATE_DETECTED, String.valueOf(heartRate));
        sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        sharedValues = SharedValues.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initialize();
        return Service.START_STICKY;
    }

    public void initialize() {
        if (arraySimulationData == null) {
            arraySimulationData = new int[SIZE_OF_SIMULATION_DATA];
            tryToReadSimulationData();
        }

        if (array_HeartRate == null) {
            array_HeartRate = new ArrayList<>();
        }
        if (heartRateMonitorUtility == null) {
            heartRateMonitorUtility = new HeartRateMonitorUtility(this);
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        broadcastUpdate(ACTION_HRM_SIMULATION_CONNECTED);
        mHandler.postDelayed(mHeartRateSimulation, 0);
        Log.i(TAG, "HeartRateSensorSimulation started.");
    }

    @Override
    public void onDestroy() {
        broadcastUpdate(ACTION_HRM_SIMULATION_DISCONNECTED);
        mHandler.removeCallbacks(mHeartRateSimulation);
        Log.i(TAG, "HeartRateSensorSimulation destroyed.");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Try to read the simulation data.
     * Implemented for better readability of code.
     */
    private void tryToReadSimulationData() {
        try {
            readSimulationFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the simulationdata.csv
     *
     * @throws IOException iff simulationData.csv is not available
     */
    private void readSimulationFile() throws IOException {
        int i = 0;
        AssetManager assetManager = getAssets();

        InputStream csvStream = assetManager.open("simulationData.csv");
        InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
        CSVReader csvReader = new CSVReader(csvStreamReader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 1);
        String[] line;
        csvReader.readNext();
        while ((line = csvReader.readNext()) != null) {
            arraySimulationData[i] = Integer.parseInt(line[1]);
            i++;
        }
    }

}
