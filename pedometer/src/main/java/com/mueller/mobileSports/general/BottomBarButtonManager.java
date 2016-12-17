package com.mueller.mobileSports.general;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.mueller.mobileSports.account.ProfileActivity;
import com.mueller.mobileSports.account.SessionManager;
import com.mueller.mobileSports.heartRate.HeartRateActivity;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.PedometerActivity;

/**
 * Created by Ete on 17/12/2016.
 */

public class BottomBarButtonManager extends AppCompatActivity implements View.OnClickListener {

    protected ImageButton mPedometerBtn;
    ImageButton mSettingsBtn, mHeartRateBtn, mProfileBtn, mLogoutBtn;
    private Activity mActivity;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        sessionManager = new SessionManager(this);
    }

    protected void mappingWidgets() {

        mPedometerBtn = (ImageButton) findViewById(R.id.PedometerBtn);
        mSettingsBtn = (ImageButton) findViewById(R.id.SettingsBtn);
        mHeartRateBtn = (ImageButton) findViewById(R.id.HeartRateBtn);
        mLogoutBtn = (ImageButton) findViewById(R.id.LogoutBtn);
        mProfileBtn = (ImageButton) findViewById(R.id.ProfileBtn);
        mPedometerBtn.setOnClickListener(this);
        mSettingsBtn.setOnClickListener(this);
        mHeartRateBtn.setOnClickListener(this);
        mLogoutBtn.setOnClickListener(this);
        mProfileBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == null)
            throw new NullPointerException(
                    "You are refering null object. "
                            + "Please check weather you had called super class method mappingWidgets() or not");
        if (v.getId() == R.id.PedometerBtn) {
            System.out.println("CLicked Pedo");
            Intent intent = new Intent(mActivity, PedometerActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.ProfileBtn) {
            Intent intent = new Intent(mActivity, ProfileActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.SettingsBtn) {
            Intent intent = new Intent(mActivity, SettingsActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.HeartRateBtn) {
            Intent intent = new Intent(mActivity, HeartRateActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.LogoutBtn) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    mActivity);

            // set title
            alertDialogBuilder.setTitle("Logout");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Are you sure you want to logout?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            sessionManager.logoutUser();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }
    }
/*
    protected void handleBackgrounds(View v) {
        if (v == mPedometerBtn) {
            mPedometerBtn.setBackgroundResource(R.drawable.bottom_btn_hover);
            mSettingsBtn.setBackgroundResource(R.drawable.bottom_btn_active);
            mHeartRateBtn.setBackgroundResource(R.drawable.bottom_btn_active);

        } else if (v == mSettingsBtn) {
            mPedometerBtn.setBackgroundResource(R.drawable.bottom_btn_active);
            mSettingsBtn.setBackgroundResource(R.drawable.bottom_btn_hover);
            mHeartRateBtn.setBackgroundResource(R.drawable.bottom_btn_active);

        } else if (v == mHeartRateBtn) {
            mPedometerBtn.setBackgroundResource(R.drawable.bottom_btn_active);
            mSettingsBtn.setBackgroundResource(R.drawable.bottom_btn_active);
            mHeartRateBtn.setBackgroundResource(R.drawable.bottom_btn_hover);
        }
    }
*/
}

