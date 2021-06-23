package com.example.a3bbernav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import es.dmoral.toasty.Toasty;

import static android.content.ContentValues.TAG;

public class resetPassword extends AppCompatActivity {

    TextInputLayout password, confirm;
    Button update, skip;
    ProgressBar progress;
    FirebaseAuth fAuth;
    SharedPreferences sp;
    String emailLink;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //set an icon for the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);

        //initialize controls
        password = findViewById(R.id.new_password);
        confirm = findViewById(R.id.confirm_new_password);
        update = findViewById(R.id.update);
        skip = findViewById(R.id.skip);
        progress = findViewById(R.id.progressBar_reset);

        //get an instance from firebase
        fAuth = FirebaseAuth.getInstance();

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = password.getEditText().getText().toString().trim();

                if(checkFields()){
                    progress.setVisibility(View.VISIBLE);
                    verifyEmailLink(pass,true);
                }
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                verifyEmailLink(null,false);
            }
        });

    }

    //check and handle empty fields
    public boolean checkFields(){
        String pass = password.getEditText().getText().toString().trim();
        String confirmPass = confirm.getEditText().getText().toString().trim();

        boolean filled = true;

        if(pass.isEmpty()){
            password.setError("Required*");
            filled = false;
        } else{
            password.setError(null);
        }

        if(confirmPass.isEmpty()){
            confirm.setError("Required*");
            filled = false;
        } else{
            confirm.setError(null);
        }

        if(filled && checkPassword(pass,confirmPass) && cofirmPassword(pass,confirmPass)){
            return true;
        }else{
            return false;
        }
    }

    //check password validity
    boolean checkPassword(String pswd, String confirmPass) {

        boolean digits = false;
        boolean letters = false;
        boolean short_pass = false;
        boolean good_pass = true;

        if(confirmPass.isEmpty()){
            confirm.setError("Required*");
        }

        if(pswd.isEmpty()){
            password.setError("Required*");
            good_pass = false;
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
                confirm.setError("Required*");
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

    public void verifyEmailLink(String pass, boolean reset){

        Intent intent = getIntent();
        if(intent.getData()!=null){
            emailLink = intent.getData().toString();
        }

        // Confirm the link is a sign-in with email link.
        if (fAuth.isSignInWithEmailLink(emailLink)) {
            // Retrieve this from wherever you stored it
            email = sp.getString("forgot_email","");
            Log.i(TAG,"signed " + email);

            // The client SDK will parse the code from the link for you.
            fAuth.signInWithEmailLink(email, emailLink)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG,"Successfully signed in with email link!");
                                Toasty.normal(getBaseContext(), "signed successfully " + email,
                                        Toast.LENGTH_LONG).show();

                                if(reset){
                                    updatePassword(pass);
                                }

                                startActivity(new Intent(resetPassword.this, MainActivity.class));
                                finish();

                            } else {
                                Log.i(TAG,"Error signing in with email link", task.getException());

                            }
                            progress.setVisibility(View.INVISIBLE);
                        }
                    });
        }else{
            Log.i(TAG,"not signed");
        }

    }

    public void updatePassword(String pass){
        FirebaseUser user = fAuth.getCurrentUser();

        if(user!=null){
            user.updatePassword(pass)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG,"updated successfully");
                                Toasty.normal(getBaseContext(), "updated successfully: "+ user.getEmail(),
                                        Toast.LENGTH_LONG).show();

                            } else {
                                Log.i(TAG,"updating not successful");
                                progress.setVisibility(View.GONE);
                                Toasty.normal(getBaseContext(), "not successful",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }else{
            Log.i(TAG,"no user");
        }
    }
}