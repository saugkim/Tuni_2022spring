package org.tuni.sensortestapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.SensorViewHolder> {

    public static String TAG = "ZZ sensorAdapter";
    public static String SENSOR_KEY = "org.tuni.sensortestapp.sensor_type_key";
    List<Sensor> sensors;

    public SensorAdapter(List<Sensor> sensorList) {
        this.sensors = sensorList;
    }

    public class SensorViewHolder extends RecyclerView.ViewHolder {

       TextView sensorName;

       public SensorViewHolder(@NonNull View itemView) {
            super(itemView);

            sensorName = itemView.findViewById(R.id.sensor_name);

            itemView.setOnClickListener(view -> {
                int idx = getAdapterPosition();
                Sensor currentSensor = sensors.get(idx);
                view.getContext().startActivity(getIntent(view, currentSensor.getType()));
            });
        }
    }

    public Intent getIntent(View view, int sensorType) {
        Intent intent;
        switch (sensorType) {
            case (Sensor.TYPE_LIGHT):
                intent = new Intent(view.getContext(), LightActivity.class);
                break;
            case (Sensor.TYPE_PROXIMITY):
                intent = new Intent(view.getContext(), ProximityActivity.class);
                break;
            case (Sensor.TYPE_AMBIENT_TEMPERATURE):
                intent = new Intent(view.getContext(), TemperatureActivity.class);
                break;
            case (Sensor.TYPE_PRESSURE):
                intent = new Intent(view.getContext(), PressureActivity.class);
                break;
            case (Sensor.TYPE_RELATIVE_HUMIDITY):
                intent = new Intent(view.getContext(), HumidityActivity.class);
                break;
            default:
                intent = new Intent(view.getContext(), OthersActivity.class);
                break;
        }
        intent.putExtra(SENSOR_KEY, sensorType);
        Log.d(TAG, "sensor type is " + sensorType);
        return intent;
    }

    @NonNull
    @Override
    public SensorAdapter.SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.sensor_listview, parent, false);

        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorAdapter.SensorViewHolder holder, int position) {
        Sensor currentSensor = sensors.get(position);
        holder.sensorName.setText(currentSensor.getName());
    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }
}
