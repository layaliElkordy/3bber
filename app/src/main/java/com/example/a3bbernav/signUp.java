package com.example.a3bbernav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

import es.dmoral.toasty.Toasty;

public class signUp extends AppCompatActivity {

    Button go2login, register;
    EditText email, phone, password, confirm;
    TextView useremail;
    ProgressBar progress;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    CheckBox show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        go2login = findViewById(R.id.go2login);
        register = findViewById(R.id.register);
        email = findViewById(R.id.emailUp);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirmpass);
        progress = findViewById(R.id.progressBar);
        show = findViewById(R.id.checkBox);
        useremail = findViewById(R.id.useremail);

        show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(show.isChecked()){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                String Phone = phone.getText().toString();
                String Pass = password.getText().toString();
                String Passconfirm = confirm.getText().toString();

                if(!Email.isEmpty() && !Phone.isEmpty() && !Pass.isEmpty() && !Passconfirm.isEmpty()){

                    if(Pass.equals(Passconfirm)){

                        progress.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toasty.normal(getBaseContext(), "Email sent to "+Email,
                                                    Toast.LENGTH_LONG).show();

                                            /*if(user.isEmailVerified()){
                                                startActivity(new Intent(signUp.this, MainActivity.class));
                                                finish();
                                            }*/
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toasty.normal(getBaseContext(), "Email couldn't be sent:"+e.getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }else{
                                    try
                                    {
                                        throw task.getException();
                                    }
                                    // if user enters wrong email.
                                    catch (FirebaseAuthWeakPasswordException weakPassword)
                                    {
                                        Log.d("TAG", "onComplete: weak_password");
                                        Toasty.normal(getBaseContext(), "weak password",
                                                Toast.LENGTH_LONG).show();

                                    }
                                    // if user enters wrong password.
                                    catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                                    {
                                        Log.d("TAG", "onComplete: malformed_email");
                                        Toasty.normal(getBaseContext(), "Wrong formatted email",
                                                Toast.LENGTH_LONG).show();

                                    }
                                    catch (FirebaseAuthUserCollisionException existEmail)
                                    {
                                        Log.d("TAG", "onComplete: exist_email");
                                        Toasty.normal(getBaseContext(), "You already have an account with this email",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    catch (Exception e)
                                    {
                                        Toasty.normal(getBaseContext(), "Email entered is not exist",
                                                Toast.LENGTH_LONG).show();
                                        Log.d("TAG", "onComplete: " + e.getMessage());
                                    }
                                }

                                progress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }else{
                        Toasty.error(getBaseContext(), "Passwords are not matching",
                                Toast.LENGTH_LONG, true).show();
                    }
                }else{
                    Toasty.error(getBaseContext(), "Complete the fields first",
                            Toast.LENGTH_LONG, true).show();
                }
            }
        });


       /* FirebaseUser user = mAuth.getCurrentUser();
        if(user.isEmailVerified()){
            startActivity(new Intent(signUp.this, MainActivity.class));
            finish();
        }*/

        go2login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                String Pass = password.getText().toString();
                //mAuth.signInWithEmailAndPassword(Email,Pass);
                FirebaseUser user = mAuth.getCurrentUser();
                if(user.isEmailVerified()) {
                    startActivity(new Intent(signUp.this, Login.class));
                    finish();
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(signUp.this, MainActivity.class));
            finish();
        }
    }
}