package com.mueller.mobileSports.pedometer.general;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.backendless.Backendless;
import com.mueller.mobileSports.pedometer.PedometerActivity;

/**
 * Created by Sandra on 8/10/2016.
 */

public class SplashActivity extends AppCompatActivity {

    private static String APP_ID = "61D5CC9D-40B5-4853-FF2F-BCFDD7F64700";
    private static String SECRET_KEY = "76967CB3-F1DE-308D-FF0F-6BA915A44300";
    private static String APPVERSION = "v1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Backendless.initApp(this, APP_ID, SECRET_KEY, APPVERSION);
        Intent intent = new Intent(this, PedometerActivity.class);
        startActivity(intent);
        finish();
    }

}
