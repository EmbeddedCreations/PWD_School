package com.example.pwdschool;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SchoolClassAdapter extends RecyclerView.Adapter<SchoolClassAdapter.ViewHolder> {

    private List<SchoolClass> SchoolList;


    SchoolClassAdapter(List<SchoolClass> SchoolList){
        this.SchoolList = SchoolList;
    }
    @NonNull
    @Override
    public SchoolClassAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchoolClassAdapter.ViewHolder holder, int position) {
        SchoolClass school = SchoolList.get(position);
        holder.schoolName.setText(school.getSchoolName());
        holder.buildingNames.setText(school.getBuildingName());

        holder.schoolCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                DisplaySchool.selectedSchoolHistory = school.getSchoolName();
                Intent i = new Intent(context,ImageActivity.class);
                context.startActivity(i);
                Toast.makeText(context,"Hello konichiwa",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return SchoolList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView schoolName;
        TextView buildingNames;
        FrameLayout schoolCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            schoolName = itemView.findViewById(R.id.SchoolNameTextView);
            buildingNames = itemView.findViewById(R.id.BuildingNamesTextView);
            schoolCard = itemView.findViewById(R.id.schoolCard);
        }
    }
}
