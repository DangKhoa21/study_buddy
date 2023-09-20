package com.example.studybuddy;

import static android.view.View.GONE;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterGroup extends RecyclerView.Adapter<AdapterGroup.GroupViewHolder> {

    private final List<ModelGroup> modelGroups;
    private final Context context;
    private final String myuid;
    private final boolean isChat;
    private ItemClickListener itemClickListener;

    public AdapterGroup(Context context, List<ModelGroup> modelGroups) {
        this.context = context;
        this.modelGroups = modelGroups;
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        isChat = false;
    }

    public AdapterGroup(Context context, List<ModelGroup> modelGroups, ItemClickListener itemClickListener, boolean isChat) {
        this.context = context;
        this.modelGroups = modelGroups;
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.itemClickListener = itemClickListener;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public AdapterGroup.GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.
                from(parent.getContext()).inflate(R.layout.row_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        final String uid = modelGroups.get(position).getUid();
        final String uname = modelGroups.get(position).getUname();
        final String uemail = modelGroups.get(position).getUemail();
        final String udp = modelGroups.get(position).getUdp();

        final String gname = modelGroups.get(position).getGname();
        final String description = modelGroups.get(position).getDescription();
        final String gimage = modelGroups.get(position).getGimage();
        final String gtime = modelGroups.get(position).getGtime();

        final String pid = modelGroups.get(position).getGtime();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(gtime));
        String time_date = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        holder.group_name.setText(gname);
        holder.group_des.setText("");

        holder.group_image.setVisibility(View.VISIBLE);
        try {
            Glide.with(context).load(gimage).placeholder(R.drawable.photo).into(holder.group_image);
        } catch (Exception e) {
            holder.group_image.setImageResource(R.drawable.photo);
        }

        DatabaseReference groupMemberRef = FirebaseDatabase.getInstance().getReference().child("Group")
                .child(gname).child("member");

        // Check joined status for this item
        groupMemberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String childKey = childSnapshot.getKey();
                    if (childKey.equals(myuid)) {
                        holder.join_butt.setText("JOINED");
                        holder.join_butt.setBackgroundTintList((ContextCompat.getColorStateList(context, R.color.dark_grey)));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        if (isChat) {
            holder.join_butt.setVisibility(GONE);
            holder.creator_name.setVisibility(GONE);
            holder.creator_image.setVisibility(GONE);
            DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference()
                    .child("Group").child(gname).child("message");
            Query latestMessageQuery = messagesRef.orderByKey().limitToLast(1);
            latestMessageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String latestMessageId = null;
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        latestMessageId = childSnapshot.getKey();
                        break;
                    }

                    if (latestMessageId != null) {
                        DatabaseReference messageRef = messagesRef.child(latestMessageId);
                        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ModelMessage modelMessage = snapshot.getValue(ModelMessage.class);
                                String name = modelMessage.getName();
                                String message = modelMessage.getMessage();
                                String type = modelMessage.getType();

                                if (type.equals("text")) {
                                    String latest = name + ": " + message;
                                    if (latest.length() > 30) {
                                        latest = latest.substring(0, 29) + "...";
                                    }
                                    holder.group_des.setText(latest);
                                }
                                else if (type.equals("image")) {
                                    holder.group_des.setText(String.format("%s has sent an image", name));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            holder.itemView.setOnClickListener(view -> {
                itemClickListener.onItemClick(modelGroups.get(position));
            });
        }
        else {
            holder.group_des.setText(description);
            holder.creator_name.setText(uname);

            holder.creator_image.setVisibility(View.VISIBLE);
            try {
                Glide.with(context).load(udp).placeholder(R.drawable.profile).into(holder.creator_image);
            } catch (Exception e) {
                holder.creator_image.setImageResource(R.drawable.profile);
            }

            holder.join_butt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean joined = holder.join_butt.getText().equals("JOINED"); // Check if already joined

                    if (!joined) {
                        groupMemberRef.child(myuid).setValue("true");

                        holder.join_butt.setText("JOINED");
                        holder.join_butt.setBackgroundTintList((ContextCompat.getColorStateList(context, R.color.dark_grey)));
                        Toast.makeText(context, "Joined!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Already joined", Toast.LENGTH_SHORT).show();
                    }

                    notifyItemChanged(holder.getBindingAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return modelGroups.size();
    }

    public interface ItemClickListener {
        void onItemClick(ModelGroup modelGroup);
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {

        private final ImageView group_image, creator_image;
        private final TextView group_name, creator_name;
        private final TextView group_des;
        private final Button join_butt;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            group_image = itemView.findViewById(R.id.group_image);
            group_name = itemView.findViewById(R.id.group_name);
            group_des = itemView.findViewById(R.id.group_des);
            join_butt = itemView.findViewById(R.id.join_butt);
            creator_image = itemView.findViewById(R.id.creator_image);
            creator_name  = itemView.findViewById(R.id.creator_name);
        }
    }

}
