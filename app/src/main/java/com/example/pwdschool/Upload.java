package com.example.pwdschool;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Upload extends AppCompatActivity {

    private static final int CAMERA_CODE = 101;
    private static final int RQS_OPEN_IMAGE = 1;
    public static String description;

    // Define public static variables to store the EXIF information
    public static Date dateTaken;
    public String date_today,time_today;
    public static Date timeTaken;
    public static double gpsLatitude;
    public static double gpsLongitude;
    public String encodedImage;
    private Button pickImageButton;
    private Button buttonUploadImage;
    private ProgressBar loader;
    private EditText editTextDescription;
    private String url = "http://192.168.137.121/app_upload_Image.php";

    Uri targetUri = null;
    TextView textUri;
    TextView textView;
    boolean[] selectedIssues;
    ArrayList<Integer> issueList = new ArrayList<>();
    String[] issueArray = {"Snake", "Grass", "Mud", "rodents", "Insects", "Mosquitoes"};
//

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

        iv_imgView = findViewById(R.id.image_view);
        pickImageButton = findViewById(R.id.pickimage);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        loader = findViewById(R.id.loader);
        TextView textViewLoggedIn = findViewById(R.id.textViewLoggedIn);
        ImageView imageViewProfile = findViewById(R.id.imageViewProfile);
        textUri = findViewById(R.id.Dimensions);
        textUri.setOnClickListener(textUriOnClickListener);
        editTextDescription = findViewById(R.id.editTextDescription);
//
        //set junior engineer loggedin
        String juniorEngineer = Login.selectedJuniorEngineer;
        textViewLoggedIn.setText("Logged in as: " + juniorEngineer);

        // Set a click listener for the "Profile" ImageView
        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Upload.this, Profile.class);
                startActivity(intent);
            }
        });

// Set button click listener for image upload
        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = editTextDescription.getText().toString().trim();
                if (description.isEmpty()) {
                    // User has not entered a description
                    Toast.makeText(Upload.this, "Please enter a description.", Toast.LENGTH_SHORT).show();
                } else if (iv_imgView.getDrawable() == null) {
                    // User has not selected an image
                    Toast.makeText(Upload.this, "Please select an image first.", Toast.LENGTH_SHORT).show();
                } else {
                    // Both description and image are selected, start the upload process
                    showLoader();
                    // Simulate a 2-second delay for demonstration purposes
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideLoader();
                            // Handle image upload after the delay
                            //Toast.makeText(Upload.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();

                            // Save the description in a public static variable for further use
                            String userDescription = editTextDescription.getText().toString();
                            // Save the userDescription in a public static variable for further use
                            Upload.description = userDescription;
                        }
                    }, 2000);
                }
            }
        });
        textView = findViewById(R.id.textViewTags);
        selectedIssues = new boolean[issueArray.length];
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(Upload.this);

                // set title
                builder.setTitle("Select Major Problems");

                // set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(issueArray, selectedIssues, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            // when checkbox selected
                            // Add position  in lang list
                            issueList.add(i);
                            // Sort array list
                            Collections.sort(issueList);
                        } else {
                            // when checkbox unselected
                            // Remove position from langList
                            issueList.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Initialize string builder
                        StringBuilder stringBuilder = new StringBuilder();
                        // use for loop
                        for (int j = 0; j < issueList.size(); j++) {
                            // concat array value
                            stringBuilder.append(issueArray[issueList.get(j)]);
                            // check condition
                            if (j != issueList.size() - 1) {
                                // When j value  not equal
                                // to lang list size - 1
                                // add comma
                                stringBuilder.append(", ");
                            }
                        }
                        // set text on textView
                        textView.setText(stringBuilder.toString());
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
                        // use for loop
                        for (int j = 0; j < selectedIssues.length; j++) {
                            // remove all selection
                            selectedIssues[j] = false;
                            // clear language list
                            issueList.clear();
                            // clear text view value
                            textView.setText("");
                        }
                    }
                });
                // show dialog
                builder.show();
            }
        });
// Set button click listener for image upload
        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = editTextDescription.getText().toString().trim();
                if (description.isEmpty()) {
                    // User has not entered a description
                    Toast.makeText(Upload.this, "Please enter a description.", Toast.LENGTH_SHORT).show();
                } else if (iv_imgView.getDrawable() == null) {
                    // User has not selected an image
                    Toast.makeText(Upload.this, "Please select an image first.", Toast.LENGTH_SHORT).show();
                } else {
                    // Both description and image are selected, start the upload process
                    showLoader();
                    // Simulate a 2-second delay for demonstration purposes
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideLoader();
                            // Handle image upload after the delay
                            //Toast.makeText(Upload.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                            uploadToServer();
                            // Save the description in a public static variable for further use
                            String userDescription = editTextDescription.getText().toString();
                            // Save the userDescription in a public static variable for further use
                            Upload.description = userDescription;
                        }
                    }, 2000);
                }
            }
        });


        //For Getting Image From gallery
        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an AlertDialog with two options
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
        });

    }

    void showExif(Uri photoUri) {
        if (photoUri != null) {
            ParcelFileDescriptor parcelFileDescriptor = null;

            try {
                parcelFileDescriptor = getContentResolver().openFileDescriptor(photoUri, "r");
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

                    // Print the EXIF information
                    System.out.println("GPS Latitude: " + latitude);
                    System.out.println("GPS Longitude: " + longitude);
                    System.out.println("Ref Latitude: " + latitudeRef);
                    System.out.println("Ref Longitude: " + longitudeRef);
                    System.out.println("GPS Latitude (Converted): " + gpsLatitude);
                    System.out.println("GPS Longitude (Converted): " + gpsLongitude);
                }

                // Parse the datetime attribute to separate date and time variables
                String datetime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                if (datetime != null) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
                    try {
                        dateTaken = dateFormat.parse(datetime);
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        timeTaken = timeFormat.parse(datetime.substring(11));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        dateTaken = null;
                        timeTaken = null;
                    }
                }

                parcelFileDescriptor.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e,
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e,
                        Toast.LENGTH_LONG).show();
            }
        }

        // Call the printExifInfo() method here to print the converted values
        printExifInfo();
    }

    void printExifInfo() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        System.out.println("Date Taken: " + (dateTaken != null ? dateFormat.format(dateTaken) : "N/A"));
        date_today = dateFormat.format(dateTaken).toString();
        System.out.println("Time Taken: " + (timeTaken != null ? timeFormat.format(timeTaken) : "N/A"));
        time_today = timeFormat.format(timeTaken).toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || requestCode == ImagePicker.REQUEST_CODE) {
            Uri uri = data.getData();
            targetUri = uri;
            iv_imgView.setImageURI(uri);
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

    private void encodeBitmap(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

        byte[] byteOfImages = byteArrayOutputStream.toByteArray();
        encodedImage = android.util.Base64.encodeToString(byteOfImages, Base64.DEFAULT);

    }

    private void uploadToServer(){
        String school_Name = Home.selectedSchool.trim();
        String po_office = Login.selectedPoOffice.trim();
        String image_name = Home.selectedBuilding.trim();
        String image_type = "jpg";
        String image_pdf = encodedImage;
        String upload_date = date_today;
        String upload_time= time_today;
        String EntryBy = Login.selectedJuniorEngineer.trim();
        String Longitude = Double.toString(gpsLongitude);
        String Latitude = Double.toString(gpsLatitude);
        String user_date = Home.selectedDate;
        String Description = description;
        String Tags = selectedIssues.toString();


        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),"Uploaded Sucesfully",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("school_Name",school_Name);
                map.put("po_office",po_office);
                map.put("image_name",image_name);
                map.put("image_type",image_type);
                map.put("image_pdf",image_pdf);
                map.put("upload_date",upload_date);
                map.put("upload_time",upload_time);
                map.put("EntryBy",EntryBy);
                map.put("Longitude",Longitude);
                map.put("Latitude",Latitude);
                map.put("user_date",user_date);
                map.put("Description",Description);
                map.put("Tags",Tags);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private void showLoader() {
        loader.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        loader.setVisibility(View.GONE);
    }
}
