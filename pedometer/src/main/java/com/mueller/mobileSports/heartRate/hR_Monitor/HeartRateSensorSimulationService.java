package com.mueller.mobileSports.heartRate.hR_Monitor;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

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

public class HeartRateSensorSimulationService extends Service implements HeartRateMonitor {


    static final public String HRM_SIMULATION_MESSAGE = "com.mueller.mobileSPorts.pedometer.heartRate.hR_Monitor.HeartRateSimulationService.STEP_VALUE";
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
            mHandler.postDelayed(this, 3000);
        }
    };

    @Override
    public void onCreate() {
        int SIZE_OF_SIMULATION_DATA = 541;
        arraySimulationData = new int[SIZE_OF_SIMULATION_DATA];
        arrayAverageHeartRate = new int[3];
        sharedValues = SharedValues.getInstance(this);
        heartRateMonitorUtility = new HeartRateMonitorUtility(this);
        mHandler = new Handler();
        tryToReadSimulationData();
    }

    @Override
    public int[] getHeartRate() {
        tryToReadSimulationData();
        return arraySimulationData;
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

    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler.postDelayed(mHeartRateSimulation, 0);
        return START_STICKY;
    }

    private void sendMessage() {

        Intent intent = new Intent();
        intent.setAction(HRM_SIMULATION_MESSAGE);
        intent.putExtra("DATA_PASSED", "");
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }
}
