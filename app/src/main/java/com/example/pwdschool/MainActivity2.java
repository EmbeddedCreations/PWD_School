package com.example.pwdschool;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinnerSchool;
    private Spinner spinnerBuilding;
    private Spinner spinnerWorkorder;
    private TextView textViewSelectedDate;
    private Calendar calendar;
    private ImageView iv_imgView;
    private Button pickImageButton;
    private Button buttonUploadImage;
    private ProgressBar loader;
    private static final int CAMERA_CODE = 101;

    // Sample data for school names, workorder names, and building names
    private String[] schoolNames = {"Select School", "School 1", "School 2", "School 3"};
    private String[] workorderNames = {"Select Workorder", "Workorder 1", "Workorder 2", "Workorder 3"};
    private String[] buildingNames = {"Select Building", "Building 1", "Building 2", "Building 3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Find views by their IDs
        spinnerSchool = findViewById(R.id.spinnerSchool);
        spinnerBuilding = findViewById(R.id.spinnerBuilding);
        spinnerWorkorder = findViewById(R.id.spinnerWorkorder);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);
        iv_imgView = findViewById(R.id.image_view);
        pickImageButton = findViewById(R.id.pickimage);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        loader = findViewById(R.id.loader);

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

        // Set button click listener for image upload
        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (iv_imgView.getDrawable() == null) {
                    Toast.makeText(MainActivity2.this, "Please select an image first.", Toast.LENGTH_SHORT).show();
                } else {
                    showLoader();
                    // Simulate a 2-second delay for demonstration purposes
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hideLoader();
                            // Handle image upload after the delay
                            Toast.makeText(MainActivity2.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }, 2000);
                }
            }
        });

        // Set text view click listener for date picker
        textViewSelectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //For Getting Image From gallery
        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MainActivity2.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(720, 720)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Handle spinner item selection
        switch (parent.getId()) {
            case R.id.spinnerSchool:
                String selectedSchool = parent.getItemAtPosition(position).toString();
                // TODO: Handle selected school
                break;
            case R.id.spinnerBuilding:
                String selectedBuilding = parent.getItemAtPosition(position).toString();
                // TODO: Handle selected building
                break;
            case R.id.spinnerWorkorder:
                String selectedWorkorder = parent.getItemAtPosition(position).toString();
                // TODO: Handle selected workorder
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle case when no item is selected
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            Uri uri = data.getData();
            iv_imgView.setImageURI(uri);

            // Get the current date and time
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateTime = sdf.format(Calendar.getInstance().getTime());

            // Set the current date and time to the "Description" TextView
            TextView descriptionTextView = findViewById(R.id.Dimensions);
            descriptionTextView.setText("Desc: " + dateTime);

            String imagePath = FileUtils.getPath(this, uri);
            if (imagePath != null) {
                try {
                    Metadata metadata = ImageMetadataReader.readMetadata(new File(imagePath));
                    ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                    if (directory != null) {
                        String date = directory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                        String aperture = directory.getString(ExifSubIFDDirectory.TAG_APERTURE);
                        String exposureTime = directory.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);

                        // You can use these variables to display or process the EXIF information as needed
                        // For example:
                        Toast.makeText(this, "Date: " + date, Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "Aperture: " + aperture, Toast.LENGTH_SHORT).show();
                        Toast.makeText(this, "Exposure Time: " + exposureTime, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Handle the case where the image path is null (not found)
                Toast.makeText(this, "Image not found or inaccessible.", Toast.LENGTH_SHORT).show();
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

    private void showLoader() {
        loader.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        loader.setVisibility(View.GONE);
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update selected date
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String selectedDate = dateFormat.format(calendar.getTime());

                        textViewSelectedDate.setText("Date: " + selectedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        // Set the maximum date to prevent selecting future dates
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }
}
