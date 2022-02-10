package org.tuni.taskukirja;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "ZZ MainActivity";
    public static String EMPTY_QUERY = "";

    Button buttonSearch, buttonShowALl;
    EditText editTextSearch;
    FloatingActionButton buttonAddBook, buttonRemoveAllBooks;
    RecyclerView recyclerView;

    BookViewModel bookViewModel;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        buttonAddBook = findViewById(R.id.buttonAdd);
        buttonRemoveAllBooks = findViewById(R.id.buttonRemoveAll);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonShowALl = findViewById(R.id.buttonShowALl);
        editTextSearch = findViewById(R.id.editTextSearch);

        final BookAdapter adapter = new BookAdapter(new BookAdapter.ItemDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);
        query = TextUtils.isEmpty(editTextSearch.getText()) ? EMPTY_QUERY : "%"+ editTextSearch.getText().toString() +"%";

        //main thread is too heavy with below two lines
        //bookViewModel.setQuery(query);
        //bookViewModel.getObservableBooks().observe(this, adapter::submitList);

        buttonSearch.setOnClickListener(view->{
            editTextSearch.setEnabled(false);
            editTextSearch.setEnabled(true);
            if (!TextUtils.isEmpty(editTextSearch.getText())) {
                query = "%"+ editTextSearch.getText().toString() +"%";
                bookViewModel.setQuery(query);
                bookViewModel.getObservableBooks().observe(this, adapter::submitList);
                Log.d(TAG, "search input: " + query);
            } else {
                bookViewModel.setQuery(EMPTY_QUERY);
                bookViewModel.getObservableBooks().observe(this, adapter::submitList);
            }
        });

        buttonShowALl.setOnClickListener(view->{
            bookViewModel.setQuery(EMPTY_QUERY);
            bookViewModel.getObservableBooks().observe(this, adapter::submitList);
        });

        buttonAddBook.setOnClickListener(view -> {
            Log.d(TAG, "add book button pressed");
            Intent intent = new Intent(this, AddBookActivity.class);
            startActivity(intent);
        });

        buttonRemoveAllBooks.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("All books in Database will be deleted permanently, are you sure?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> removeAll());
            builder.setNegativeButton("No", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "cancelled", Toast.LENGTH_LONG).show());
            AlertDialog ad = builder.create();
            ad.show();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //query = TextUtils.isEmpty(editTextSearch.getText()) ? EMPTY_QUERY : "%"+ editTextSearch.getText().toString() +"%";
    }
    void removeAll(){
        bookViewModel.removeAll();
        Toast.makeText(getApplicationContext(), "database are empty now", Toast.LENGTH_LONG).show();
    }
}