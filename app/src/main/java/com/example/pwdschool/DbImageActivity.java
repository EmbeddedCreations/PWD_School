package com.example.pwdschool;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DbImageActivity extends Fragment {

    private RecyclerView recyclerView;
    private LocalDbAdapter adapter;
    private List<ImageDescriptionModel> dataList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_loacaldb, container, false);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UploadDatabaseHelper dbHelper = new UploadDatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        recyclerView = requireView().findViewById(R.id.localDbRecyclerView);

        // Perform the query
        String query = "SELECT " + UploadDatabaseHelper.COLUMN_SCHOOL_NAME + ","
                + UploadDatabaseHelper.COLUMN_PO_OFFICE + ","
                + UploadDatabaseHelper.COLUMN_JE + ","
                + UploadDatabaseHelper.COLUMN_BUILDING_NAME + ","
                + UploadDatabaseHelper.COLUMN_DATE_ADDED + ","
                + UploadDatabaseHelper.COLUMN_DESC + ","
                + UploadDatabaseHelper.COLUMN_IMG + " FROM uploads WHERE junior_engg = '"+Home.juniorEngineer+"'";
        Cursor cursor = db.rawQuery(query, null);
        dataList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String image = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_IMG));
            String buildingName = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_BUILDING_NAME));
            String Date = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_DATE_ADDED));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(UploadDatabaseHelper.COLUMN_DESC));
            dataList.add(new ImageDescriptionModel(image, buildingName,Date,description));
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new LocalDbAdapter(dataList);
        recyclerView.setAdapter(adapter);
        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();
    }
}
