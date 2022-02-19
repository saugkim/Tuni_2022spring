package org.tuni.firestoretestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "ZZ MainActivity";
    public static final String COLLECTION_NAME = "org.tuni.taskukirjaFB";

    private EditText nro, nimi, vuosi, sivu;
    private List<Aku> kaikkiAkut;
    private ListView mAkuListView;

    private Context context;
    private AkuArrayAdapter mAkuListAdapter;

    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;
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
                mAkuListAdapter = new AkuArrayAdapter(context, R.layout.item_aku, kaikkiAkut);
                mAkuListAdapter.notifyDataSetChanged();
                mAkuListView.setAdapter(mAkuListAdapter);
            }else {
                Log.d(TAG, "Querysnapshot on null");
            }
        });
    }

    private void addAku() {
        Log.d(TAG,"addAku()");

        String number = nro.getText().toString();
        String name = nimi.getText().toString();
        String year = vuosi.getText().toString();
        String pages = sivu.getText().toString();

        if ( TextUtils.isEmpty(number) || TextUtils.isEmpty(name) || TextUtils.isEmpty(year) || TextUtils.isEmpty(pages) ) {
            Log.d(TAG, "Input field cannot be null");
            Toast.makeText(context, "FILL ALL INPUT FIELDS" ,Toast.LENGTH_LONG).show();
        } else {
            Aku aku = new Aku(number, name, year, pages);
            FirestoreHandler.addAku(mFirestore, aku);
            Log.d(TAG, aku.toString());
            clearUI();
        }
/*        Aku aku = new Aku();
        aku.setKirjanNumero(nro.getText().toString());
        aku.setKirjanNimi(nimi.getText().toString());
        aku.setKirjanVuosi(vuosi.getText().toString());
        aku.setKirjanSivu(sivu.getText().toString());
        FirestoreHandler.addAku(mFirestore, aku);
        Log.d(TAG, aku.toString());
*/
    }

    private void deleteAll() {
        Log.d(TAG, "delete all items without warning");
        FirestoreHandler.deleteAll(mFirestore);
    }

    private void clearUI() {
        Log.d(TAG,"cleanup input fields: ");
        nro.setText("");
        nimi.setText("");
        vuosi.setText("");
        sivu.setText("");
    }
}