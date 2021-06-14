package com.example.a3bbernav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

public class Login extends AppCompat {

    Button login;
    TextView signup;
    EditText email, password;
    ProgressBar progress;
    FirebaseAuth mAuth;
    CheckBox show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //set an icon for the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);

        mAuth = FirebaseAuth.getInstance();

        login = findViewById(R.id.login);
        signup = findViewById(R.id.signUp);
        //email = (EditText)findViewById(R.id.email);
        //password = (EditText)findViewById(R.id.loginPassword);
        progress = findViewById(R.id.progressBar);
        show = findViewById(R.id.loginShow);
        //TextView useremail = findViewById(R.id.useremail);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginEmail = email.getText().toString();
                String loginPass = password.getText().toString();

                if(!loginEmail.isEmpty() && !loginPass.isEmpty()){
                    progress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail,loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(user.isEmailVerified()){
                                    //useremail.setText(user.getEmail());
                                    startActivity(new Intent(Login.this, MainActivity.class));
                                    finish();
                                }else{
                                    Toasty.normal(getBaseContext(), "Your email is not verified yet!",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                //Toasty.error(getBaseContext(), "Username or password is not correct",
                                        //Toast.LENGTH_LONG, true).show();
                                Toasty.normal(getBaseContext(), "Error"+task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                                progress.setVisibility(View.INVISIBLE);
                            }
                            progress.setVisibility(View.INVISIBLE);
                        }
                    });

                }
            }
        });


        show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(show.isChecked()){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, signUp.class));
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