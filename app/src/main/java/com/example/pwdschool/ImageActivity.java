package com.example.pwdschool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageDescriptionAdapter adapter;
    private List<ImageDescriptionModel> dataList;
    private String Address ="http://192.168.1.2/appFetchSchoolBuildings.php?user="+Login.selectedJuniorEngineer+"&school="+DisplaySchool.selectedSchoolHistory;
    private InputStream is;
    private String line,result;
    String f_imgurl[],f_buildingName[],f_date[],f_description[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        getData();

        // Find views by their IDs
        TextView schoolNameTextView = findViewById(R.id.schoolNameTextView);
        recyclerView = findViewById(R.id.recyclerView);

        // Set school name and date
        schoolNameTextView.setText(DisplaySchool.selectedSchoolHistory);
        dataList = new ArrayList<>();
        //Add Fetched Data to the data list
        for(int i =0;i<f_buildingName.length;i++){
            dataList.add(new ImageDescriptionModel(f_imgurl[i],f_buildingName[i],f_date[i],f_description[i]));
        }


        // Initialize the RecyclerView and its adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ImageDescriptionAdapter(dataList);
        recyclerView.setAdapter(adapter);


        // Notify the adapter that the data has changed
        adapter.notifyDataSetChanged();

    }

    private void getData(){
        try {
            URL url = new URL(Address);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            is = new BufferedInputStream(con.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine())!= null){
                sb.append(line+"/n");
            }
            result = sb.toString();
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            JSONArray js = new JSONArray(result);
            JSONObject jo = null;
            f_imgurl = new String[js.length()];
            f_date = new String[js.length()];
            f_buildingName = new String[js.length()];
            f_description = new String[js.length()];
            for(int i =0;i< js.length();i++){
                jo = js.getJSONObject(i);
                f_imgurl[i] = jo.getString("image_pdf");
                if(f_imgurl[i].equals("")){
                    f_imgurl[i] = "https://www.google.com/url?sa=i&url=https%3A%2F%2Fblog.rahulbhutani.com%2Fif-not-found-that-image-then-call-default-image-on-error%2F&psig=AOvVaw2k-A-HaiJ_wuSRpbrlAuQi&ust=1691845482298000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCICp8N3V1IADFQAAAAAdAAAAABAD";
                }
                f_date[i] = jo.getString("user_upload_date");
                f_buildingName[i] = jo.getString("image_name");
                f_description[i] = jo.getString("Description");
//                Log.d("info",f_imgurl[i]+","f_date[i]='')
            }
        }catch (Exception e){

        }


    }
}
