package com.mueller.mobileSports.heartRate.HRMUtility;

import android.content.Context;
import android.content.res.AssetManager;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Ete on 15/12/2016.
 */

public class SimulationHRM implements HeartRateMonitor {


    Context context;

    public SimulationHRM(Context context) {
        this.context = context;
    }

    @Override
    public int getHeartRate() {

        try {
            readSimulationFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;

    }

    private void readSimulationFile() throws IOException {

        AssetManager assetManager = context.getAssets();

        try {

            InputStream csvStream = assetManager.open("simulationData.csv");
            InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
            CSVReader csvReader = new CSVReader(csvStreamReader);
            String[] line;

            // throw away the header

            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {
                System.out.println(" Heart Rate: " + line[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
