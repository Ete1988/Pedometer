package com.mueller.mobileSports.account;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.user.UserProfileData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Sandra on 8/10/2016.
 * A profile screen to display and edit user profile data
 */

public class ProfileActivity extends AppCompatActivity {

    public static final int GET_FROM_GALLERY = 3;
    private EditText username, height, age, gender, weight;
    private int heartRate, weeklyStepCount, monthlyStepCount;
    private Date created, updated;
    private File uploadedFile;
    private Button saveChangesButton;
    private UserProfileData myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Edit Profile");
        myData = new UserProfileData();
        height = (EditText) findViewById(R.id.input_height);
        username = (EditText) findViewById(R.id.input_name);
        gender = (EditText) findViewById(R.id.input_gender);
        weight = (EditText) findViewById(R.id.input_weight);
        age = (EditText) findViewById(R.id.input_age);
        saveChangesButton = (Button) findViewById(R.id.btn_saveChanges);
        loadData();

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = validate();
                if (check) {

                    updateData();
                }
            }
        });
    }

    private void loadData() {


        height.setText(Integer.toString(myData.getHeight()));
        age.setText(Integer.toString(myData.getAge()));
        weight.setText(Integer.toString(myData.getWeight()));
        gender.setText(myData.getGender());
        username.setText(myData.getUsername());

    }

    public void updateData()
    {
        myData.setAge(Integer.parseInt(age.getText().toString()));
        myData.setGender(gender.getText().toString());
        myData.setHeight(Integer.parseInt(height.getText().toString()));
        myData.setWeight(Integer.parseInt(weight.getText().toString()));
        myData.setUsername(username.getText().toString());

        final ProgressDialog pd = new ProgressDialog(ProfileActivity.this);
        pd.setTitle("Saving changes...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();

        Backendless.Persistence.of(UserProfileData.class).save(myData, new AsyncCallback<UserProfileData>()
        {
            @Override
            public void handleResponse(UserProfileData updatedData)
            {
                System.out.println("Person's name after update " + updatedData.toString());
                pd.dismiss();
                Toast.makeText(getBaseContext(), "Success!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                pd.dismiss();
                Toast.makeText(getBaseContext(), "Failure!", Toast.LENGTH_LONG).show();


            }
        });
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
        String emailText = height.getText().toString();

/*
        if (name.length() < 3) {
            username.setError("at least 3 characters");
            valid = false;
        } else {
            username.setError(null);
        }
*/
        return valid;
    }

}

