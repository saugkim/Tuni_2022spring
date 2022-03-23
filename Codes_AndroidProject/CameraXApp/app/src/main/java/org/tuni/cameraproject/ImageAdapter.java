package org.tuni.cameraproject;

import android.net.Uri;
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
    List<File> files;

    public ImageAdapter(List<File> files) {
        this.files = files;
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
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
        return new ImageAdapter.ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageHolder holder, int position) {
        File file = files.get(position);
        Log.d(TAG, "file name " + file.getPath());
        holder.imageView.setImageURI(Uri.fromFile(file));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }
}