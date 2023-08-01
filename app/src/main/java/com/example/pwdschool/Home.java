package com.example.pwdschool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Home extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Public variables to store user-selected data
    public static String selectedSchool;
    public static String selectedWorkorder;
    public static String selectedBuilding;
    public static String selectedDate;
    // Sample data for school names, workorder names, and building names
    private final String[] schoolNames = {"Select School", "School 1", "School 2", "School 3"};
    private final String[] workorderNames = {"Select Workorder", "Workorder 1", "Workorder 2", "Workorder 3"};
    private final String[] buildingNames = {"Select Building", "Building 1", "Building 2", "Building 3"};
    private Spinner spinnerSchool;
    private Spinner spinnerBuilding;
    private Spinner spinnerWorkorder;
    private TextView textViewSelectedDate;
    private Calendar calendar;
    private Button buttonSurvey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Find views by their IDs
        spinnerSchool = findViewById(R.id.spinnerSchool);
        spinnerBuilding = findViewById(R.id.spinnerBuilding);
        spinnerWorkorder = findViewById(R.id.spinnerWorkorder);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);
        buttonSurvey = findViewById(R.id.buttonSurvey);
        TextView textViewLoggedIn = findViewById(R.id.textViewLoggedIn);
        TextView textViewAtc = findViewById(R.id.atc);
        TextView textViewPoOffice = findViewById(R.id.poOffice);

        ImageView imageViewProfile = findViewById(R.id.imageViewProfile);
        // Set spinner listeners
        spinnerSchool.setOnItemSelectedListener(this);
        spinnerBuilding.setOnItemSelectedListener(this);
        spinnerWorkorder.setOnItemSelectedListener(this);

        // Set up spinner adapters with the arrays
        ArrayAdapter<String> schoolAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, schoolNames);
        schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSchool.setAdapter(schoolAdapter);

        ArrayAdapter<String> workorderAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, workorderNames);
        workorderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkorder.setAdapter(workorderAdapter);

        ArrayAdapter<String> buildingAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, buildingNames);
        buildingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBuilding.setAdapter(buildingAdapter);
        // Get current date
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        // Set current date as the selected date
        textViewSelectedDate.setText(currentDate);

        //set junior engineer loggedin
        String juniorEngineer = Login.selectedJuniorEngineer;
        textViewLoggedIn.setText("Logged in as: " + juniorEngineer);

        // Set a click listener for the "Profile" ImageView
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Profile.class);
                startActivity(intent);
            }
        });

        // set atc office
        String atcOffice = Login.selectedAtcOffice;
        textViewAtc.setText("Atc: " + atcOffice);

        // set po office
        String poOffice = Login.selectedPoOffice;
        textViewPoOffice.setText("PO Office: " + poOffice);

        // Set button click listener for changing screen
        buttonSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected date
                selectedDate = textViewSelectedDate.getText().toString();

                if (selectedSchool.equals("Select School")) {
                    showToast("Please select a school.");
                } else if (selectedBuilding.equals("Select Building")) {
                    showToast("Please select a building.");
                } else if (selectedWorkorder.equals("Select Workorder")) {
                    showToast("Please select a workorder.");
                } else {
                    // All items are selected, start the survey
                    Intent intent = new Intent(Home.this, Upload.class);
                    startActivity(intent);
                }
            }

            private void showToast(String message) {
                Toast.makeText(Home.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Handle spinner item selection
        switch (parent.getId()) {
            case R.id.spinnerSchool:
                selectedSchool = parent.getItemAtPosition(position).toString();
                break;
            case R.id.spinnerBuilding:
                selectedBuilding = parent.getItemAtPosition(position).toString();
                break;
            case R.id.spinnerWorkorder:
                selectedWorkorder = parent.getItemAtPosition(position).toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle case when no item is selected
    }
}
