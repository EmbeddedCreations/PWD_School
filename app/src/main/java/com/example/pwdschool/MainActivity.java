package com.example.pwdschool;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;

    // Hardcoded username and password for demonstration purposes
    private static final String CORRECT_USERNAME = "admin";
    private static final String CORRECT_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve input values
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validate input
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Check username and password
                    if (username.equals(CORRECT_USERNAME) && password.equals(CORRECT_PASSWORD)) {
                        // Username and password are correct, proceed to the next activity
                        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                        startActivity(intent);
                        finish(); // Optional: Finish the login activity so the user can't go back to it
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    } else if (!username.equals(CORRECT_USERNAME) && !password.equals(CORRECT_PASSWORD)) {
                        // Both username and password are incorrect
                        Toast.makeText(MainActivity.this, "Invalid username and password", Toast.LENGTH_SHORT).show();
                    } else if (!username.equals(CORRECT_USERNAME)) {
                        // Incorrect username
                        Toast.makeText(MainActivity.this, "Invalid username", Toast.LENGTH_SHORT).show();
                    } else {
                        // Incorrect password
                        Toast.makeText(MainActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
