package edu.ucsd.cse110.secards.personalphisycaltracker2024;

public class ActivityStats {
    public String activity_type;
    public long total_duration; // Duration in milliseconds

    public ActivityStats(String activity_type, long total_duration){
        this.activity_type = activity_type;
        this.total_duration = total_duration;
    }
}
