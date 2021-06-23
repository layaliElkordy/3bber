package com.example.a3bbernav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
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
import com.google.android.material.textfield.TextInputLayout;
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

    TextInputLayout email, username, password, confirm;
    TextView gender_error, role_error, go2login, profile_Email, profile_name;
    RadioButton male, female;
    Spinner role;
    Button createAcc;
    ProgressBar progress;
    FirebaseAuth fAuth;


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

        gender_error = (TextView)findViewById(R.id.gender_error);
        role_error = (TextView)findViewById(R.id.role_error);
        go2login = (TextView)findViewById(R.id.go2login);

        profile_Email = (TextView)findViewById(R.id.profile_email);
        profile_name = (TextView)findViewById(R.id.profile_name);

        male = (RadioButton)findViewById(R.id.male);
        female = (RadioButton)findViewById(R.id.female);

        role = (Spinner)findViewById(R.id.rolesp);
        createAcc = (Button)findViewById(R.id.create);
        progress = (ProgressBar)findViewById(R.id.progressBar_signUp);

        //get an instance from firebase
        fAuth = FirebaseAuth.getInstance();

        //set the default language to English
        LanguageManger lang = new LanguageManger(this);
        lang.updateResource("en");


        //set action listener for Create Account button
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getEditText().getText().toString().trim();
                String Username = username.getEditText().getText().toString().trim();
                String Pass = password.getEditText().getText().toString().trim();
                String Passconfirm = confirm.getEditText().getText().toString().trim();
                String Role = role.getSelectedItem().toString();
                String Gender = getGender();

                //Check all conditions and errors
                if(checkAll(Email,Username,Pass,Passconfirm,Role) && Gender!="None") {

                    progress.setVisibility(View.VISIBLE);
                    fAuth.createUserWithEmailAndPassword(Email, Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser user = fAuth.getCurrentUser();

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(Username).build();
                                user.updateProfile(profileUpdates);

                                startActivity(new Intent(signUp.this, verify_email.class));
                                finish();

                            } else {
                                try {
                                    throw task.getException();
                                }
                                // if user enters wrong email format.
                                catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                    email.setError("Email is badly formatted");
                                } catch (FirebaseAuthUserCollisionException existEmail) {
                                    email.setError("This email is already registered");
                                } catch (Exception e) {
                                    Toasty.normal(getBaseContext(), "Account couldn't be created, try again please!",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            progress.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });


        go2login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signUp.this, Login.class));
            }
        });
    }

    //check if all required fields are filled
    boolean checkEmpty (String Email, String Name, String pswd, String  cpswd){
        boolean filled = true;
        if(Email.isEmpty()){
            email.setError("Required*");
            filled = false;
        } else{
            email.setError(null);
        }

        if(Name.isEmpty()){
            username.setError("Required*");
            filled = false;
        } else{
            username.setError(null);
        }

        if(pswd.isEmpty()){
            password.setError("Required*");
            filled = false;
        } else{
            password.setError(null);
        }

        if(cpswd.isEmpty()){
            confirm.setError("Required*");
            filled = false;
        } else{
            confirm.setError(null);
        }

        return filled;
    }

    //check password validity
    boolean checkPassword(String pswd) {

        boolean digits = false;
        boolean letters = false;
        boolean short_pass = false;
        boolean good_pass = true;

        if(pswd.isEmpty()){
        }
        else{
            password.setError(null);
            password.setHelperTextColor(ColorStateList.valueOf(Color.rgb(37,129,44)));
            //check password length
            if (pswd.length() < 8) {
                short_pass = true;
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

            if(!digits||!letters||short_pass){
                good_pass = false;
                password.setError(getString(R.string.pass_error));
            }
        }

        return good_pass;
    }

    //check is both entered passwords are matching
    boolean cofirmPassword(String pswd, String cpswd){
        if(pswd.equals(cpswd)){
            if(cpswd.isEmpty()){

            } else{
                confirm.setError(null);
            }
            return true;
        }
        else{
            confirm.setError(getString(R.string.confirm_error));
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
        boolean check_fill = checkEmpty(email, username, pass, confirmP);
        boolean check_pass = checkPassword(pass);
        boolean check_match = cofirmPassword(pass, confirmP);
        boolean check_role = checkRole(role);

        if(check_fill && check_pass && check_match && check_role){
            return true;
        }
        return false;
    }
}