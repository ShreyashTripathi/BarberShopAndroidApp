package com.example.barbershop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.barbershop.ui.FirstPage.FirstPage;
import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final String USER_COLLECTION_PATH = "Users";
    private static final String EMAIL_FIELD = "email";
    private static int SPLASH_SCREEN_TIME_OUT=2000;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "login";
    private AccessToken fb_token;
    private String login_type,user_email,user_email_from_pref;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();
        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        login_type = mPreferences.getString("login_type", "LoggedOut");
        user_email_from_pref = mPreferences.getString("user_email","");

        //fb_token = AccessToken.getCurrentAccessToken();


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // Fb login
                /*if (fb_token != null && !fb_token.isExpired()) {
                    Toast.makeText(MainActivity.this, login_type + " Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,FirstPage.class);
                    startActivity(intent);
                }*/

                // Firebase Auth Login
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Toast.makeText(MainActivity.this, login_type + " Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, FirstPage.class));        // Logged in
                } else {
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));     //Not logged in
                }

                finish();
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }



}