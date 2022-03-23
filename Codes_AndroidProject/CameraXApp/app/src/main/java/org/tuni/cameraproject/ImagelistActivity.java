package org.tuni.cameraproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.GridLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImagelistActivity extends AppCompatActivity {

    public static String TAG = "ZZ ImageList...";

    RecyclerView recyclerView;
    ImageAdapter adapter;
    List<File> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagelist);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        File[] files = listValidFiles(new File(Environment.getExternalStorageDirectory().toString() + "/images"));
        Log.d(TAG, "number of files " + files.length);
        fileList = Arrays.asList(files);
        Log.d(TAG,"fileList " + fileList.get(12).getName());

        adapter = new ImageAdapter(fileList);
        recyclerView.setAdapter(adapter);
    }

    private File[] listValidFiles(File file) {
        return file.listFiles((dir, filename) -> {
            File file2 = new File(dir, filename);
            return (filename.contains(".png") || filename.contains(".jpg") || file2
                    .isDirectory())
                    && !file2.isHidden()
                    && !filename.startsWith(".");

        });
    }
}