package edu.ucsd.cse110.secards.personalphisycaltracker2024;

import android.os.Handler;
import android.widget.TextView;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService; //modo piu elegante rispetto creare i thread manualmente

import edu.ucsd.cse110.secards.personalphisycaltracker2024.Date.AppDatabase;

public class TimerManager {
    private TextView timerTextView;
    private Handler handler;
    private long startTime;
    private boolean isRunning;
    private AppDatabase db;
    private String currentActivity;
    private ExecutorService executorService;

    public TimerManager(TextView timerTextView, AppDatabase db, String currentActivity, ExecutorService executorService) {
        this.timerTextView = timerTextView;
        this.handler = new Handler();
        this.isRunning = false;
        this.db = db;
        this.currentActivity = currentActivity;
        this.executorService = executorService;
    }

    public void startTimer() {
        if (!isRunning) {
            startTime = System.currentTimeMillis();
            handler.post(runnable);
            isRunning = true;
        }
    }

    public void stopTimer() {
        if (isRunning) {
            handler.removeCallbacks(runnable);
            long duration = System.currentTimeMillis() - startTime;
            timerTextView.setText("00:00:00");
            isRunning = false;
            saveActivityRecord(currentActivity, duration);
        }
    }

    private void saveActivityRecord(String activityName, long duration) {
        executorService.execute(() -> {
            ActivityRecord activityRecord = new ActivityRecord(activityName, duration);
            db.activityRecordDao().insert(activityRecord);
        });
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            minutes = minutes % 60;

            timerTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

            handler.postDelayed(this, 1000);
        }
    };
}