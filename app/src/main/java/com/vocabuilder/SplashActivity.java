package com.vocabuilder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 1500; // 1.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Use a handler to delay loading the main activity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Start main activity
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            
            // Apply a fade transition
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            
            // Close splash activity
            finish();
        }, SPLASH_DELAY);
    }
}