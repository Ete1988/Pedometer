package com.mueller.mobileSports.general;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.backendless.Backendless;
import com.mueller.mobileSports.pedometer.PedometerActivity;

/**
 * Created by Sandra on 8/10/2016.
 * <p>
 * Just a splash screen on startup
 */

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String APP_ID = "61D5CC9D-40B5-4853-FF2F-BCFDD7F64700";
        String APPVERSION = "v1";
        String SECRET_KEY = "76967CB3-F1DE-308D-FF0F-6BA915A44300";
        Backendless.initApp(this, APP_ID, SECRET_KEY, APPVERSION);
        Intent intent = new Intent(this, PedometerActivity.class);
        startActivity(intent);
        finish();
    }

}
