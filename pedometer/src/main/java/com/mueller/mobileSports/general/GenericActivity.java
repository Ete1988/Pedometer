package com.mueller.mobileSports.general;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.mueller.mobileSports.heartRate.HeartRateActivity;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.PedometerActivity;
import com.mueller.mobileSports.user.ProfileActivity;
import com.mueller.mobileSports.user.SessionManager;

/**
 * Created by Ete on 17/12/2016.
 * <p>
 * Abstract base class for all activities to who include the bottom bar layout.
 */

public abstract class GenericActivity extends Activity implements View.OnClickListener {

    private Activity mActivity;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        sessionManager = new SessionManager(this);
    }

    /**
     * Map all buttons accordingly
     */
    protected void mappingWidgets() {

        ImageButton mPedometerBtn = (ImageButton) findViewById(R.id.PedometerBtn);
        ImageButton mHeartRateBtn = (ImageButton) findViewById(R.id.HeartRateBtn);
        ImageButton mProfileBtn = (ImageButton) findViewById(R.id.ProfileBtn);
        mPedometerBtn.setOnClickListener(this);
        mHeartRateBtn.setOnClickListener(this);
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
        } else if (v.getId() == R.id.HeartRateBtn) {
            Intent intent = new Intent(mActivity, HeartRateActivity.class);
            startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Only one button for now.
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.menu_logout:
                sessionManager.logoutUser();
                break;
            default:
                break;
        }
        return true;
    }
}

