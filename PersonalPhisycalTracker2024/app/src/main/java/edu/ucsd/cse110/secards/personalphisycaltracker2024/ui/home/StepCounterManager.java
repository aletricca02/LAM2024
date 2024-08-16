package edu.ucsd.cse110.secards.personalphisycaltracker2024.ui.home;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class StepCounterManager implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private int initialStepCount = 0; // Passi al momento dell'inizio del monitoraggio
    private int sessionStepCount = 0; // Passi accumulati durante la sessione
    private int currentStepCount = 0; // Passi attuali dal sensore


    public StepCounterManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    public void startListening() {
        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }
    }

    public void stopListening() {
        sensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            if (initialStepCount == 0) {
                // Imposta initialStepCount al primo cambiamento di sensore
                initialStepCount = (int) event.values[0];
            } else {
                currentStepCount = (int) event.values[0];
                // Calcola i passi della sessione come differenza rispetto al valore iniziale
                sessionStepCount = currentStepCount - initialStepCount;
                // Gestisci valori negativi per sessionStepCount
                if (sessionStepCount < 0) {
                    sessionStepCount = 0; // Imposta a zero o gestisci diversamente come necessario
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }

    public int getStepCount() {
        return Math.max(sessionStepCount, 0); // Assicurati che il conteggio non sia negativo
    }


}