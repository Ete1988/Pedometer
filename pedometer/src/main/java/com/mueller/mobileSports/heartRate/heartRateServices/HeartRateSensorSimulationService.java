package com.mueller.mobileSports.heartRate.heartRateServices;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.mueller.mobileSports.general.SharedValues;
import com.mueller.mobileSports.heartRate.hR_Utility.HeartRateMonitorUtility;
import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Ete on 15/12/2016.
 */

public class HeartRateSensorSimulationService extends Service {

    public final static String HRM_SIMULATION_MESSAGE = "com.mueller.mobileSPorts.pedometer.heartRate.hR_Monitor.HeartRateSimulationService.STEP_VALUE";
    private final static String TAG = HeartRateSensorSimulationService.class.getSimpleName();
    private final static int SIZE_OF_SIMULATION_DATA = 541;
    private final static int TIME_DELAY = 3000;
    private final IBinder mBinder = new LocalBinder();
    private int[] arraySimulationData, arrayAverageHeartRate;
    private int updateCounter, averageHeartRateCalculationCounter;
    private Handler mHandler;
    private HeartRateMonitorUtility heartRateMonitorUtility;
    private SharedValues sharedValues;
    private Runnable mHeartRateSimulation = new Runnable() {
        @Override
        public void run() {
            if (updateCounter == arraySimulationData.length) {
                updateCounter = 0;
            }
            arrayAverageHeartRate[averageHeartRateCalculationCounter++] = arraySimulationData[updateCounter];

            if (averageHeartRateCalculationCounter == 3) {
                heartRateMonitorUtility.calculateAverageHeartRate(arrayAverageHeartRate);
                averageHeartRateCalculationCounter = 0;
            }
            heartRateMonitorUtility.storeMinAndMaxHeartRate(arraySimulationData[updateCounter]);
            sharedValues.saveInt("currentHeartRate", arraySimulationData[updateCounter++]);
            sendMessage();
            mHandler.postDelayed(this, TIME_DELAY);
        }
    };

    @Override
    public void onCreate() {
        sharedValues = SharedValues.getInstance(this);
    }

    private void tryToReadSimulationData() {
        try {
            readSimulationFile();
        } catch (IOException e) {
            //TODO make error handling
            e.printStackTrace();
        }
    }

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

    private void sendMessage() {
        Intent intent = new Intent();
        intent.setAction(HRM_SIMULATION_MESSAGE);
        intent.putExtra("DATA_PASSED", "");
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public boolean initialize() {
        if (arraySimulationData == null) {
            arraySimulationData = new int[SIZE_OF_SIMULATION_DATA];
            tryToReadSimulationData();
        }
        if (arrayAverageHeartRate == null) {
            arrayAverageHeartRate = new int[3];
        }
        if (heartRateMonitorUtility == null) {
            heartRateMonitorUtility = new HeartRateMonitorUtility(this);
        }
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(mHeartRateSimulation, 0);
        Log.e(TAG, "HeartRateSensorSimulation started.");
        return true;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        public HeartRateSensorSimulationService getService() {
            return HeartRateSensorSimulationService.this;
        }
    }

}
