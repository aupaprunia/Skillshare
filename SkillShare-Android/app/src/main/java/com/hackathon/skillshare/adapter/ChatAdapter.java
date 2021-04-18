package com.hackathon.skillshare.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hackathon.skillshare.R;
import com.hackathon.skillshare.data.ChatData;
import com.hackathon.skillshare.data.MatchData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MatchViewHolder> {

    ArrayList<ChatData> data;
    Context context;
    OnChatSelected onChatSelected;

    public ChatAdapter(ArrayList<ChatData> data, Context context, OnChatSelected onChatSelected){
        this.data = data;
        this.context = context;
        this.onChatSelected = onChatSelected;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MatchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_chat,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {

        holder.txtUserName.setText(data.get(position).getUsername());
        holder.txtLast.setText(data.get(position).getMessage());

        Picasso.get().load(data.get(position).getImage_link()).error(R.drawable.ic_user_150)
                .into(holder.imgChat);

        holder.rlChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChatSelected.onChatClicked(data.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder{

        TextView txtLast, txtUserName;
        ImageView imgChat;
        RelativeLayout rlChat;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);

            txtLast = itemView.findViewById(R.id.txtChatLast);
            txtUserName = itemView.findViewById(R.id.txtChatUsername);
            imgChat = itemView.findViewById(R.id.imgChat);
            rlChat = itemView.findViewById(R.id.rlChat);

        }
    }

    public interface OnChatSelected{
        void onChatClicked(ChatData chatData);
    }

}
