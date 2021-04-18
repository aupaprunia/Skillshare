package com.hackathon.skillshare.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hackathon.skillshare.R;
import com.hackathon.skillshare.data.ActivityData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    ArrayList<ActivityData> data;
    OnActChatClicked onActChatClicked;
    Context context;

    public ActivityAdapter(ArrayList<ActivityData> data, Context context, OnActChatClicked onActChatClicked) {
        this.data = data;
        this.context = context;
        this.onActChatClicked = onActChatClicked;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ActivityViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_activity, parent, false));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {

        ActivityData activityData = data.get(position);
        holder.txtTime.setText("DATE : " + activityData.getTimestamp());

        holder.txtSkill.setText("SKILL : " + activityData.getSkill());

        String sub_skill = "";
        for (int i = 0; i < activityData.getSub_skill_data().size(); i++) {
            if (i == activityData.getSub_skill_data().size() - 1) {
                sub_skill = sub_skill + activityData.getSub_skill_data().get(i).getValue();
            } else {
                sub_skill = sub_skill + activityData.getSub_skill_data().get(i).getValue() + " , ";
            }
        }
        holder.txtSubSkill.setText("SUB SKILL : " + sub_skill);
        holder.txtDes.setText("DESCRIPTION : " + activityData.getDescription());
        if (activityData.getType().equals("1")) {
            holder.txtMode.setText("MODE : Virtual");
        } else {
            holder.txtMode.setText("MODE : Offline");
        }

        holder.txtUserName.setText(data.get(position).getUsername());
        holder.txtContact.setText(data.get(position).getMobile());

        Picasso.get().load(data.get(position).getImage_link()).error(R.drawable.ic_user_150)
                .into(holder.imgMatch);

        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + data.get(position).getMobile()));
                context.startActivity(intent);
            }
        });

        holder.imgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onActChatClicked.onChatClicked(data.get(position));
            }
        });
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {

        TextView txtTime, txtSkill, txtSubSkill, txtDes, txtMode;

        TextView txtContact, txtUserName;
        ImageView imgMatch, imgCall, imgChat;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTime = itemView.findViewById(R.id.txtActivityTimeStamp);
            txtSkill = itemView.findViewById(R.id.txtActivitySkill);
            txtSubSkill = itemView.findViewById(R.id.txtActivitySubSkill);
            txtDes = itemView.findViewById(R.id.txtActivityDes);
            txtMode = itemView.findViewById(R.id.txtActivityMode);
            txtContact = itemView.findViewById(R.id.txtMatchContact);
            txtUserName = itemView.findViewById(R.id.txtMatchUsername);
            imgMatch = itemView.findViewById(R.id.imgMatch);
            imgCall = itemView.findViewById(R.id.imgMatchCall);
            imgChat = itemView.findViewById(R.id.imgMatchChat);
        }
    }

    public interface OnActChatClicked{
        void onChatClicked(ActivityData activityData);
    }

}
