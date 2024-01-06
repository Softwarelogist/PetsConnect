package com.taas.petsconnect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class activity_splash extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000; // Splash screen duration in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Delay for the splash screen and start the main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(activity_splash.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close the splash activity to prevent going back to it
            }
        }, SPLASH_DURATION);
    }
}
