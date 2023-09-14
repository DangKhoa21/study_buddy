package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChatFragment extends Fragment {

    private View groupFragmentView ;
    private ListView list_View;
    private ArrayAdapter<String>arrayAdapter ;
    private final ArrayList<String>list_of_group = new ArrayList<>();

    private DatabaseReference GroupRef;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        groupFragmentView =  inflater.inflate(R.layout.fragment_chat, container, false);

        GroupRef = FirebaseDatabase.getInstance().getReference().child("Group");

        InitializeField();

        RetrieveAndDisplayGroup()  ;

        list_View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String currentGroupName = parent.getItemAtPosition(position).toString() ;

                Intent groupChatIntent = new Intent(getContext(), ChatActivity.class);

                groupChatIntent.putExtra("groupName",currentGroupName);
                startActivity(groupChatIntent);
            }
        });

        return groupFragmentView ;
    }



    private void InitializeField() {
        list_View = groupFragmentView.findViewById(R.id.list_view_chat);
        list_View.setAdapter(arrayAdapter);
    }

    private void RetrieveAndDisplayGroup() {

        final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list_of_group.clear();

                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    String groupName = groupSnapshot.getKey();

                    DataSnapshot membersSnapshot = groupSnapshot.child("member");
                    for (DataSnapshot memberSnapshot : membersSnapshot.getChildren()) {
                        String memberUid = memberSnapshot.getKey();

                        if (memberUid.equals(currentUserId)) {
                            list_of_group.add(groupName);
                            break;
                        }
                    }
                }

                arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list_of_group);
                list_View.setAdapter(arrayAdapter);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}