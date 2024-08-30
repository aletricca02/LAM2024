/*package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.Geo;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.android.gms.location.Geofence;
import java.util.HashMap;
import java.util.Map;
import edu.ucsd.cse110.secards.personalphisycaltracker2024.R;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final Map<String, Long> geofenceEnterTimes = new HashMap<>();
    private static final String CHANNEL_ID = "Lam_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Pinello", "Intent Action: " + intent.getAction());
        Log.d("Pinello", "Intent Data: " + intent.getDataString());

        // Recupera i dati della transizione dall'Intent
        int geofenceTransition = intent.getIntExtra("geofence_transition", -1);
        String requestId = intent.getStringExtra("geofence_request_id");

        // Controlla se i dati sono validi
        if (geofenceTransition == -1 || requestId == null) {
            Log.e("Pinello", "Geofencing nullo o dati non validi");
            return;
        }

        // Gestisci le transizioni di geofence come faresti normalmente
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Log.e("Pinello", "SONO DENTROOOO" );
            sendNotification(context, "Sei entrato nella geofence " + requestId, "Ciao!");
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            sendNotification(context, "Sei uscito dalla geofence " + requestId, "Ciao!");
        }
    }



    @SuppressLint("MissingPermission")
    private void sendNotification(Context context, String title, String message) {
        // Crea il canale di notifica se necessario (solo per Android 8.0 e successivi)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Geofence Channel";
            String description = "Channel for Geofence notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Registra il canale con il sistema
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Crea l'intento per l'apertura dell'app quando si clicca sulla notifica
        Intent intent = new Intent(context, GeoFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Costruisci la notifica
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)  // icona della notifica
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Mostra la notifica
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(0, builder.build());
    }
}
*/