package com.example.pwdschool;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Login extends AppCompatActivity {
    private static final int flag = 0;
    // Public variables to store user input
    public static String selectedAtcOffice;
    public static String selectedPoOffice;
    public static String selectedJuniorEngineer;
    public static String Password = null, inputPassword;
    // ATC Office initial Array before Reading data from DB
    private static String[] ATC = {"Select ATC Office"};
    private static JSONArray js_Schools, js_Buildings;
    //Po-Office initial Array before Reading data from DB
    private static String[] PO_OFFICE = {"Select PO Office"};
    //Junior Engineer initial Array before Reading data from DB
    private static String[] JUNIOR_ENGINEERS = {"Select JE"};
    private static String school_Address;
    private static String[] retrievedArray;
    private final String address = "https://embeddedcreation.in/tribalpwd/adminPanelNewVer2/app_login_pwd.php";
    private final String building_address = "https://embeddedcreation.in/tribalpwd/adminPanelNewVer2/app_building_select.php";
    SharedPreferences sharedPreferences;
    InputStream is_school;
    String line, result;
    InputStream is = null;
    String[] atc_array, po_array, je_array, Pass;
    private Spinner selectAtcOfficeSpinner;
    private Spinner selectPoOfficeSpinner;
    private Spinner selectJuniorEngineerSpinner;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar loader;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("PWD_App", MODE_PRIVATE);
        String jsonArrayString = sharedPreferences.getString("array_key", "");
        String schoolArrayString = sharedPreferences.getString("schools", "");
        String buildingArrayString = sharedPreferences.getString("buildings", "");
        if (!jsonArrayString.equals("")) {
            try {
                JSONArray jsonArray = new JSONArray(jsonArrayString);
                retrievedArray = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    retrievedArray[i] = jsonArray.getString(i);
                }
                selectedAtcOffice = retrievedArray[0];
                selectedPoOffice = retrievedArray[1];
                selectedJuniorEngineer = retrievedArray[2];
                if (!schoolArrayString.equals("")) {
                    JSONArray schoolJsonArray = new JSONArray(schoolArrayString);
                    JSONObject jo = null;
                    Home.schools = new String[schoolJsonArray.length()];
                    Home.school_id = new String[schoolJsonArray.length()];
                    for (int i = 0; i < schoolJsonArray.length(); i++) {
                        jo = schoolJsonArray.getJSONObject(i);
                        Home.schools[i] = jo.getString("school_name");
                        Home.school_id[i] = jo.getString("id");
                    }
                    JSONArray buildingJsonArray = new JSONArray(buildingArrayString);
                    JSONObject jso = null;
                    Home.all_buildings = new String[buildingJsonArray.length()];
                    Home.schoolIDBuilding = new String[buildingJsonArray.length()];
                    for (int i = 0; i < buildingJsonArray.length(); i++) {
                        jso = buildingJsonArray.getJSONObject(i);
                        Home.all_buildings[i] = jso.getString("type_building");
                        Home.schoolIDBuilding[i] = jso.getString("unq_id");
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // Check for internet connection and show alert dialog if not available
            if (!isNetworkAvailable()) {
                showNoInternetDialog();
            } else {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
                getData();
            }
        }

        // Find the views by their IDs
        selectAtcOfficeSpinner = findViewById(R.id.select_atc_office);
        selectPoOfficeSpinner = findViewById(R.id.select_po_office);
        selectJuniorEngineerSpinner = findViewById(R.id.select_junior_engineer);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        loader = findViewById(R.id.loader);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        ArrayList<String> uniqueList = new ArrayList<>();
        HashSet<String> uniqueSet = new HashSet<>();
        uniqueList.add("Select ATC Office");
        if (atc_array != null) {
            for (String element : atc_array) {
                if (!element.equals("Select ATC Office") && !uniqueSet.contains(element)) {
                    uniqueList.add(element);
                    uniqueSet.add(element);
                }
            }
        }
        ATC = uniqueList.toArray(new String[0]);

        // Set up the adapters for the Spinners
        ArrayAdapter<String> atcAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ATC);
        // Specify the layout to use when the list of choices appears
        atcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapters to the Spinners
        selectAtcOfficeSpinner.setAdapter(atcAdapter);
        // Set an OnItemSelectedListener to handle the user selection
        selectAtcOfficeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (flag == 0) {
                    selectedAtcOffice = parent.getItemAtPosition(position).toString();
                }
                ArrayList<String> tempPOList = new ArrayList<>();
                tempPOList.add("Select PO Office");
                if (po_array != null) {
                    for (int i = 0; i < po_array.length; i++) {
                        if (selectedAtcOffice.equals(atc_array[i])) {
                            tempPOList.add(po_array[i]);
                        }
                    }
                }

                PO_OFFICE = tempPOList.toArray(new String[0]);
                ArrayAdapter<String> poAdapter = new ArrayAdapter<>(Login.this, android.R.layout.simple_spinner_item, PO_OFFICE);
                poAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectPoOfficeSpinner.setAdapter(poAdapter);
                if (position == 0) {
                    // Reset the spinner selection to the Select if the user selects the hint item again
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
                if (flag == 0) {
                    selectedPoOffice = parent.getItemAtPosition(position).toString();
                }
                ArrayList<String> tempJeList = new ArrayList<>();
                tempJeList.add("Select JE");
                if (je_array != null) {
                    for (int i = 0; i < je_array.length; i++) {
                        if (selectedAtcOffice.equals(atc_array[i]) && selectedPoOffice.equals(po_array[i])) {
                            tempJeList.add(je_array[i]);
                        }
                    }
                }
                JUNIOR_ENGINEERS = tempJeList.toArray(new String[0]);
                ArrayAdapter<String> jeAdapter = new ArrayAdapter<>(Login.this, android.R.layout.simple_spinner_item, JUNIOR_ENGINEERS);
                jeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectJuniorEngineerSpinner.setAdapter(jeAdapter);
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
                if (flag == 0) {
                    selectedJuniorEngineer = parent.getItemAtPosition(position).toString();
                }
                if (Pass != null) {
                    for (int i = 0; i < Pass.length; i++) {
                        if (selectedJuniorEngineer.equals(je_array[i]) && selectedPoOffice.equals(po_array[i])
                                && selectedAtcOffice.equals(atc_array[i])) {
                            Password = Pass[i];
                        }
                    }
                }
                if (position == 0) {
                    // Reset the spinner selection to the hint if the user selects the hint item again
                    selectJuniorEngineerSpinner.setSelection(0, false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // ye kis liye daala?
        if (!jsonArrayString.equals("")) {
            Log.d("why", jsonArrayString);
            Intent i = new Intent(Login.this, Home.class);
            startActivity(i);
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Authentication Logic

                inputPassword = passwordEditText.getText().toString();

                if (Password == null || selectedJuniorEngineer == null || selectedPoOffice == null || selectedAtcOffice == null) {
                    Toast.makeText(Login.this, "Incorrect Password or Incorrect Credentials", Toast.LENGTH_SHORT).show();
                } else if (Password.equals(inputPassword)) {
                    progressDialog.show(); // Show the progress dialog

                    // Perform tasks and network operations here
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // Update shared preferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String[] myArray = {selectedAtcOffice, selectedPoOffice, selectedJuniorEngineer};
                            JSONArray jsonArray2 = new JSONArray(Arrays.asList(myArray));
                            String jsonArrayString2 = jsonArray2.toString();
                            editor.putString("array_key", jsonArrayString2);
                            editor.apply();

                            // Fetch school data
                            school_Address = "https://embeddedcreation.in/tribalpwd/adminPanelNewVer2/app_school_select.php?atc_office=" + selectedAtcOffice + "&po_office=" + selectedPoOffice;
                            getSchoolData();
                            editor.putString("schools", js_Schools.toString());
                            editor.apply();

                            // Fetch building data
                            getBuildings();
                            Log.d("js", js_Buildings.toString());
                            editor.putString("buildings", js_Buildings.toString());
                            editor.apply();

                            // After tasks are complete, hide the progress dialog
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(Login.this, "Successful Login", Toast.LENGTH_SHORT).show();

                                    // Start the Home activity after successful login and tasks completion
                                    Intent i = new Intent(Login.this, Home.class);
                                    startActivity(i);
                                }
                            });
                        }
                    }).start();
                }
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!isNetworkAvailable()) {
            showNoInternetDialog();
        } else {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
            getData();
        }
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

    //Function to get Data from php script which gets data from mysql database
    public void getData() {

        try {
            URL url = new URL(address);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            is = new BufferedInputStream(con.getInputStream());
            //Reading it into a string
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONArray js = new JSONArray(result);
            JSONObject jo = null;
            atc_array = new String[js.length()];
            po_array = new String[js.length()];
            je_array = new String[js.length()];
            Pass = new String[js.length()];
            for (int i = 0; i < js.length(); i++) {
                jo = js.getJSONObject(i);
                atc_array[i] = jo.getString("atc_office");
                po_array[i] = jo.getString("po_office");
                je_array[i] = jo.getString("username");
                Pass[i] = jo.getString("password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getSchoolData() {
        String result = null;
        try {
            URL url = new URL(school_Address);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            is_school = new BufferedInputStream(con.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is_school));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            is_school.close();
            result = sb.toString();
            js_Schools = new JSONArray(result);
            JSONObject jo = null;
            Home.schools = new String[js_Schools.length()];
            Home.school_id = new String[js_Schools.length()];
            for (int i = 0; i < js_Schools.length(); i++) {
                jo = js_Schools.getJSONObject(i);
                Home.schools[i] = jo.getString("school_name");
                Home.school_id[i] = jo.getString("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getBuildings() {
        String result = null;
        try {
            URL url = new URL(building_address);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            is_school = new BufferedInputStream(con.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is_school));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            is_school.close();
            result = sb.toString();
            js_Buildings = new JSONArray(result);
            JSONObject jso = null;
            Home.all_buildings = new String[js_Buildings.length()];
            Home.schoolIDBuilding = new String[js_Buildings.length()];
            for (int i = 0; i < js_Buildings.length(); i++) {
                jso = js_Buildings.getJSONObject(i);
                Home.all_buildings[i] = jso.getString("type_building");
                Home.schoolIDBuilding[i] = jso.getString("unq_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Method to show the no-internet alert dialog
    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection")
                .setMessage("Please make sure you have an active internet connection.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish(); // Close the app
                    }
                })
                .setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
