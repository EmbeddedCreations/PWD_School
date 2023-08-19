package com.example.pwdschool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Find the TextView elements by their IDs
        TextView atcOfficeText = findViewById(R.id.atc_office_text);
        TextView poOfficeText = findViewById(R.id.po_office_text);
        TextView juniorEngineerNameText = findViewById(R.id.junior_engineer_name_text);

        // Get the values from the MainActivity (or any other class where you have stored these values)
        String atcOfficeValue = Login.selectedAtcOffice;
        String poOfficeValue = Login.selectedPoOffice;
        String juniorEngineerValue = Login.selectedJuniorEngineer;

        // Set the values for the TextView elements
        atcOfficeText.setText(atcOfficeValue);
        poOfficeText.setText(poOfficeValue);
        juniorEngineerNameText.setText(juniorEngineerValue);
        Button viewHistoryButton = findViewById(R.id.view_history_button);
        Button logOutButton = findViewById(R.id.logOutButton);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("Auth_Token", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("array_key"); // Remove the stored token
                editor.apply();
                Intent intent = new Intent(Profile.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        // Set a click listener for the button
        viewHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isNetworkAvailable()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                    builder.setTitle("Cannot Connect To the Server")
                            .setMessage("Please make Sure you have an Internet Connection to View History")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                }else{
                    Intent intent = new Intent(Profile.this, DisplaySchool.class);
                    startActivity(intent);
                }

            }
        });
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
