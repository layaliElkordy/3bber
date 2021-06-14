package com.example.a3bbernav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
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
import com.google.firebase.auth.UserProfileChangeRequest;

import es.dmoral.toasty.Toasty;

public class signUp extends AppCompat {

    EditText email, username, password, confirm;
    TextView fields_error, email_error, username_error, pass_error, confirm_error, gender_error, role_error, go2login, profile_Email, profile_name;
    CheckBox show;
    RadioButton male, female;
    Spinner role;
    Button createAcc;
    ProgressBar progress;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //set an icon for the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);

        //initialize controls
        email = findViewById(R.id.user_email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirmpass);

        fields_error = findViewById(R.id.fields_Error);
        email_error = findViewById(R.id.email_Error);
        username_error = findViewById(R.id.name_Error);
        pass_error = findViewById(R.id.pass_error);
        confirm_error = findViewById(R.id.confirm_Error);
        gender_error = findViewById(R.id.gender_error);
        role_error = findViewById(R.id.role_error);
        go2login = findViewById(R.id.go2login);

        profile_Email = findViewById(R.id.profile_email);
        profile_name = findViewById(R.id.profile_name);

        male = findViewById(R.id.male);
        female = findViewById(R.id.female);

        show = findViewById(R.id.checkBox);
        role = findViewById(R.id.rolesp);
        createAcc = findViewById(R.id.create);
        progress = findViewById(R.id.progressBar);

        //set the default language to English
        LanguageManger lang = new LanguageManger(this);
        lang.updateResource("en");

        //Show or hide password text
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

        //set action listener for Create Account button
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                String Username = username.getText().toString();
                String Pass = password.getText().toString();
                String Passconfirm = confirm.getText().toString();
                String Role = role.getSelectedItem().toString();
                String Gender = getGender();

                //Check all conditions and errors
                if(checkAll(Email,Username,Pass,Passconfirm,Role) && Gender!="None") {

                    progress.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(Email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser user = mAuth.getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(Username).build();
                                user.updateProfile(profileUpdates);

                                //set user profile
                                profile_Email.setText(Email);
                                profile_name.setText(user.getDisplayName());

                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toasty.normal(getBaseContext(), "Email sent to " + Email,
                                                Toast.LENGTH_LONG).show();

                                        if (user.isEmailVerified()) {
                                            startActivity(new Intent(signUp.this, MainActivity.class));
                                            finish();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toasty.normal(getBaseContext(), "Email couldn't be sent:" + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                }
                                // if user enters wrong email format.
                                catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                    email_error.setText("Email is badly formatted");
                                    email_error.setVisibility(View.VISIBLE);
                                } catch (FirebaseAuthUserCollisionException existEmail) {
                                    email_error.setText("This email is already registered");
                                    email_error.setVisibility(View.VISIBLE);
                                } catch (Exception e) {
                                    Toasty.normal(getBaseContext(), "Account couldn't be created, try again please!",
                                            Toast.LENGTH_LONG).show();
                                    Log.d("TAG", "onComplete: " + e.getMessage());
                                }
                            }

                            progress.setVisibility(View.GONE);
                        }
                    });
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
                startActivity(new Intent(signUp.this, Login.class));
            }
        });
    }

    //check if all required fields are filled
    boolean checkFields (String email, String name, String pswd, String  cpswd){
        if (email.isEmpty() || name.isEmpty() || pswd.isEmpty() || cpswd.isEmpty()) {
            fields_error.setVisibility(View.VISIBLE);
            return false;
        }
        else{
            fields_error.setVisibility(View.INVISIBLE);
            return true;
        }
    }

    //check password validity
    boolean checkPassword(String pswd) {

        pass_error.setVisibility(View.INVISIBLE);

        boolean digits = false;
        boolean letters = false;
        boolean short_pass = false;
        boolean good_pass = true;
        String error = "";

        if(pswd.isEmpty()){
        }
        else{
            //check password length
            if (pswd.length() < 8) {
                short_pass = true;
                good_pass = false;
                error += getString(R.string.pass_error_8);
                pass_error.setVisibility(View.VISIBLE);
            }

            //check password if contains digits and letters
            for (int i = 0; i < pswd.length(); i++) {
                if (Character.isDigit(pswd.charAt(i))) {
                    digits = true;
                }
                if (Character.isLetter(pswd.charAt(i))) {
                    letters = true;
                }
            }

            if(!digits||!letters){
                good_pass = false;
                pass_error.setVisibility(View.VISIBLE);
                if(short_pass){
                    error += "\n";
                }
                error += getString(R.string.pass_Error_numchar);
            }
        }

        pass_error.setText(error);
        return good_pass;
    }

    //check is both entered passwords are matching
    boolean cofirmPassword(String pswd, String cpswd){
        if(pswd.equals(cpswd)){
            confirm_error.setVisibility(View.INVISIBLE);
            return true;
        }
        else{
            confirm_error.setVisibility(View.VISIBLE);
            return false;
        }
    }

    //check if the user did chose the account type
    boolean checkRole (String roleString){
        if(roleString.equals("User") || roleString.equals("Psychologist/Guardian")){
            role_error.setVisibility(View.INVISIBLE);
            return true;
        }
        else{
            role_error.setVisibility(View.VISIBLE);
            return false;
        }
    }

    //check gender choice
    public String getGender(){
        if(male.isChecked()){
            gender_error.setVisibility(View.INVISIBLE);
            return "Male";
        }
        else if(female.isChecked()){
            gender_error.setVisibility(View.INVISIBLE);
            return "Female";
        }
        else{
            gender_error.setVisibility(View.VISIBLE);
            return "None";
        }
    }

    //check all conditions are error free
    boolean checkAll(String email, String username, String pass, String confirmP, String role){
        boolean check_fill = checkFields(email, username, pass, confirmP);
        boolean check_pass = checkPassword(pass);
        boolean check_match = cofirmPassword(pass, confirmP);
        boolean check_role = checkRole(role);

        if(check_fill && check_pass && check_match && check_role){
            return true;
        }
        return false;
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