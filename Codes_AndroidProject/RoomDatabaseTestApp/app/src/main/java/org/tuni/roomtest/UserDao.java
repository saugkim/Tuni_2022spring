package org.tuni.roomtest;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user_table")
    LiveData<List<User>> getAllUsers();

   // @Query("SELECT * FROM user_table WHERE id == :userid")
   // User getUserById(int userid);

    @Query("DELETE FROM user_table")
    void deleteAll();

    @Delete
    void delete(User user);

    @Query("DELETE FROM user_table WHERE id = :id")
    void deleteById(long id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(User user);

    @Update
    void update(User user);

    @Query("UPDATE user_table SET first_name=:first, last_name=:last WHERE id = :id")
    void updateById(long id, String first, String last);
}