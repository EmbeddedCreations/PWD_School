package com.example.pwdschool;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private final String url = "https://embeddedcreation.in/tribalpwd/adminPanelNewVer2/app_upload_Image.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
//        UploadDatabaseHelper dbHelper = new UploadDatabaseHelper(this);

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
        UploadDatabaseHelper dbHelper = new UploadDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        logOutButton.setOnClickListener(new View.OnClickListener() {
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
                Intent intent = new Intent(Profile.this, Login.class);
                startActivity(intent);
                finish(); // Finish the current activity to prevent going back
            }
        });




        Button uploadDbButton = findViewById(R.id.upload_db_button);
        uploadDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for the "Upload Local DB" button
                // Initiate the database upload process here
                // Create a DatabaseHelper instance and get a readable database
                UploadDatabaseHelper dbHelper = new UploadDatabaseHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getReadableDatabase();




                // Perform the query
                String query = "SELECT "+UploadDatabaseHelper.COLUMN_SCHOOL_NAME+","
                        +UploadDatabaseHelper.COLUMN_PO_OFFICE +","
                        +UploadDatabaseHelper.COLUMN_JE+","
                        +UploadDatabaseHelper.COLUMN_BUILDING_NAME+","
                        +UploadDatabaseHelper.COLUMN_DATE_ADDED+","
                        +UploadDatabaseHelper.COLUMN_DATE_EXCIF+","
                        +UploadDatabaseHelper.COLUMN_TIME_EXCIF+","
                        +UploadDatabaseHelper.COLUMN_LATI+","
                        +UploadDatabaseHelper.COLUMN_LONGI+","
                        +UploadDatabaseHelper.COLUMN_DESC+","
                        +UploadDatabaseHelper.COLUMN_TAGS+","
                        +UploadDatabaseHelper.COLUMN_IMG + " FROM uploads";
                Cursor cursor = db.rawQuery(query, null);
                // Iterate through the cursor to retrieve the data and upload it to the server
                while (cursor.moveToNext()) {
                    uploadToServer(cursor);

                }
                // Close the cursor and the database
                cursor.close();
                db.close();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(
                            "upload_channel_id",   // Make sure this matches the channel ID used in your code
                            "Upload Channel",
                            NotificationManager.IMPORTANCE_LOW
                    );
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
                // Create a notification when the button is clicked
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(Profile.this, "upload_channel_id")
                        .setSmallIcon(R.drawable.ic_baseline_cloud_upload_24)
                        .setContentTitle("Uploading Local Database")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setOnlyAlertOnce(true);

                // Show the initial notification
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Profile.this);
                notificationManager.notify(0, notificationBuilder.build());
                // Start an AsyncTask or a service for the database upload process
                new UploadTask(notificationManager, notificationBuilder, dbHelper).execute();

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

    private void uploadToServer(Cursor cursor) {
        String schoolName = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_SCHOOL_NAME));
        String poOffice = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_PO_OFFICE));
        //String atcOffice = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_ATC_OFFICE));
        String je = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_JE));
        //String visitType = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_VISIT_TYPE));
        String buildingName = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_BUILDING_NAME));
        String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_DATE_ADDED));
        String dateExcif = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_DATE_EXCIF));
        String timeExcif = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_TIME_EXCIF));
        String latitude = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_LATI));
        String longitude = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_LONGI));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_DESC));
        String tags = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_TAGS));
        String image = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_IMG));

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
              //TODO Write Code for response.
                deleteEntry(image);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               //TODO Write Code For Error From Server.

            }
        }) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("school_Name", schoolName);
                map.put("po_office", poOffice);
                map.put("image_name", buildingName);
                map.put("image_type", "jpg");
                map.put("image_pdf", image);
                map.put("upload_date", dateExcif);
                map.put("upload_time", timeExcif);
                map.put("EntryBy", je);
                map.put("Longitude", longitude);
                map.put("Latitude", latitude);
                map.put("user_upload_date", dateAdded);
                map.put("Description", description);
                map.put("Tags", tags);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
    private void deleteEntry(String img) {
        // Create a DatabaseHelper instance and get a writable database
        UploadDatabaseHelper dbHelper = new UploadDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define the selection and selectionArgs to specify the row to delete
        String selection = "upload_img = ?";
        String[] selectionArgs = {img};

        // Perform the delete operation
        int rowsDeleted = db.delete(UploadDatabaseHelper.TABLE_UPLOAD, selection, selectionArgs);

        // Close the database
        db.close();
    }
}
