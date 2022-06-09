package org.tuni.cameraproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ImagelistActivity extends AppCompatActivity {

    public static String TAG = "ZZ ImageList...";

    public static String SHARED_KEY = "org.tuni.cameraproject.sharedPreferences";
    public static String URI_STRING_KEY = "org.tuni.cameraproject.uri_string.key";

    SharedPreferences sharedPreferences;

    RecyclerView recyclerView;
    ImageAdapter adapter;

    List<String> savedUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagelist);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        sharedPreferences = this.getSharedPreferences(SHARED_KEY, MODE_PRIVATE);
        savedUris = getList();
        Log.d(TAG, "number of saved files: " + savedUris.size());

        adapter = new ImageAdapter(savedUris);
        recyclerView.setAdapter(adapter);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            if (sharedPreferences.getStringSet(URI_KEY, null) != null)
//                stringUris.addAll(sharedPreferences.getStringSet(URI_KEY, null));
//            fileList = listFilesQ(stringUris);
//        } else {
//            File[] files = listValidFiles(new File(Environment.getExternalStorageDirectory().toString() + "/images"));
//            fileList = Arrays.asList(files);
//        }
// https://stackoverflow.com/questions/60024543/android-q-how-to-get-a-list-of-images-from-a-specific-directory
    }

    private List<String> getList() {
        Gson gson = new Gson();
        if (sharedPreferences.getString(URI_STRING_KEY, null) == null) {
            return new ArrayList<>();
        }
        String json = sharedPreferences.getString(URI_STRING_KEY, null);
        Type type = new TypeToken<List<String>>(){}.getType();
        return gson.fromJson(json, type);
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
//    private List<File> listFilesQ(List<String> uris) {
//        List<File> lists = new ArrayList<>();
//        for (String uri_string: uris) {
//            Uri uri = Uri.parse(uri_string);
//            Log.d(TAG, "uri string: "+ uri.getPath());
//
//            lists.add(new File(uri.getPath()));
//        }
//        return lists;
//    }
}