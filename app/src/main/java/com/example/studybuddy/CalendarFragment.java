package com.example.studybuddy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    CalendarView calendarView ;
    TextView meetingText ;
    Calendar calendar;

    RecyclerView recyclerView ;
    AdapterMeeting adapterMeeting;
    List<ModelMeeting> modelMeetingList ;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String myuid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendar);
        meetingText = view.findViewById(R.id.meetingText);
        calendar = Calendar.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.meeting_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        modelMeetingList = new ArrayList<>();
        loadMeetings();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                int count = 0;

                for (ModelMeeting meeting : modelMeetingList) {
                    String datetime = meeting.getDatetime();
                    if (datetime.startsWith(date)) {
                        count++;
                    }
                }

                Toast.makeText(getContext(),count + " meetings scheduled on this date", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadMeetings() {

        myuid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Group");
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot groupSnapshot) {
                final List<String> groupNames = new ArrayList<>();
                for (DataSnapshot groupData : groupSnapshot.getChildren()) {
                    String groupName = groupData.getKey();
                    DataSnapshot membersSnapshot = groupData.child("member");
                    if (membersSnapshot.hasChild(myuid)) {
                        groupNames.add(groupName);
                    }
                }

                //meetings belong to those groups
                databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelMeetingList.clear();
                        for(DataSnapshot meetingSnapshot : snapshot.getChildren()){
                            ModelMeeting modelMeeting = meetingSnapshot.getValue(ModelMeeting.class);
                            if (modelMeeting != null && groupNames.contains(modelMeeting.getGname())) {
                                modelMeetingList.add(modelMeeting);
                            }
                        }
                        adapterMeeting = new AdapterMeeting(getActivity(), modelMeetingList);
                        recyclerView.setAdapter(adapterMeeting);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setDate(int day , int month , int year) {
        calendar.set(Calendar.YEAR , year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH,day);

        long milli = calendar.getTimeInMillis() ;
        calendarView.setDate(milli);

    }

    public void getDate() {
        long date = calendarView.getDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        calendar.setTimeInMillis(date);

        String selected_date = simpleDateFormat.format(calendar.getTime());

        Toast.makeText(getContext(), selected_date, Toast.LENGTH_SHORT).show();
    }

}