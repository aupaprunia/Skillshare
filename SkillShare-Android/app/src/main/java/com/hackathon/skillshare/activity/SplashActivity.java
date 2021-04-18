package com.hackathon.skillshare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.hackathon.skillshare.R;
import com.hackathon.skillshare.util.Constants;
import com.hackathon.skillshare.util.ToastHelper;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION};

    final int REQUEST_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkPermissionStatus();
    }

    private void checkPermissionStatus() {
        List<String> permissionsNeeded = new ArrayList<>();
        for (String isPermissionRequired : permissions) {
            int result = ActivityCompat.checkSelfPermission(SplashActivity.this, isPermissionRequired);
            if (result == PackageManager.PERMISSION_DENIED) {
                permissionsNeeded.add(isPermissionRequired);
            }
        }
        if (permissionsNeeded.isEmpty()) {
            performIntent();
        } else {
            ActivityCompat.requestPermissions(SplashActivity.this, permissionsNeeded.toArray(new String[permissionsNeeded.size()]), REQUEST_PERMISSION);
        }
    }

    private void performIntent() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedPreferences.getBoolean(Constants.SHOW_ONBOARDING, true)) {
                    Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    if (sharedPreferences.getBoolean(Constants.IS_LOGGED_IN, false)) {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        },1500);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            boolean isAllGranted = true;
            for (int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                performIntent();
            } else {
                new ToastHelper().makeToast(SplashActivity.this, "Permission Required.", Toast.LENGTH_LONG);
                finishAffinity();
            }
        }
    }
}