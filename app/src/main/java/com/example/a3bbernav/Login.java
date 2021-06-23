package com.example.a3bbernav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class Login extends AppCompat {

    TextInputLayout email, password;
    TextView forgot;
    Button login;
    TextView signUp, profile_Email, profile_name;
    ProgressBar progress;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //set an icon for the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);


        //initialize controls
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        forgot = findViewById(R.id.forgot);
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.signUp);
        progress = findViewById(R.id.progressBar_login);

        profile_Email = findViewById(R.id.profile_email);
        profile_name = findViewById(R.id.profile_name);

        //control user's language preference
        Spinner mLanguage = (Spinner) findViewById(R.id.language);

        LanguageManger lang = new LanguageManger(this);
        String langChoice = mLanguage.getSelectedItem().toString();
        lang.updateResource("en");
        /*if(langChoice.equals("عربي")){
            lang.updateResource("ar");
        }
        else{
            lang.updateResource("en");
        }

        /*mLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        lang.updateResource("ar");
                        break;
                    case 1:
                        lang.updateResource("en");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        //get an instance from firebase
        fAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginEmail = email.getEditText().getText().toString().trim();
                String loginPass = password.getEditText().getText().toString().trim();

                if (!loginEmail.isEmpty() && !loginPass.isEmpty()) {
                    email.setError(null);
                    password.setError(null);
                    progress.setVisibility(View.VISIBLE);

                    fAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //email.setError(null);
                                //password.setError(null);
                                FirebaseUser user = fAuth.getCurrentUser();
                                if (user.isEmailVerified()) {
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(Login.this, verify_email.class));
                                    finish();
                                }
                            } else {
                                try {
                                    email.setError(null);
                                    password.setError(getString(R.string.wrong_email_or_password));
                                    progress.setVisibility(View.INVISIBLE);
                                    throw task.getException();
                                }
                                catch (Exception e) {
                                    Toasty.normal(getBaseContext(), "Login failed, please try again!",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            progress.setVisibility(View.INVISIBLE);
                        }
                    });
                }else {
                    if(loginEmail.isEmpty()) {
                        email.setError("Required!");
                    }else{
                        email.setError(null);
                    }
                    if(loginPass.isEmpty()){
                        password.setError("Required!");
                    }else{
                        password.setError(null);
                    }
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, signUp.class));
                finish();
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Forgot_password.class));
                finish();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }
    }
}