package com.mueller.mobileSports.heartRate.hR_Monitor;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Ete on 17/11/2016.
 */

public class HeartRateSensorSimulationFactory implements SensorFactory {

    private Context context;

    public HeartRateSensorSimulationFactory(Context context) {
        this.context = context;
    }

    @Override
    public void createHRM() {
        Intent i = new Intent(context, HeartRateSensorSimulationService.class);
        context.startService(i);
    }
}
