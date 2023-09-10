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

import java.util.Arrays;

public class WorkOrderCheckSheet extends Fragment implements AdapterView.OnItemSelectedListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workorder_checksheet, container, false);

        // Example values, replace with your actual input values
        String[] columnHeadings = {"Month1", "Month 2", "Month 3", "Month 4"};
        String[] rowHeadings = {"work 1 schedule", "work 1 progress", "work 2 schedule", "work 2 progress", "work 3 schedule", "work 3 progress", "work 4 schedule", "work 4 progress", "work 5 schedule", "work 5 progress", "work 6 schedule", "work 6 progress"};
        int numRows = rowHeadings.length;
        int numCols = columnHeadings.length;

// Specify which checkboxes to precheck for odd rows (e.g., columns 2 and 4)
        int[] precheckedColumns = {1,2, 4};

        createDynamicTable(view, numRows, numCols, columnHeadings, rowHeadings, precheckedColumns);


        return view;
    }

    private void createDynamicTable(View view, int numRows, int numCols, String[] colLabels, String[] rowLabels, int[] precheckedColumns) {
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
        for (int i = 0; i < numRows; i++) {
            TableRow row = new TableRow(requireContext());

            for (int j = 0; j <= numCols; j++) {
                if (j == 0) {
                    // Add row label (without checkbox)
                    TextView textView = new TextView(requireContext());
                    textView.setText(rowLabels[i]);
                    row.addView(textView);
                } else {
                    // Add four checkboxes in the data cells (including the new row)
                    for (int k = 0; k < 4; k++) {
                        CheckBox checkBox = new CheckBox(requireContext());

                        // Check if the current row index is odd and if the current checkbox column is in precheckedColumns
                        if (i % 2 == 0 && Arrays.binarySearch(precheckedColumns, j) >= 0) {
                            checkBox.setChecked(true);
                            checkBox.setEnabled(false);
                        } else {
                            // Add an OnTouchListener to block unchecking
                            checkBox.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                        CheckBox clickedCheckBox = (CheckBox) v;
                                        // If the checkbox is checked, display a toast and block unchecking
                                        if (clickedCheckBox.isChecked()) {
                                            Toast.makeText(requireContext(), "Checkbox is now checked and cannot be unchecked.", Toast.LENGTH_SHORT).show();
                                            return true; // Block touch events
                                        }
                                    }
                                    return false; // Allow touch events
                                }
                            });
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
