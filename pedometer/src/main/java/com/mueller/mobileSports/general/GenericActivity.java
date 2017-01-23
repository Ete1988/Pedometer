package com.mueller.mobileSports.general;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mueller.mobileSports.heartRate.HeartRateActivity;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.PedometerActivity;
import com.mueller.mobileSports.user.ProfileActivity;
import com.mueller.mobileSports.user.UserSessionManager;

public class GenericActivity extends AppCompatActivity implements View.OnClickListener {

    protected Toolbar toolbar;
    protected NavigationView navigationView;
    protected DrawerLayout drawerLayout;
    protected TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_layout);
        init();
        setUpNavigation();
    }

    protected void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        text = (TextView) navigationView.getHeaderView(0).findViewById(R.id.BackgrText);
        String temp = "Step Up " + UserSessionManager.getUserData().getUsername();
        text.setText(temp);
    }

    protected void setUpNavigation() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent i;
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.HeartRateBtn:
                        i = new Intent(getApplicationContext(), HeartRateActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.PedometerBtn:
                        i = new Intent(getApplicationContext(), PedometerActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.SettingsBtn:
                        i = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.ProfileBtn:
                        i = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.StatisticsBtn:
                        i = new Intent(getApplicationContext(), StatisticsActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.logOutBtn:
                        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
                        userSessionManager.uploadUserData(getApplicationContext(), false, true, true);
                        return true;
                    default:
                        return true;
                }
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

    }

    @Override
    public void onClick(View v) {
    }


}
