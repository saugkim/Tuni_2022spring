package org.tuni.roomtest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static org.tuni.roomtest.UserViewHolder.*;

public class UpdateUserActivity extends AppCompatActivity {

    public static final String TAG = "ZZ UpdateUserActivity: ";
    EditText editTextFirst, editTextLast;
    Button updateButton, deleteButton;

    UserRepository userRepository = new UserRepository(getApplication());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        editTextFirst = findViewById(R.id.updatefirst);
        editTextLast = findViewById(R.id.updatelast);
        updateButton = findViewById(R.id.button_update);
        deleteButton = findViewById(R.id.button_delete);

        long id = getIntent().getLongExtra(SELECTED_USER_ID, 0);
        User user = (User) getIntent().getSerializableExtra(SELECTED_USER);
        if (id != user.getId()) throw new AssertionError();

        Log.d(TAG, "onCreate " + user.getFirstName() + " " + user.getId());

        loadUser(user);

        updateButton.setOnClickListener(view -> update(user.getId()));

        deleteButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(UpdateUserActivity.this);
            builder.setTitle("Are you sure?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> delete(user, user.getId()));
            builder.setNegativeButton("No", (dialogInterface, i) -> Toast.makeText(getApplicationContext(), "DELETE cancelled", Toast.LENGTH_LONG).show());
            AlertDialog ad = builder.create();
            ad.show();
        });
    }

    void loadUser(User user) {
        if (user != null) {
            editTextFirst.setText(user.getFirstName());
            editTextLast.setText(user.getLastName());
        }
    }

    void update(long id) {
        final String sFirst = editTextFirst.getText().toString();
        final String sLast = editTextLast.getText().toString();

        if (sFirst.isEmpty()) {
            editTextFirst.setError("Firstname required");
            editTextFirst.requestFocus();
            return;
        }
        if (sLast.isEmpty()) {
            editTextLast.setError("Lastname required");
            editTextLast.requestFocus();
            return;
        }
        userRepository.updateById(id, sFirst, sLast);
        startActivity(new Intent(this, MainActivity.class));
    }

    void delete(User user, long id) {
        Log.d(TAG, "delete method first line user getId: " + user.getId() + ", input Id: " + id);

        userRepository.deleteById(id);
        Log.d(TAG, "delete method after userViewModel " + user.getFirstName());
        startActivity(new Intent(this, MainActivity.class));
    }
}


    /*
    void updateUser(User user) {
        final String sFirst = editTextFirst.getText().toString().trim();
        final String sLast = editTextLast.getText().toString().trim();

        if (sFirst.isEmpty()) {
            editTextFirst.setError("Firstname required");
            editTextFirst.requestFocus();
            return;
        }
        if (sLast.isEmpty()) {
            editTextLast.setError("Lastname required");
            editTextLast.requestFocus();
            return;
        }
        new BackgroundTask(UpdateUserActivity.this) {
            @Override
            public void doInBackground() {
                user.setFirstName(sFirst);
                user.setLastName(sLast);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .userDao()
                        .update(user);
                Log.d(TAG, "doInBackground " + user.getFirstName());
            }
            @Override
            public void onPostExecute() {
                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
                startActivity(new Intent(UpdateUserActivity.this, MainActivity.class));
            }
        }.execute();
    }
    void removeUser(User user) {
        new BackgroundTask(UpdateUserActivity.this) {
            @Override
            public void doInBackground() {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .userDao()
                        .delete(user);
                //put you background code same like doingBackground
                //Background Thread
            }

            @Override
            public void onPostExecute() {
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                startActivity(new Intent(UpdateUserActivity.this, MainActivity.class));
                //hear is result part same like post execute UI Thread(update your UI widget)
            }
        }.execute();
    }
} */
