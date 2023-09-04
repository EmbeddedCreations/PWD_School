package com.example.pwdschool;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class WorkOrderCheckSheet extends Fragment implements AdapterView.OnItemSelectedListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workorder_checksheet, container, false);

        // Example values, replace with your actual input values
        int numRows = 3;
        int numCols = 4;
        String[] columnHeadings = {"Month1", "Month 2", "Month 3", "Month 4"};
        String[] rowHeadings = {"work 1", " work 2", "work 3"};

        createDynamicTable(view, numRows, numCols, columnHeadings, rowHeadings);

        return view;
    }

    private void createDynamicTable(View view, int numRows, int numCols, String[] colLabels, String[] rowLabels) {
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
                    // Add four checkboxes in the data cells
                    for (int k = 0; k < 4; k++) {
                        CheckBox checkBox = new CheckBox(requireContext());
                        row.addView(checkBox);
                    }
                }
            }

            tableLayout.addView(row);
        }
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
