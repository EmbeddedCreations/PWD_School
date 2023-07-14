package com.example.pwdschool;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinnerSchool;
    private Spinner spinnerBuilding;
    private TextView textViewSelectedDate;
    private Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Initialize views
        spinnerSchool = findViewById(R.id.spinnerSchool);
        spinnerBuilding = findViewById(R.id.spinnerBuilding);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);

        // Set spinner listeners
        spinnerSchool.setOnItemSelectedListener(this);
        spinnerBuilding.setOnItemSelectedListener(this);

        // Set up spinner adapters
        ArrayAdapter<CharSequence> schoolAdapter = ArrayAdapter.createFromResource(this,
                R.array.school_names, android.R.layout.simple_spinner_item);
        schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSchool.setAdapter(schoolAdapter);

        ArrayAdapter<CharSequence> buildingAdapter = ArrayAdapter.createFromResource(this,
                R.array.building_names, android.R.layout.simple_spinner_item);
        buildingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBuilding.setAdapter(buildingAdapter);

        // Get current date
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());

        // Set current date as the selected date
        textViewSelectedDate.setText(currentDate);

        // Set button click listener for image upload
        Button buttonUploadImage = findViewById(R.id.buttonUploadImage);
        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle image upload button click event
                Toast.makeText(MainActivity2.this, "Image upload functionality not implemented yet.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set text view click listener for date picker
        textViewSelectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
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
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle case when no item is selected
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