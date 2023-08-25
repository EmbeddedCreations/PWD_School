package com.example.pwdschool;

import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplaySchool extends AppCompatActivity {


    String address = "https://www.embeddedcreation.in/tribalpwd/adminPanelNewVer2/appFetchSchools.php?user="+Home.juniorEngineer;

    public static String selectedSchoolHistory;

    InputStream is;
    String line;
    String result;
    Map<String, String> schoolBuildingsMap = new HashMap<>();
    private RecyclerView recyclerView;
    private SchoolClassAdapter schoolClassAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_schools);
        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        getData();

        List<SchoolClass> Schools = new ArrayList<>();
        for (Map.Entry<String, String> entry : schoolBuildingsMap.entrySet()) {

            Schools.add(new SchoolClass(entry.getKey(), entry.getValue()));
        }
        recyclerView = findViewById(R.id.schoolRecyclerView);
        schoolClassAdapter = new SchoolClassAdapter(Schools);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(schoolClassAdapter);

    }

    public void getData() {
        try {
            URL url = new URL(address);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            is = new BufferedInputStream(con.getInputStream());

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "/n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONArray js = new JSONArray(result);
            JSONObject jo = null;
            for (int i = 0; i < js.length(); i++) {
                jo = js.getJSONObject(i);
                String school = jo.getString("school_name");
                String building = jo.getString("image_name");
                if (schoolBuildingsMap.containsKey(school)) {
                    String existingBuildings = schoolBuildingsMap.get(school);
                    schoolBuildingsMap.put(school, existingBuildings + ", " + building);
                } else {
                    schoolBuildingsMap.put(school, building);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
