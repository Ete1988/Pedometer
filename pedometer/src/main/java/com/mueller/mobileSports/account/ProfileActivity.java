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

/**
 * Created by Sandra on 8/10/2016.
 * A profile screen to display and edit user profile data
 */

public class ProfileActivity extends AppCompatActivity {

    public static final int GET_FROM_GALLERY = 3;
    private EditText mUsernameText, mHeightText, mAgeText, mGenderText, mWeightText;
    private File uploadedFile;
    private Button mSaveChangesBtn;
    private UserProfileData myData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Edit Profile");
        myData = new UserProfileData();
        mHeightText = (EditText) findViewById(R.id.input_height);
        mUsernameText = (EditText) findViewById(R.id.input_name);
        mGenderText = (EditText) findViewById(R.id.input_gender);
        mWeightText = (EditText) findViewById(R.id.input_weight);
        mAgeText = (EditText) findViewById(R.id.input_age);
        mSaveChangesBtn = (Button) findViewById(R.id.btn_saveChanges);
        loadData();

        mSaveChangesBtn.setOnClickListener(new View.OnClickListener() {
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


        mHeightText.setText(Integer.toString(myData.getHeight()));
        mAgeText.setText(Integer.toString(myData.getAge()));
        mWeightText.setText(Integer.toString(myData.getWeight()));
        mGenderText.setText(myData.getGender());
        mUsernameText.setText(myData.getUsername());

    }

    public void updateData()
    {
        myData.setAge(Integer.parseInt(mAgeText.getText().toString()));
        myData.setGender(mGenderText.getText().toString());
        myData.setHeight(Integer.parseInt(mHeightText.getText().toString()));
        myData.setWeight(Integer.parseInt(mWeightText.getText().toString()));
        myData.setUsername(mUsernameText.getText().toString());

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
        String emailText = mHeightText.getText().toString();

/*
        if (name.length() < 3) {
            mUsernameText.setError("at least 3 characters");
            valid = false;
        } else {
            mUsernameText.setError(null);
        }
*/
        return valid;
    }

}

