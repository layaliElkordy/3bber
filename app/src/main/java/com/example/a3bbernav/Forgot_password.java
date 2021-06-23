package com.example.a3bbernav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;

import static android.content.ContentValues.TAG;

public class Forgot_password extends AppCompatActivity {

    TextInputLayout email;
    Button send, back2login;
    ProgressBar progress;
    FirebaseAuth fAuth;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //set an icon for the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);

        //initialize controls
        email = findViewById(R.id.forgot_Email);
        send = findViewById(R.id.send);
        back2login = findViewById(R.id.back2login);
        progress = findViewById(R.id.progressBar_forgot);

        //get an instance from firebase
        fAuth = FirebaseAuth.getInstance();

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "Clicked");

                String Email = email.getEditText().getText().toString().trim();

                if (!Email.isEmpty()){

                    progress.setVisibility(View.VISIBLE);
                    email.setError(null);

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("forgot_email",Email);
                    editor.commit();

                    ActionCodeSettings actionCodeSettings =
                            ActionCodeSettings.newBuilder()
                                    // URL you want to redirect back to.
                                    // URL must be whitelisted in the Firebase Console.
                                    .setUrl("https://3bber.com/forgot")
                                    // This must be true
                                    .setHandleCodeInApp(true)
                                    .setAndroidPackageName(
                                            BuildConfig.APPLICATION_ID,
                                            false, /* installIfNotAvailable */
                                            "21"    /* minimumVersion */)
                                    .setDynamicLinkDomain("3bber.page.link")
                                    .build();

                    Log.i(TAG, "Built "+actionCodeSettings.toString());

                    fAuth.sendSignInLinkToEmail(Email, actionCodeSettings)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.i(TAG, "sent");
                                        Toasty.normal(getBaseContext(), getString(R.string.email_sent) + " " + Email,
                                                Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Log.i(TAG, task.getException().toString());
                                    }

                                    progress.setVisibility(View.INVISIBLE);
                                }
                            });
                }else{
                    email.setError("Required*");
                }
            }
        });

        back2login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Forgot_password.this, Login.class));
                finish();
            }
        });

    }
}