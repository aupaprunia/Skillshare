package com.hackathon.skillshare.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.skillshare.R;
import com.hackathon.skillshare.activity.HomeActivity;
import com.hackathon.skillshare.activity.LoginActivity;
import com.hackathon.skillshare.adapter.SkillAdapter;
import com.hackathon.skillshare.data.SkillData;
import com.hackathon.skillshare.data.UserData;
import com.hackathon.skillshare.util.Constants;
import com.hackathon.skillshare.util.ToastHelper;

import java.util.ArrayList;

public class DialogSkill extends DialogFragment implements SkillAdapter.OnSkillSelected {


    RecyclerView recyclerView;
    SkillAdapter skillAdapter;
    GridLayoutManager gridLayoutManager;
    ArrayList<SkillData> data = new ArrayList<>();
    ArrayList<SkillData> selectedSkill = new ArrayList<>();
    SharedPreferences sharedPreferences;

    ImageView imgNext;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null){
            int w = ViewGroup.LayoutParams.MATCH_PARENT;
            int h = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(w,h);
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_full_dialog));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog_skill, container, false);

        recyclerView = view.findViewById(R.id.recyclerSkill);
        imgNext = view.findViewById(R.id.imgSkillNext);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        sharedPreferences = getActivity().getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);

        imgNext.setVisibility(View.GONE);

        DialogProgress dialogProgress = new DialogProgress("One Moment, Please");
        dialogProgress.setCancelable(false);
        dialogProgress.show(getActivity().getSupportFragmentManager(),"Dialog Progress");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Skill");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        SkillData skillData = dataSnapshot.getValue(SkillData.class);
                        skillData.setSkillSelected(false);
                        data.add(skillData);
                    }

                skillAdapter = new SkillAdapter(data, getContext(), DialogSkill.this,1);
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

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogProgress dialogProgressSkill = new DialogProgress("Setting up your profile!");
                dialogProgressSkill.setCancelable(false);
                dialogProgressSkill.show(getActivity().getSupportFragmentManager(),"Dialog Skill");

                DatabaseReference databaseReferenceSkill = firebaseDatabase.getReference("Users/"+sharedPreferences.getString(Constants.USERNAME,"guest")+"/skillData");
                databaseReferenceSkill.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(SkillData skillData : selectedSkill){
                            databaseReferenceSkill.child(skillData.getSkill_name()).setValue(skillData);
                        }

                        DatabaseReference databaseReferenceFirstLogin = firebaseDatabase.getReference("Users/"+sharedPreferences.getString(Constants.USERNAME,"guest"));
                        databaseReferenceFirstLogin.child("first_login").setValue("0").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    dialogProgressSkill.dismiss();
                                    sharedPreferences.edit().putBoolean(Constants.SHOW_SKILL, false).apply();
                                    dismiss();
                                    ((HomeActivity) getActivity()).bottomNavigationView.setSelectedItemId(R.id.nav_profile);
                                }else{
                                    dialogProgressSkill.dismiss();
                                    databaseReferenceSkill.removeValue();
                                    new ToastHelper().makeToast(getActivity(),"Something went wrong! Please try again later.", Toast.LENGTH_LONG);
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialogProgressSkill.dismiss();
                        new ToastHelper().makeToast(getActivity(),"Something went wrong! Please try again later.", Toast.LENGTH_LONG);
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onSkillSelected(SkillData skillData) {
        skillAdapter.notifyDataSetChanged();
        if(!selectedSkill.contains(skillData)){
            selectedSkill.add(skillData);
        }else{
            selectedSkill.remove(skillData);
        }
        if(selectedSkill.size() > 0){
            imgNext.setVisibility(View.VISIBLE);
        }else{
            imgNext.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSkillDelete(SkillData skillData) {

    }
}