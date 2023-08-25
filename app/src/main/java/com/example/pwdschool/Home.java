package com.example.pwdschool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;


public class Home extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Public variables to store user-selected data
    public static String selectedSchool;
    public static String selectedWorkorder;
    public static String selectedBuilding;
    public static String selectedDate;
    public static String juniorEngineer;
    public static String atcOffice;
    public static String poOffice;
    public static String[] schools;
    public static String[] school_id;
    public static String[] all_buildings;
    public static String[] schoolIDBuilding;
    // Sample data for school names, workorder names, and building names
    private static String[] schoolNames = {"Select School"};
    private static String[] buildingNames = {"Select Building"};
    private final String[] workorderNames = {"Select Workorder", "General Inspection", "Workorder related Inspection"};
    public ArrayList<String> buildings;
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

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        //getSchoolData();

        // Find views by their IDs
        spinnerSchool = findViewById(R.id.spinnerSchool);
        spinnerBuilding = findViewById(R.id.spinnerBuilding);
        spinnerWorkorder = findViewById(R.id.spinnerWorkorder);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);
        buttonSurvey = findViewById(R.id.buttonSurvey);
        TextView textViewLoggedIn = findViewById(R.id.textViewLoggedIn);
        TextView textViewAtc = findViewById(R.id.atc);
        TextView textViewPoOffice = findViewById(R.id.poOffice);
        ImageView imageViewLogout = findViewById(R.id.imageViewLogout);
        ImageView imageViewProfile = findViewById(R.id.imageViewProfile);
        // Set spinner listeners
        spinnerSchool.setOnItemSelectedListener(this);
        spinnerBuilding.setOnItemSelectedListener(this);
        spinnerWorkorder.setOnItemSelectedListener(this);

        ArrayList<String> tempSchoolList = new ArrayList<>();
        tempSchoolList.add("Select School");
        if (schools != null) {
            for (int i = 0; i < schools.length; i++) {
                tempSchoolList.add(schools[i]);
            }
        }

        schoolNames = tempSchoolList.toArray(new String[0]);
        // Set up spinner adapters with the arrays
        ArrayAdapter<String> schoolAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, schoolNames);
        schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSchool.setAdapter(schoolAdapter);

        ArrayAdapter<String> workorderAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, workorderNames);
        workorderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkorder.setAdapter(workorderAdapter);


        // Get current date
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        // Set current date as the selected date
        textViewSelectedDate.setText(currentDate);

        //set junior engineer loggedin
        juniorEngineer = Login.selectedJuniorEngineer;
        textViewLoggedIn.setText("Logged in as: " + juniorEngineer);
        Log.d("Home", Login.selectedAtcOffice + ',' + Login.selectedPoOffice + "," + Login.selectedJuniorEngineer);
        // Set a click listener for the "Profile" ImageView
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Profile.class);
                startActivity(intent);
            }
        });
        imageViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                // Clear the stored data from "PWD_App" SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("PWD_App", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("array_key");
                editor.remove("buildings");
                editor.remove("schools");
                editor.apply();
                System.out.println("logout is in process");

                // Create and start the intent to the Login activity
                Intent intent = new Intent(Home.this, Login.class);
                startActivity(intent);
                finish(); // Finish the current activity to prevent going back
            }
        });


        // set atc office
        atcOffice = Login.selectedAtcOffice;
        textViewAtc.setText("Atc: " + atcOffice);

        // set po office
        poOffice = Login.selectedPoOffice;
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
                int index = 0;
                String ID = "";
                if (schools != null) {
                    for (int i = 0; i < schools.length; i++) {
                        if (selectedSchool.equals(schools[i])) {
                            index = i;
                            break;
                        }
                    }
                    if (school_id.length > 0) {
                        ID = school_id[index];
                    }
                }
                Log.d("All the Buildings", Arrays.toString(Home.all_buildings));
                Log.d("All the ID's", Arrays.toString(Home.schoolIDBuilding));
                if (all_buildings.length > 0) {
                    buildings = new ArrayList<>();
                    for (int i = 0; i < all_buildings.length; i++) {
                        if (schoolIDBuilding[i].equals(ID)) {
                            buildings.add(all_buildings[i]);
                        }
                    }
                }
                ArrayList<String> tempBuildings = new ArrayList<>();
                tempBuildings.add("Select Building");
                if (buildings != null) {
                    for (int i = 0; i < buildings.size(); i++) {
                        tempBuildings.add(buildings.get(i));
                    }
                }
                buildingNames = tempBuildings.toArray(new String[0]);
                ArrayAdapter<String> buildingAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, buildingNames);
                buildingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerBuilding.setAdapter(buildingAdapter);
                break;
            case R.id.spinnerBuilding:
                selectedBuilding = parent.getItemAtPosition(position).toString();
                break;
            case R.id.spinnerWorkorder:
                selectedWorkorder = parent.getItemAtPosition(position).toString();
                // Check if the selected value is "Workorder related Inspection"
                if (selectedWorkorder.equals("Workorder related Inspection")) {
                    // Show the second dropdown with the specified values
                    TextView textViewSecondDropdownTitle = findViewById(R.id.textViewSecondDropdownTitle);
                    Spinner spinnerSecondDropdown = findViewById(R.id.spinnerSecondDropdown);
                    textViewSecondDropdownTitle.setVisibility(View.VISIBLE);
                    spinnerSecondDropdown.setVisibility(View.VISIBLE);

                    // Populate the second dropdown with values
                    String[] workorderValues = {"Workorder 1", "Workorder 2", "Workorder 3"};
                    ArrayAdapter<String> secondDropdownAdapter = new ArrayAdapter<>(
                            this, android.R.layout.simple_spinner_item, workorderValues);
                    secondDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSecondDropdown.setAdapter(secondDropdownAdapter);

                    // Set a listener for the second dropdown
                    spinnerSecondDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            // Handle the selection of the second dropdown if needed
                            // For example, you can save the selected value in a variable
                            String selectedSecondDropdownValue = parent.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Handle case when no item is selected in the second dropdown
                        }
                    });
                } else {
                    // Hide the second dropdown if the selected value is not "Workorder related Inspection"
                    TextView textViewSecondDropdownTitle = findViewById(R.id.textViewSecondDropdownTitle);
                    Spinner spinnerSecondDropdown = findViewById(R.id.spinnerSecondDropdown);
                    textViewSecondDropdownTitle.setVisibility(View.GONE);
                    spinnerSecondDropdown.setVisibility(View.GONE);
                }
                break;
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle case when no item is selected
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Closing the PWD App")
                .setMessage("Are you sure?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Home.super.onBackPressed();
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
