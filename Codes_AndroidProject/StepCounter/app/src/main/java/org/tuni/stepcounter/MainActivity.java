package org.tuni.stepcounter;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.pm.PackageInfoCompat;

import android.Manifest;
import android.content.Context;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static String TAG = "ZZ MainActivity";

    public static String REQUIRED_PERMISSION_10 = "android.permission.ACTIVITY_RECOGNITION";
    public static String REQUIRED_PERMISSION = "com.google.android.gms.permission.ACTIVITY_RECOGNITION";

    ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                Log.d(TAG, "launcher result " + isGranted);
                if (isGranted) {
                    Log.d(TAG, "activity recognition permission granted");
                } else {
                    Log.d(TAG, "activity recognition permission denied");
                }
            });

    public static String STEPS_DATA = "org.tuni.stepcounter";
    public static String STEPS_DATA_KEY = "org.tuni.stepcounter.stepstaken.key";

    SensorManager sensorManager;
    int[] sensorTypes = {
            Sensor.TYPE_STEP_COUNTER,
            Sensor.TYPE_STEP_DETECTOR,
            Sensor.TYPE_ACCELEROMETER,
    };
    SharedPreferences sharedPreferences;

    List<Integer> stepsTaken;
    List<Sensor> sensors;

    Button checkButton, startButton, stopButton;
    FloatingActionButton saveButton, deleteAllButton;
    TextView textViewSensors, vSteps, vLastSteps, vTotalSteps, vAccumulatedSteps;

    float stepCounter;
    int initial_step, final_step;

    boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        checkButton = findViewById(R.id.buttonCheck);
        startButton = findViewById(R.id.buttonStart);
        stopButton = findViewById(R.id.buttonStop);

        saveButton = findViewById(R.id.saveButton);
        deleteAllButton = findViewById(R.id.clearButton);

        textViewSensors = findViewById(R.id.textView_sensors);
        vSteps = findViewById(R.id.textView_steps);
        vLastSteps = findViewById(R.id.lastSteps);
        vTotalSteps = findViewById(R.id.totalSteps);
        vAccumulatedSteps = findViewById(R.id.accumulatedSteps);

        sharedPreferences = getSharedPreferences(STEPS_DATA, Context.MODE_PRIVATE);

        checkPermission();
        getAvailableSensors();
        registerSensors();
        getHistory();

        stepCounter = 0;
        initial_step = -1;
        final_step = 0;
        running = false;

        startButton.setOnClickListener(view-> startCounting());
        stopButton.setOnClickListener(view-> stopCounting());

        saveButton.setOnClickListener(view->saveCurrentSteps());
        deleteAllButton.setOnClickListener(view->deleteAll());
    }

    @Override
    protected void onPause() {
        super.onPause();
        save();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initial_step = -1;
        final_step = 0;
        running = false;
        registerSensors();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {  //=29
            if (ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSION_10) !=
                    PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(REQUIRED_PERMISSION_10);
            }
        } else {
            Log.d(TAG, "check Permission "+ Build.VERSION.SDK_INT);
            if (ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSION) !=
                    PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(REQUIRED_PERMISSION);
            }
        }
    }

    public void getAvailableSensors() {
        sensors = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int type : sensorTypes) {
            Sensor sensor = sensorManager.getDefaultSensor(type);
            if (sensor != null){
                sensors.add(sensor);
                sb.append(sensor.getName());
                sb.append("\n");
            }
        }
        String ret = (sensors.size() == 0) ? "Sensors NOT FOUND in this device" : sb.toString();
        textViewSensors.setText(ret);

        if (sensors.size() == 0) {
            String tmp = "Steps counting is not available ";
            vSteps.setText(tmp);
        }
    }

    public void registerSensors() {
        for (int type : sensorTypes) {
            Sensor sensor = sensorManager.getDefaultSensor(type);
            if (sensor != null)
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void getHistory() {
        stepsTaken = new ArrayList<>();
        String ret = sharedPreferences.getString(STEPS_DATA_KEY, "0");

        Log.d(TAG, "history: " + ret);
        for (int i = 0; i < ret.split(" ").length; i++) {
            stepsTaken.add(Integer.valueOf(ret.split(" ")[i]));
        }

        String lastSteps = "Last walks: " + ret.trim().split(" ")[ret.split(" ").length - 1];
        String totalSteps = "Total walks: " + stepsTaken.stream().mapToInt(Integer::intValue).sum();

        vLastSteps.setText(lastSteps);
        vTotalSteps.setText(totalSteps);
    }

    public void startCounting() {
        running = true;
        vSteps.setBackgroundColor(Color.YELLOW);
        initial_step = (int)stepCounter;
        Log.d(TAG, "initial steps: " + initial_step + " final steps: " + final_step);
    }

    public void stopCounting() {
        if (!running) {
            Toast.makeText(this,"START counting first", Toast.LENGTH_LONG).show();
            return;
        }
        final_step = (int)stepCounter;
        running = false;
        vSteps.setBackgroundColor(Color.WHITE);
        Log.d(TAG, "final steps: " + final_step + " initial steps: " + initial_step);
    }

    public void saveCurrentSteps() {
        if (running) {
            Toast.makeText(this,"STOP counting steps", Toast.LENGTH_LONG).show();
            return;
        } else if (initial_step == -1) {
            Toast.makeText(this,"START counting steps", Toast.LENGTH_LONG).show();
            return;
        }

        int takenSteps = final_step - initial_step;
        stepsTaken.add(takenSteps);

        String lastSteps = "Last walks: " + takenSteps;
        String totalSteps = "Total walks: " + stepsTaken.stream().mapToInt(Integer::intValue).sum();

        vLastSteps.setText(lastSteps);
        vTotalSteps.setText(totalSteps);

        initial_step = -1;
    }


    public void save() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<stepsTaken.size() ;i++) {
            sb.append(stepsTaken.get(i));
            sb.append(" ");
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STEPS_DATA_KEY, sb.toString());
        editor.apply();
    }

    public void deleteAll() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("This will delete ALL saved steps, are you sure?");
        alertDialogBuilder.setPositiveButton("OK",
                (arg0, arg1) -> {
                    Toast.makeText(MainActivity.this,"deleting....", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(STEPS_DATA_KEY, "");
                    editor.apply();
                });

        alertDialogBuilder.setNegativeButton("CANCEL",
                (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //NOT implemented
    public void calculateStepsFromAccelerometer(SensorEvent sensorEvent) {
        for (Sensor sensor : sensors) {
            if (sensor.getType() == Sensor.TYPE_STEP_COUNTER || sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                return;
            }
        }
        stepCounter = sensorEvent.values[0];
    }

    public void getStepsFromStepDetector(SensorEvent sensorEvent) {
        for (Sensor sensor : sensors) {
            if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                return;
            }
        }
        String ret;
        int currentSteps;
        stepCounter = sensorEvent.values[0];
        currentSteps = (int)stepCounter - initial_step;
        if (running) {
            ret = String.format(Locale.getDefault(), "Number of steps: %d", currentSteps);
        } else if (initial_step != -1) {
            ret = "Number of steps: " + (final_step - initial_step);
        } else {
            ret = "Number of steps: 0";
        }
        vSteps.setText(ret);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        String ret;
        int currentSteps;

        switch (sensorType) {
            case Sensor.TYPE_STEP_COUNTER:
                vAccumulatedSteps.setText(String.format(Locale.getDefault(),"Accumulated steps: %d", (int)sensorEvent.values[0]));

                stepCounter = sensorEvent.values[0];
                currentSteps = (int)stepCounter - initial_step;

                if (running) {
                    ret = String.format(Locale.getDefault(), "Number of steps: %d", currentSteps);
                } else if (initial_step != -1) {
                    ret = "Number of steps: " + (final_step - initial_step);
                } else {
                    ret = "Number of steps: 0";
                }
                vSteps.setText(ret);
                break;
            case Sensor.TYPE_STEP_DETECTOR:
                getStepsFromStepDetector(sensorEvent);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                calculateStepsFromAccelerometer(sensorEvent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}