package com.example.a3bbernav;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class verify_email extends AppCompatActivity {

    Button verify;
    TextView sendAgain;
    CircleImageView next, back;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        //set an icon for the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);

        verify = (Button)findViewById(R.id.verify);
        sendAgain = (TextView)findViewById(R.id.send_again);
        next = (CircleImageView)findViewById(R.id.next);
        back = (CircleImageView)findViewById(R.id.back);
        fAuth = FirebaseAuth.getInstance();

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationEmail();
            }
        });

        sendAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationEmail();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = fAuth.getCurrentUser();
                user.reload();
                //wait for 500 milliseconds to reload the updates
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (user.isEmailVerified()) {
                            startActivity(new Intent(verify_email.this, MainActivity.class));
                            finish();
                        }else{
                            Toasty.normal(getBaseContext(), getString(R.string.email_verify_failed),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }, 500);

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(verify_email.this, Login.class));
                finish();
            }
        });
    }

    public void sendVerificationEmail(){
        fAuth.useAppLanguage();
        FirebaseUser user = fAuth.getCurrentUser();

        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toasty.normal(getBaseContext(), getString(R.string.email_sent) + " " + user.getEmail(),
                        Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.normal(getBaseContext(), getString(R.string.email_sent_error),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            startActivity(new Intent(verify_email.this, MainActivity.class));
            finish();
        }
    }
}