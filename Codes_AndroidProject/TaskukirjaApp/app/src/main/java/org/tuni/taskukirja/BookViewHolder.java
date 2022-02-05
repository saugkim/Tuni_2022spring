package org.tuni.taskukirja;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = "ZZ BookViewHolder: ";
    public static String SELECTED_BOOK = "org.tuni.taskukirja.selected_book";
    public static String SELECTED_BOOK_ID = "org.tuni.taskukirja.selected_book_id";

    TextView bookInfo;

    Book currentBook;

    public BookViewHolder(@NonNull View itemView) {
        super(itemView);
        bookInfo = itemView.findViewById(R.id.textViewBook);

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(itemView.getContext(), UpdateBookActivity.class);
            Log.d(TAG, "onClickEvent current book: id " + currentBook.getId() + ", title " + currentBook.getTitle());
            intent.putExtra(SELECTED_BOOK_ID, currentBook.getId());
            intent.putExtra(SELECTED_BOOK, currentBook);
            itemView.getContext().startActivity(intent);
        });
    }

    public void bind(Book book) {
        currentBook = book;
        String outputText = book.getNumberOfIssue() + ". " + book.getTitle();

        bookInfo.setText(outputText);
    }

    static BookViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_book, parent, false);
        return new BookViewHolder(view);
    }
}
