package com.example.pwdschool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageDescriptionAdapter adapter;
    private List<ImageDescriptionModel> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        // Find views by their IDs
        TextView schoolNameTextView = findViewById(R.id.schoolNameTextView);
        recyclerView = findViewById(R.id.recyclerView);

        // Set school name and date
        schoolNameTextView.setText("Your School Name");


        // Initialize the RecyclerView and its adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();
        adapter = new ImageDescriptionAdapter(dataList);
        recyclerView.setAdapter(adapter);

        // Fetch data from the server and populate the list
        // For example, you can use Retrofit or Volley to make API calls

// After fetching data from the server and adding to dataList:
        dataList.add(new ImageDescriptionModel("https://picsum.photos/id/237/100/100", "Building A", "2023-08-05", "Description 1"));
        dataList.add(new ImageDescriptionModel("https://picsum.photos/id/235/100/100", "Building B", "2023-08-06", "Description 2"));


        // Add more items as needed...

        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();

    }
}
