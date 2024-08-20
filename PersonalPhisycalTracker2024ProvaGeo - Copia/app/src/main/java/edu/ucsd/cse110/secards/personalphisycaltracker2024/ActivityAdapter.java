package edu.ucsd.cse110.secards.personalphisycaltracker2024;

import static androidx.core.util.TimeUtils.formatDuration;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityViewHolder> {
    private List<ActivityRecord> activityList;
    long duration = 0;

    public ActivityAdapter(List<ActivityRecord> activityList) {
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityRecord activity = activityList.get(position);
        holder.textViewActivityName.setText(activity.getActivityName());
        holder.textViewActivityDuration.setText(holder.formatDuration(activity.getDuration()));
        if(activity.getActivityName().equals("Camminare")) {
            holder.textViewSteps.setText(activity.getSteps());
        }else {
            holder.textViewSteps.setText("Passi non registrati per questa attività");
        }

    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

}