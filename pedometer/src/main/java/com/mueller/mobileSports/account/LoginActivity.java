package com.mueller.mobileSports.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.mueller.mobileSports.general.AlertDialogManager;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.PedometerActivity;


/**
 * Created by Sandra on 8/10/2016.
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity {

    EditText txtEmail,txtPassword;
    Button loginButton;
    SessionManager session;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    AsyncCallback<BackendlessUser> loginResponder = new AsyncCallback<BackendlessUser>() {
        @Override
        public void handleResponse(BackendlessUser backendlessUser) {

            Toast.makeText(getBaseContext(), "Thanks for logging in!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), PedometerActivity.class);
            startActivity(intent);
        }

        @Override
        public void handleFault(BackendlessFault backendlessFault) {
            Toast.makeText(getBaseContext(), "Error logging in! Please register or check your log in details", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        android.support.v7.widget.Toolbar myToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Pedometer");
        session = new SessionManager(getApplicationContext());
        loginButton = (Button) findViewById(R.id.email_sign_in_button);
        txtEmail = (EditText) findViewById(R.id.input_email);
        txtPassword = (EditText) findViewById(R.id.password);
        loginButton.setOnClickListener(new View.OnClickListener() {
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

    public void onClick(View v) {
        if (v.getId() == R.id.register_button) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }

    }

    public void login () {
        //Get username, password from EditText
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        // Check if username, password is filled
        if(email.trim().length() > 0 && password.trim().length() > 0){
            final ProgressDialog progress = new ProgressDialog(this);


            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();

            //Backendless Login and let user stay logged in.
            Backendless.UserService.login(email, password, loginResponder, true);
            progress.dismiss();

        }else{
            // user didn't entered username or password
            // Show alert asking him to enter the details
            alert.showAlertDialog(LoginActivity.this, "Login failed..", "Please enter username and password", false);
        }

    }
}