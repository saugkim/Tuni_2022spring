package org.tuni.project_vision;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ImageDao {

    @Query("SELECT * FROM image_table")
    LiveData<List<Image>> getAll();

    @Query("SELECT COUNT(id) FROM image_table")
    LiveData<Integer> getCounts();

    @Query("DELETE FROM image_table")
    void deleteAll();

    @Delete
    void delete(Image image);

    @Query("DELETE FROM image_table WHERE id = :id")
    void deleteById(long id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Image image);

    @Update
    void update(Image image);

    @Query("SELECT * FROM image_table WHERE isCorrect == 0")
    LiveData<List<Image>> getInCorrectOnes();

    @Query("SELECT * FROM image_table WHERE isCorrect == 1")
    LiveData<List<Image>> getCorrectOnes();

    @Query("SELECT COUNT(id) FROM image_table WHERE isCorrect == 1")
    LiveData<Integer> getCountsCorrect();

}
