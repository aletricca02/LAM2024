package edu.ucsd.cse110.secards.personalphisycaltracker2024;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class ActivityViewHolder extends RecyclerView.ViewHolder {
    public final TextView activityName;
    public final TextView activityDuration;
    public final TextView activityDate;

    public ActivityViewHolder(View itemView) {
        super(itemView);
        activityName = itemView.findViewById(R.id.activity_name);
        activityDuration = itemView.findViewById(R.id.activity_duration);
        activityDate = itemView.findViewById(R.id.activity_date);
    }

}