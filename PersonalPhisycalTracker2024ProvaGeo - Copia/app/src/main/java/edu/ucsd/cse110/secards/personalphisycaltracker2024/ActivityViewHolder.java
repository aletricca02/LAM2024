package edu.ucsd.cse110.secards.personalphisycaltracker2024;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;

public class ActivityViewHolder extends RecyclerView.ViewHolder {
    TextView textViewActivityName, textViewActivityDuration, textViewSteps;

    public ActivityViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewActivityName = itemView.findViewById(R.id.textViewActivityName);
        textViewActivityDuration = itemView.findViewById(R.id.textViewActivityDuration);
        textViewSteps = itemView.findViewById(R.id.textViewActivityStep);
    }

    protected String formatDuration(long durationInSeconds) {
        int seconds = (int) (durationInSeconds/1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;
        minutes %= 60;
        seconds %= 60;

        return String.format("%d ore %d minuti %d secondi", hours, minutes, seconds);
    }

}