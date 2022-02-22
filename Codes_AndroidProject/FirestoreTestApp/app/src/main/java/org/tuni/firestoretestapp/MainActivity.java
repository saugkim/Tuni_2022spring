package org.tuni.firestoretestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "ZZ MainActivity";
    public static final String COLLECTION_NAME = "org.tuni.taskukirjaFB";

    private EditText nro, nimi, vuosi, sivu;
    private List<Aku> kaikkiAkut;
    private ListView mAkuListView;

    private AkuArrayAdapter mAkuListAdapter;

    private FirebaseFirestore mFirestore;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            user = auth.getCurrentUser();
            Objects.requireNonNull(getSupportActionBar()).setTitle("Hello " + user.getEmail());
        } else {
            startActivity(new Intent(this, AuthenticationActivity.class));
        }

        mFirestore=FirebaseFirestore.getInstance();

        nro = findViewById(R.id.editTextNumero);
        nimi = findViewById(R.id.editTextNimi);
        vuosi = findViewById(R.id.editTextVuosi);
        sivu = findViewById(R.id.editTextSivu);
        Button addButton = findViewById(R.id.add);
        Button deleteAllButton = findViewById(R.id.delete);

        mAkuListView = findViewById(R.id.listView);
        kaikkiAkut = new ArrayList<>();
        muutosKysely();

        addButton.setOnClickListener(view-> addAku());
        deleteAllButton.setOnClickListener(view-> deleteAll());
    }

    // TODO toteuta muutoskysely
    public void muutosKysely(){
        mFirestore.collection(COLLECTION_NAME).addSnapshotListener((queryDocumentSnapshots, e) -> {
            kaikkiAkut.clear();
            Log.d(TAG, "nakyman paivitys?");
            if(queryDocumentSnapshots!=null) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Aku aku = snapshot.toObject(Aku.class);
                    kaikkiAkut.add(aku);
                }
                mAkuListAdapter = new AkuArrayAdapter(this, R.layout.item_aku, kaikkiAkut);
                mAkuListAdapter.notifyDataSetChanged();
                mAkuListView.setAdapter(mAkuListAdapter);
            }else {
                Log.d(TAG, "Querysnapshot on null");
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sign_out_menu) {
            signOut();
            return true;
        }
        if (item.getItemId() == R.id.remove_account_menu) {
            removeAccount();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addAku() {
        Log.d(TAG,"addAku()");

        String number = nro.getText().toString();
        String name = nimi.getText().toString();
        String year = vuosi.getText().toString();
        String pages = sivu.getText().toString();

        if ( TextUtils.isEmpty(number) || TextUtils.isEmpty(name) || TextUtils.isEmpty(year) || TextUtils.isEmpty(pages) ) {
            Log.d(TAG, "Input field cannot be null");
            showSnackbar("Fill all input fields");
        } else {
            Aku aku = new Aku(number, name, year, pages);
            FirestoreHandler.addAku(mFirestore, aku);
            Log.d(TAG, aku.toString());
            showSnackbar(aku + " is added to the database");
            clearUI();
        }
    }

    private void deleteAll() {
        Log.d(TAG, "delete all items without warning");
        showSnackbar("All records are deleted");
        FirestoreHandler.deleteAll(mFirestore);
    }

    private void clearUI() {
        Log.d(TAG,"cleanup input fields: ");
        nro.setText("");
        nimi.setText("");
        vuosi.setText("");
        sivu.setText("");
    }

    public void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    public void signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(@NonNull Task<Void> task) {
                    // user is now signed out
                    startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
                    finish();
                }
            });
    }

    public void removeAccount() {
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Deletion succeeded
                        startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
                        finish();
                    } else {
                        // Deletion failed
                        showSnackbar("Deleting failed");
                    }
                }
            });
    }
}