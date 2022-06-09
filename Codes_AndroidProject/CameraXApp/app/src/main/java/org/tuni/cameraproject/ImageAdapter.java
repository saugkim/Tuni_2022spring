package org.tuni.cameraproject;

import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {
    public static String TAG = "ZZ ImageAdapter...";
    //List<File> files;
    List<String> filesUri;

//    public ImageAdapter(List<File> files) {
//        this.files = files;
//    }

    public ImageAdapter(List<String> files) {
        this.filesUri = files;
    }

    public static class ImageHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }

    @NonNull
    @Override
    public ImageAdapter.ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.image, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageHolder holder, int position) {
//        File file = files.get(position);
        Uri uri = Uri.parse(filesUri.get(position));
        holder.imageView.setImageURI(uri);

//        Log.d(TAG, "file path: " + file.getPath());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            holder.imageView.setImageURI(Uri.parse("content://media" + file.getPath()));
//        } else {
//            holder.imageView.setImageURI(Uri.fromFile(file));
//        }
    }

    @Override
    public int getItemCount() {
        return filesUri.size();
    }
}