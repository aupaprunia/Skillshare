package com.hackathon.skillshare.fragment;

import android.app.AsyncNotedAppOp;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hackathon.skillshare.R;
import com.hackathon.skillshare.activity.HomeActivity;
import com.hackathon.skillshare.adapter.SkillAdapter;
import com.hackathon.skillshare.data.ActivityData;
import com.hackathon.skillshare.data.ListData;
import com.hackathon.skillshare.data.MatchData;
import com.hackathon.skillshare.data.SeekData;
import com.hackathon.skillshare.data.SkillData;
import com.hackathon.skillshare.data.SubSkillData;
import com.hackathon.skillshare.dialog.DialogProgress;
import com.hackathon.skillshare.network.APIClient;
import com.hackathon.skillshare.network.Api;
import com.hackathon.skillshare.util.Constants;
import com.hackathon.skillshare.util.GoogleMap;
import com.hackathon.skillshare.util.ToastHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


public class SeekFragment extends Fragment {

    EditText etSubSkill, etDescription;
    FlexboxLayout flSeek;
    TextView txtLocation;
    CheckBox cbLocation;
    Spinner spnSkill;
    SharedPreferences sharedPreferences;
    ArrayList<SkillData> spnSkillList = new ArrayList<>();
    ArrayList<String> spnSkillListName = new ArrayList<>();

    boolean isLocationRequired = false;

    boolean isLocationSelected = false;
    double latitude = 0.0;
    double longitude = 0.0;

    ScrollView svSeek;
    CardView cvSubmit;

    ArrayList<SubSkillData> subSkills = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seek, container, false);

        etSubSkill = view.findViewById(R.id.etSeekSubSkill);
        etDescription = view.findViewById(R.id.etSeekDescription);
        txtLocation = view.findViewById(R.id.txtSeekLocation);
        cbLocation = view.findViewById(R.id.cbSeekLocation);
        spnSkill = view.findViewById(R.id.spnSeekSkill);
        flSeek = view.findViewById(R.id.flSeek);
        svSeek = view.findViewById(R.id.svSeek);
        cvSubmit = view.findViewById(R.id.btnSeekSubmit);
        sharedPreferences = getActivity().getSharedPreferences(Constants.MY_PREF, Context.MODE_PRIVATE);

        txtLocation.setVisibility(View.GONE);
        svSeek.setVisibility(View.GONE);
        spnSkillListName.add("Select Skill");

        cbLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                clearFocus();
                isLocationRequired = b;
                if(isLocationRequired){
                    txtLocation.setVisibility(View.VISIBLE);
                }else{
                    txtLocation.setVisibility(View.GONE);
                }
            }
        });

        etSubSkill.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().endsWith(" ")){
                    SubSkillData subSkillData = new SubSkillData(charSequence.toString().trim());
                    addNewChip(charSequence.toString().trim(),flSeek, subSkillData);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        spnSkill.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                clearFocus();
                if(spnSkill.getSelectedItemPosition() == 0){
                    ((TextView)view).setTextColor(getResources().getColor(R.color.hint, getActivity().getTheme()));
                }else{
                    ((TextView)view).setTextColor(getResources().getColor(R.color.black_02, getActivity().getTheme()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus();
                Intent intent = new Intent(getActivity(), GoogleMap.class);
                startActivityForResult(intent, 101);
            }
        });

        cvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus();
                String description = etDescription.getText().toString().trim();

                if(spnSkill.getSelectedItemPosition() == 0){
                    new ToastHelper().makeErrorToast(getActivity(),"Select skill!", Toast.LENGTH_LONG, spnSkill);
                }else if(subSkills.isEmpty()){
                    new ToastHelper().makeErrorToast(getActivity(),"Enter sub skills!", Toast.LENGTH_LONG, flSeek);
                }else if(description.isEmpty()){
                    new ToastHelper().makeErrorToast(getActivity(),"Enter description!", Toast.LENGTH_LONG, etDescription);
                }else if(isLocationRequired && !isLocationSelected){
                    new ToastHelper().makeErrorToast(getActivity(),"Select location!", Toast.LENGTH_LONG,txtLocation);
                }else{
                    DialogProgress dialogProgress = new DialogProgress("Submitting request");
                    dialogProgress.setCancelable(false);
                    dialogProgress.show(getActivity().getSupportFragmentManager(), "Dialog Progress");

                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = null;

                    if(isLocationSelected){
                        databaseReference = firebaseDatabase.getReference("Seek/Location/"+spnSkill.getSelectedItem().toString().trim()+"/"+sharedPreferences.getString(Constants.USERNAME,"guest"));
                    }else{
                        databaseReference = firebaseDatabase.getReference("Seek/NoLocation/"+spnSkill.getSelectedItem().toString().trim()+"/"+sharedPreferences.getString(Constants.USERNAME,"guest"));
                    }

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String timeStamp = simpleDateFormat.format(calendar.getTime());

                    SeekData seekData = new SeekData(sharedPreferences.getString(Constants.USERNAME, "guest"),
                            sharedPreferences.getString(Constants.MOBILE,"guest"), sharedPreferences.getString(Constants.IMAGE_LINK,"NO_IMAGE"),
                            description,spnSkill.getSelectedItem().toString().trim(),String.valueOf(latitude),String.valueOf(longitude),timeStamp,subSkills);

                    databaseReference.child(timeStamp).setValue(seekData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                dialogProgress.dismiss();
                                new ToastHelper().makeToast(getActivity(),"Request Submitted!",Toast.LENGTH_LONG);
                                //((HomeActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.nav_seek);

                                FindSeekMatch findSeekMatch = new FindSeekMatch();
                                findSeekMatch.execute(seekData);

                            }else{
                                dialogProgress.dismiss();
                                new ToastHelper().makeToast(getActivity(),"Something went wrong! Please try again later.", Toast.LENGTH_LONG);
                            }
                        }
                    });

                }
            }
        });

        getSkillData();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                isLocationSelected = true;
                latitude = Double.parseDouble(data.getBundleExtra("result").getString("lat"));
                longitude = Double.parseDouble(data.getBundleExtra("result").getString("long"));
                if (data.getBundleExtra("result") != null) {
                    txtLocation.setText(data.getBundleExtra("result").getString("area"));
                }
                txtLocation.setTextColor(getResources().getColor(R.color.black_02));
            }
        }
    }

    private void getSkillData(){
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
                    spnSkillList.add(skillData);
                    spnSkillListName.add(skillData.getSkill_name());
                }
                spnSkill.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.custom_spinner, spnSkillListName));
                svSeek.setVisibility(View.VISIBLE);
                dialogProgress.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogProgress.dismiss();
                new ToastHelper().makeToast(getActivity(),"Something went wrong! Please try again later.", Toast.LENGTH_LONG);
            }
        });
    }

    private void clearFocus(){
        etSubSkill.clearFocus();
        etDescription.clearFocus();
    }

    private void addNewChip(String value, FlexboxLayout chipGroup, SubSkillData subSkillData) {
        Chip chip = new Chip(getActivity());
        chip.setText(value);
        chip.setCheckable(false);
        chip.setClickable(true);
        chip.setCloseIconEnabled(true);
        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(5, 0, 5, 0);
        chip.setLayoutParams(params);
        chipGroup.addView(chip, chipGroup.getChildCount()-1);
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFocus();
                chipGroup.removeView(chip);
                subSkills.remove(subSkillData);
            }
        });
        subSkills.add(subSkillData);
        etSubSkill.setText(null);
    }

    public static class FindSeekMatch extends AsyncTask<SeekData, SeekData,SeekData>{

        @Override
        protected SeekData doInBackground(SeekData... seekData) {
            SeekData data = seekData[0];

            ArrayList<MatchData> matchDataList = new ArrayList<>();

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = null;
            if (data.getLatitude().equals("0.0")) {
                databaseReference = firebaseDatabase.getReference("List/NoLocation/" + data.getSkill());
            } else {
                databaseReference = firebaseDatabase.getReference("List/Location/" + data.getSkill());
            }

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot seekSnapshot : dataSnapshot.getChildren()) {
                            ListData listData = seekSnapshot.getValue(ListData.class);
                            if (!listData.getUsername().equals(data.getUsername())) {
                                if (data.getLatitude().equals("0.0")) {
                                    MatchData matchData = new MatchData(listData.getUsername(), listData.getMobile(), listData.getImage_link(), listData.getLatitude(), listData.getLongitude(), listData.getTimestamp());
                                    matchDataList.add(matchData);

                                    SendSeekNotification sendSeekNotification = new SendSeekNotification();
                                    sendSeekNotification.execute(matchData);
                                } else {
                                    double lat1 = Double.parseDouble(data.getLatitude());
                                    double lon1 = Double.parseDouble(data.getLongitude());
                                    double lat2 = Double.parseDouble(listData.getLatitude());
                                    double lon2 = Double.parseDouble(listData.getLongitude());
                                    double theta = lon1 - lon2;
                                    double dist = Math.sin((lat1 * Math.PI / 180.0)) * Math.sin((lat2 * Math.PI / 180.0)) + Math.cos((lat1 * Math.PI / 180.0)) * Math.cos((lat2 * Math.PI / 180.0)) * Math.cos((theta * Math.PI / 180.0));
                                    dist = Math.acos(dist);
                                    dist = (dist * 180.0 / Math.PI);
                                    dist = dist * 60 * 1.1515 * 1.609344;

                                    double seekDistance = Double.parseDouble(listData.getDistance());
                                    if (dist <= seekDistance) {
                                        MatchData matchData = new MatchData(listData.getUsername(), listData.getMobile(), listData.getImage_link(), listData.getLatitude(), listData.getLongitude(), listData.getTimestamp());
                                        matchDataList.add(matchData);

                                        SendSeekNotification sendSeekNotification = new SendSeekNotification();
                                        sendSeekNotification.execute(matchData);
                                    }
                                }
                            }
                        }
                    }


                    DatabaseReference databaseRefFindPre = firebaseDatabase.getReference("Activity/1");
                    databaseRefFindPre.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                if(!dataSnapshot.getKey().equals(data.getUsername())) {
                                    for (DataSnapshot dataPre : dataSnapshot.getChildren()) {
                                        ActivityData activityData = dataPre.getValue(ActivityData.class);
                                        String type = "";
                                        if(data.getLatitude().equals("0.0")){
                                            type = "1";
                                        }else{
                                            type = "2";
                                        }
                                        if(activityData.getSkill().equals(data.getSkill()) && type.equals(activityData.getType())){
                                            if(type.equals("1")){
                                                MatchData matchData = new MatchData(data.getUsername(), data.getMobile(), data.getImage_link(), data.getLatitude(), data.getLongitude(), data.getTimestamp());

                                                ArrayList<MatchData> temp = activityData.getMatch_data();
                                                if(temp==null) {
                                                    temp = new ArrayList<>();
                                                }
                                                temp.add(matchData);
                                                DatabaseReference databaseRefUpdate = firebaseDatabase.getReference("Activity/1/"+activityData.getUsername()+"/"+activityData.getTimestamp());
                                                databaseRefUpdate.child("match_data").setValue(temp);

                                                matchData.setUsername(activityData.getUsername());

                                                SendSeekNotification sendSeekNotification = new SendSeekNotification();
                                                sendSeekNotification.execute(matchData);
                                            }else{
                                                double lat1 = Double.parseDouble(data.getLatitude());
                                                double lon1 = Double.parseDouble(data.getLongitude());
                                                double lat2 = Double.parseDouble(activityData.getLatitude());
                                                double lon2 = Double.parseDouble(activityData.getLongitude());
                                                double theta = lon1 - lon2;
                                                double dist = Math.sin((lat1 * Math.PI / 180.0)) * Math.sin((lat2 * Math.PI / 180.0)) + Math.cos((lat1 * Math.PI / 180.0)) * Math.cos((lat2 * Math.PI / 180.0)) * Math.cos((theta * Math.PI / 180.0));
                                                dist = Math.acos(dist);
                                                dist = (dist * 180.0 / Math.PI);
                                                dist = dist * 60 * 1.1515 * 1.609344;

                                                double seekDistance = Double.parseDouble(activityData.getDistance());
                                                if (dist <= seekDistance) {
                                                    MatchData matchData = new MatchData(data.getUsername(), data.getMobile(), data.getImage_link(), data.getLatitude(), data.getLongitude(), data.getTimestamp());

                                                    ArrayList<MatchData> temp = activityData.getMatch_data();
                                                    temp.add(matchData);

                                                    DatabaseReference databaseRefUpdate = firebaseDatabase.getReference("Activity/1/"+activityData.getUsername()+"/"+activityData.getTimestamp());
                                                    databaseRefUpdate.child("match_data").setValue(temp);

                                                    matchData.setUsername(activityData.getUsername());

                                                    SendSeekNotification sendSeekNotification = new SendSeekNotification();
                                                    sendSeekNotification.execute(matchData);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            DatabaseReference databaseRefActivity = firebaseDatabase.getReference("Activity/2/" + data.getUsername());
                            String type = "";
                            if(data.getLatitude().equals("0.0")){
                                type = "1";
                            }else{
                                type = "2";
                            }
                            if (matchDataList.size() > 0) {
                                ActivityData activity  = new ActivityData(type, data.getDescription(), data.getSkill(), data.getTimestamp(),
                                        data.getSub_skill_data(), matchDataList, data.getUsername(), data.getMobile(), data.getLatitude(), data.getLongitude(), data.getImage_link(),"0.0","2");;
                                databaseRefActivity.child(data.getTimestamp()).setValue(activity);
                            } else {
                                ActivityData activity  = new ActivityData(type, data.getDescription(), data.getSkill(), data.getTimestamp(),
                                        data.getSub_skill_data(), null, data.getUsername(), data.getMobile(), data.getLatitude(), data.getLongitude(), data.getImage_link(),"0.0","2");;
                                databaseRefActivity.child(data.getTimestamp()).setValue(activity);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            return null;
        }
    }

    public static class SendSeekNotification extends AsyncTask<MatchData, MatchData, MatchData>{

        @Override
        protected MatchData doInBackground(MatchData... matchData) {
            MatchData data = matchData[0];

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("to", "/topics/"+data.getUsername());
                JSONObject notificationBody = new JSONObject();
                notificationBody.put("title", "List Match Found!");
                notificationBody.put("body", "Hola, we have found match for your list request made on "+data.getTimestamp()+". Please check activity section for more details.");

                jsonObject.put("data", notificationBody);

                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

                Call<ResponseBody> call = APIClient.getClient().create(Api.class).sendNotification("key=" + "AAAA_0uhSWA:APA91bFIOxJgC41-C7tklsEmQnXZ6FCoq1c4y0Gf-OE7zWHMUS-GEWKhNyOGB08EmRML8dCgRHLmzKE24JtINqAnZOeFpFfaGIf4lu2K4qTyuKWd06RbzBxVonAV4nlP6kRw-G8RRzOI", body);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d("hello", "Res : " + response.code() + " " + response.message());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("hello", "Res Failed : " + t.getMessage());
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}