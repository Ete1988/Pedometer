package com.mueller.mobileSports.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.PedometerActivity;
import com.mueller.mobileSports.user.SessionManager;


/**
 * Created by Sandra on 8/10/2016.
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailText, mPasswordText;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");
        sessionManager = new SessionManager(this);
        Button mLoginButton = (Button) findViewById(R.id.email_sign_in_button);
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
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class); //???
            startActivity(intent);
        }

    }

    //TODO add user logged in check to skip
    private void login() {
        //Get username, password from EditText
        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();

        // Check if username, password is filled
        if (email.trim().length() > 0 && password.trim().length() > 0) {
            //Backendless Login and let user stay logged in.
            Intent intent = new Intent(getApplicationContext(), PedometerActivity.class);
            sessionManager.userLogin(email, password, intent);
        } else {
            // user didn't enter username or password
            Toast.makeText(getBaseContext(), "Please enter username and password", Toast.LENGTH_LONG).show();
        }

    }

}