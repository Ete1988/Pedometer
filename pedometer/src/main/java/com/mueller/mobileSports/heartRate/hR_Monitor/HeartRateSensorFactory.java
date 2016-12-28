package com.mueller.mobileSports.heartRate.hR_Monitor;

import android.content.Context;

/**
 * Created by Ete on 28/12/2016.
 */

public class HeartRateSensorFactory implements SensorFactory {

    private Context context;

    public HeartRateSensorFactory(Context context) {
        this.context = context;
    }

    @Override
    public void createHRM() {


    }
}
