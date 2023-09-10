package com.example.pwdschool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class WorkOrderCheckSheet extends Fragment implements AdapterView.OnItemSelectedListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workorder_checksheet, container, false);

        // Example values, replace with your actual input values
        String[] columnHeadings = {"Month1", "Month 2", "Month 3", "Month 4"};
        String[] rowHeadings = {"work 1 schedule", "work 1 progress", "work 2 schedule", "work 2 progress", "work 3 schedule", "work 3 progress", "work 4 schedule", "work 4 progress", "work 5 schedule", "work 5 progress", "work 6 schedule", "work 6 progress"};
        int numRows = rowHeadings.length;
        int numCols = columnHeadings.length;

        // Example checkboxStates array
        int[][] checkboxStates = {
                {1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        };

        createDynamicTable(view, numRows, numCols, columnHeadings, rowHeadings, checkboxStates);

        return view;
    }

    private void createDynamicTable(View view, int numRows, int numCols, String[] colLabels, String[] rowLabels, int[][] checkboxStates) {
        TableLayout tableLayout = view.findViewById(R.id.tableLayout);

        // Create header row with column labels and checkboxes
        TableRow headerRow = new TableRow(requireContext());
        for (int j = 0; j <= numCols; j++) {
            TextView textView = new TextView(requireContext());
            textView.setText(j == 0 ? "" : colLabels[j - 1]);
            headerRow.addView(textView);

            // Add three more checkboxes under each column in the header
            for (int k = 0; k < 3; k++) {
                CheckBox checkBox = new CheckBox(requireContext());
                checkBox.setVisibility(View.INVISIBLE);
                headerRow.addView(checkBox);
            }
        }
        tableLayout.addView(headerRow);

        // Create rows with labels and checkboxes
        for (int i = 1; i <= numRows; i++) {
            TableRow row = new TableRow(requireContext());

            for (int j = 0; j <= numCols; j++) {
                if (j == 0) {
                    // Add row label (without checkbox)
                    TextView textView = new TextView(requireContext());
                    textView.setText(rowLabels[i - 1]);
                    row.addView(textView);
                } else {
                    // Add four checkboxes in the data cells (including the new row)
                    for (int k = 0; k < 4; k++) {
                        final int checkboxIndex = (j - 1) * 4 + k; // Make 'j' effectively final
                        final int columnIndex = j; // Make 'j' effectively final
                        final int checkboxColumnIndex = k; // Make 'k' effectively final

                        CheckBox checkBox = new CheckBox(requireContext());

                        // Check if the checkbox should be checked based on the checkboxStates array
                        checkBox.setChecked(checkboxStates[i - 1][checkboxIndex] == 1);

                        // Add an OnCheckedChangeListener to prevent unchecking and update the array
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (!isChecked) {
                                    // If the checkbox is unchecked, force it to be checked
                                    checkBox.setChecked(true);
                                    return;
                                }
                                // Update the corresponding value in the checkboxStates array
                                int rowIndex = tableLayout.indexOfChild(row);
                                int checkboxArrayIndex = (columnIndex - 1) * 4 + checkboxColumnIndex;
                                checkboxStates[rowIndex - 1][checkboxArrayIndex] = isChecked ? 1 : 0;

                                // Print the updated array to the console
                                for (int i = 0; i < numRows; i++) {
                                    for (int j = 0; j < numCols * 4; j++) {
                                        System.out.print(checkboxStates[i][j] + " ");
                                    }
                                    System.out.println(); // Move to the next row
                                }
                            }
                        });


                        // Set checkboxes in even rows to be disabled
                        if ((i - 1) % 2 == 0) {
                            checkBox.setEnabled(false);
                        }

                        row.addView(checkBox);
                    }
                }
            }




            tableLayout.addView(row);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            showLandscapeModeDialog();
            // Lock the orientation to portrait
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void showLandscapeModeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Landscape Mode");
        builder.setMessage("This app works best in portrait mode. Please rotate your device back to portrait mode.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Implement your item selection logic here
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Implement your nothing selected logic here
    }
}
