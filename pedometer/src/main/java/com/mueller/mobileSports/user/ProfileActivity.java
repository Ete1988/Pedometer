package com.mueller.mobileSports.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mueller.mobileSports.general.GenericActivity;
import com.mueller.mobileSports.general.SharedValues;
import com.mueller.mobileSports.pedometer.MainActivity.R;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Sandra on 8/10/2016.
 * A profile screen to display and edit user profile data
 */

public class ProfileActivity extends GenericActivity {

    public static final int GET_FROM_GALLERY = 3;
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
        Button mSaveChangesBtn = (Button) findViewById(R.id.btn_saveChangesProfile);
        init();
        prepareView();
        mSaveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = validateInput();
                if (check) {
                    updateUserData();
                }
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(getBaseContext(), "Please set your gender!", Toast.LENGTH_LONG).show();
                } else if (position == 1) {
                    gender = "Male";
                } else if (position == 2) {
                    gender = "Female";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //DoNothing.
            }
        });
    }

    private void init() {

        sessionManager = new SessionManager(this);
        sharedValues = SharedValues.getInstance(this);
        mInputData = new EditText[5];
        mInputData[0] = (EditText) findViewById(R.id.input_name);
        mInputData[1] = (EditText) findViewById(R.id.input_age);
        mInputData[2] = (EditText) findViewById(R.id.input_weight);
        mInputData[3] = (EditText) findViewById(R.id.input_height);
        mInputData[4] = (EditText) findViewById(R.id.input_email);
        spinner = (Spinner) findViewById(R.id.spinner_gender);


        mappingWidgets();


    }

    private void prepareView() {

        loadUserData();
        if (Objects.equals(sharedValues.getString("gender"), "Female")) {
            spinner.setSelection(2);
        } else if (Objects.equals(sharedValues.getString("gender"), "Male")) {
            spinner.setSelection(1);
        } else {
            spinner.setSelection(0);
        }
    }

    private void loadUserData() {

        mInputData[0].setText(sharedValues.getString("username"));
        mInputData[1].setText(String.format(Locale.getDefault(), "%d", sharedValues.getInt("age")));
        mInputData[2].setText(String.format(Locale.getDefault(), "%d", sharedValues.getInt("weight")));
        mInputData[3].setText(String.format(Locale.getDefault(), "%d", sharedValues.getInt("heigth")));
        mInputData[4].setText(sharedValues.getString("email"));
    }

    public void updateUserData() {
        validateInput();
        sharedValues.saveString("gender", gender);
        sharedValues.saveString("username", mInputData[0].getText().toString());
        sharedValues.saveInt("age", Integer.parseInt(mInputData[1].getText().toString()));
        sharedValues.saveInt("weight", Integer.parseInt(mInputData[2].getText().toString()));
        sharedValues.saveInt("height", Integer.parseInt(mInputData[3].getText().toString()));

        sessionManager.uploadUserData(this);
    }

    public void onClickProfile(View v) {
        if (v.getId() == R.id.imageButton) {
            startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
        }
    }

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

    //TODO implement validation;
    public boolean validateInput() {
        boolean valid = true;

        if (mInputData[1].length() == 0 || mInputData[1].toString().equals("0")) {
            Toast.makeText(this, "Please set your age!", Toast.LENGTH_LONG).show();
            valid = false;
        } else if (spinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please set your gender!", Toast.LENGTH_LONG).show();
            valid = false;
        }

        return valid;
    }

    @Override
    protected void mappingWidgets() {
        super.mappingWidgets();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
    @Override
    protected void onResume() {
        super.onResume();
        sessionManager.checkUserState();
    }

}

