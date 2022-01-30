package org.tuni.roomtest;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class UserRepository {

    public static String TAG = "ZZ UserRepository ";
    UserDao userDao;
    LiveData<List<User>> users;

    UserRepository(Application application) {
        userDao = AppDatabase.getDatabase(application).userDao();
        users = userDao.getAllUsers();
    }

    public LiveData<List<User>> getAllUsers() {
        return users;
    }

    public long insert(User user) {
        AppDatabase.databaseWriteExecutor.execute(()-> {
             long id = userDao.insert(user);
             user.setId(id);
        });
        return user.getId();
    }

    public void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(()-> userDao.deleteAll());
    }
    public void delete(User user) {
        Log.d(TAG, "delete method begin");
        AppDatabase.databaseWriteExecutor.execute(()-> userDao.delete(user));
        Log.d(TAG, "delete method after execute ");
    }
    public void deleteById(long id) {
        Log.d(TAG, "deleteById method before execute id? " + id);
        AppDatabase.databaseWriteExecutor.execute(()-> userDao.deleteById(id));
    }

    public void update(User user) {
        AppDatabase.databaseWriteExecutor.execute(()-> userDao.update(user));
    }
    public void updateById(long id, String name1, String name2) {
        AppDatabase.databaseWriteExecutor.execute(()-> userDao.updateById(id, name1, name2));
    }
}
