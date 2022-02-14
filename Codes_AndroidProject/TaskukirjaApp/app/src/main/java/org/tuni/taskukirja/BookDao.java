package org.tuni.taskukirja;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM library ORDER BY issue")
    LiveData<List<Book>> getAllBooks();

    @Query("SELECT * FROM library WHERE title LIKE :search ORDER BY issue")
    LiveData<List<Book>> getBooksByTitle(String search);

    @Query("DELETE FROM library")
    void deleteAll();

    @Delete
    void delete(Book book);

    @Query("DELETE FROM library WHERE id = :id")
    void deleteById(long id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Book book);

    @Update
    void update(Book book);

    @Query("UPDATE library SET issue=:issue, title=:title, pages=:pages, publish_year=:year, purchase_date=:date WHERE id = :id")
    void updateById(long id, int issue, String title, String pages, String year, String date);
}