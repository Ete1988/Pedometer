package com.mueller.mobileSports.user;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.PedometerActivity;

import java.util.Locale;
import java.util.Objects;

/**
 * Created by Sandra on 8/10/2016.
 * A profile screen to display and edit user profile data
 */
public class ProfileActivity extends AppCompatActivity {

    boolean firstTime;
    private EditText mInputUserName, mInputAge, mInputWeight, mInputHeight, mInputEmail;
    private UserSessionManager userSessionManager;
    private String gender, username, email;
    private int age, weight, height;
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

    @Override
    protected void onResume() {
        Intent intent = getIntent();
        firstTime = intent.getBooleanExtra("firstTime", false);
        if (firstTime) {
            Toast.makeText(this, "Please set up your profile!", Toast.LENGTH_SHORT).show();
        }
        userSessionManager.checkUserState();
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        if (firstTime) {
            if (validateInput()) {
                startActivity(new Intent(this, PedometerActivity.class));
            }
        } else {
            super.onBackPressed();
        }

    }

    /**
     * Initializes most fields of activity
     */
    private void init() {
        UserData userData = UserSessionManager.getUserData();
        age = userData.getAge();
        height = userData.getHeight();
        weight = userData.getWeight();
        username = userData.getUsername();
        email = userData.getEmail();
        userSessionManager = new UserSessionManager(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mInputUserName = (EditText) findViewById(R.id.input_name);
        mInputAge = (EditText) findViewById(R.id.input_age);
        mInputWeight = (EditText) findViewById(R.id.input_weight);
        mInputHeight = (EditText) findViewById(R.id.input_height);
        mInputEmail = (EditText) findViewById(R.id.input_email);
        spinner = (Spinner) findViewById(R.id.spinner_gender);
        mapUserDataToView();
        if (Objects.equals(userData.getGender(), "Female")) {
            spinner.setSelection(1);
        } else if (Objects.equals(userData.getGender(), "Male")) {
            spinner.setSelection(0);
        } else {
            spinner.setSelection(0);
        }
    }

    /**
     * Maps userdata to widgets in view
     */
    private void mapUserDataToView() {

        if (Objects.equals(username, "")) {
            mInputUserName.setText("");
            }else {
            mInputUserName.setText(username);
        }

        mInputEmail.setText(email);

        if (!(age == 0))
            mInputAge.setText(String.format(Locale.getDefault(), "%d", age));
        if (!(weight == 0))
            mInputWeight.setText(String.format(Locale.getDefault(), "%d", weight));
        if (!(height == 0))
            mInputHeight.setText(String.format(Locale.getDefault(), "%d", height));
    }

    /**
     * Safe and persist userdata on server
     *
     * @param showProgressBar set true iff progressbar shall be visible.
     *                        makes user interaction with the app impossible during updateprocess.
     */
    private void updateData(boolean showProgressBar) {
        if (validateInput()) {
            UserData userData = UserSessionManager.getUserData();

            userData.setAge(Integer.parseInt(mInputAge.getText().toString()));
            userData.setGender(gender);
            userData.setUsername(mInputUserName.getText().toString());
            userData.setHeight(Integer.parseInt(mInputHeight.getText().toString()));
            userData.setWeight(Integer.parseInt(mInputWeight.getText().toString()));

            UserSessionManager.setUserData(userData);
            userSessionManager.uploadUserData(this, showProgressBar, false);
        }
    }

    /**
     * Validate UserInput
     *
     * @return true iff all data is valid
     */
    private boolean validateInput() {
        if (mInputUserName.length() == 0) {
            mInputUserName.setText(" ");
        }

        if (mInputAge.length() == 0) {
            Toast.makeText(this, "Please set your age!", Toast.LENGTH_LONG).show();
            return false;
        } else if (Integer.parseInt(mInputAge.getText().toString()) < 10 || Integer.parseInt(mInputAge.getText().toString()) > 100) {
            Toast.makeText(this, "Please set valid age. From 10yrs - 100yrs.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (mInputWeight.length() == 0) {
            mInputWeight.setText("");
            Toast.makeText(this, "Please set your weight!", Toast.LENGTH_LONG).show();
            return false;
        } else if (Integer.parseInt(mInputWeight.getText().toString()) < 20 || Integer.parseInt(mInputWeight.getText().toString()) > 250) {
            Toast.makeText(this, "Please set valid weight. From 20kg - 250kg.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (mInputHeight.length() == 0) {
            mInputHeight.setText("");
            Toast.makeText(this, "Please set your height!", Toast.LENGTH_LONG).show();
            return false;
        } else if (Integer.parseInt(mInputHeight.getText().toString()) < 100 || Integer.parseInt(mInputHeight.getText().toString()) > 250) {
            Toast.makeText(this, "Please set valid height. From 100cm - 250cm.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void onClickProfileActivity(View v) {
        if (v.getId() == R.id.PF_saveChangesBtn) {
            //firstTime is true iff the user just created the account and has to set up his profile
            if (firstTime) {
                if (validateInput()) {
                    updateData(false);
                    startActivity(new Intent(this, PedometerActivity.class));
                }
            } else {
                updateData(true);
            }
        }
    }
}

