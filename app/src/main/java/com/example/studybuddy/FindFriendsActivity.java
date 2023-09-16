package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private RecyclerView FindFriendRecyclerList;

    private DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super   .onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        FindFriendRecyclerList = (RecyclerView) findViewById(R.id.find_friend_recycler_list);
        FindFriendRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (Toolbar) findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(UserRef ,Contact.class)
                .build();
        FirebaseRecyclerAdapter<Contact,FindFriendViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contact, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, int position, @NonNull Contact model)
            {
                holder.userName.setText(model.getName());
                holder.userStatus.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile).into(holder.profileImage);

            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
              View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.user_display_layout,parent,false);
              FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
              return  viewHolder; 
            }
        };

        FindFriendRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }

    private static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {

        TextView userName ,userStatus;
        CircleImageView profileImage ;
        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);

        }
    }
}