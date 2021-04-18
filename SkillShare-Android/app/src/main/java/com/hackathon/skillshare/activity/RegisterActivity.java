package com.hackathon.skillshare.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hackathon.skillshare.R;
import com.hackathon.skillshare.data.UserData;
import com.hackathon.skillshare.dialog.DialogProgress;
import com.hackathon.skillshare.util.GoogleMap;
import com.hackathon.skillshare.util.ToastHelper;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    EditText etUsername, etPassword, etCPassword, etPhone, etOtp;
    TextView txtLogin, txtOtpDes;
    CardView btnSignUp, btnOtp;
    ImageView imgProfile;

    String imageLink = "";
    boolean isImageSelected = false;
    String verificationToken = "";
    final int CAMERA_REQ = 102;

    String username, mobile, password, cPassword;

    FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    DialogFragment progressDialogOTP;
    DialogFragment progressDialogSendingOTP;

    boolean isOTPVerified = false;

    TextView txtLocation;
    boolean isLocationSelected = false;
    double latitude = 0.0;
    double longitude = 0.0;
    String address = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etRegUsername);
        etPassword = findViewById(R.id.etRegPassword);
        etCPassword = findViewById(R.id.etRegCPassword);
        etPhone = findViewById(R.id.etRegPhone);
        txtLogin = findViewById(R.id.txtRegLoginIn);
        txtLocation = findViewById(R.id.txtRegLocation);
        imgProfile = findViewById(R.id.imgRegProfile);
        btnOtp = findViewById(R.id.btnSignUpOtp);
        btnSignUp = findViewById(R.id.btnSignUp);
        etOtp = findViewById(R.id.etSignUpOtp);
        txtOtpDes = findViewById(R.id.txtSignUpOtpDes);

        progressDialogOTP = new DialogProgress("Verifying OTP...");
        progressDialogSendingOTP = new DialogProgress("Sending OTP...");
        progressDialogOTP.setCancelable(false);
        progressDialogSendingOTP.setCancelable(false);
        firebaseAuth = FirebaseAuth.getInstance();

        btnOtp.setVisibility(View.GONE);
        txtOtpDes.setVisibility(View.GONE);
        etOtp.setVisibility(View.GONE);

        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus();
                Intent intent = new Intent(RegisterActivity.this, GoogleMap.class);
                startActivityForResult(intent, 101);
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus();
                isImageSelected = false;
                Intent intent = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, CAMERA_REQ);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                progressDialogSendingOTP.dismiss();
                new ToastHelper().makeToast(RegisterActivity.this, "OTP Verified!", Toast.LENGTH_LONG);
                onOTPVerified();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialogSendingOTP.dismiss();
                new ToastHelper().makeToast(RegisterActivity.this, "OTP Verification Failed!", Toast.LENGTH_LONG);
                Log.d("hello", "OTP Failed : " + e.getMessage() + " ");
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                progressDialogSendingOTP.dismiss();
                txtOtpDes.setVisibility(View.VISIBLE);
                etOtp.setVisibility(View.VISIBLE);
                btnSignUp.setVisibility(View.GONE);
                btnOtp.setVisibility(View.VISIBLE);
                txtOtpDes.setText("OTP have been sent to : " + mobile + ".Please enter OTP to complete verification.");

                new ToastHelper().makeToast(RegisterActivity.this, "OTP sent successfully!", Toast.LENGTH_LONG);
                verificationToken = verificationId;
            }
        };

        btnOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus();
                if (!isOTPVerified) {
                    String otp = etOtp.getText().toString().trim();
                    if (otp.isEmpty()) {
                        new ToastHelper().makeErrorToastForEditText(RegisterActivity.this, "Enter OTP!", "Enter OTP", Toast.LENGTH_LONG, etOtp);
                    } else if (otp.length() < 6) {
                        new ToastHelper().makeErrorToastForEditText(RegisterActivity.this, "Enter 6 digit OTP!", "Enter 6 digit OTP", Toast.LENGTH_LONG, etOtp);
                    } else {
                        progressDialogOTP.show(getSupportFragmentManager(), "Dialog Progress");
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationToken, otp);
                        SigninWithPhone(credential);
                    }
                } else {
                    username = etUsername.getText().toString().trim();
                    mobile = etPhone.getText().toString().trim();
                    password = etPassword.getText().toString().trim();
                    cPassword = etCPassword.getText().toString().trim();
                    onOTPVerified();
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus();
                username = etUsername.getText().toString().trim();
                mobile = etPhone.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                cPassword = etCPassword.getText().toString().trim();

                if (username.isEmpty() || mobile.isEmpty() || password.isEmpty() || cPassword.isEmpty()) {
                    new ToastHelper().makeToast(RegisterActivity.this, "All fields are mandatory!", Toast.LENGTH_LONG);
                } else if (mobile.length() < 10) {
                    new ToastHelper().makeErrorToastForEditText(RegisterActivity.this, "Invalid Contact Number!", "Enter 10 digit contact number", Toast.LENGTH_LONG, etPhone);
                } else if (!password.equals(cPassword)) {
                    new ToastHelper().makeErrorToastForEditText(RegisterActivity.this, "Password Mismatch!", "Password mismatch", Toast.LENGTH_LONG, etPassword);
                    new ToastHelper().makeErrorToastForEditText(RegisterActivity.this, "Password Mismatch!", "Password mismatch", Toast.LENGTH_LONG, etCPassword);
                } else if(!isLocationSelected){
                    new ToastHelper().makeErrorToast(RegisterActivity.this,"Select location!", Toast.LENGTH_LONG,txtLocation);
                } else {

                    if (!isOTPVerified) {
                        progressDialogSendingOTP.show(getSupportFragmentManager(), "Dialog Progress");
                        Log.d("hello", "Mobile : " + mobile);

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+91" + mobile,
                                60,
                                TimeUnit.SECONDS,
                                RegisterActivity.this,
                                mCallbacks);
                    } else {
                        onOTPVerified();
                    }
                }
            }
        });
    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialogOTP.dismiss();
                            new ToastHelper().makeToast(RegisterActivity.this, "OTP Verified!", Toast.LENGTH_LONG);
                            onOTPVerified();
                        } else {
                            progressDialogOTP.dismiss();
                            new ToastHelper().makeErrorToastForEditText(RegisterActivity.this, "Invalid OTP!", "Invalid otp", Toast.LENGTH_LONG, etOtp);
                        }
                    }
                });
    }

    public void onOTPVerified() {
        isOTPVerified = true;
        DialogFragment progressDialog = new DialogProgress("Creating account...");
        progressDialog.setCancelable(false);
        progressDialog.show(getSupportFragmentManager(), "Dialog Progress");

        if (!isImageSelected) {
            imageLink = "no_image";
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users/" + username);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    UserData userData = new UserData(username, mobile, password, imageLink,"1",null,String.valueOf(latitude),String.valueOf(longitude),address);
                    databaseReference.setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                new ToastHelper().makeToast(RegisterActivity.this, "Registration successful!", Toast.LENGTH_LONG);
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                finish();
                            } else {
                                progressDialog.dismiss();
                                new ToastHelper().makeToast(RegisterActivity.this, "Something went wrong! Please try again later.", Toast.LENGTH_LONG);
                            }
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    new ToastHelper().makeErrorToastForEditText(RegisterActivity.this, "User already exists!", "Username not available", Toast.LENGTH_LONG, etUsername);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                new ToastHelper().makeToast(RegisterActivity.this, "Something went wrong! Please try again later.", Toast.LENGTH_LONG);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQ && resultCode == RESULT_OK && data != null) {
            Log.d("hello", "Intent Proccess");
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String pictureFilePath = cursor.getString(columnIndex);
            cursor.close();
            File imgFile = new File(pictureFilePath);
            if (imgFile.exists()) {
                Log.d("hello", "Upload Proccess");
                imgProfile.setImageURI(Uri.fromFile(imgFile));
                uploadToCloud(selectedImage, System.currentTimeMillis() + "_" + imgFile.getName());
            }
        }

        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                isLocationSelected = true;
                latitude = Double.parseDouble(data.getBundleExtra("result").getString("lat"));
                longitude = Double.parseDouble(data.getBundleExtra("result").getString("long"));
                if (data.getBundleExtra("result") != null) {
                    txtLocation.setText(data.getBundleExtra("result").getString("area"));
                }
                txtLocation.setTextColor(getResources().getColor(R.color.black_02));
                address = data.getBundleExtra("result").getString("area");
            }
        }
    }

    private void uploadToCloud(Uri file, String filename) {
        Log.d("hello", "Upload Initiated");
        DialogFragment dialogProgress = new DialogProgress("Uploading Image...");
        dialogProgress.setCancelable(false);
        dialogProgress.show(getSupportFragmentManager(), "Dialog Progress");

        StorageReference storageRef = FirebaseStorage.getInstance().getReference(filename);

        storageRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                dialogProgress.dismiss();
                                new ToastHelper().makeToast(RegisterActivity.this, "Image Uploaded Successfully!", Toast.LENGTH_LONG);
                                imageLink = uri.toString();
                                isImageSelected = true;
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        dialogProgress.dismiss();
                        new ToastHelper().makeToast(RegisterActivity.this, "Something went wrong! Please try again later OR continue without image.", Toast.LENGTH_LONG);
                        Log.d("hello", "Exception : " + exception.getMessage());
                    }
                });
    }

    private void clearFocus() {
        etUsername.clearFocus();
        etPassword.clearFocus();
        etCPassword.clearFocus();
        etPhone.clearFocus();
    }
}