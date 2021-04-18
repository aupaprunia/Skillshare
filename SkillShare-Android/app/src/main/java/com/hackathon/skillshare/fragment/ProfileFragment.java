package com.hackathon.skillshare.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hackathon.skillshare.R;
import com.hackathon.skillshare.activity.HomeActivity;
import com.hackathon.skillshare.activity.LoginActivity;
import com.hackathon.skillshare.adapter.SkillAdapter;
import com.hackathon.skillshare.data.SkillData;
import com.hackathon.skillshare.data.UserData;
import com.hackathon.skillshare.dialog.DialogProgress;
import com.hackathon.skillshare.dialog.DialogSkill;
import com.hackathon.skillshare.util.Constants;
import com.hackathon.skillshare.util.GoogleMap;
import com.hackathon.skillshare.util.ToastHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements SkillAdapter.OnSkillSelected {

    TextView txtName, txtAddSkill;
    ImageView imgProfile, imgLogout;

    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;
    SkillAdapter skillAdapter;
    GridLayoutManager gridLayoutManager;
    ArrayList<SkillData> data = new ArrayList<>();

    TextView txtEditAddress, txtAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPreferences = getActivity().getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);
        txtName = view.findViewById(R.id.txtProfileUsername);
        imgProfile = view.findViewById(R.id.imgProfile);
        recyclerView = view.findViewById(R.id.recyclerProfileSkill);
        txtAddSkill = view.findViewById(R.id.txtProfileAddSkill);
        imgLogout = view.findViewById(R.id.imgProfileLogout);
        txtEditAddress = view.findViewById(R.id.txtProfileEditAddress);
        txtAddress = view.findViewById(R.id.txtProfileAddress);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);

        txtAddress.setText(sharedPreferences.getString(Constants.ADDRESS,"--"));
        txtName.setText("HELLO "+sharedPreferences.getString(Constants.USERNAME,"")+"!");
        Picasso.get().load(sharedPreferences.getString(Constants.IMAGE_LINK,"no_image"))
                .error(R.drawable.ic_person).into(imgProfile);

        txtEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GoogleMap.class);
                startActivityForResult(intent, 101);
            }
        });

        DialogProgress dialogProgress = new DialogProgress("One Moment, Please");
        dialogProgress.setCancelable(false);
        dialogProgress.show(getActivity().getSupportFragmentManager(),"Dialog Progress");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users/"+sharedPreferences.getString(Constants.USERNAME,"guest")+"/skillData");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SkillData skillData = dataSnapshot.getValue(SkillData.class);
                    skillData.setSkillSelected(false);
                    data.add(skillData);
                }

                skillAdapter = new SkillAdapter(data, getContext(), ProfileFragment.this,2);
                recyclerView.setAdapter(skillAdapter);
                recyclerView.setLayoutManager(gridLayoutManager);

                dialogProgress.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogProgress.dismiss();
                new ToastHelper().makeToast(getActivity(),"Something went wrong! Please try again later.", Toast.LENGTH_LONG);
            }
        });

        txtAddSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new DialogSkill();
                dialogFragment.show(getActivity().getSupportFragmentManager(),"Dialog Skill");
            }
        });

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(sharedPreferences.getString(Constants.USERNAME,"guest"))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                sharedPreferences.edit().clear().apply();
                sharedPreferences.edit().putBoolean(Constants.SHOW_ONBOARDING,false).apply();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                double latitude = Double.parseDouble(data.getBundleExtra("result").getString("lat"));
                double longitude = Double.parseDouble(data.getBundleExtra("result").getString("long"));

                DialogProgress dialogProgress = new DialogProgress("Updating address");
                dialogProgress.setCancelable(false);
                dialogProgress.show(getActivity().getSupportFragmentManager(),"Dialog Progress");

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference databaseReference = firebaseDatabase.getReference("Users/"+sharedPreferences.getString(Constants.USERNAME,"guest"));
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserData userData = snapshot.getValue(UserData.class);

                        userData.setLatitude(String.valueOf(latitude));
                        userData.setLongitude(String.valueOf(longitude));
                        userData.setAddress(data.getBundleExtra("result").getString("area"));

                        databaseReference.setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    dialogProgress.dismiss();
                                    if (data.getBundleExtra("result") != null) {
                                        txtAddress.setText(data.getBundleExtra("result").getString("area"));
                                    }
                                    sharedPreferences.edit().putString(Constants.ADDRESS,data.getBundleExtra("result").getString("area")).apply();
                                    sharedPreferences.edit().putString(Constants.LATITUDE, String.valueOf(latitude)).apply();
                                    sharedPreferences.edit().putString(Constants.LONGITUDE, String.valueOf(longitude)).apply();
                                    new ToastHelper().makeToast(getActivity(),"Address updated.", Toast.LENGTH_LONG);
                                }else{
                                    dialogProgress.dismiss();
                                    new ToastHelper().makeToast(getActivity(),"Something went wrong! Please try again later.", Toast.LENGTH_LONG);
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialogProgress.dismiss();
                        new ToastHelper().makeToast(getActivity(),"Something went wrong! Please try again later.", Toast.LENGTH_LONG);
                    }
                });
            }
        }
    }


    @Override
    public void onSkillSelected(SkillData skillData) {

    }

    @Override
    public void onSkillDelete(SkillData skillData) {
        if(data.size() > 1) {
            DialogProgress dialogProgress = new DialogProgress("Removing skill");
            dialogProgress.setCancelable(false);
            dialogProgress.show(getActivity().getSupportFragmentManager(), "Dialog Progress");

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("Users/" + sharedPreferences.getString(Constants.USERNAME, "guest") + "/skillData/" + skillData.getSkill_name());
            databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        dialogProgress.dismiss();
                        ((HomeActivity) getActivity()).bottomNavigationView.setSelectedItemId(R.id.nav_profile);
                    } else {
                        dialogProgress.dismiss();
                        new ToastHelper().makeToast(getActivity(), "Something went wrong! Please try again later.", Toast.LENGTH_LONG);
                    }
                }
            });
        }else{
            new ToastHelper().makeErrorToast(getActivity(), "At-least one skill needed!", Toast.LENGTH_LONG, recyclerView);
        }
    }
}