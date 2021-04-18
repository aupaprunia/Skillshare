package com.hackathon.skillshare.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hackathon.skillshare.R;
import com.hackathon.skillshare.dialog.DialogSkill;
import com.hackathon.skillshare.fragment.ActivityFragment;
import com.hackathon.skillshare.fragment.ChatFragment;
import com.hackathon.skillshare.fragment.ListFragment;
import com.hackathon.skillshare.fragment.ProfileFragment;
import com.hackathon.skillshare.fragment.SeekFragment;
import com.hackathon.skillshare.util.Constants;

public class HomeActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    public static BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);
        if(sharedPreferences.getBoolean(Constants.SHOW_SKILL,false)){
            DialogFragment dialogFragment = new DialogSkill();
            dialogFragment.setCancelable(false);
            dialogFragment.show(getSupportFragmentManager(),"Dialog Skill");
        }

        FirebaseMessaging.getInstance().subscribeToTopic(sharedPreferences.getString(Constants.USERNAME,"guest"))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

        bottomNavigationView = findViewById(R.id.bottomNavigation);

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        openProfile();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.nav_profile){
                    openProfile();
                }else if(item.getItemId() == R.id.nav_list){
                    openList();
                }else if(item.getItemId() == R.id.nav_chat){
                    openChat();
                }else{
                    openActivity();
                }

                return true;
            }
        });
    }

    private void openProfile(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                .replace(R.id.frameLayout, new ProfileFragment()).commit();
    }

    private void openList(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                .replace(R.id.frameLayout, new ListFragment()).commit();
    }

    /*private void openSeek(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                .replace(R.id.frameLayout, new SeekFragment()).commit();
    }*/

    private void openChat(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                .replace(R.id.frameLayout, new ChatFragment()).commit();
    }

    private void openActivity(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                .replace(R.id.frameLayout, new ActivityFragment()).commit();
    }
}