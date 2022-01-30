package org.tuni.roomtest;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    UserRepository userRepository;
    private final LiveData<List<User>> allUsers;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        allUsers = userRepository.getAllUsers();
    }

    LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    //public long insert(User user) {
    //    return userRepository.insert(user);
    //}
}
