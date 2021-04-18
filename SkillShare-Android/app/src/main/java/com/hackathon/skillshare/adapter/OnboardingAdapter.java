package com.hackathon.skillshare.adapter;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.hackathon.skillshare.R;
import com.hackathon.skillshare.data.OBData;

import java.util.ArrayList;

public class OnboardingAdapter extends PagerAdapter {

    private ArrayList<OBData> itemList;
    public OnboardingAdapter(ArrayList<OBData> itemList){
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_onboarding,container,false);

        ImageView imageView = view.findViewById(R.id.imgOb);
        TextView txtDes, txtTitle;
        txtDes = view.findViewById(R.id.txtObDes);
        txtTitle = view.findViewById(R.id.txtObTitle);

        txtDes.setText(itemList.get(position).getDes());
        txtTitle.setText(itemList.get(position).getTitle());
        imageView.setImageResource(itemList.get(position).getImage());

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
