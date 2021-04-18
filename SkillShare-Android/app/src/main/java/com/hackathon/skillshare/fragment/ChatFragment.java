package com.hackathon.skillshare.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.skillshare.R;
import com.hackathon.skillshare.adapter.ActivityAdapter;
import com.hackathon.skillshare.adapter.ChatAdapter;
import com.hackathon.skillshare.data.ActivityData;
import com.hackathon.skillshare.data.ChatData;
import com.hackathon.skillshare.dialog.DialogChat;
import com.hackathon.skillshare.dialog.DialogProgress;
import com.hackathon.skillshare.util.Constants;
import com.hackathon.skillshare.util.ToastHelper;

import java.util.ArrayList;

public class ChatFragment extends Fragment implements ChatAdapter.OnChatSelected {

    ChatAdapter chatAdapter;
    TextView txtNo;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ArrayList<ChatData> data = new ArrayList<>();

    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        txtNo = view.findViewById(R.id.txtNoChat);
        recyclerView = view.findViewById(R.id.recyclerChat);
        layoutManager = new LinearLayoutManager(getActivity());
        sharedPreferences = getActivity().getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);

        txtNo.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        getData();

        return view;
    }

    private void getData(){
        DialogProgress dialogProgress = new DialogProgress("One Moment, Please");
        dialogProgress.setCancelable(false);
        dialogProgress.show(getActivity().getSupportFragmentManager(),"Dialog Progress");

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Chat/"+sharedPreferences.getString(Constants.USERNAME,"guest"));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    long ctr = 0;
                    for(DataSnapshot finalData : dataSnapshot.getChildren()) {
                        ctr++;
                        if(ctr == dataSnapshot.getChildrenCount()) {
                            ChatData chatData = finalData.getValue(ChatData.class);
                            data.add(chatData);
                        }
                    }
                }

                if(data.isEmpty()){
                    dialogProgress.dismiss();
                    txtNo.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else{
                    chatAdapter = new ChatAdapter(data, getActivity(), ChatFragment.this);
                    recyclerView.setAdapter(chatAdapter);
                    recyclerView.setLayoutManager(layoutManager);

                    dialogProgress.dismiss();
                    txtNo.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtNo.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                dialogProgress.dismiss();
                new ToastHelper().makeToast(getActivity(),"Something went wrong! Please try again later.", Toast.LENGTH_LONG);
            }
        });

    }

    @Override
    public void onChatClicked(ChatData chatData) {
        DialogFragment dialogFragment = new DialogChat(chatData);
        dialogFragment.show(getActivity().getSupportFragmentManager(),"Dialog Chat");
    }
}