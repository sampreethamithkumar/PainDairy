package com.example.paindairy.viewmodel;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.paindairy.entity.User;
import com.example.paindairy.repository.UserRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private LiveData<List<User>> allUsers;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        allUsers = userRepository.getAllUsers();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<com.example.paindairy.entity.User> findByIDFuture(final int userId) {
        return userRepository.findByIDFuture(userId);
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }

    public void insert(User user) {
        userRepository.insert(user);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }

    public void update(User user) {
        userRepository.updateUser(user);
    }
}
