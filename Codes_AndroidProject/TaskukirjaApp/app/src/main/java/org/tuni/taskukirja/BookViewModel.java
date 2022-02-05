package org.tuni.taskukirja;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public class BookViewModel extends AndroidViewModel {

    public static String QUERY_KEY = "org.tuni.taskukirja.query_key";
    SavedStateHandle savedStateHandle;
    BookRepository bookRepository;
    private final LiveData<List<Book>> observableBooks;

    public BookViewModel(@NonNull Application application, @NotNull SavedStateHandle savedStateHandle) {
        super(application);
        this.savedStateHandle = savedStateHandle;

        bookRepository = new BookRepository(application);

        observableBooks = Transformations.switchMap(
                savedStateHandle.getLiveData(QUERY_KEY, null),
                (Function<CharSequence, LiveData<List<Book>>>) query -> {
                    if (TextUtils.isEmpty(query)) {
                        return bookRepository.getAllBooks();
                    } else {
                        Log.d("ZZ ViewModel", "call list from query ");
                        return bookRepository.getBooksByTitle(query.toString());
                    }
                });

    }

    public void setQuery(CharSequence query) {
        savedStateHandle.set(QUERY_KEY, query);
    }

    LiveData<List<Book>> getObservableBooks() {
        return observableBooks;
    }

    public void removeAll() {
        bookRepository.deleteAll();
    }

}
