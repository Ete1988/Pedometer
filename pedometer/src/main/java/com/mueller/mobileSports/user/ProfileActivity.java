package com.mueller.mobileSports.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mueller.mobileSports.general.SettingsActivity;
import com.mueller.mobileSports.general.SharedValues;
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
    boolean firstTime;
    private EditText mInputUserName, mInputAge, mInputWeight, mInputHeight, mInputEmail;
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

    @Override
    protected void onResume() {
        Intent intent = getIntent();
        firstTime = intent.getBooleanExtra("firstTime", false);
        if (firstTime) {
            Toast.makeText(this, "Please set up your profile!", Toast.LENGTH_SHORT).show();
        }
        sessionManager.checkUserState();
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

    private void init() {

        sessionManager = new SessionManager(this);
        sharedValues = SharedValues.getInstance(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        mInputUserName = (EditText) findViewById(R.id.input_name);
        mInputAge = (EditText) findViewById(R.id.input_age);
        mInputWeight = (EditText) findViewById(R.id.input_weight);
        mInputHeight = (EditText) findViewById(R.id.input_height);
        mInputEmail = (EditText) findViewById(R.id.input_email);
        spinner = (Spinner) findViewById(R.id.spinner_gender);
        mapUserDataToView();
        if (Objects.equals(sharedValues.getString("gender"), "Female")) {
            spinner.setSelection(1);
        } else if (Objects.equals(sharedValues.getString("gender"), "Male")) {
            spinner.setSelection(0);
        } else {
            spinner.setSelection(0);
        }
    }

    private void mapUserDataToView() {

        if(Objects.equals(sharedValues.getString("username"), " ")){
            mInputUserName.setText(sharedValues.getString("username2"));
            }else {
            mInputUserName.setText(sharedValues.getString("username"));
        }


        mInputEmail.setText(sharedValues.getString("email"));

        if (!(sharedValues.getInt("age") == 0))
            mInputAge.setText(String.format(Locale.getDefault(), "%d", sharedValues.getInt("age")));
        if (!(sharedValues.getInt("weight") == 0))
            mInputWeight.setText(String.format(Locale.getDefault(), "%d", sharedValues.getInt("weight")));
        if (!(sharedValues.getInt("height") == 0))
            mInputHeight.setText(String.format(Locale.getDefault(), "%d", sharedValues.getInt("height")));


    }

    private void updateData(boolean showProgressBar) {
        if (validateInput()) {
            sharedValues.saveString("gender", gender);
            sharedValues.saveString("username", mInputUserName.getText().toString());
            sharedValues.saveInt("age", Integer.parseInt(mInputAge.getText().toString()));
            sharedValues.saveInt("weight", Integer.parseInt(mInputWeight.getText().toString()));
            sharedValues.saveInt("height", Integer.parseInt(mInputHeight.getText().toString()));

            sessionManager.uploadUserData(this, showProgressBar, false);

        }
    }

    //TODO remove?
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
                e.printStackTrace();
            }
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

