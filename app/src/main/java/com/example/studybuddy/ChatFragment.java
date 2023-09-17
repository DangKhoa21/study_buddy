package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    List<ModelGroup> group_list;
    AdapterGroup adapterGroup;

    private DatabaseReference GroupRef;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        GroupRef = FirebaseDatabase.getInstance().getReference().child("Group");

        InitializeField(view);

        RetrieveAndDisplayGroup()  ;

        return view ;
    }

    private void InitializeField(View view) {
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.group_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        group_list = new ArrayList<>();
    }

    private void RetrieveAndDisplayGroup() {

        final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                group_list.clear();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    ModelGroup modelGroup = groupSnapshot.getValue(ModelGroup.class);

                    DataSnapshot membersSnapshot = groupSnapshot.child("member");
                    for (DataSnapshot memberSnapshot : membersSnapshot.getChildren()) {
                        String memberUid = memberSnapshot.getKey();

                        if (memberUid.equals(currentUserId)) {
                            group_list.add(modelGroup);
                            break;
                        }
                    }
                }
                adapterGroup = new AdapterGroup(getActivity(), group_list, new AdapterGroup.ItemClickListener() {
                    @Override
                    public void onItemClick(ModelGroup modelGroup) {
                        String currentGroupName = modelGroup.getGname() ;
                        Intent groupChatIntent = new Intent(getActivity(), ChatActivity.class);
                        groupChatIntent.putExtra("groupName",currentGroupName);
                        startActivity(groupChatIntent);
                    }
                }, true);

                recyclerView.setAdapter(adapterGroup);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    // search for groups
    private void searchGroups(final String search) {
        final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                group_list.clear();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    ModelGroup modelGroup = groupSnapshot.getValue(ModelGroup.class);

                    DataSnapshot membersSnapshot = groupSnapshot.child("member");
                    for (DataSnapshot memberSnapshot : membersSnapshot.getChildren()) {
                        String memberUid = memberSnapshot.getKey();

                        if (memberUid.equals(currentUserId)) {
                            if (modelGroup.getGname().toLowerCase().contains(search.toLowerCase())) {
                                group_list.add(modelGroup);
                            }
                            break;
                        }
                    }
                }
                adapterGroup = new AdapterGroup(getActivity(), group_list, new AdapterGroup.ItemClickListener() {
                    @Override
                    public void onItemClick(ModelGroup modelGroup) {
                        String currentGroupName = modelGroup.getGname() ;
                        Intent groupChatIntent = new Intent(getActivity(), ChatActivity.class);
                        groupChatIntent.putExtra("groupName",currentGroupName);
                        startActivity(groupChatIntent);
                    }
                }, true);

                recyclerView.setAdapter(adapterGroup);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    searchGroups(query);
                } else {
                    RetrieveAndDisplayGroup();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    searchGroups(newText);
                } else {
                    RetrieveAndDisplayGroup();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

}