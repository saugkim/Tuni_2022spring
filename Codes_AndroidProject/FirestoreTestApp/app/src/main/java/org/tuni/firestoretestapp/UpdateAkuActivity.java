package org.tuni.firestoretestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateAkuActivity extends AppCompatActivity {

    EditText nro, nimi, vuosi, sivu;
    Button updateButton, deleteButton;

    FirebaseFirestore db;

    public final String TAG = "ZZ UpdateAkuActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_aku);

        String id = getIntent().getStringExtra(AkuArrayAdapter.EXTRA_KEY);
        Aku aku = (Aku) getIntent().getSerializableExtra(AkuArrayAdapter.EXTRA_OBJECT_KEY);
        Log.d(TAG, "onCreate item id: " + id);

        db = FirebaseFirestore.getInstance();

        nro = findViewById(R.id.editTextNumero);
        nimi = findViewById(R.id.editTextNimi);
        vuosi = findViewById(R.id.editTextVuosi);
        sivu = findViewById(R.id.editTextSivu);
        updateButton = findViewById(R.id.update);
        deleteButton = findViewById(R.id.delete);

        updateUI(aku);

        updateButton.setOnClickListener(view -> update(db, id));
        deleteButton.setOnClickListener(view -> delete(db, id));
    }

    private void updateUI(Aku aku) {
        if (aku != null) {
            nro.setText(aku.getKirjanNumero());
            nimi.setText(aku.getKirjanNimi());
            vuosi.setText(aku.getKirjanVuosi());
            sivu.setText(aku.getKirjanSivu());
        }
    }

    private void update(FirebaseFirestore db, String id) {
        String number = nro.getText().toString();
        String name = nimi.getText().toString();
        String year = vuosi.getText().toString();
        String pages = sivu.getText().toString();

        FirestoreHandler.updateByDocId(db, id, number, name, year, pages);

        startActivity(new Intent(this, MainActivity.class));
    }

    private void delete(FirebaseFirestore db, String id) {
        FirestoreHandler.deleteByDocId(db, id);

        startActivity(new Intent(this, MainActivity.class));
    }
}