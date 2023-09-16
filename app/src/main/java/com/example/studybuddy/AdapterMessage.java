package com.example.studybuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AdapterMessage extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ModelMessage> modelMessages;
    private final Context context;
    private final String myuid;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public AdapterMessage(Context context, List<ModelMessage> modelMessages) {
        this.modelMessages = modelMessages;
        this.context = context;
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.
                    from(parent.getContext()).inflate(R.layout.row_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        }
        else {
            View view = LayoutInflater.
                    from(parent.getContext()).inflate(R.layout.row_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final String name = modelMessages.get(position).getName();
        final String message = modelMessages.get(position).getMessage();
        final String date = modelMessages.get(position).getDate();
        final String time = modelMessages.get(position).getTime();
        final String uid = modelMessages.get(position).getUid();
        final String uimage = modelMessages.get(position).getUimage();
        final String messageID = modelMessages.get(position).getMessageID();
        final String type = modelMessages.get(position).getType();
        
        if (type.equals("text")) {
            if(getItemViewType(position) == VIEW_TYPE_SENT) {
                ((SentMessageViewHolder) holder).messageText.setText(message);
                ((SentMessageViewHolder) holder).dateText.setText(String.format("%s %s", date, time));
            }
            else {
                ((ReceivedMessageViewHolder) holder).nameText.setText(name);
                ((ReceivedMessageViewHolder) holder).messageText.setText(message);
                ((ReceivedMessageViewHolder) holder).dateText.setText(String.format("%s %s", date, time));

                ((ReceivedMessageViewHolder) holder).profileImage.setVisibility(View.VISIBLE);
                try {
                    Glide.with(context).load(uimage).placeholder(R.drawable.profile)
                            .into(((ReceivedMessageViewHolder) holder).profileImage);
                } catch (Exception e) {
                    ((ReceivedMessageViewHolder) holder).profileImage.setImageResource(R.drawable.profile);
                }
            }
        } else if (type.equals("image")) {
            if(getItemViewType(position) == VIEW_TYPE_SENT) {
                ((SentMessageViewHolder) holder).messageText.setVisibility(View.GONE);
                ((SentMessageViewHolder) holder).dateText.setText(String.format("%s %s", date, time));

                ((SentMessageViewHolder) holder).uploadImage.setVisibility(View.VISIBLE);
                try {
                    Glide.with(context).load(message).placeholder(R.drawable.photo)
                            .into(((SentMessageViewHolder) holder).uploadImage);
                } catch (Exception e) {
                    ((SentMessageViewHolder) holder).uploadImage.setImageResource(R.drawable.photo);
                }
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ((SentMessageViewHolder) holder).dateText.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, ((SentMessageViewHolder) holder).uploadImage.getId());
                ((SentMessageViewHolder) holder).dateText.setLayoutParams(params);
            }
            else {
                ((ReceivedMessageViewHolder) holder).nameText.setText(name);
                ((ReceivedMessageViewHolder) holder).messageText.setVisibility(View.GONE);
                ((ReceivedMessageViewHolder) holder).dateText.setText(String.format("%s %s", date, time));

                ((ReceivedMessageViewHolder) holder).profileImage.setVisibility(View.VISIBLE);
                try {
                    Glide.with(context).load(uimage).placeholder(R.drawable.profile)
                            .into(((ReceivedMessageViewHolder) holder).profileImage);
                } catch (Exception e) {
                    ((ReceivedMessageViewHolder) holder).profileImage.setImageResource(R.drawable.profile);
                }

                ((ReceivedMessageViewHolder) holder).uploadImage.setVisibility(View.VISIBLE);
                try {
                    Glide.with(context).load(message).placeholder(R.drawable.photo)
                            .into(((ReceivedMessageViewHolder) holder).uploadImage);
                } catch (Exception e) {
                    ((ReceivedMessageViewHolder) holder).uploadImage.setImageResource(R.drawable.photo);
                }
                
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ((ReceivedMessageViewHolder) holder).dateText.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, ((ReceivedMessageViewHolder) holder).uploadImage.getId());
                ((ReceivedMessageViewHolder) holder).dateText.setLayoutParams(params);

                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) ((ReceivedMessageViewHolder) holder).profileImage.getLayoutParams();
                params1.addRule(RelativeLayout.ALIGN_BOTTOM, ((ReceivedMessageViewHolder) holder).uploadImage.getId());
                ((ReceivedMessageViewHolder) holder).profileImage.setLayoutParams(params1);
            }
        }
    }

    @Override
    public int getItemCount() {
        return modelMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(modelMessages.get(position).getUid().equals(myuid))
            return VIEW_TYPE_SENT;
        else
            return VIEW_TYPE_RECEIVED;
    }

    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ImageView uploadImage;
        private final TextView messageText;
        private final TextView dateText;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            uploadImage = itemView.findViewById(R.id.uploadImage);
            messageText = itemView.findViewById(R.id.messageText);
            dateText = itemView.findViewById(R.id.dateText);
        }
    }

    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private final ImageView uploadImage;
        private final ImageView profileImage;
        private final TextView nameText;
        private final TextView messageText;
        private final TextView dateText;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            uploadImage = itemView.findViewById(R.id.uploadImage);
            profileImage = itemView.findViewById(R.id.profileImage);
            nameText = itemView.findViewById(R.id.nameText);
            messageText = itemView.findViewById(R.id.messageText);
            dateText = itemView.findViewById(R.id.dateText);
        }
    }

}

