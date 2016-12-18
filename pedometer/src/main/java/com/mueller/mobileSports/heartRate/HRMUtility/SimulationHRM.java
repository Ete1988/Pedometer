package com.mueller.mobileSports.heartRate.HRMUtility;

import android.content.Context;
import android.content.res.AssetManager;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Ete on 15/12/2016.
 */

public class SimulationHRM implements HeartRateMonitor {


    private Context context;
    private int[] simulationData;

    public SimulationHRM(Context context) {
        this.context = context;
        int SIZE_OF_SIMULATION_DATA = 541;
        simulationData = new int[SIZE_OF_SIMULATION_DATA];
    }

    @Override
    public int[] getHeartRate() {

        tryToReadSimulationData();
        return simulationData;
    }

    private void tryToReadSimulationData() {
        try {
            readSimulationFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void readSimulationFile() throws IOException {
        int i = 0;
        AssetManager assetManager = context.getAssets();

        try {

            InputStream csvStream = assetManager.open("simulationData.csv");
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
            CSVReader csvReader = new CSVReader(csvStreamReader, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 1);
            String[] line;

            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {
                simulationData[i] = Integer.parseInt(line[1]);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
