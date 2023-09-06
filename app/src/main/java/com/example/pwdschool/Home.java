package com.example.pwdschool;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Home extends Fragment implements AdapterView.OnItemSelectedListener {

    public static String selectedSchool;
    public static String selectedWorkorder;
    public static String selectedBuilding;
    public static String selectedDate;
    public static String juniorEngineer;
    public static String atcOffice;
    public static String poOffice;
    public static String[] schools;
    public static String[] school_id;
    public static String[] all_buildings;
    public static String[] schoolIDBuilding;
    private static String[] schoolNames = {"Select School"};
    private static String[] buildingNames = {"Select Building"};
    private final String[] workorderNames = {"Select Workorder", "General Inspection", "Workorder related Inspection"};
    public ArrayList<String> buildings;
    private Spinner spinnerSchool;
    private Spinner spinnerBuilding;
    private Spinner spinnerWorkorder;
    private TextView textViewSelectedDate;
    private Calendar calendar;
    private Button buttonSurvey;
    private ImageView status;
    private NetworkStatusUtility networkStatusUtility;
    public static int dbCount;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);
        return view;
    }
    UploadDatabaseHelper dbHelper = new UploadDatabaseHelper(getContext());


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        UploadDatabaseHelper dbHelper = new UploadDatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM uploads WHERE junior_engg = '" + Home.juniorEngineer + "'";
        Cursor countCursor = db.rawQuery(query, null);

        if (countCursor.moveToFirst()) {
            dbCount = countCursor.getInt(0);
        }

        countCursor.close();
        status = requireView().findViewById(R.id.statusIcon);
        networkStatusUtility = new NetworkStatusUtility(requireContext());
        if (networkStatusUtility.isNetworkAvailable()) {
            status.setImageResource(R.drawable.online);
        } else {
            status.setImageResource(R.drawable.offline);
        }
        networkStatusUtility.startMonitoringNetworkStatus(new NetworkStatusUtility.NetworkStatusListener() {
            @Override
            public void onNetworkAvailable() {
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
                status.setImageResource(R.drawable.offline);
                status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showToast("Offline");
                    }
                });
            }
        });

        spinnerSchool = requireView().findViewById(R.id.spinnerSchool);
        spinnerBuilding = requireView().findViewById(R.id.spinnerBuilding);
        spinnerWorkorder = requireView().findViewById(R.id.spinnerWorkorder);
        textViewSelectedDate = requireView().findViewById(R.id.textViewSelectedDate);
        buttonSurvey = requireView().findViewById(R.id.buttonSurvey);
        TextView textViewLoggedIn = requireView().findViewById(R.id.textViewLoggedIn);
        TextView textViewAtc = requireView().findViewById(R.id.atc);
        TextView textViewPoOffice = requireView().findViewById(R.id.po);

        spinnerSchool.setOnItemSelectedListener(this);
        spinnerBuilding.setOnItemSelectedListener(this);
        spinnerWorkorder.setOnItemSelectedListener(this);

        ArrayList<String> tempSchoolList = new ArrayList<>();
        tempSchoolList.add("Select School");
        if (schools != null) {
            for (int i = 0; i < schools.length; i++) {
                tempSchoolList.add(schools[i]);
            }
        }

        schoolNames = tempSchoolList.toArray(new String[0]);

        ArrayAdapter<String> schoolAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, schoolNames);
        schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSchool.setAdapter(schoolAdapter);

        ArrayAdapter<String> workorderAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, workorderNames);
        workorderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkorder.setAdapter(workorderAdapter);

        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        textViewSelectedDate.setText(currentDate);

        juniorEngineer = Login.selectedJuniorEngineer;
        textViewLoggedIn.setText("Logged in as: " + juniorEngineer);

        atcOffice = Login.selectedAtcOffice;
        textViewAtc.setText("Atc Office: " + atcOffice);

        poOffice = Login.selectedPoOffice;
        textViewPoOffice.setText("PO Office: " + poOffice);

        buttonSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate = textViewSelectedDate.getText().toString();

                if (selectedSchool.equals("Select School")) {
                    showToast("Please select a school.");
                } else if (selectedBuilding.equals("Select Building")) {
                    showToast("Please select a building.");
                } else if (selectedWorkorder.equals("Select Workorder")) {
                    showToast("Please select a workorder.");
                } else {
                    Fragment uploadFragment = new Upload();
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    General.replaceFragment(fragmentManager, R.id.container, uploadFragment, true);
                }
            }
            private void showToast(String message) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinnerSchool:
                selectedSchool = parent.getItemAtPosition(position).toString();
                int index = 0;
                String ID = "";
                if (schools != null) {
                    for (int i = 0; i < schools.length; i++) {
                        if (selectedSchool.equals(schools[i])) {
                            index = i;
                            break;
                        }
                    }
                    if (school_id.length > 0) {
                        ID = school_id[index];
                    }
                }
                if (all_buildings != null) {
                    buildings = new ArrayList<>();
                    for (int i = 0; i < all_buildings.length; i++) {
                        if (schoolIDBuilding[i].equals(ID)) {
                            buildings.add(all_buildings[i]);
                        }
                    }
                }
                ArrayList<String> tempBuildings = new ArrayList<>();
                tempBuildings.add("Select Building");
                if (buildings != null) {
                    for (int i = 0; i < buildings.size(); i++) {
                        tempBuildings.add(buildings.get(i));
                    }
                }
                buildingNames = tempBuildings.toArray(new String[0]);
                ArrayAdapter<String> buildingAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, buildingNames);
                buildingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerBuilding.setAdapter(buildingAdapter);
                break;
            case R.id.spinnerBuilding:
                selectedBuilding = parent.getItemAtPosition(position).toString();
                break;
            case R.id.spinnerWorkorder:
                selectedWorkorder = parent.getItemAtPosition(position).toString();
                if (selectedWorkorder.equals("Workorder related Inspection")) {
                    TextView textViewSecondDropdownTitle = requireView().findViewById(R.id.textViewSecondDropdownTitle);
                    Spinner spinnerSecondDropdown = requireView().findViewById(R.id.spinnerSecondDropdown);
                    textViewSecondDropdownTitle.setVisibility(View.VISIBLE);
                    spinnerSecondDropdown.setVisibility(View.VISIBLE);
                    String[] workorderValues = {"Workorder 1", "Workorder 2", "Workorder 3"};
                    ArrayAdapter<String> secondDropdownAdapter = new ArrayAdapter<>(
                            requireContext(), android.R.layout.simple_spinner_item, workorderValues);
                    secondDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSecondDropdown.setAdapter(secondDropdownAdapter);

                    spinnerSecondDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String selectedSecondDropdownValue = parent.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                } else {
                    TextView textViewSecondDropdownTitle = requireView().findViewById(R.id.textViewSecondDropdownTitle);
                    Spinner spinnerSecondDropdown = requireView().findViewById(R.id.spinnerSecondDropdown);
                    textViewSecondDropdownTitle.setVisibility(View.GONE);
                    spinnerSecondDropdown.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        networkStatusUtility.stopMonitoringNetworkStatus();
        dbHelper.close();
    }

    private void showToast(String statusText) {
        Toast.makeText(requireContext(), statusText, Toast.LENGTH_SHORT).show();
    }
}
