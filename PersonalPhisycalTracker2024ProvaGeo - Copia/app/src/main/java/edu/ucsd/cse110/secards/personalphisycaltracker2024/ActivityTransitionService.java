/*package edu.ucsd.cse110.secards.personalphisycaltracker2024;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import android.app.PendingIntent;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;


public class ActivityTransitionService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        List<ActivityTransition> transitions = new ArrayList<>();

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.IN_VEHICLE)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        transitions.add(
                new ActivityTransition.Builder()
                        .setActivityType(DetectedActivity.WALKING)
                        .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        .build());

        ActivityTransitionRequest request = new ActivityTransitionRequest(transitions);

        PendingIntent myPendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(this, MyBroadcastReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Task<Void> task = ActivityRecognition.getClient(this)
                .requestActivityTransitionUpdates(request, myPendingIntent);

        task.addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        // Gestisci il successo
                        Log.d("ActivityTransitionService", "Registration successful");
                    }
                }
        );

        task.addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Gestisci l'errore
                        Log.d("ActivityTransitionService", "Registration failed: " + e.getMessage());
                    }
                }
        );

        return START_STICKY; // Il servizio verrà riavviato se terminato dal sistema
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Questo è un servizio "non-bound"
    }
}*/