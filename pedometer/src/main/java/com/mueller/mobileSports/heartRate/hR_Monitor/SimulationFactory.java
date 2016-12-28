package com.mueller.mobileSports.heartRate.hR_Monitor;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Ete on 17/11/2016.
 */

public class SimulationFactory implements SensorFactory {

    Context context;

    public SimulationFactory(Context context) {
        this.context = context;
    }

    @Override
    public void createHRM() {
        System.out.println("SimuFac");
        Intent i = new Intent(context, HeartRateSimulationService.class);
        context.startService(i);
    }

}
