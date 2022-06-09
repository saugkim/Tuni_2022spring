package org.tuni.project_vision;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class StatisticsActivity extends AppCompatActivity {

    public static String TAG = "ZZ Statistics..";
    TextView textViewValue;
    Button buttonShowTrue, buttonShowFalse, buttonGetCorrectness;
    RecyclerView recyclerView;

    ImageAdapter adapter;
    ImageViewModel viewModel;

    Executor executor = Executors.newSingleThreadExecutor();

    int total_number;
    int correct_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        textViewValue = findViewById(R.id.textViewValue);
        buttonShowTrue = findViewById(R.id.buttonShowTrue);
        buttonShowFalse = findViewById(R.id.buttonShowFalse);
        buttonGetCorrectness = findViewById(R.id.buttonCorrect);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ImageAdapter(new ImageAdapter.ItemDiff());

        viewModel = new ViewModelProvider(this).get(ImageViewModel.class);

        getCorrectness();

        buttonGetCorrectness.setOnClickListener(view -> getCorrectness());

        buttonShowTrue.setOnClickListener(view->{
            viewModel.getTrueImages().observe(this, adapter::submitList);
            recyclerView.setAdapter(adapter);
        });

        buttonShowFalse.setOnClickListener(view -> {
            viewModel.getFalseImages().observe(this, adapter::submitList);
            recyclerView.setAdapter(adapter);
        });
    }

    /**
     * Used to get number of object correctly assigned (classified) and total number of object classified
     * result shown in textView format of  number_correct / number_total from the room database
     */
    public void getCorrectness() {
        executor.execute(() -> runOnUiThread(() -> {

            viewModel.getCounts().observe(this, integer -> {
                total_number = integer;
                float ret = total_number == 0 ? 0 : (float) correct_number / total_number;
                textViewValue.setText(String.format(Locale.getDefault(), "%.4f", ret));
                //textViewValue.setText(String.format(Locale.getDefault(), "%d / %d", correct_number, total_number));
            });

            viewModel.getCountsCorrect().observe(this, integer -> {
                correct_number = integer;
                float ret = total_number == 0 ? 0 : (float) correct_number / total_number;
                textViewValue.setText(String.format(Locale.getDefault(), "%.4f", ret));
                //textViewValue.setText(String.format(Locale.getDefault(), "%d / %d", correct_number, total_number));
            });
        }));
    }
}