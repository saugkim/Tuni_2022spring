package org.tuni.taskukirja;

import androidx.appcompat.app.AppCompatActivity;

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

public class AddBookActivity extends AppCompatActivity {

    public static final String TAG = "ZZ AddBookActivity: ";
    public static String DATE_FORMAT = "dd.MM.yyyy";

    EditText editTextIssue, editTextTitle, editTextYear, editTextPages, editTextDate;
    Button buttonSave;

    Boolean date_selected = false;
    BookRepository bookRepository = new BookRepository(getApplication());

    final Calendar calendar = Calendar.getInstance();
    final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month,int dayOfMonth) {
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
        setContentView(R.layout.activity_add_book);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextIssue = findViewById(R.id.editTextIssue);
        editTextYear = findViewById(R.id.editTextYear);
        editTextPages = findViewById(R.id.editTextPages);
        editTextDate = findViewById(R.id.editTextD);

        editTextDate.setFocusable(false);
        editTextDate.setOnClickListener(view -> {
            new DatePickerDialog(this, datePickerListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
            date_selected = true;
        });

        buttonSave = findViewById(R.id.button_save);
        buttonSave.setOnClickListener(view -> save());
    }

    void save() {
        Intent goToMainIntent = new Intent(this, MainActivity.class);

        if (TextUtils.isEmpty(editTextTitle.getText())) {
            editTextTitle.setError("title required");
            editTextTitle.requestFocus();
            Toast.makeText(getApplicationContext(), "Not saved", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Not saved - empty title inputs");
            return;
        }
        if (TextUtils.isEmpty(editTextIssue.getText())) {
            editTextIssue.setError("number of issue required");
            editTextIssue.requestFocus();
            Log.d(TAG, "Not saved - empty issue input");
            return;
        }

        int issue = Integer.parseInt(editTextIssue.getText().toString());
        String title = editTextTitle.getText().toString();
        Log.d(TAG, "date selected ? " + date_selected);
        String date = date_selected ? editTextDate.getText().toString() : getToday();
        String year = TextUtils.isEmpty(editTextYear.getText()) ? null : editTextYear.getText().toString();
        String pages = TextUtils.isEmpty(editTextPages.getText()) ? null : editTextPages.getText().toString();

        date_selected = false;
        Book book = new Book(issue, title, year, pages, date);
        book.setId(bookRepository.insert(book));
        startActivity(goToMainIntent);

        finish();
    }

    public String getToday() {
        Date today = new Date();
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(today);
    }
}