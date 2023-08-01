package com.example.pwdschool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    // Hardcoded passwords for demonstration purposes
    private static final String ATC_PASS_1 = "atcpass1";
    private static final String ATC_PASS_2 = "atcpass2";

    // Hardcoded Junior Engineer names for demonstration purposes
    private static final String[] JUNIOR_ENGINEERS = {"Select Junior Engineer", "Abhishek", "Deepak", "Kushagra", "Yash"};

    // Public variables to store user input
    public static String selectedAtcOffice;
    public static String selectedPoOffice;
    public static String selectedJuniorEngineer;
    public static String enteredPassword;

    private Spinner selectAtcOfficeSpinner;
    private Spinner selectPoOfficeSpinner;
    private Spinner selectJuniorEngineerSpinner;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find the views by their IDs
        selectAtcOfficeSpinner = findViewById(R.id.select_atc_office);
        selectPoOfficeSpinner = findViewById(R.id.select_po_office);
        selectJuniorEngineerSpinner = findViewById(R.id.select_junior_engineer);
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
        ArrayAdapter<String> jeAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, JUNIOR_ENGINEERS);

        // Specify the layout to use when the list of choices appears
        atcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        poAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapters to the Spinners
        selectAtcOfficeSpinner.setAdapter(atcAdapter);
        selectPoOfficeSpinner.setAdapter(poAdapter);
        selectJuniorEngineerSpinner.setAdapter(jeAdapter);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve input values
                selectedAtcOffice = selectAtcOfficeSpinner.getSelectedItem().toString();
                selectedPoOffice = selectPoOfficeSpinner.getSelectedItem().toString();
                selectedJuniorEngineer = selectJuniorEngineerSpinner.getSelectedItem().toString();
                enteredPassword = passwordEditText.getText().toString().trim();

                // Validate input
                if (selectedAtcOffice.equals("Select ATC Office")
                        || selectedPoOffice.equals("Select PO Office")
                        || selectedJuniorEngineer.equals("Select Junior Engineer")
                        || TextUtils.isEmpty(enteredPassword)) {
                    Toast.makeText(Login.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the password matches the selected ATC office
                    if (selectedAtcOffice.equals("ATC Office 1") && enteredPassword.equals(ATC_PASS_1)) {
                        // Password, ATC office match, proceed to the next activity
                        Intent intent = new Intent(Login.this, Home.class);
                        startActivity(intent);
                        finish(); // Optional: Finish the login activity so the user can't go back to it
                        Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                    } else if (selectedAtcOffice.equals("ATC Office 2") && enteredPassword.equals(ATC_PASS_2)) {
                        // Password, ATC office match, proceed to the next activity
                        Intent intent = new Intent(Login.this, Home.class);
                        startActivity(intent);
                        finish(); // Optional: Finish the login activity so the user can't go back to it
                        Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                    } else {
                        // Incorrect password, ATC office, or Junior Engineer
                        Toast.makeText(Login.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
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

        // Set an OnItemSelectedListener to handle the user selection
        selectJuniorEngineerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Reset the spinner selection to the hint if the user selects the hint item again
                    selectJuniorEngineerSpinner.setSelection(0, false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

// Eye button for password visibility
        ImageView eyeButton = findViewById(R.id.eye_button);
        eyeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
    }

    // Method to toggle password visibility
    private void togglePasswordVisibility() {
        EditText passwordEditText = findViewById(R.id.password);
        if (passwordEditText.getInputType() == 129) { // 129 corresponds to InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordEditText.setInputType(1); // InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            passwordEditText.setInputType(129);
        }
        passwordEditText.setSelection(passwordEditText.getText().length()); // Move cursor to the end of the text
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Closing the PWD App")
                .setMessage("Are you sure?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Login.super.onBackPressed();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Do nothing or add specific handling for cancel
                    }
                })
                .setCancelable(false);

        AlertDialog alert = builder.create();
        alert.show();
    }
}
