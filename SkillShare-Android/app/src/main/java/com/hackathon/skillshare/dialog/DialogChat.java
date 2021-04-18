package com.hackathon.skillshare.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.skillshare.R;
import com.hackathon.skillshare.adapter.ChatAdapter;
import com.hackathon.skillshare.data.BaseMessage;
import com.hackathon.skillshare.data.ChatData;
import com.hackathon.skillshare.fragment.ChatFragment;
import com.hackathon.skillshare.util.Constants;
import com.hackathon.skillshare.util.ExampleChatController;
import com.hackathon.skillshare.util.ToastHelper;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DialogChat extends DialogFragment {


    ImageView imgChat;
    TextView txtName, txtContact;
    EditText etMessage;
    Button btnSend;

    ChatData chatData;
    SharedPreferences sharedPreferences;

    ArrayList<ChatData> data = new ArrayList<>();

    private ExampleChatController mExampleChatController;


    public DialogChat(ChatData chatData) {
        this.chatData = chatData;
    }

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
        View view = inflater.inflate(R.layout.fragment_dialog_chat, container, false);

        txtName = view.findViewById(R.id.txtDChatUsername);
        txtContact = view.findViewById(R.id.txtDChatContact);
        imgChat = view.findViewById(R.id.imgDChat);
        etMessage = view.findViewById(R.id.edit_gchat_message);
        btnSend = view.findViewById(R.id.button_gchat_send);
        sharedPreferences = getActivity().getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);

        mExampleChatController = new ExampleChatController(getActivity(), (ListView) view.findViewById(R.id.ChatListView), R.layout.chatline, R.id.chat_line_textview, R.id.chat_line_timeview);


        txtName.setText(chatData.getUsername());
        txtContact.setText(chatData.getContact());
        Picasso.get().load(chatData.getImage_link()).error(R.drawable.ic_user_150)
                .into(imgChat);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMessage.getText().toString().trim();
                if(!message.isEmpty()){
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String timestamp = simpleDateFormat.format(calendar.getTime());

                    BaseMessage baseMessage = new BaseMessage(message,sharedPreferences.getString(Constants.USERNAME,"--"),
                           sharedPreferences.getString(Constants.IMAGE_LINK,"no_image"), sharedPreferences.getString(Constants.MOBILE,"--"),timestamp);
                    ChatData chatDataCurrent = new ChatData(chatData.getUsername(), chatData.getImage_link(), message,chatData.getContact(), baseMessage);

                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReferenceSender = firebaseDatabase.getReference("Chat/"+sharedPreferences.getString(Constants.USERNAME,"guest")+"/"+chatData.getUsername());
                    databaseReferenceSender.push().setValue(chatDataCurrent);

                    DatabaseReference databaseReferenceReceiver = firebaseDatabase.getReference("Chat/"+chatData.getUsername()+"/"+sharedPreferences.getString(Constants.USERNAME,"guest"));
                    databaseReferenceReceiver.push().setValue(chatDataCurrent);

                    if (mExampleChatController != null) {
                        mExampleChatController.add(message);
                        mExampleChatController.show();
                    }
                }
            }
        });

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
                    for(DataSnapshot finalData : dataSnapshot.getChildren()) {
                            ChatData chatData = finalData.getValue(ChatData.class);
                            data.add(chatData);
                    }
                }

                if(!data.isEmpty()){

                }

                dialogProgress.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogProgress.dismiss();
                new ToastHelper().makeToast(getActivity(),"Something went wrong! Please try again later.", Toast.LENGTH_LONG);
            }
        });

    }
}