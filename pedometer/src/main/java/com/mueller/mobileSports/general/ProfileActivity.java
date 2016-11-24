package com.mueller.mobileSports.general;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.backendless.Backendless;
import com.mueller.mobileSports.pedometer.MainActivity.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    public static final int GET_FROM_GALLERY = 3;
    private String objectId;
    private EditText comments;
    private EditText username;
    private EditText email;
    private EditText age;
    private EditText gender;
    private EditText weight;
    private int heartRate;
    private int weeklyStepCount;
    private int monthlyStepCount;
    private Date created;
    private Date updated;
    private File uploadedFile;

    private Button saveChanges;

    public static String APP_ID = "61D5CC9D-40B5-4853-FF2F-BCFDD7F64700";
    public static String SECRET_KEY = "76967CB3-F1DE-308D-FF0F-6BA915A44300";
    public static String APPVERSION = "v1";

    public ProfileActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Edit Profile");

        Backendless.initApp(this, APP_ID, SECRET_KEY, APPVERSION);

        email = (EditText) findViewById(R.id.input_email);
        username = (EditText) findViewById(R.id.input_name);
        gender = (EditText) findViewById(R.id.input_gender);
        weight = (EditText) findViewById(R.id.weight);
        age = (EditText) findViewById(R.id.input_age);
        saveChanges = (Button) findViewById(R.id.btn_saveChanges);


    }




    public void onClick(View v) {
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
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public boolean validate() {
        boolean valid = true;
        String name = username.getText().toString();
        String emailText = email.getText().toString();


        if (name.length() < 3) {
            username.setError("at least 3 characters");
            valid = false;
        } else {
            username.setError(null);
        }

        if (emailText.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            email.setError("enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }

        return valid;
    }

}

