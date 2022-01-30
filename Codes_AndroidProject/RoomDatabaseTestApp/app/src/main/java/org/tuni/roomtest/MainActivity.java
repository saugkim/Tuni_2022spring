package org.tuni.roomtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "ZZ MainActivity";

    FloatingActionButton buttonAddUser;
    RecyclerView recyclerView;

    UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        buttonAddUser = findViewById(R.id.buttonAdd);

        final UserAdapter adapter = new UserAdapter(new UserAdapter.UserDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getAllUsers().observe(this, adapter::submitList);

        buttonAddUser.setOnClickListener(view -> {
            Log.d(TAG, "add user button pressed");
            Intent intent = new Intent(this, AddUserActivity.class);
            startActivity(intent);
        });
    }
}