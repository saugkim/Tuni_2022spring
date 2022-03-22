package org.tuni.sensortestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class OrientationActivity extends AppCompatActivity implements SensorEventListener {

    public static String TAG = "ZZ Orientation";

    private Display display;

    SensorManager sensorManager;
    Sensor sensorAccelerometer, sensorMagnetometer;
    TextView textSensorAzimuth, textSensorPitch, textSensorRoll;
    private ImageView mSpotTop;
    private ImageView mSpotBottom;
    private ImageView mSpotLeft;
    private ImageView mSpotRight;

    private static final float VALUE_DRIFT = 0.05f;

    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];

 //   @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientation);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        textSensorAzimuth = findViewById(R.id.value_azimuth);
        textSensorPitch = findViewById(R.id.value_pitch);
        textSensorRoll = findViewById(R.id.value_roll);

        mSpotTop = findViewById(R.id.spot_top);
        mSpotBottom = findViewById(R.id.spot_bottom);
        mSpotLeft = findViewById(R.id.spot_left);
        mSpotRight = findViewById(R.id.spot_right);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sensorAccelerometer != null) {
            sensorManager.registerListener(this, sensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorMagnetometer != null) {
            sensorManager.registerListener(this, sensorMagnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        // Unregister all sensor listeners in this callback so they don't
        // continue to use resources when the app is stopped.
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = sensorEvent.values.clone();
                break;
            default:
                return;
        }
        float[] rotationMatrix = new float[9];
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix,
                null, mAccelerometerData, mMagnetometerData);

        float[] rotationMatrixAdjusted = new float[9];

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                rotationMatrixAdjusted = rotationMatrix.clone();
                break;
            case Surface.ROTATION_90:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X,
                        rotationMatrixAdjusted);
                break;
            case Surface.ROTATION_180:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y,
                        rotationMatrixAdjusted);
                break;
            case Surface.ROTATION_270:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X,
                        rotationMatrixAdjusted);
                break;
        }

        float[] orientationValues = new float[3];
        if (rotationOK) {
            SensorManager.getOrientation(rotationMatrixAdjusted, orientationValues);
        }

        float azimuth = (float) (orientationValues[0] * 180 / Math.PI);
        float pitch = (float) (orientationValues[1] * 180 / Math.PI);
        float roll = (float) (orientationValues[2] * 180 / Math.PI);

        if (Math.abs(orientationValues[1]) < VALUE_DRIFT) {
            pitch = 0;
        }
        if (Math.abs(orientationValues[2]) < VALUE_DRIFT) {
            roll = 0;
        }

        textSensorAzimuth.setText(getResources().getString(
                R.string.value_format, azimuth));
        textSensorPitch.setText(getResources().getString(
                R.string.value_format, pitch));
        textSensorRoll.setText(getResources().getString(
                R.string.value_format, roll));

        mSpotTop.setAlpha(0f);
        mSpotBottom.setAlpha(0f);
        mSpotLeft.setAlpha(0f);
        mSpotRight.setAlpha(0f);

        if (pitch > 0) {
            mSpotBottom.setAlpha(orientationValues[1]);
        } else {
            mSpotTop.setAlpha(Math.abs(orientationValues[1]));
        }
        if (roll > 0) {
            mSpotLeft.setAlpha(orientationValues[2]);
        } else {
            mSpotRight.setAlpha(Math.abs(orientationValues[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}