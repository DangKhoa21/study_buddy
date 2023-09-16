package com.example.studybuddy;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Utility
{
    static  void showToast(Context context , String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    static DatabaseReference getCollectionReferenceForNotes(){
        FirebaseUser currentUser  = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference CalendarRef ;
        return FirebaseDatabase.getInstance().getReference().child("Calendar").child(currentUser.getUid()).child("my_notes") ;


    }
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    static String timeStampToString(Timestamp timestamp) {
       return new SimpleDateFormat("dd/MM/yy").format(timestamp.toDate());
    }


}
