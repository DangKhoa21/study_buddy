package com.example.studybuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.UserViewHolder>
{
    private final List<ModelUser> modelUsers;
    private final Context context;
    private final String myuid;

    public AdapterUser(Context context, List<ModelUser> modelUsers) {
        this.context = context;
        this.modelUsers = modelUsers;
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public AdapterUser.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.
                from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        return new AdapterUser.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.userName.setText(modelUsers.get(position).getName());
        holder.userStatus.setText(modelUsers.get(position).getOnlineStatus());

        holder.userImage.setVisibility(View.VISIBLE);
        try {
            Glide.with(context).load(modelUsers.get(position).getImage()).placeholder(R.drawable.profile)
                    .into(holder.userImage);
        } catch (Exception e) {
            holder.userImage.setImageResource(R.drawable.profile);
        }
    }

    @Override
    public int getItemCount() {
        return modelUsers.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private final TextView userName, userStatus;
        private  final ImageView userImage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userStatus = itemView.findViewById(R.id.user_status);
            userImage = itemView.findViewById(R.id.user_image);
        }
    }
}
