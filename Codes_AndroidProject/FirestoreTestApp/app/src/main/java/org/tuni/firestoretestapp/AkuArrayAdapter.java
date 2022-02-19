package org.tuni.firestoretestapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.List;
//https://www.geeksforgeeks.org/how-to-access-any-component-outside-recyclerview-from-recyclerview-in-android/

public class AkuArrayAdapter extends ArrayAdapter<Aku> {
    private final String TAG = "ZZ AkuArrayAdapter";
    public static String EXTRA_KEY = "org.tuni.firestoretestapp.extra_key";
    public static String EXTRA_OBJECT_KEY = "org.tuni.firestoretestapp.extra_object_key";

    //TODO constructor
    public AkuArrayAdapter(Context context, int resource, List<Aku> objects) {
        super(context, resource, objects);
    }

    //TODO getView
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.item_aku, parent, false);
        }
        TextView infoTextView = convertView.findViewById(R.id.nimiTextView);
        Aku taskukirja = getItem(position);

        infoTextView.setVisibility(View.VISIBLE);
        infoTextView.setText(taskukirja.toString());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        infoTextView.setOnLongClickListener(view -> {
            //NEED confirm panel
            FirestoreHandler.deleteByDocId(db, taskukirja.getDocId());
            return true;
        });

        infoTextView.setOnClickListener(view-> {
            Log.d(TAG, "clicked: " + taskukirja.getDocId());
            Intent intent = new Intent(view.getContext(), UpdateAkuActivity.class);
            intent.putExtra(EXTRA_KEY, taskukirja.getDocId());
            intent.putExtra(EXTRA_OBJECT_KEY, (Serializable) taskukirja);
            view.getContext().startActivity(intent);
        });

        return convertView;
    }
}