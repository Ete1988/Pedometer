package com.mueller.mobileSports.general;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.mueller.mobileSports.pedometer.MainActivity.R;
import com.mueller.mobileSports.pedometer.PedometerActivity;

import butterknife.Bind;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    public static String APP_ID = "61D5CC9D-40B5-4853-FF2F-BCFDD7F64700";
    public static String SECRET_KEY = "76967CB3-F1DE-308D-FF0F-6BA915A44300";
    public static String APPVERSION = "v1";


    private EditText _emailText;
    private EditText _passwordText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        android.support.v7.widget.Toolbar myToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Pedometer");
        Backendless.initApp(this, APP_ID, SECRET_KEY, APPVERSION);

        loginButton = (Button) findViewById(R.id.email_sign_in_button);
        _emailText = (EditText) findViewById(R.id.email);
        _passwordText = (EditText) findViewById(R.id.password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
        pd.setTitle("Logging in...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                pd.dismiss();
            }
        }, 3000); // 3000 milliseconds delay

        Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {


            @Override
            public void handleResponse(BackendlessUser backendlessUser) {

                Toast.makeText(getBaseContext(), "Thanks for logging in!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(LoginActivity.this, PedometerActivity.class);
                startActivity(intent);

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

                Toast.makeText(getBaseContext(), "Error logging in! Please register or check your log in details", Toast.LENGTH_LONG).show();

                pd.dismiss();
            }
        });
    }
}