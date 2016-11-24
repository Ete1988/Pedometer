package com.mueller.mobileSports.general;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.mueller.mobileSports.pedometer.MainActivity.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.password) EditText _passwordText;
    @Bind(R.id.confirmpassword)  EditText _confirmPasswordText;
    @Bind(R.id.createAccount_button) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;


    private final static String APP_ID = "61D5CC9D-40B5-4853-FF2F-BCFDD7F64700";
    private final static String SECRET_KEY = "76967CB3-F1DE-308D-FF0F-6BA915A44300";
    private final static String APPVERSION = "v1";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("Register");
        Backendless.initApp(this, APP_ID, SECRET_KEY, APPVERSION);


        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = validate();
                if (check) {

                    String email = _emailText.getText().toString();
                    String password = _passwordText.getText().toString();
                    String username = _nameText.getText().toString();

                    BackendlessUser user = new BackendlessUser();
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setProperty("name", username);

                    final ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
                    pd.setTitle("Creating account...");
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

                    Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser backendlessUser) {

                            Toast.makeText(getBaseContext(), "You have been registered", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_LONG).show();
                            pd.dismiss();
                        }

                    });
                }
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

    }


    public boolean validate() {
        boolean valid = true;
        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmPassword = _confirmPasswordText.getText().toString();


        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);

        }

        if (confirmPassword.isEmpty() || !(confirmPassword.contentEquals(password))) {
            _confirmPasswordText.setError("Both passwords are not identical");
            valid = false;
        } else {
            _confirmPasswordText.setError(null);
        }
        return valid;
    }

}
