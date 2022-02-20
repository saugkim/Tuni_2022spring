package org.tuni.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    List<Article> localDataSet;
    private static final String TAG = "ZZ AdapterClass";
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textTitle;
        private final TextView textDesc;
        private final TextView textDate;
        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            textTitle = view.findViewById(R.id.newsTitle);
            textDesc = view.findViewById(R.id.newsDescription);
            textDate = view.findViewById(R.id.newsDate);
            imageView = view.findViewById(R.id.newsImage);

            view.setOnClickListener(v -> {
                Log.d(TAG, "in ViewHolder constructor " + getAdapterPosition());
            });
        }
        public TextView getTextTitle() {
            return textTitle;
        }
        public TextView getTextDesc() {
            return textDesc;
        }
        public TextView getTextDate() {
            return textDate;
        }
        public ImageView getImageView() {
            return imageView;
        }
    }
    /* Initialize the dataset of the Adapter. */
    public ArticleAdapter(List<Article> dataSet) {
        localDataSet = dataSet;
    }
    public void setLocalDataset(List<Article> set) {
        localDataSet = set;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_linear_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the contents of the view with that element
        viewHolder.getTextTitle().setText(localDataSet.get(position).getTitle());
        viewHolder.getTextDesc().setText(localDataSet.get(position).getDescription());
        viewHolder.getTextDate().setText(localDataSet.get(position).getDate());
//        viewHolder.getImageView().setImageURI(Uri.parse(localDataSet.get(position).getImageUrl()));
        Glide.with(viewHolder.itemView.getContext()).load(localDataSet.get(position).getImageUrl()).into(viewHolder.getImageView());

        viewHolder.itemView.setOnClickListener(view ->{
            Toast.makeText(view.getContext(), "Recycle Click" + position, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Recycle click at position " + position);
            try {
                //Uri uri = Uri.parse(localDataSet.get(position).getUrl());
                //Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                //view.getContext().startActivity(intent);
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(localDataSet.get(position).getUrl())));
            } catch(Exception e){
                e.printStackTrace();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
