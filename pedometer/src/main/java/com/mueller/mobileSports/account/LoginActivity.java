package com.mueller.mobileSports.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.PedometerActivity;
import com.mueller.mobileSports.user.UserSessionManager;

/**
 * Created by Sandra on 8/10/2016.
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailText, mPasswordText;
    private UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String APP_ID = "61D5CC9D-40B5-4853-FF2F-BCFDD7F64700";
        String APPVERSION = "v1";
        String SECRET_KEY = "76967CB3-F1DE-308D-FF0F-6BA915A44300";
        Backendless.initApp(this, APP_ID, SECRET_KEY, APPVERSION);
        setContentView(R.layout.activity_login);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Login");
        userSessionManager = new UserSessionManager(this);
        userSessionManager.isUserLoggedIn();
        mEmailText = (EditText) findViewById(R.id.input_email);
        mPasswordText = (EditText) findViewById(R.id.password);
    }

    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.register_button) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.email_sign_in_button) {
            login();
        }
    }

    private void login() {
        //Get username, password from EditText
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();

        // Check if username, password is filled
        if (email.trim().length() > 0 && password.trim().length() > 0) {
            //Backendless Login and let user stay logged in.
            Intent intent = new Intent(this, PedometerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            userSessionManager.userLogin(email, password, intent);
        } else {
            // user didn't enter username or password
            Toast.makeText(getBaseContext(), "Please enter username and password", Toast.LENGTH_LONG).show();
        }
    }
}