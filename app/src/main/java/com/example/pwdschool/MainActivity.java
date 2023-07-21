package com.example.pwdschool;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Hardcoded username and password for demonstration purposes
    private static final String CORRECT_USERNAME = "admin";
    private static final String CORRECT_PASSWORD = "password";

    private Spinner selectAtcOfficeSpinner;
    private Spinner selectPoOfficeSpinner;
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the views by their IDs
        selectAtcOfficeSpinner = findViewById(R.id.select_atc_office);
        selectPoOfficeSpinner = findViewById(R.id.select_po_office);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        // Define the arrays directly in the code
        String[] atcOfficeArray = new String[]{"Select ATC Office", "ATC Office 1", "ATC Office 2"};
        String[] poOfficeArray = new String[]{"Select PO Office", "PO Office 1", "PO Office 2"};

        // Set up the adapters for the Spinners
        ArrayAdapter<String> atcAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, atcOfficeArray);
        ArrayAdapter<String> poAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, poOfficeArray);

        // Specify the layout to use when the list of choices appears
        atcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        poAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapters to the Spinners
        selectAtcOfficeSpinner.setAdapter(atcAdapter);
        selectPoOfficeSpinner.setAdapter(poAdapter);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve input values
                String atcOffice = selectAtcOfficeSpinner.getSelectedItem().toString();
                String poOffice = selectPoOfficeSpinner.getSelectedItem().toString();
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validate input
                if (atcOffice.equals("Select ATC Office") || poOffice.equals("Select PO Office")
                        || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                } else {
                    // Check username and password
                    if (username.equals(CORRECT_USERNAME) && password.equals(CORRECT_PASSWORD)) {
                        // Username and password are correct, proceed to the next activity
                        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                        startActivity(intent);
                        finish(); // Optional: Finish the login activity so the user can't go back to it
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    } else {
                        // Incorrect username or password
                        Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Set an OnItemSelectedListener to handle the user selection
        selectAtcOfficeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Reset the spinner selection to the hint if the user selects the hint item again
                    selectAtcOfficeSpinner.setSelection(0, false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set an OnItemSelectedListener to handle the user selection
        selectPoOfficeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Reset the spinner selection to the hint if the user selects the hint item again
                    selectPoOfficeSpinner.setSelection(0, false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
