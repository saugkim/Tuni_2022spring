package org.tuni.sensortestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class LightActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor sensor;
    TextView textSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        textSensor = findViewById(R.id.textSensor);
        String sensor_error = getResources().getString(R.string.error_no_sensor);

        if (sensor == null) {
            textSensor.setText(sensor_error);
            Log.d("ZZ ", sensor_error);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float currentValue = sensorEvent.values[0];
        @SuppressLint("DefaultLocale") String ret = String.format("%.2f lx", currentValue);
        textSensor.setText(ret);
   //     textSensor.setText(getResources().getString(R.string.label_light, currentValue));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}