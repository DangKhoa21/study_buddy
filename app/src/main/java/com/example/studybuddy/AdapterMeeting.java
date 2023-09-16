package com.example.studybuddy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AdapterMeeting extends RecyclerView.Adapter<AdapterMeeting.MeetingViewHolder>
{
    private final List<ModelMeeting> modelMeetings;
    private final Context context;
    private final String myuid;

    public AdapterMeeting(Context context, List<ModelMeeting> modelMeetings) {
        this.context = context;
        this.modelMeetings = modelMeetings;
        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public AdapterMeeting.MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.
                from(parent.getContext()).inflate(R.layout.row_meeting, parent, false);
        return new AdapterMeeting.MeetingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingViewHolder holder, int position) {
        final String datetime = modelMeetings.get(position).getDatetime();
        String date = datetime.substring(0, 10);
        String time = datetime.substring(11);
        holder.datetimeText.setText(String.format("%s Meet at %s", date, time));

        final String content = modelMeetings.get(position).getContent();
        final String gname = modelMeetings.get(position).getGname();
        holder.contentText.setText(content);
        holder.groupNameText.setText(gname);
    }

    @Override
    public int getItemCount() {
        return modelMeetings.size();
    }

    public static class MeetingViewHolder extends RecyclerView.ViewHolder {

        private final TextView datetimeText, contentText, groupNameText;

        public MeetingViewHolder(@NonNull View itemView) {
            super(itemView);
            datetimeText = itemView.findViewById(R.id.datetimeText);
            contentText = itemView.findViewById(R.id.contentText);
            groupNameText = itemView.findViewById(R.id.groupNameText);
        }
    }
}
