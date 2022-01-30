package org.tuni.roomtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddUserActivity extends AppCompatActivity {

    public static final String TAG = "ZZ AddUserActivity: ";

    EditText editTextName1, editTextName2;
    Button buttonSave;

    UserRepository userRepository = new UserRepository(getApplication());

    /*
    public static final String EXTRA_USERNAME = "org.tuni.RoomTest.AddUser";
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent intent = result.getData();
                assert intent != null;
                String[] name = intent.getStringArrayExtra(EXTRA_USERNAME);
                User user = new User(name[0], name[1]);
                userRepository.insert(user);
            });
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        editTextName1 = findViewById(R.id.editTextfirst);
        editTextName2 = findViewById(R.id.editTextlast);

        buttonSave = findViewById(R.id.button_save);
        buttonSave.setOnClickListener(view -> save());
    }

    void save() {
        Log.d(TAG, "Inside save method");
        Intent goToMainIntent = new Intent(this, MainActivity.class);
        if (TextUtils.isEmpty(editTextName1.getText()) || TextUtils.isEmpty(editTextName2.getText())) {
            editTextName1.requestFocus();
            editTextName2.requestFocus();
            Toast.makeText(getApplicationContext(), "Not saved", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Not saved empty inputs");
            return;
        } else {
            String firstname = editTextName1.getText().toString();
            String lastname = editTextName2.getText().toString();
            User user = new User(firstname, lastname);
            user.setId(userRepository.insert(user));
            startActivity(goToMainIntent);
            //String[] full_name = new String[] {firstname, lastname};
            //replyIntent.putExtra(EXTRA_USERNAME, full_name);
            //launcher.launch(goToMainIntent);
        }
        finish();
    }
}