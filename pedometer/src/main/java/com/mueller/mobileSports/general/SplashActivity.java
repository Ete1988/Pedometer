package com.mueller.mobileSports.general;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mueller.mobileSports.account.LoginActivity;
import com.mueller.mobileSports.pedometer.PedometerActivity;

/**
 * Created by Sandra on 8/10/2016.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, PedometerActivity.class);
        startActivity(intent);
        finish();
    }

}
