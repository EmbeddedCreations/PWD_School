package com.example.pwdschool;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Upload extends AppCompatActivity {

    private static final int CAMERA_CODE = 101;
    private static final int RQS_OPEN_IMAGE = 1;
    public static String description;
    private static final int INITIAL_IMAGE_RESOURCE = R.drawable.uploadfile;

    // Define public static variables to store the EXIF information
    public static Date dateTaken;
    public static Date timeTaken;
    public static double gpsLatitude;
    public static double gpsLongitude;
    private final String url = "https://embeddedcreation.in/tribalpwd/adminPanelNewVer2/app_upload_Image.php";
    public String date_today, time_today;
    public String encodedImage;
    Uri targetUri = null;
    private boolean imageChanged = false;
    TextView textUri;
    TextView textView;
    List<String> selectedIssuesList = new ArrayList<>();
    String[] issueArray = {"Snake", "Grass", "Mud", "rodents", "Insects", "Mosquitoes"};
    private Button pickImageButton;
    private Button buttonUploadImage;
    private Button buttonSaveImage;
    private ProgressBar loader;
    private EditText editTextDescription;
    private ProgressDialog progressDialog;
    private ImageView status;
    private NetworkStatusUtility networkStatusUtility;
    private ImageView iv_imgView;
    View.OnClickListener textUriOnClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (targetUri != null) {
                        Bitmap bm;
                        try {
                            bm = BitmapFactory.decodeStream(
                                    getContentResolver()
                                            .openInputStream(targetUri));
                            iv_imgView.setImageBitmap(bm);
                            encodeBitmap(bm);
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }

            };
    private UploadDatabaseHelper dbHelper;

    // Helper method to convert GPS coordinates from degrees, minutes, seconds to decimal degrees
    private static double convertToDegree(String coordinate, String ref) {
        try {
            String[] parts = coordinate.split(",");

            String[] degreesParts = parts[0].split("/");
            double degrees = Double.parseDouble(degreesParts[0]) / Double.parseDouble(degreesParts[1]);

            String[] minutesParts = parts[1].split("/");
            double minutes = Double.parseDouble(minutesParts[0]) / Double.parseDouble(minutesParts[1]);

            String[] secondsParts = parts[2].split("/");
            double seconds = Double.parseDouble(secondsParts[0]) / Double.parseDouble(secondsParts[1]);

            double result = degrees + minutes / 60.0 + seconds / 3600.0;
            return ref.equals("N") || ref.equals("E") ? result : -result;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        // Initialize the dbHelper
        dbHelper = new UploadDatabaseHelper(this);

        status = findViewById(R.id.statusIcon);
        iv_imgView = findViewById(R.id.image_view);
        pickImageButton = findViewById(R.id.pickimage);
        buttonSaveImage = findViewById(R.id.buttonSaveImage);
        loader = findViewById(R.id.loader);
        TextView textViewLoggedIn = findViewById(R.id.textViewLoggedIn);
        ImageView imageViewProfile = findViewById(R.id.imageViewProfile);
        textUri = findViewById(R.id.Dimensions);
        textUri.setOnClickListener(textUriOnClickListener);
        editTextDescription = findViewById(R.id.editTextDescription);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading, please wait...");
        progressDialog.setCancelable(false);

        // Disable description and tags initially
        editTextDescription.setEnabled(false);
        //set junior engineer loggedin
        String juniorEngineer = Home.juniorEngineer;
        textViewLoggedIn.setText("Logged in as: " + juniorEngineer);
        Log.d("Upload",Home.atcOffice+','+Home.poOffice+","+Home.juniorEngineer);
        // Set a click listener for the "Profile" ImageView
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Upload.this, Profile.class);
                startActivity(intent);
            }
        });

// Find the Upload button and set it initially disabled and faded;
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        buttonUploadImage.setEnabled(false);
        buttonUploadImage.setAlpha(0.5f); // Set the alpha value to make it appear faded

        status = findViewById(R.id.statusIcon);
        networkStatusUtility = new NetworkStatusUtility(this);

        networkStatusUtility.startMonitoringNetworkStatus(new NetworkStatusUtility.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
                runOnUiThread(() -> {
                    status.setImageResource(R.drawable.online);
                    buttonUploadImage.setEnabled(true);
                    buttonUploadImage.setAlpha(1.0f);
                    status.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showToast("Online");
                        }
                    });
                });
            }

            @Override
            public void onNetworkLost() {
                runOnUiThread(() -> {
                    status.setImageResource(R.drawable.offline);
                    buttonUploadImage.setEnabled(false);
                    buttonUploadImage.setAlpha(0.5f);
                    status.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showToast("Offline");
                        }
                    });
                });
            }
        });

// Set button click listener for image upload
        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(Upload.this, "No internet connection available", Toast.LENGTH_SHORT).show();
                    return;
                }
                String description = editTextDescription.getText().toString().trim();
                if (description.isEmpty() || description.equals("")) {
                    // User has not entered a description
                    Toast.makeText(Upload.this, "Please enter a description.", Toast.LENGTH_SHORT).show();
                } else if (iv_imgView.getDrawable() == null) {
                    // User has not selected an image
                    Toast.makeText(Upload.this, "Please select an image first.", Toast.LENGTH_SHORT).show();
                }else if (!imageChanged) {
                    // Display a message to the user indicating they need to select an image
                    Toast.makeText(getApplicationContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    // Save the description in a public static variable for further use
                    Upload.description = description; // Save the description here

                    // Disable the upload button to prevent multiple clicks
                    buttonUploadImage.setEnabled(false);

                    // Show progress dialog to indicate the upload is in progress
                    progressDialog.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            uploadToServer();
                            // Enable the button and reset its alpha after upload
                            buttonUploadImage.setEnabled(true);
                            buttonUploadImage.setAlpha(1.0f);
                        }
                    }, 2000);
                }
            }
        });

        buttonSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = editTextDescription.getText().toString().trim();
                if (description.isEmpty() || description.equals("")) {
                    // User has not entered a description
                    Toast.makeText(Upload.this, "Please enter a description.", Toast.LENGTH_SHORT).show();
                } else if (iv_imgView.getDrawable() == null) {
                    // User has not selected an image
                    Toast.makeText(Upload.this, "Please select an image first.", Toast.LENGTH_SHORT).show();
                }else if (!imageChanged) {
                    // Display a message to the user indicating they need to select an image
                    Toast.makeText(getApplicationContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    // Save the description in a variable
                    Upload.description = description;

                    // Disable the save button to prevent multiple clicks
                    buttonSaveImage.setEnabled(false);

                    // Insert data into the offline database
                    insertDataIntoDatabase();

                    // Show a message to indicate successful insertion
                    Toast.makeText(Upload.this, "Image saved to offline database.", Toast.LENGTH_SHORT).show();

                    // Re-enable the save button
                    buttonSaveImage.setEnabled(true);
                }

            }
        });

        textView = findViewById(R.id.textViewTags);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Upload.this);

                // set title
                builder.setTitle("Select Major Problems");

                // set dialog non-cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(issueArray, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // Update the selected issues list based on user selections
                        if (b) {
                            selectedIssuesList.add(issueArray[i]);
                        } else {
                            selectedIssuesList.remove(issueArray[i]);
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Update the text view with the selected issues
                        textView.setText(TextUtils.join(", ", selectedIssuesList));
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Clear all selections
                        selectedIssuesList.clear();
                        textView.setText("");
                    }
                });

                // show dialog
                builder.show();
            }
        });



// OnClickListener for pickImageButton
        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageOptionsDialog();
            }
        });

// OnClickListener for img_view
        iv_imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageOptionsDialog();
            }
        });


    }

    private void uploadToServer() {
        String school_Name = Home.selectedSchool.trim();
        String po_office = Home.poOffice.trim();
        String image_name = Home.selectedBuilding.trim();
        String image_type = "jpg";
        String image_pdf = encodedImage;
        String upload_date = date_today;
        String upload_time = time_today;
        String EntryBy = Home.juniorEngineer.trim();
        String Longitude = Double.toString(gpsLongitude);
        String Latitude = Double.toString(gpsLatitude);
        String user_upload_date = Home.selectedDate;
        String Description = description;
        String Tags = Arrays.toString(selectedIssuesList.toArray());
        Tags = Tags.substring(1, Tags.length() - 1);
        String finalTags = Tags;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                editTextDescription.setText("");
                iv_imgView.setImageResource(INITIAL_IMAGE_RESOURCE); // Reset to the initial image
                selectedIssuesList.clear();
                textView.setText("");
                imageChanged = false;
                // Re-enable the "Upload" button after the upload is completed
                buttonUploadImage.setEnabled(true);

                // Dismiss the progress dialog
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Uploaded Sucesfully", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Re-enable the "Upload" button after the upload is completed
                buttonUploadImage.setEnabled(true);
                imageChanged = true;
                // Dismiss the progress dialog
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Upload Failed, try again", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("school_Name", school_Name);
                map.put("po_office", po_office);
                map.put("image_name", image_name);
                map.put("image_type", image_type);
                map.put("image_pdf", image_pdf);
                map.put("upload_date", upload_date);
                map.put("upload_time", upload_time);
                map.put("EntryBy", EntryBy);
                map.put("Longitude", Longitude);
                map.put("Latitude", Latitude);
                map.put("user_upload_date", user_upload_date);
                map.put("Description", Description);
                map.put("Tags", finalTags);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    void showExif(Uri photoUri) {
        if (photoUri != null) {
            try (ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(photoUri, "r")) {
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                ExifInterface exifInterface = new ExifInterface(fileDescriptor);

                // Extract and store the EXIF information in proper variables
                String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                String latitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                String longitudeRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

                // Convert latitude and longitude to double values
                if (latitude != null && latitudeRef != null && longitude != null && longitudeRef != null) {
                    gpsLatitude = convertToDegree(latitude, latitudeRef);
                    gpsLongitude = convertToDegree(longitude, longitudeRef);
                }

                // Parse the datetime attribute to separate date and time variables
                String datetime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                if (datetime != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
                    try {
                        dateTaken = dateFormat.parse(datetime);
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        timeTaken = timeFormat.parse(datetime.substring(11));
                        date_today = dateFormat.format(dateTaken);
                        time_today = timeFormat.format(timeTaken);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        dateTaken = null;
                        timeTaken = null;
                        date_today = null;
                        time_today = null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || requestCode == ImagePicker.REQUEST_CODE) {
            Uri uri = data.getData();
            // Enable description and tags input fields after image selection
            editTextDescription.setEnabled(true);
            targetUri = uri;
            iv_imgView.setImageURI(uri);
            imageChanged = true;
            try {
                encodeBitmap(BitmapFactory.decodeStream(getApplicationContext().getContentResolver().openInputStream(uri)));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            showExif(targetUri);

            Uri dataUri = data.getData();
            if (requestCode == RQS_OPEN_IMAGE) {
                targetUri = dataUri;
                iv_imgView.setImageURI(uri);
                showExif(targetUri);
                imageChanged = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "PERMISSION Denied ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void encodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] byteOfImages = byteArrayOutputStream.toByteArray();
        encodedImage = android.util.Base64.encodeToString(byteOfImages, Base64.DEFAULT);

    }

    // Add this method to insert data into the offline database
    private void insertDataIntoDatabase() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        String Tags = Arrays.toString(selectedIssuesList.toArray());
        Tags = Tags.substring(1, Tags.length() - 1);
        String finalTags = Tags;
        values.put(UploadDatabaseHelper.COLUMN_SCHOOL_NAME, Home.selectedSchool.trim());
        values.put(UploadDatabaseHelper.COLUMN_PO_OFFICE, Home.poOffice.trim());
        values.put(UploadDatabaseHelper.COLUMN_ATC_OFFICE, Login.selectedAtcOffice.trim());
        values.put(UploadDatabaseHelper.COLUMN_JE, Home.juniorEngineer.trim());
        values.put(UploadDatabaseHelper.COLUMN_VISIT_TYPE, Home.selectedWorkorder);
        values.put(UploadDatabaseHelper.COLUMN_BUILDING_NAME, Home.selectedBuilding.trim());
        values.put(UploadDatabaseHelper.COLUMN_DATE_ADDED, Home.selectedDate);
        values.put(UploadDatabaseHelper.COLUMN_DATE_EXCIF, date_today);
        values.put(UploadDatabaseHelper.COLUMN_TIME_EXCIF, time_today);
        values.put(UploadDatabaseHelper.COLUMN_LATI, Upload.gpsLatitude);
        values.put(UploadDatabaseHelper.COLUMN_LONGI, Upload.gpsLongitude);
        values.put(UploadDatabaseHelper.COLUMN_DESC, Upload.description);
        values.put(UploadDatabaseHelper.COLUMN_TAGS, finalTags);
        values.put(UploadDatabaseHelper.COLUMN_IMG, encodedImage);

        try {
            // Insert the data
            long newRowId = db.insertOrThrow(UploadDatabaseHelper.TABLE_UPLOAD, null, values);

            if (newRowId != -1) {
                editTextDescription.setText("");
                iv_imgView.setImageResource(INITIAL_IMAGE_RESOURCE); // Reset to the initial image
                imageChanged = false;
                selectedIssuesList.clear();
                textView.setText("");
                Toast.makeText(getApplicationContext(), "Inserted in DB Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error in saving the data", Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            // Handle the exception here, you can log it or show a specific error message
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
    private void showToast(String statusText) {
        Toast.makeText(getApplicationContext(), statusText, Toast.LENGTH_SHORT).show();
    }

    // Method to show the options dialog for capturing or selecting an image
    private void showImageOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Upload.this);
        builder.setTitle("Choose an option")
                .setItems(new String[]{"Capture from Camera", "Select from Gallery"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // "Capture from Camera" option is selected
                                ImagePicker.with(Upload.this)
                                        .cameraOnly()
                                        .crop()
                                        .compress(1024)
                                        .maxResultSize(720, 720)
                                        .start();
                                break;
                            case 1:
                                // "Select from Gallery" option is selected
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.setType("image/*"); // Allow all types of images (png, jpg, jpeg)
                                startActivityForResult(intent, RQS_OPEN_IMAGE);
                                break;
                        }
                    }
                });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
        networkStatusUtility.stopMonitoringNetworkStatus();
    }

}
