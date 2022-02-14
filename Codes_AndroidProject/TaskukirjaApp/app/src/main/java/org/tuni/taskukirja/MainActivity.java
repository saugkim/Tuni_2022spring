package org.tuni.taskukirja;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "ZZ MainActivity";
    public static String EMPTY_QUERY = "";

    Button buttonSearch, buttonShowALl;
    EditText editTextSearch;
    FloatingActionButton buttonAddBook, buttonRemoveAllBooks;
    RecyclerView recyclerView;

    BookAdapter adapter;
    BookViewModel bookViewModel;
    String query;

    ActivityResultLauncher<Intent> addBookActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Book book = (Book) data.getSerializableExtra(AddBookActivity.EXTRA_REPLY);
                        Log.d(TAG, "line 47: " + book.getTitle());
                        bookViewModel.insert(book);
                    }
                }
            });

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

        //final BookAdapter adapter = new BookAdapter(new BookAdapter.ItemDiff());
        adapter = new BookAdapter(new BookAdapter.ItemDiff());
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
                Snackbar.make(view, "title search input: " + editTextSearch.getText().toString(), Snackbar.LENGTH_LONG).show();
            } else {
                bookViewModel.setQuery(EMPTY_QUERY);
                bookViewModel.getObservableBooks().observe(this, adapter::submitList);
            }
        });

        buttonShowALl.setOnClickListener(view->{
            editTextSearch.setText(null);
            bookViewModel.setQuery(EMPTY_QUERY);
            bookViewModel.getObservableBooks().observe(this, adapter::submitList);
        });

        buttonAddBook.setOnClickListener(view -> {
            Log.d(TAG, "add book button pressed");
            //openAddBookActivity();
            openAddBookActivityForResult();
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
        query = TextUtils.isEmpty(editTextSearch.getText()) ? EMPTY_QUERY : "%"+ editTextSearch.getText().toString() +"%";
        bookViewModel.setQuery(query);
        bookViewModel.getObservableBooks().observe(this, adapter::submitList);
    }
    void removeAll(){
        bookViewModel.removeAll();
        Toast.makeText(getApplicationContext(), "database are empty now", Toast.LENGTH_LONG).show();

        AddSampleBooks.addSamples(bookViewModel);
    }
    public void openAddBookActivityForResult() {
        Intent intent = new Intent(this, AddBookActivity.class);
        addBookActivityResultLauncher.launch(intent);
    }

    public void openAddBookActivity() {
        Intent intent = new Intent(this, AddBookActivity.class);
        startActivity(intent);
    }
}