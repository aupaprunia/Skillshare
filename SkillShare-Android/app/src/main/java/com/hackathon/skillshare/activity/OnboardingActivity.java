package com.hackathon.skillshare.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hackathon.skillshare.R;
import com.hackathon.skillshare.adapter.OnboardingAdapter;
import com.hackathon.skillshare.data.OBData;
import com.hackathon.skillshare.util.Constants;

import java.util.ArrayList;

public class OnboardingActivity extends AppCompatActivity {

    public ViewPager viewPager;
    private final ArrayList<OBData> itemList = new ArrayList();
    OnboardingAdapter onboardingAdapter;
    ImageView imgNext;
    TextView txtOBReady;
    RelativeLayout llObBottom;
    LinearLayout llObReg;
    int ctr=0;

    TextView txtSkip, txtReg, txtSignIn;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        viewPager = findViewById(R.id.viewPager);
        imgNext = findViewById(R.id.imgObNext);
        txtOBReady = findViewById(R.id.txtOBReady);
        llObBottom = findViewById(R.id.llOB);
        llObReg = findViewById(R.id.llObSignUp);
        txtSkip = findViewById(R.id.txtObSkip);
        txtSignIn = findViewById(R.id.txtOBSignIn);
        txtReg = findViewById(R.id.txtOBReg);
        sharedPreferences = getSharedPreferences(Constants.MY_PREF, MODE_PRIVATE);

        sharedPreferences.edit().putBoolean(Constants.SHOW_ONBOARDING, false).apply();

        imgNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_ob_next1));
        itemList.add(new OBData("List your skills","We know finding the right skill listing platform is quite a struggle, we’re here to help you with that",R.drawable.ic_ob1));
        itemList.add(new OBData("Find people that match your skill requirement","Sometimes, you just want to find a perfect match for your skill requirements, and we know that.", R.drawable.ic_ob2));
        itemList.add(new OBData("Skills you’ve never heard","Somedays, you want to discover skills that are unknown, we’ve people with a variety of skill set", R.drawable.ic_ob3));
        itemList.add(new OBData("Experience skill recommendation","We understand, skills are gained through experience and so is recommending them," +
                " unlock the experience ", R.drawable.ic_ob4));

        txtOBReady.setVisibility(View.GONE);

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ctr == 0){
                    imgNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_ob_next2));
                    viewPager.setCurrentItem(1);
                }else if(ctr == 1){
                    imgNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_ob_next3));
                    viewPager.setCurrentItem(2);
                }else if(ctr == 2){
                    viewPager.setCurrentItem(3);
                    llObBottom.startAnimation(AnimationUtils.loadAnimation(OnboardingActivity.this, R.anim.fadeout));
                    llObReg.startAnimation(AnimationUtils.loadAnimation(OnboardingActivity.this, R.anim.fadeout));
                    txtOBReady.startAnimation(AnimationUtils.loadAnimation(OnboardingActivity.this, R.anim.fadein));

                    llObBottom.setVisibility(View.INVISIBLE);
                    llObReg.setVisibility(View.INVISIBLE);
                    txtOBReady.setVisibility(View.VISIBLE);
                }
                ctr++;
            }
        });

        onboardingAdapter = new OnboardingAdapter(itemList);
        viewPager.beginFakeDrag();
        viewPager.setAdapter(onboardingAdapter);

        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OnboardingActivity.this, AuthActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
        });

        txtOBReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OnboardingActivity.this, AuthActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
        });

        txtReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OnboardingActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
        });

        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
        });
    }
}