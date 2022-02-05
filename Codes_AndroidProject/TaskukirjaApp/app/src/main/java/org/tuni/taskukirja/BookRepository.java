package org.tuni.taskukirja;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

public class BookRepository {

    public static String TAG = "ZZ BookRepository ";
    BookDao bookDao;
    MediatorLiveData<List<Book>> books;

    BookRepository(Application application) {
        bookDao = BookDatabase.getDatabase(application).bookDao();
        books = new MediatorLiveData<>();
        books.addSource(bookDao.getAllBooks(),
                mBooks -> books.postValue(mBooks) );
    }

    public LiveData<List<Book>> getAllBooks() {
        return books;
    }

    public long insert(Book book) {
        BookDatabase.databaseWriteExecutor.execute(()-> {
             long id = bookDao.insert(book);
             book.setId(id);
        });
        return book.getId();
    }

    public LiveData<List<Book>> getBooksByTitle(String query) {
        //BookDatabase.databaseWriteExecutor.execute(()-> {
        //   selectedBooks = bookDao.getBooksByTitle(keyword);
        //});
        Log.d(TAG, "inside getBooksByTitle()");
        return bookDao.getBooksByTitle(query);
    }

    public void deleteAll() {
        BookDatabase.databaseWriteExecutor.execute(()-> bookDao.deleteAll());
    }
    public void delete(Book book) {
        BookDatabase.databaseWriteExecutor.execute(()->bookDao.delete(book));
    }
    public void update(Book book) {
        BookDatabase.databaseWriteExecutor.execute(()-> bookDao.update(book));
    }

    public void deleteById(long id) {
        Log.d(TAG, "deleteById method before execute id? " + id);
        BookDatabase.databaseWriteExecutor.execute(()-> bookDao.deleteById(id));
    }
    public void updateByID(long id, int issue, String title, String pages, String year, String date) {
        BookDatabase.databaseWriteExecutor.execute(()-> bookDao.updateById(id, issue, title, pages, year, date));
    }
}
