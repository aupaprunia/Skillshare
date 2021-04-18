package com.hackathon.skillshare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.skillshare.R;
import com.hackathon.skillshare.data.UserData;
import com.hackathon.skillshare.dialog.DialogProgress;
import com.hackathon.skillshare.util.Constants;
import com.hackathon.skillshare.util.ToastHelper;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    TextView txtLogin, txtRegister;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);
        txtLogin = findViewById(R.id.txtLogin);
        txtRegister = findViewById(R.id.txtLoginReg);
        sharedPreferences = getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus();
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus();
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if(username.isEmpty() || password.isEmpty()){
                    new ToastHelper().makeToast(LoginActivity.this,"All fields are mandatory!", Toast.LENGTH_LONG);
                }else{
                    DialogProgress dialogProgress = new DialogProgress("Verifying details...");
                    dialogProgress.setCancelable(false);
                    dialogProgress.show(getSupportFragmentManager(),"Dialog Progress");

                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = firebaseDatabase.getReference("Users/"+username);
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.getChildrenCount() == 0){
                                dialogProgress.dismiss();
                                new ToastHelper().makeErrorToastForEditText(LoginActivity.this,"No user found!","No user found",Toast.LENGTH_LONG, etUsername);
                                etPassword.setText(null);
                                clearFocus();
                            }else{
                                UserData userData = snapshot.getValue(UserData.class);
                                if(userData.getPassword().equals(password)){
                                    dialogProgress.dismiss();
                                    new ToastHelper().makeToast(LoginActivity.this,"Login Successful!",Toast.LENGTH_LONG);

                                    sharedPreferences.edit().putString(Constants.USERNAME, userData.getUsername()).apply();
                                    sharedPreferences.edit().putString(Constants.MOBILE, userData.getMobile()).apply();
                                    sharedPreferences.edit().putString(Constants.IMAGE_LINK, userData.getImage_link()).apply();
                                    sharedPreferences.edit().putString(Constants.LATITUDE, userData.getLatitude()).apply();
                                    sharedPreferences.edit().putString(Constants.LONGITUDE, userData.getLongitude()).apply();
                                    sharedPreferences.edit().putString(Constants.ADDRESS, userData.getAddress()).apply();
                                    sharedPreferences.edit().putBoolean(Constants.IS_LOGGED_IN , true).apply();

                                    if(userData.getFirst_login().equals("1")) {
                                        sharedPreferences.edit().putBoolean(Constants.SHOW_SKILL, true).apply();
                                    }else{
                                        sharedPreferences.edit().putBoolean(Constants.SHOW_SKILL, false).apply();
                                    }

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                    finish();
                                }else{
                                    dialogProgress.dismiss();
                                    new ToastHelper().makeErrorToastForEditText(LoginActivity.this,"Invalid Password!","Invalid password",Toast.LENGTH_LONG, etPassword);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dialogProgress.dismiss();
                            new ToastHelper().makeToast(LoginActivity.this,"Something went wrong! Please try again later.", Toast.LENGTH_LONG);
                        }
                    });
                }
            }
        });

    }

    private void clearFocus(){
        etPassword.clearFocus();
        etUsername.clearFocus();
    }
}