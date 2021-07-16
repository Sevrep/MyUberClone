package com.sevrep.myuberclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    enum State {
        SIGNUP, LOGIN
    }

    private State state;
    private Button btnSignUpLogin;
    private Button btnOneTimeLogin;
    private RadioButton rdbDriver;
    private RadioButton rdbPassenger;
    private EditText edtUserName;
    private EditText edtPassword;
    private EditText edtDriverOrPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseInstallation.getCurrentInstallation().saveInBackground();
        if (ParseUser.getCurrentUser() != null) {
            transitionToPassengerActivity();
            transitionToDriverRequestListActivity();
        }

        state = State.SIGNUP;

        rdbDriver = findViewById(R.id.rdbDriver);
        rdbPassenger = findViewById(R.id.rdbPassenger);

        btnSignUpLogin = findViewById(R.id.btnSignUpLogin);
        btnSignUpLogin.setOnClickListener(this);
        btnOneTimeLogin = findViewById(R.id.btnOneTimeLogin);
        btnOneTimeLogin.setOnClickListener(this);

        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        edtDriverOrPassenger = findViewById(R.id.edtDriverOrPassenger);

    }

    @Override
    public void onClick(View view) {

        if (view == btnSignUpLogin) {
            signUpLogin();
        } else if (view == btnOneTimeLogin) {
            onTimeLogin();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_signup_activity, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.loginItem) {
            if (state == State.SIGNUP) {
                state = State.LOGIN;
                item.setTitle("Sign Up");
                btnSignUpLogin.setText(R.string.log_in);
            } else if (state == State.LOGIN) {
                state = State.SIGNUP;
                item.setTitle("Log In");
                btnSignUpLogin.setText(R.string.sign_up);
            }
        }
        return super.onOptionsItemSelected(item);

    }

    private void signUpLogin() {

        if (state == State.SIGNUP) {
            if (!rdbDriver.isChecked() && !rdbPassenger.isChecked()) {
                Toast.makeText(MainActivity.this, "Are you a driver or a passenger?", Toast.LENGTH_SHORT).show();
                return;
            }

            ParseUser appUser = new ParseUser();
            appUser.setUsername(edtUserName.getText().toString());
            appUser.setPassword(edtPassword.getText().toString());

            if (rdbDriver.isChecked()) {
                appUser.put("as", "Driver");
            } else if (rdbPassenger.isChecked()) {
                appUser.put("as", "Passenger");
            }

            appUser.signUpInBackground(e -> {
                if (e == null) {
                    Toast.makeText(MainActivity.this, "Signed up!", Toast.LENGTH_SHORT).show();
                    transitionToPassengerActivity();
                    transitionToDriverRequestListActivity();
                }
            });
        } else if (state == State.LOGIN) {
            ParseUser.logInInBackground(edtUserName.getText().toString(), edtPassword.getText().toString(), (user, e) -> {
                if (user != null && e == null) {
                    Toast.makeText(MainActivity.this, "User logged in", Toast.LENGTH_SHORT).show();
                    transitionToPassengerActivity();
                    transitionToDriverRequestListActivity();
                }
            });
        }

    }

    private void onTimeLogin() {

        String driverOrPassenger = edtDriverOrPassenger.getText().toString().toLowerCase();
        if (driverOrPassenger.equals("driver") || driverOrPassenger.equals("passenger")) {
            if (ParseUser.getCurrentUser() == null) {
                ParseAnonymousUtils.logIn((user, e) -> {
                    if (user != null && e == null) {

                        Toast.makeText(MainActivity.this, "We have an anonymous user", Toast.LENGTH_SHORT).show();

                        user.put("as", edtDriverOrPassenger.getText().toString());
                        user.saveInBackground(e1 -> {
                            transitionToPassengerActivity();
                            transitionToDriverRequestListActivity();
                        });
                    }
                });
            }
        } else {
            Toast.makeText(MainActivity.this, "Are you a driver or a passenger?", Toast.LENGTH_SHORT).show();
        }

    }

    private void transitionToPassengerActivity() {

        if (ParseUser.getCurrentUser() != null) {
            if (Objects.requireNonNull(ParseUser.getCurrentUser().get("as")).equals("Passenger")) {
                /*Intent intent = new Intent(MainActivity.this, PassengerActivity.class);
                startActivity(intent);*/
            }
        }

    }

    private void transitionToDriverRequestListActivity() {

        if (ParseUser.getCurrentUser() != null) {
            if (Objects.requireNonNull(ParseUser.getCurrentUser().get("as")).equals("Driver")) {
                /*Intent intent = new Intent(this, DriverRequestListActivity.class);
                startActivity(intent);*/
            }
        }

    }

}