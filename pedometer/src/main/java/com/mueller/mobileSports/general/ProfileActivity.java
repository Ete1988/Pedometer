package com.mueller.mobileSports.general;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.BackendlessCallback;
import com.mueller.mobileSports.pedometer.MainActivity.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    public static final int GET_FROM_GALLERY = 3;
    private EditText username;
    private EditText height;
    private EditText age;
    private EditText gender;
    private EditText weight;
    private int heartRate;
    private int weeklyStepCount;
    private int monthlyStepCount;

    private Date created;
    private Date updated;
    private File uploadedFile;

    private Button saveChangesButton;

    public static String APP_ID = "61D5CC9D-40B5-4853-FF2F-BCFDD7F64700";
    public static String SECRET_KEY = "76967CB3-F1DE-308D-FF0F-6BA915A44300";
    public static String APPVERSION = "v1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Edit Profile");

        Backendless.initApp(this, APP_ID, SECRET_KEY, APPVERSION);

        height = (EditText) findViewById(R.id.input_height);
        username = (EditText) findViewById(R.id.input_name);
        gender = (EditText) findViewById(R.id.input_gender);
        weight = (EditText) findViewById(R.id.weight);
        age = (EditText) findViewById(R.id.input_age);
        saveChangesButton = (Button) findViewById(R.id.btn_saveChanges);



        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = validate();
                if (check) {

                    saveChanges();

                    final ProgressDialog pd = new ProgressDialog(ProfileActivity.this);
                    pd.setTitle("Saving changes...");
                    pd.setMessage("Please wait.");
                    pd.setCancelable(false);
                    pd.setIndeterminate(true);
                    pd.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            pd.dismiss();
                        }
                    }, 2000); // 2000 milliseconds delay



                    height.setText("");
                    username.setText("");
                    height.setText("");
                    age.setText("");
                    gender.setText("");
                    weight.setText("");

                }
            }
        });
    }




    public void saveChanges()
    {
        final BackendlessUser user = new BackendlessUser();

        // save object asynchronously

        Backendless.Persistence.save(new saveProfileChanges(username.getText().toString(), Integer.parseInt(height.getText().toString()),
        Integer.parseInt(age.getText().toString()), gender.getText().toString(), Integer.parseInt(weight.getText().toString()), 12, 111, 1345), new BackendlessCallback<saveProfileChanges>()
        {
            @Override
            public void handleResponse( saveProfileChanges change )
            {
                Log.i( "Changes", "Got new changes from " + user.getEmail() );
            }
        } );

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
        String emailText = height.getText().toString();


        if (name.length() < 3) {
            username.setError("at least 3 characters");
            valid = false;
        } else {
            username.setError(null);
        }

        return valid;
    }

}

