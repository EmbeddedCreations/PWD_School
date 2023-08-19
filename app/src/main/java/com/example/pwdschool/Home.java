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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class Home extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Public variables to store user-selected data
    public static String selectedSchool;
    public static String selectedWorkorder;
    public static String selectedBuilding;
    public static String selectedDate;
    // Sample data for school names, workorder names, and building names
    private static String[] schoolNames = {"Select School"};
    private static String[] buildingNames = {"Select Building"};
    private final String[] workorderNames = {"Select Workorder", "General Inspection", "Workorder related Inspection"};
    private final String school_Address = "https://embeddedcreation.in/tribalpwd/admin_panel/app_school_select.php?atc_office=" + Login.selectedAtcOffice + "&po_office=" + Login.selectedPoOffice;
    String[] schools, school_id, buildings, building_uniq_ids;
    InputStream is_schoool;
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
        getSchoolData();

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
        for (int i = 0; i < schools.length; i++) {
            tempSchoolList.add(schools[i]);
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
        imageViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect the user back to the Login activity
                SharedPreferences sharedPreferences = getSharedPreferences("Auth_Token", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("array_key"); // Remove the stored token
                editor.apply();
                Intent intent = new Intent(Home.this, Login.class);
                startActivity(intent);
                finish(); // Close the current activity
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
                int index = 0;
                for (int i = 0; i < schools.length; i++) {
                    if (selectedSchool.equals(schools[i])) {
                        index = i;
                        break;
                    }
                }
//                String ID = school_id[index];
//                String building_address = "https://embeddedcreation.in/tribalpwd/admin_panel/app_building_select.php?school_id=" + ID;
//                getBuildings(building_address);
                ArrayList<String> tempBuildings = new ArrayList<>();
                tempBuildings.add("Select Building");
                if(buildings != null){
                    for (int i = 0; i < buildings.length; i++) {
                        tempBuildings.add(buildings[i]);
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

    private void getSchoolData() {
        String result = null;
        try {
            URL url = new URL(school_Address);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            is_schoool = new BufferedInputStream(con.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is_schoool));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            is_schoool.close();
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONArray js = new JSONArray(result);
            JSONObject jo = null;
            schools = new String[js.length()];
            school_id = new String[js.length()];
            for (int i = 0; i < js.length(); i++) {
                jo = js.getJSONObject(i);
                schools[i] = jo.getString("school_name");
                school_id[i] = jo.getString("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getBuildings(String address) {
        String result = null;
        try {
            URL url = new URL(address);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            is_schoool = new BufferedInputStream(con.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is_schoool));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            is_schoool.close();
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONArray js = new JSONArray(result);
            JSONObject jo = null;
            buildings = new String[js.length()];
            building_uniq_ids = new String[js.length()];
            for (int i = 0; i < js.length(); i++) {
                jo = js.getJSONObject(i);
                buildings[i] = jo.getString("type_building");
                building_uniq_ids[i] = jo.getString("building_unq_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
