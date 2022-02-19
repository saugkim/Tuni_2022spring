package org.tuni.firestoretestapp;

import android.util.Log;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class FirestoreHandler {

    private static final String TAG = "ZZ FirestoreHandler";
    public static final String COLLECTION_NAME = "org.tuni.taskukirjaFB";

    public static void addAku(FirebaseFirestore db, Aku aku) {
        db.collection(COLLECTION_NAME)
            .add(aku)
            .addOnSuccessListener(documentReference -> {
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                aku.setDocId(documentReference.getId());
                update(db, documentReference.getId());
            })
            .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public static void update(FirebaseFirestore db, String id) {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        docRef.update("docId", id);
    }

    public static void updateByDocId(FirebaseFirestore db, String id, String number, String title, String year, String pages) {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        docRef.update("kirjanNumero", number);
        docRef.update("kirjanNimi", title);
        docRef.update("kirjanVuosi", year);
        docRef.update("kirjanSivu", pages);
    }
    public static void deleteByDocId(FirebaseFirestore db, String id) {
        DocumentReference docRef = db.collection(COLLECTION_NAME).document(id);
        docRef.delete();
    }

    public static void deleteAll(FirebaseFirestore db) {
        db.collection(COLLECTION_NAME)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, "delete this " + document.getId() + " => " + document.getData());
                        document.getReference().delete();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            });
    }

    public static void getAll(FirebaseFirestore db, List<Aku> akut) {
        db.collection(COLLECTION_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Aku aku = document.toObject(Aku.class);
                            akut.add(aku);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
    public static void deleteAkuByQuery(FirebaseFirestore db, String category, String keyword) {
        db.collection(COLLECTION_NAME)
            .whereEqualTo(category, keyword)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        document.getReference().delete();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            });
    }
}
