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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {

    CalendarView calendarView ;
    TextView textView ;
    Calendar calendar;
    FloatingActionButton addNoteBtn;
    RecyclerView    recyclerView ;

    NoteAdapter noteAdapter;

    Context mContext ;
    List<Note> note_list ;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalendarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_calendar,null);

        firebaseAuth = FirebaseAuth.getInstance();

        calendarView = root.findViewById(R.id.calendar);
        textView = root.findViewById(R.id.textview);

        calendar = Calendar.getInstance();

        addNoteBtn = root.findViewById(R.id.add_note);

        note_list = new ArrayList<>();


        recyclerView = root.findViewById(R.id.recyclerview_calendar);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar").child("my_notes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    Note noteList = snapshot1.getValue(Note.class);
                    note_list.add(noteList);
                    noteAdapter = new NoteAdapter(note_list ,  getActivity());
                    recyclerView.setAdapter(noteAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent ;
                intent = new Intent(getContext() , NoteActivity.class);
                startActivity(intent);
            }
        });

        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        textView.setText("Today is : " + currentDate);



        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month = month+1 ;
                String date = dayOfMonth +"/" + month   +"/" +year ;

                Log.d("date" , date);

                textView.setText("This Date is : " + date);
                Toast.makeText(getContext(), dayOfMonth + "/"+ month +"/" + year, Toast.LENGTH_SHORT).show();
            }
        });
        return root;
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