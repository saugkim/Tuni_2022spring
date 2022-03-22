package org.tuni.sensortestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

public class OthersActivity extends AppCompatActivity implements SensorEventListener {

    public static String TAG = "ZZ OthersActivity";

    SensorManager sensorManager;
    Sensor sensor;

    TextView sensorName;
    TextView sensorValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others);

        int type = getIntent().getIntExtra(SensorAdapter.SENSOR_KEY, 0);
        Log.d(TAG, "sensor type is: " + type);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(type);

        sensorValue = findViewById(R.id.textValue);
        sensorName = findViewById(R.id.textName);
        String sensor_error = getResources().getString(R.string.error_no_sensor);

        if (sensor == null) {
            sensorName.setText(sensor_error);
            sensorValue.setText(0);
            Log.d(TAG, sensor_error);
        } else {
            sensorName.setText(sensor.getName());
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
        int sensorType = sensorEvent.sensor.getType();
        String unit;
        switch (sensorType) {
            case Sensor.TYPE_GRAVITY:
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
            case Sensor.TYPE_LINEAR_ACCELERATION:
                unit = "m/s2";
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
            case Sensor.TYPE_GYROSCOPE:
                unit = "rad/s";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                unit = "μT";
                break;
            default:
                unit = "";
                break;
        }
        if (sensorEvent.values.length >= 2) {
            StringBuilder sb = new StringBuilder();
            sb.append("Current Values");
            sb.append("\n x : ");
            sb.append(String.format(Locale.getDefault(), "%.3f %s", sensorEvent.values[0], unit));
            sb.append("\n y : ");
            sb.append(String.format(Locale.getDefault(), "%.3f %s", sensorEvent.values[1], unit));
            sb.append("\n z : ");
            sb.append(String.format(Locale.getDefault(), "%.3f %s", sensorEvent.values[2], unit));
            sensorValue.setText(sb.toString());
        } else {
            sensorValue.setText(getResources().getString(R.string.label_others, "Value: ", sensorEvent.values[0], unit));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}