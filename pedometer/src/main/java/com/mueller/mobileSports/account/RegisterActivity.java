package com.mueller.mobileSports.account;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by Sandra on 8/10/2016.
 * A register screen to create a new user account
 */

public class RegisterActivity extends AppCompatActivity {

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.password)
    EditText _passwordText;
    @Bind(R.id.confirmpassword)
    EditText _confirmPasswordText;
    @Bind(R.id.createAccount_button)
    Button _signupButton;
    @Bind(R.id.link_login)
    TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setContentView(R.layout.activity_register);
        setTitle("Register Account");
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = validate();
                if (check) {

                    String email = _emailText.getText().toString();
                    String password = _passwordText.getText().toString();

                    BackendlessUser user = new BackendlessUser();
                    user.setEmail(email);
                    user.setPassword(password);

                    final ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
                    pd.setTitle("Creating account...");
                    pd.setMessage("Please wait.");
                    pd.setCancelable(false);
                    pd.setIndeterminate(true);
                    pd.show();

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
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

    }

    public boolean validate() {
        boolean valid = true;
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmPassword = _confirmPasswordText.getText().toString();

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
