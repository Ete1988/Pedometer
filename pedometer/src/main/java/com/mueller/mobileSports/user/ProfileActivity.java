package com.mueller.mobileSports.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.mueller.mobileSports.general.SettingsActivity;
import com.mueller.mobileSports.general.SharedValues;
import com.mueller.mobileSports.heartRate.HeartRateActivity;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.PedometerActivity;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Sandra on 8/10/2016.
 * A profile screen to display and edit user profile data
 */

public class ProfileActivity extends AppCompatActivity {

    private static final int GET_FROM_GALLERY = 3;
    private EditText[] mInputData;
    private SharedValues sharedValues;
    private SessionManager sessionManager;
    private String gender;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Edit Profile");
        init();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    gender = "Male";
                } else if (position == 1) {
                    gender = "Female";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void init() {

        sessionManager = new SessionManager(this);
        sharedValues = SharedValues.getInstance(this);
        mInputData = new EditText[5];
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mInputData[0] = (EditText) findViewById(R.id.input_name);
        mInputData[1] = (EditText) findViewById(R.id.input_age);
        mInputData[2] = (EditText) findViewById(R.id.input_weight);
        mInputData[3] = (EditText) findViewById(R.id.input_height);
        mInputData[4] = (EditText) findViewById(R.id.input_email);
        spinner = (Spinner) findViewById(R.id.spinner_gender);
        loadUserData();
        if (Objects.equals(sharedValues.getString("gender"), "Female")) {
            spinner.setSelection(1);
        } else if (Objects.equals(sharedValues.getString("gender"), "Male")) {
            spinner.setSelection(0);
        } else {
            spinner.setSelection(0);
        }
    }

    private void loadUserData() {

        mInputData[0].setText(sharedValues.getString("username"));
        mInputData[1].setText(String.format(Locale.getDefault(), "%d", sharedValues.getInt("age")));
        mInputData[2].setText(String.format(Locale.getDefault(), "%d", sharedValues.getInt("weight")));
        mInputData[3].setText(String.format(Locale.getDefault(), "%d", sharedValues.getInt("height")));
        mInputData[4].setText(sharedValues.getString("email"));
    }

    private void updateData() {
        if (validateInput()) {
            sharedValues.saveString("gender", gender);
            sharedValues.saveString("username", mInputData[0].getText().toString());
            sharedValues.saveInt("age", Integer.parseInt(mInputData[1].getText().toString()));
            sharedValues.saveInt("weight", Integer.parseInt(mInputData[2].getText().toString()));
            sharedValues.saveInt("height", Integer.parseInt(mInputData[3].getText().toString()));
            sessionManager.uploadUserData(this, null, true);
        }
    }

    //TODO remove, not used!?
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    //TODO implement validation
    private boolean validateInput() {
        boolean valid = true;
/*
        if (mInputData[1].length() == 0 || mInputData[1].toString().equals("0")) {
            Toast.makeText(this, "Please set your age!", Toast.LENGTH_LONG).show();
            valid = false;
        } else if (spinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please set your gender!", Toast.LENGTH_LONG).show();
            valid = false;
        }
*/
        return valid;
    }

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
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionManager.isLoginValid();
    }

    public void onClickProfile(View v) {
        if (v == null)
            throw new NullPointerException(
                    "You are referring null object. "
                            + "Please check weather you had called super class method mappingWidgets() or not");
        if (v.getId() == R.id.PF_PedometerBtn) {
            Intent i = new Intent(this, PedometerActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.PF_HeartRateBtn) {
            Intent i = new Intent(this, HeartRateActivity.class);
            startActivity(i);
        } else if (v.getId() == R.id.PF_saveChangesBtn) {
            updateData();
        }
    }
}

