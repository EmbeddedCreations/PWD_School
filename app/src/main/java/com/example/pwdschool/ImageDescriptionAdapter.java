package com.example.pwdschool;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageDescriptionAdapter extends RecyclerView.Adapter<ImageDescriptionAdapter.ViewHolder> {

    private List<ImageDescriptionModel> dataList;

    public ImageDescriptionAdapter(List<ImageDescriptionModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_description, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageDescriptionModel model = dataList.get(position);

        // Load image using Picasso or your preferred image-loading library
        Log.d("url",model.getImageUrl());
        Picasso.get()
                .load(model.getImageUrl())
                .placeholder(R.drawable.upload) // Placeholder image from drawable
                .error(R.drawable.imgnotfound) // Image to show if loading from URL fails
                .into(holder.imageView);
       // holder.Picasso.get().load(model.getImageUrl()).into(holder.imageView);
        holder.descriptionTextView.setText("Description: " + model.getDescription());
        holder.Date.setText("Upload Date: "+ model.getDate());
        holder.BuildingName.setText("Building Name: " + model.getBuildingName());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView descriptionTextView;
        TextView BuildingName;
        TextView Date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            BuildingName = itemView.findViewById(R.id.buildingNameTextView);
            Date = itemView.findViewById(R.id.dateTextView);
        }
    }
}

