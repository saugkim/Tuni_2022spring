package org.tuni.taskukirja;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = "ZZ BookViewHolder: ";
    public static String SELECTED_BOOK = "org.tuni.taskukirja.selected_book";
    public static String SELECTED_BOOK_ID = "org.tuni.taskukirja.selected_book_id";

    TextView bookInfo;
    Button removeButton;
    Book currentBook;

    BookRepository bookRepository;

    public BookViewHolder(@NonNull View itemView) {
        super(itemView);
        bookInfo = itemView.findViewById(R.id.textViewBook);
        removeButton = itemView.findViewById(R.id.removeButton);

        Application application = (Application) itemView.getContext().getApplicationContext();
        bookRepository = new BookRepository(application);

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(itemView.getContext(), UpdateBookActivity.class);
            Log.d(TAG, "onClickEvent current book: id " + currentBook.getId() + ", title " + currentBook.getTitle());
            intent.putExtra(SELECTED_BOOK_ID, currentBook.getId());
            intent.putExtra(SELECTED_BOOK, currentBook);
            itemView.getContext().startActivity(intent);
        });

        itemView.setOnLongClickListener(view-> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Selected book will be removed permanently, are you sure?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> bookRepository.delete(currentBook) );
            builder.setNegativeButton("No", (dialogInterface, i) -> Toast.makeText(view.getContext(), "cancelled", Toast.LENGTH_LONG).show());
            AlertDialog ad = builder.create();
            ad.show();
            return true;
        });

        removeButton.setOnClickListener(view-> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Selected book will be removed permanently, are you sure?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> bookRepository.delete(currentBook) );
            builder.setNegativeButton("No", (dialogInterface, i) -> Toast.makeText(view.getContext(), "cancelled", Toast.LENGTH_LONG).show());
            AlertDialog ad = builder.create();
            ad.show();
        });
    }

    public void bind(Book book) {
        currentBook = book;
        String outputText = book.getNumberOfIssue() + ".\t" + book.getTitle();
        bookInfo.setText(outputText);
    }

    static BookViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_book, parent, false);
        return new BookViewHolder(view);
    }
}
