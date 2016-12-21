package com.mueller.mobileSports.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.mueller.mobileSports.heartRate.HRMUtility.HeartRateMonitor;
import com.mueller.mobileSports.heartRate.HRMUtility.SimulationHRM;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.PedometerActivity;


/**
 * Created by Sandra on 8/10/2016.
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends Activity {

    final AsyncCallback<BackendlessUser> loginResponder = new AsyncCallback<BackendlessUser>() {
        @Override
        public void handleResponse(BackendlessUser backendlessUser) {
            Backendless.UserService.setCurrentUser(backendlessUser);
            Toast.makeText(getBaseContext(), "Thanks for logging in!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), PedometerActivity.class);
            startActivity(intent);
        }

        @Override
        public void handleFault(BackendlessFault backendlessFault) {
            Toast.makeText(getBaseContext(), "Error logging in! Please register or check your log in details", Toast.LENGTH_LONG).show();
        }
    };
    EditText mEmailText, mPasswordText;
    Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");
        mLoginButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailText = (EditText) findViewById(R.id.input_email);
        mPasswordText = (EditText) findViewById(R.id.password);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                login();
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        this.finish();

    }

    public void onClickLogin(View v) {
        if (v.getId() == R.id.register_button) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }

        if (v.getId() == R.id.test_button) {
            HeartRateMonitor hrm = new SimulationHRM(this);

            hrm.getHeartRate();

        }

    }

    public void login() {
        //Get username, password from EditText
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();

        //TODO Progressbar doesn't work.
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);
        progress.show(); // disable dismiss by tapping outside of the dialog

        // Check if username, password is filled
        if (email.trim().length() > 0 && password.trim().length() > 0) {
            //Backendless Login and let user stay logged in.
            Backendless.UserService.login(email, password, loginResponder, true);
            progress.dismiss();

        } else {
            // user didn't entered username or password
            progress.dismiss();
            Toast.makeText(getBaseContext(), "Please enter username and password", Toast.LENGTH_LONG).show();
        }

    }

}