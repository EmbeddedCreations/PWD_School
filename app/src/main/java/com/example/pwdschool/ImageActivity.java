package com.example.pwdschool;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

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
import java.util.List;

public class ImageActivity extends AppCompatActivity {

    private final String Address = "https://www.embeddedcreation.in/tribalpwd/adminPanelNewVer2/appFetchSchoolBuildings.php?user=" + Home.juniorEngineer + "&school=" + DisplaySchool.selectedSchoolHistory;
    String[] f_imgurl, f_buildingName, f_date, f_description;
    private RecyclerView recyclerView;
    private ImageDescriptionAdapter adapter;
    private List<ImageDescriptionModel> dataList;
    private InputStream is;
    private String line, result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        // Find views by their IDs
        TextView schoolNameTextView = findViewById(R.id.schoolNameTextView);
        recyclerView = findViewById(R.id.recyclerView);

        // Set school name and date
        schoolNameTextView.setText(DisplaySchool.selectedSchoolHistory);
        dataList = new ArrayList<>();

        // Initialize the RecyclerView and its adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImageDescriptionAdapter(dataList);
        recyclerView.setAdapter(adapter);

        // Start the AsyncTask to fetch data
        new FetchDataAsyncTask().execute(Address);
    }

    private void parseData(String result) {
        try {
            JSONArray js = new JSONArray(result);
            JSONObject jo = null;
            f_imgurl = new String[js.length()];
            f_date = new String[js.length()];
            f_buildingName = new String[js.length()];
            f_description = new String[js.length()];
            for (int i = 0; i < js.length(); i++) {
                jo = js.getJSONObject(i);
                f_imgurl[i] = jo.getString("image_pdf");
                if (f_imgurl[i].equals("")) {
                    f_imgurl[i] = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAK4AAACCCAMAAADovAORAAAAZlBMVEVYWFpYWFlXV1pXV1nz8/RcXF7Fxcb8/P1QUFL29vf5+fqzs7Svr7Hq6uvZ2dru7u+Ghoijo6RDQ0VLS016enw+PkHT09Tk5OVycnRoaGpiYmSRkZO9vb7Ly8x/f4CXl5g1NTcsLC9j9rKlAAAKiUlEQVR4nO1bi5KyOgxuG5SCCggKeP/P+7/k6b0p4gXYxXFmszsYPxKSljRNCxLyR3/kiYo/caTEHqnmMUzcyRCeX/NLifXwLDyDRT6nSYOPXvhe5AE8h2Ynjkw0ISVKn4jMqun/jYL+d4FOKZZyIpR+QpMSkCIgvjLTBBCfjILVFDBomDgYiBOZWZN8HdEgmnAq7MA0gOlHNFHypZgP2tPHfkRTNYWaBlueUmo+7mFyB8+p6VuFbgNOJ8QDdox2RGbWtKeB2Y43vDnXhc29gY9o2ltg+717RHfpucgsmriDXTQHN4uE96AjN7OmSL3MJmCm8jaTiAIEzBRMNUwQLP4+oUm+jOhXkRuAnRQSRNCdiPr4hKZKFfoK1GqZKY9h2CTtjr35NRGxXnbISuD3NT8ZicOJqDujXLe8m/YARQ2CnegnNJXPDPkPnmfQCyN+bk0ULJ0lqOI7MPMw+aTmN9GDypkMhGfStN/pPRrAiMei82qaHK1HISOoWJat06tQDCserP7cmmGr3KfiWdB0M+0Qc7HuqVk1KdKlIRvAb4j8vuaDmH7AD4N/WFPHkV5omrUQtRWHhQmCA5HZNaW/oERUjeSWR8RMO4ZXInITyFhCMA00wcNdTdrVHGNz0t0DPpymRoxeG+nxxzzU/WTkbh5ksF4NpW01zaZd7jHN4qOGmVFDsOUXUTyUmj2bYnPMPoCtkijZRFEyhKIk27+4+FObvuBx98QvkXWrrMg9DMLd5LJ8n7JEuMsm2FRRTO2+MDEzXpdnir0Tke7G7b58l/5tY9m7k2y6uCG+qkThRDowLjyVuzvOdIaxSUiFGLVrRZUpQRurlLtsik3fLIrnPp9CKGqiXZgSzRl3X2hyOO4OwEUCrUzvjrepXAc78BQvvDAjlHjeijAsYt19qlm1dRrH2Ykz27tsgs2hxXEoot19psnbJo1k/rhVVLv7+OJv2AQx1wGVR3EAzAa8Zrsi2l0Pc36nyZfSW+FvVBp370QG2CRTCHzvKqrWt6or0jaJcjeKT7zymWEs0W4mDrLyA9guqe1QM8TrdFeFqZ3vcuvutjLuvrj4M5vEpYnO/M3MKgRP3Pew713xle82aXbgBIvw+94142ukTeJawajjgbiKTwaMEyFYhLjY1Zp8nUZxvQCsCdATu1NsTqAwdukiEx0Zn8tARnRvLLxNo5PNDJNidwqF7sJO9WN8C/2t2kui8i4hPzTUdMKgKlVQsCw14wnQkYYiLpFJvjzH6q7nRUWxJifH9rAQUi4YxtvULps/YpZutiUeJmgl6mE0CYthUMZ6TCVNy0NNlaJJ2LsjbdIphBNZdTLuRmnGoVc8TGSjDBJT1KOKiAW8Y8k9bCoy9aWsTcYS/p73TgTAa7pJeLxNW8KjxOGL+qcw9YlMsPyaR47SbWU0ZT87Tde7o20+GIB3C7pewpmhWifeXTHcDNpeTmiSdr073qYJChscaH1EPNzHuYpMfoFjhtwVw+0o65WyaOJm4QLZ9u4km676AV8OoTJJphBwuSeEtbuS51fsrfS3FPXZSiwm4/PeaprehfE27cTnS3vqGLOb4mDL2w5xsSvL2nMcuCu8rA6ZwuKiMrkJx+44mwQlZ9MGQl3TPByIONYmMmA8CntX0LlJbWBIEb/4ub/g2zb99hkxLTEp+xFscrURsUONn+Kut2IBYfPEqtLPn8LVxEibbg4EcO3R0WNg3MwQtrFL981d5yJKT3qiRrE70ubrpPIkv7hExttn3kbJRhesaOE+1qZJwjba7eRsk7K7Sxj2kW+GWrl66q6e5lzvTrA5iWzvdpJuX/+eKvJqmniL6BQy7vLTc2dldjjwHylx3EdfEaWrYfcVHExwvQvsnL7yNz1zPNTG2tQ9rPOMfr8LLGuyNPFveJpM40RMMNDF5kUsiO6NC+7cnWDTha99Zq94/OopQyIhbNytTmn+BnFTQFI23qaB3JFSXML7sv4epq53+a54hw7gSpzxNifEvavI3nuGAj+zmuh0dZd9xDO0VnvTlondxxd8bRPUJrbcnGQg39SQgOLlOJT7lIJlCJYzoXx7TqxqmOzddPBmv9ydHmtT/gt1+UmZ5lWVphD5rp8QtbAUAaZ4DcvejdIBpB6lEBhvE9fxfpXkV0u9sFtBkTwd9OAnSWK1ez7aZjdQXscPIiDrwaQfA462Samr1s2OpErM+oGs4sGJkEBEsFANJzrFJnHrd7Pw1zsPpil9MMMwUS/Uml0A+RYrU0OCqn0Dubvo9wyo2ftk02waIZSeA7aXfwDPoKneMzOrC8qoW0bLcaiLT3Aidn2qnqGx15pktGaPTfX+MfE3SPc6Q18RjKUYU8/1QD/fEwdZrYDmpQGmILUWF/lH7edJVkqrvERUGmMwyCZxe+dDiajNAWVe+Ae8PXBY0MVioXKxRBbtUUCELGQXixOcH1rgUpqAagsIlPWVj69MDyY4xkUWH8tNct0v42vVZpt8WWVpk9+qchm1ZdwWUROtd3mabgre5vnmcqzzTcNXTdkmu7pZ7PJDLOCWv7b2iKgeeab+cc8xKBqQunVwTK51sm03+a7Mm1V5zo7H87E+Lw4LqM7xuUyvzbo6xQeoL6Jf280OqvVmQZrVttm3+e4Srw/NITkd66x61yb2kPnXIOybD5bvg7lwN1vW63Penpprw5v1/rI81EmetLw6Z1mb3KIFL6Pbv8tyz3jb5NG2PpflOttmyt1lvmsOUbE/5fCuTf/+hZ9Re459sOzdrNgkbdrWTR0L8+UtKurVf6WoEJfLYrMp8mJ/SK+iFSWFdnM9Vsus3F8u2+ZfG7X1bZtm0t1VU71r07OEUpvDiU7r1O77ethnfEq4iN2mWG54vI1ORV3vNstzUmSb8/LEy0v975K063zV1FWZXUoCbSTq9zZfnqP2Gq0uG8jW/202h7he6tXxWzZVhHQmZZyPe7K65YHeDqdDW/Db7lZV7a26rrbFodhut8KvoqgOtyOcVtsj8OIqSuGj+Eqq3Xp9reC6Wre82PH2tLhtt3IL+E2bnlXFm6pA3ZGqI8GwzqQ6uQIXf4LEB1FHuVKQR6byG3CqvhhNKUsMIiWpUUUir20aOVtABqntQcYbLTJQ8+kFfU8/Wm+8Xp7Mrcnck5cA78AMw+RjmoyEfL90WC3fw3Np9l2p544xdKU7+oTmV5CZf+3rcMzOy8QFjC3okYiD59acqVP+6CuIqc0eIms5UOtYub/DaAcG9Y6cereWYXhuTULNKVX1qi0p3QSil1OgRgC1ZSdTl9DwJzTJ0AnynTn0NzW/joKdMlkFQwBTe84WyKrqRVsW82pixW6/9+ZmFsCf0PwissUZI/bdXoL/GQtFHsCzabIeEYZgy1u4R2Q+zT+ah4b09P1YnktT/7ZBRYrKdvrnFfo9egUDsbBOjzq8VBacX7O/qW/MgAPn0Z/TDFRYeK5PpVdkNs2vIlVmyj1tuUWvspx8TKieKerKTZ7RvwsB/UsR84sR+ITmp3trBIUxjl40eCRCrMjsmsRWQMzwDM11zMM43J3IJzQt5qdpX0c4SczhL/Nr/tEvkt3xcbx/+NIPswCeWRP73c8Pg39bE76K5BPbL6L/AacCku9PjWVNAAAAAElFTkSuQmCC";
                }
                f_date[i] = jo.getString("user_upload_date");
                f_buildingName[i] = jo.getString("image_name");
                f_description[i] = jo.getString("Description");

                // Add Fetched Data to the data list
                dataList.add(new ImageDescriptionModel(f_imgurl[i], f_buildingName[i], f_date[i], f_description[i]));
            }

            // Notify the adapter that the data has changed
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // AsyncTask to fetch data in the background
    private class FetchDataAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String data = "";
            try {
                URL url = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                is = new BufferedInputStream(con.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line + "/n");
                }
                data = sb.toString();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            // Process the fetched data
            parseData(result);
        }
    }
}
