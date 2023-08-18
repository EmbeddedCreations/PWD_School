package com.example.pwdschool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

public class Login extends AppCompatActivity {
    // Public variables to store user input
    public static String selectedAtcOffice;
    public static String selectedPoOffice;
    public static String selectedJuniorEngineer;
    public static String Password = null, inputPassword;
    // ATC Office initial Array before Reading data from DB
    private static String[] ATC = {"Select ATC Office"};
    //Po-Office initial Array before Reading data from DB
    private static String[] PO_OFFICE = {"Select PO Office"};
    //Junior Engineer initial Array before Reading data from DB
    private static String[] JUNIOR_ENGINEERS = {"Select JE"};
    String line, result;
    InputStream is = null;
    String[] atc_array, po_array, je_array, Pass;
    private final String address = "https://embeddedcreation.in/tribalpwd/admin_panel/app_login_pwd.php";
    private Spinner selectAtcOfficeSpinner;
    private Spinner selectPoOfficeSpinner;
    private Spinner selectJuniorEngineerSpinner;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(!isNetworkAvailable()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cannot Connect To the Server")
                    .setMessage("Please make Sure you have an Internet Connection at the time of Login")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
        }else{
            getData();
        }



        // Find the views by their IDs
        selectAtcOfficeSpinner = findViewById(R.id.select_atc_office);
        selectPoOfficeSpinner = findViewById(R.id.select_po_office);
        selectJuniorEngineerSpinner = findViewById(R.id.select_junior_engineer);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        ArrayList<String> uniqueList = new ArrayList<>();
        HashSet<String> uniqueSet = new HashSet<>();
        uniqueList.add("Select ATC Office");

        for (String element : atc_array) {
            if (!element.equals("Select ATC Office") && !uniqueSet.contains(element)) {
                uniqueList.add(element);
                uniqueSet.add(element);
            }
        }
        ATC = uniqueList.toArray(new String[0]);

        // Set up the adapters for the Spinners
        ArrayAdapter<String> atcAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ATC);
        // Specify the layout to use when the list of choices appears
        atcAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapters to the Spinners
        selectAtcOfficeSpinner.setAdapter(atcAdapter);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Authentication Logic
                inputPassword = passwordEditText.getText().toString();
                if (Password == null || selectedJuniorEngineer == null || selectedPoOffice == null || selectedAtcOffice == null) {
                    Toast.makeText(Login.this, "Incorrect Password or Incorrect Credentials", Toast.LENGTH_SHORT).show();
                } else if (Password.equals(inputPassword)) {
                    Toast.makeText(Login.this, "SuccessFul Login", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Login.this, Home.class);
                    startActivity(i);
                }

            }
        });

        // Set an OnItemSelectedListener to handle the user selection
        selectAtcOfficeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAtcOffice = parent.getItemAtPosition(position).toString();
                ArrayList<String> tempPOList = new ArrayList<>();
                tempPOList.add("Select PO Office");
                for (int i = 0; i < po_array.length; i++) {
                    if (selectedAtcOffice.equals(atc_array[i])) {
                        tempPOList.add(po_array[i]);
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
                selectedPoOffice = parent.getItemAtPosition(position).toString();
                ArrayList<String> tempJeList = new ArrayList<>();
                tempJeList.add("Select JE");
                for (int i = 0; i < je_array.length; i++) {
                    if (selectedAtcOffice.equals(atc_array[i]) && selectedPoOffice.equals(po_array[i])) {
                        tempJeList.add(je_array[i]);
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
                selectedJuniorEngineer = parent.getItemAtPosition(position).toString();
                for (int i = 0; i < Pass.length; i++) {
                    if (selectedJuniorEngineer.equals(je_array[i]) && selectedPoOffice.equals(po_array[i])
                            && selectedAtcOffice.equals(atc_array[i])) {
                        Password = Pass[i];
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

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
