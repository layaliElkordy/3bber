package com.example.a3bbernav;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import es.dmoral.toasty.Toasty;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    FirebaseAuth fAuth;

    TextView profile_Email;
    TextView profile_name;

    SharedPreferences sp;
    String emailLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        profile_Email = (TextView)findViewById(R.id.profile_email);
        profile_name = (TextView)findViewById(R.id.profile_name);

        //get an instance from firebase
        fAuth = FirebaseAuth.getInstance();

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        //set language to arabic

        // fAuth.setLanguageCode("ar");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            if(fAuth.getCurrentUser()!=null){
                fAuth.signOut();
                Toasty.normal(getBaseContext(), "Logout",
                        Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }else{
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
            return true;
        });
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_history, R.id.nav_report, R.id.nav_about, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        /*fAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        if(intent.getData()!=null){
            emailLink = intent.getData().toString();
        }

        // Confirm the link is a sign-in with email link.
        if (fAuth.isSignInWithEmailLink(emailLink)) {
            // Retrieve this from wherever you stored it
            String email = sp.getString("forgot_email","");
            Log.i(TAG, "signed");

            // The client SDK will parse the code from the link for you.
            fAuth.signInWithEmailLink(email, emailLink)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "Successfully signed in with email link!");
                                Toasty.normal(getBaseContext(), "signed successfully",
                                        Toast.LENGTH_LONG).show();
                                if (task.getResult() != null && task.getResult().getUser() != null) {

                                    FirebaseUser user = task.getResult().getUser();

                                    if (!TextUtils.isEmpty(user.getUid()))
                                        Log.i(TAG, "signInWithCredential: " + user.getUid());
                                }

                            } else {
                                Log.e(TAG, "Error signing in with email link", task.getException());
                            }
                        }
                    });
        }*/


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Log.i("SignActivity","We have a dynamic link: main");
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        if(deepLink!=null){
                            Log.i("SignActivity","Here is the dynamic link"+deepLink.toString());/////////
                            //startActivity(new Intent(MainActivity.this, resetPassword.class));
                            //finish();
                        }

                        //String email = deepLink.getQueryParameter("email");
                        //String password = deepLink.getQueryParameter("password");

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.normal(getBaseContext(), e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        }else if(!currentUser.isEmailVerified()){
            startActivity(new Intent(MainActivity.this, verify_email.class));
            finish();
        }
        else{
            //profile_Email.setText(currentUser.getEmail());
            //profile_name.setText(currentUser.getDisplayName());
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                Toasty.info(getBaseContext(), "Home",
                        Toast.LENGTH_SHORT, true).show();
                Log.d("Bayan", "home");
                break;
            case R.id.nav_history:
                Toasty.info(getBaseContext(), "History",
                        Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.nav_report:
                Toasty.info(getBaseContext(), "Report",
                        Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.nav_about:
                Toasty.info(getBaseContext(), "About",
                        Toast.LENGTH_SHORT, true).show();
                break;
            case R.id.nav_logout:
                Toasty.info(getBaseContext(), "Logout",
                        Toast.LENGTH_SHORT, true).show();
                //fAuth.signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
                return true;
        }

        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}