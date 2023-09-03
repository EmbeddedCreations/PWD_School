package com.example.pwdschool;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class Profile extends Fragment {

    private final String url = "https://embeddedcreation.in/tribalpwd/adminPanelNewVer2/app_upload_Image.php";
    private ImageView status;
    private NetworkStatusUtility networkStatusUtility;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_profile, container, false);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // Find the TextView elements by their IDs
        TextView atcOfficeText = requireView().findViewById(R.id.atc_office_text);
        TextView poOfficeText = requireView().findViewById(R.id.po_office_text);
        TextView juniorEngineerNameText = requireView().findViewById(R.id.junior_engineer_name_text);
        Button viewHistoryButton = requireView().findViewById(R.id.view_history_button);
        Button logOutButton = requireView().findViewById(R.id.logOutButton);
        status = requireView().findViewById(R.id.statusIcon);
        Button uploadDbButton = requireView().findViewById(R.id.upload_db_button);
        Button viewLocalDBButton = requireView().findViewById(R.id.view_db_button);
        TextView localDbCount = requireView().findViewById(R.id.local_dbCount);
        networkStatusUtility = new NetworkStatusUtility(requireContext());
        updateButtonStatus(networkStatusUtility.isNetworkAvailable());
        networkStatusUtility.startMonitoringNetworkStatus(new NetworkStatusUtility.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                updateButtonStatus(true);  // Update buttons to be visible and clickable
                status.setImageResource(R.drawable.online);
                status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showToast("Online");
                    }
                });
            }

            @Override
            public void onNetworkLost() {
                updateButtonStatus(false);  // Update buttons to be visible and clickable
                status.setImageResource(R.drawable.offline);
                status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showToast("Offline");
                    }
                });
            }
        });

        UploadDatabaseHelper dbHelper = new UploadDatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM uploads WHERE junior_engg = '" + Home.juniorEngineer + "'";
        Cursor countCursor = db.rawQuery(query, null);

        if (countCursor .moveToFirst()) {
            Home.dbCount = countCursor.getInt(0); // Get the count from the first column
            localDbCount.setText("items in Local DB : "+ Home.dbCount);
            // Do something with the count, e.g., display it or use it in your code
        }

        countCursor.close();
        // Get the values from the MainActivity (or any other class where you have stored these values)
        String atcOfficeValue = Home.atcOffice;
        String poOfficeValue = Home.poOffice;
        String juniorEngineerValue = Home.juniorEngineer;

        // Set the values for the TextView elements
        atcOfficeText.setText(atcOfficeValue);
        poOfficeText.setText(poOfficeValue);
        juniorEngineerNameText.setText(juniorEngineerValue);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the progress dialog
                ProgressDialog progressDialog = new ProgressDialog(requireContext());
                progressDialog.setMessage("Logging out...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Clear the stored data from "PWD_App" SharedPreferences
                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("PWD_App", requireActivity().MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("array_key");
                        editor.remove("buildings");
                        editor.remove("schools");
                        editor.apply();

                        // Simulate some tasks being done
                        try {
                            Thread.sleep(1500); // Simulate tasks taking 1 second
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // After tasks are complete, hide the progress dialog
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(requireContext(), "Logged out Successfully", Toast.LENGTH_SHORT).show();

                                // Start the Login activity after successful logout and tasks completion
                                Intent i = new Intent(requireContext(), Login.class);
                                startActivity(i);
                            }
                        });
                    }
                }).start();
            }
        });




        uploadDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event for the "Upload Local DB" button
                // Initiate the database upload process here
                // Create a DatabaseHelper instance and get a readable database
                UploadDatabaseHelper dbHelper = new UploadDatabaseHelper(requireContext());
                SQLiteDatabase db = dbHelper.getReadableDatabase();


                if(Home.dbCount >0){
                    // Perform the query
                    String query2 = "SELECT " + UploadDatabaseHelper.COLUMN_SCHOOL_NAME + ","
                            + UploadDatabaseHelper.COLUMN_PO_OFFICE + ","
                            + UploadDatabaseHelper.COLUMN_JE + ","
                            + UploadDatabaseHelper.COLUMN_BUILDING_NAME + ","
                            + UploadDatabaseHelper.COLUMN_DATE_ADDED + ","
                            + UploadDatabaseHelper.COLUMN_DATE_EXCIF + ","
                            + UploadDatabaseHelper.COLUMN_TIME_EXCIF + ","
                            + UploadDatabaseHelper.COLUMN_LATI + ","
                            + UploadDatabaseHelper.COLUMN_LONGI + ","
                            + UploadDatabaseHelper.COLUMN_DESC + ","
                            + UploadDatabaseHelper.COLUMN_TAGS + ","
                            + UploadDatabaseHelper.COLUMN_IMG + " FROM uploads WHERE junior_engg = '" + Home.juniorEngineer + "'";
                    Cursor cursor = db.rawQuery(query2, null);
                    // Iterate through the cursor to retrieve the data and upload it to the server
                    while (cursor.moveToNext()) {
                        uploadToServer(cursor);
                    }
                    String query = "SELECT COUNT(*) FROM uploads WHERE junior_engg = '" + Home.juniorEngineer + "'";
                    Cursor countCursor = db.rawQuery(query, null);

                    if (countCursor .moveToFirst()) {
                        Home.dbCount = countCursor.getInt(0); // Get the count from the first column
                        localDbCount.setText("items in Local DB : "+ Home.dbCount);
                        // Do something with the count, e.g., display it or use it in your code
                    }
                    // Close the cursor and the database
                    countCursor.close();
                    cursor.close();
                    db.close();
                }else{
                    Toast.makeText(requireContext(), "There is no data in the local database", Toast.LENGTH_SHORT).show();
                }


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(
                            "upload_channel_id",   // Make sure this matches the channel ID used in your code
                            "Upload Channel",
                            NotificationManager.IMPORTANCE_LOW
                    );
                    NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
                // Create a notification when the button is clicked
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(requireContext(), "upload_channel_id")
                        .setSmallIcon(R.drawable.ic_baseline_cloud_upload_24)
                        .setContentTitle("Uploading Local Database")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setOnlyAlertOnce(true);

                // Show the initial notification
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
                notificationManager.notify(0, notificationBuilder.build());
                // Start an AsyncTask or a service for the database upload process
                new UploadTask(notificationManager, notificationBuilder, dbHelper).execute();

            }
        });

        viewLocalDBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Home.dbCount >0){
                    Intent intent = new Intent(requireContext(),DbImageActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(requireContext(),"There is No Data in the local database",Toast.LENGTH_SHORT).show();
                }

            }
        });

        viewHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!networkStatusUtility.isNetworkAvailable()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("Cannot Connect To the Server")
                            .setMessage("Please make Sure you have an Internet Connection to View History")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                } else {
                    Intent intent = new Intent(requireContext(), DisplaySchool.class);
                    startActivity(intent);
                }

            }
        });
    }
    private void uploadToServer(Cursor cursor) {
        String schoolName = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_SCHOOL_NAME));
        String poOffice = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_PO_OFFICE));
        String je = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_JE));
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

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
        deleteEntry(image);
    }

    private void deleteEntry(String img) {
        // Create a DatabaseHelper instance and get a writable database
        UploadDatabaseHelper dbHelper = new UploadDatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Define the selection and selectionArgs to specify the row to delete
        String selection = "upload_img = ?";
        String[] selectionArgs = {img};

        // Perform the delete operation
        int rowsDeleted = db.delete(UploadDatabaseHelper.TABLE_UPLOAD, selection, selectionArgs);

        // Close the database
        db.close();
    }

    private void updateButtonStatus(boolean isNetworkAvailable) {
        Button uploadDbButton = requireView().findViewById(R.id.upload_db_button);
        Button viewHistoryButton = requireView().findViewById(R.id.view_history_button);

        if (isNetworkAvailable) {
            uploadDbButton.setAlpha(1f);
            uploadDbButton.setEnabled(true);
            viewHistoryButton.setAlpha(1f);
            viewHistoryButton.setEnabled(true);
        } else {
            uploadDbButton.setAlpha(0.5f);
            uploadDbButton.setEnabled(false);
            viewHistoryButton.setAlpha(0.5f);
            viewHistoryButton.setEnabled(false);
        }
    }

    private void showToast(String statusText) {
        Toast.makeText(requireContext(), statusText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        networkStatusUtility.stopMonitoringNetworkStatus();
    }
}
