package edu.ucsd.cse110.secards.personalphisycaltracker2024;

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
        ActivityRecord record = activityList.get(position);
        holder.activityName.setText(record.getActivityName());
        duration = record.getDuration();
        holder.activityDuration.setText(""+duration);
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

   /* public List<ActivityRecord> getActivityListForDate(){
        List<ActivityRecord> filteredList = new ArrayList<>();

    }*/

    public void updateList(List<ActivityRecord> newList) {
        activityList = newList;
        notifyDataSetChanged();
    }
}