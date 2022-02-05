package org.tuni.taskukirja;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static org.tuni.taskukirja.BookViewHolder.*;

public class UpdateBookActivity extends AppCompatActivity {

    public static final String TAG = "ZZ UpdateBookActivity: ";
    public static String DATE_FORMAT = "dd.MM.yyyy";

    EditText editTextIssue, editTextTitle, editTextYear, editTextPages, editTextDate;

    Button updateButton, deleteButton;
    boolean date_selected = false;

    BookRepository bookRepository = new BookRepository(getApplication());

    final Calendar calendar = Calendar.getInstance();
    final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            // TODO Auto-generated method stub
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            editTextDate.setText(dateFormat.format(calendar.getTime()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_book);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextIssue = findViewById(R.id.editTextIssue);
        editTextYear = findViewById(R.id.editTextYear);
        editTextPages = findViewById(R.id.editTextPages);
        editTextDate = findViewById(R.id.editTextD);

        updateButton = findViewById(R.id.button_update);
        deleteButton = findViewById(R.id.button_delete);

        long id = getIntent().getLongExtra(SELECTED_BOOK_ID, 0);
        Book book = (Book) getIntent().getSerializableExtra(SELECTED_BOOK);
        if (id != book.getId()) throw new AssertionError();

        Log.d(TAG, "onCreate " + book.getTitle() + " " + book.getId());

        loadCurrentBook(book);

        editTextDate.setOnClickListener(view -> {
            new DatePickerDialog(this, datePickerListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
            date_selected = true;
        });

        updateButton.setOnClickListener(view -> update(book));

        deleteButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateBookActivity.this);
            builder.setTitle("REMOVE THIS BOOK from the library?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> delete(book));
            builder.setNegativeButton("No", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "DELETE cancelled", Toast.LENGTH_LONG).show());
            AlertDialog ad = builder.create();
            ad.show();
        });
    }

    void loadCurrentBook(Book book) {
        final String year = book.getYearOfPublish();
        final String page = book.getNumberOfPage();

        editTextIssue.setText(String.valueOf(book.getNumberOfIssue()));
        editTextTitle.setText(book.getTitle());
        editTextYear.setText(year == null ? "" : year);
        editTextPages.setText(page == null ? "" : page);
        editTextDate.setText(book.getDateOfPurchase());
    }

    void update(Book book) {

        if (editTextTitle.getText().toString().isEmpty()) {
            editTextTitle.setError("title of book is required");
            editTextTitle.requestFocus();
            return;
        }
        if (editTextIssue.getText().toString().isEmpty()) {
            editTextIssue.setError("number of issue required");
            editTextIssue.requestFocus();
            return;
        }

        int issue = Integer.parseInt(editTextIssue.getText().toString());
        String title = editTextTitle.getText().toString();
        String date = date_selected ? editTextDate.getText().toString() : getToday();
        date_selected = false;
        String year = TextUtils.isEmpty(editTextYear.getText()) ? null : editTextYear.getText().toString();
        String pages = TextUtils.isEmpty(editTextPages.getText()) ? null : editTextPages.getText().toString();

        book.setNumberOfIssue(issue);
        book.setTitle(title);
        book.setDateOfPurchase(date);
        book.setNumberOfPage(pages);
        book.setYearOfPublish(year);

        bookRepository.update(book);
        //bookRepository.updateByID(id, issue, title, pages, year, date);
        startActivity(new Intent(this, MainActivity.class));
    }

    public String getToday() {
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date());
    }

    void delete(Book book) {
        //Log.d(TAG, "delete method first line book id: " + book.getId() + ", input Id: " + id);
        bookRepository.delete(book);
        //bookRepository.deleteById(id);
        startActivity(new Intent(this, MainActivity.class));
    }
}