package com.example.barbershop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN_TIME_OUT=2000;
    private SharedPreferences mPreferences;
    private String sharedPrefFile = "login";
    AccessToken fb_token;
    String login_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        login_type = mPreferences.getString("login_type", "LoggedOut");

        fb_token = AccessToken.getCurrentAccessToken();


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // Fb login
                if (fb_token != null && !fb_token.isExpired()) {
                    Toast.makeText(MainActivity.this, login_type + " Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,FirstPage.class);
                    startActivity(intent);
                }

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