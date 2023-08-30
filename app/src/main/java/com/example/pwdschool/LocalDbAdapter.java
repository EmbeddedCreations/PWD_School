package com.example.pwdschool;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class LocalDbAdapter extends RecyclerView.Adapter<ImageDescriptionAdapter.ViewHolder>{

    private final List<ImageDescriptionModel> dataList;

    public LocalDbAdapter(List<ImageDescriptionModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ImageDescriptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_description, parent, false);
        return new ImageDescriptionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageDescriptionAdapter.ViewHolder holder, int position) {
        ImageDescriptionModel model = dataList.get(position);

        // Load image using Picasso or your preferred image-loading library
        Log.d("url", model.getImageUrl());
        byte[] decodedImage = Base64.decode(model.getImageUrl(), Base64.DEFAULT);

        // Create a Bitmap from the byte array
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);

        holder.imageView.setImageBitmap(decodedBitmap);
        holder.descriptionTextView.setText("Description: " + model.getDescription());
        holder.Date.setText("Upload Date: " + model.getDate());
        holder.BuildingName.setText("Building Name: " + model.getBuildingName());

        holder.imageView.setOnClickListener(v -> {
            // Create a dialog to display the enlarged image
            Dialog dialog = new Dialog(holder.itemView.getContext());
            dialog.setContentView(R.layout.dialog_enlarged_image);

            ImageView enlargedImageView = dialog.findViewById(R.id.enlargedImageView);
            enlargedImageView.setImageBitmap(decodedBitmap);

            // Handle the close button click to dismiss the dialog
            ImageButton closeButton = dialog.findViewById(R.id.closeImageButton);
            closeButton.setOnClickListener(v1 -> dialog.dismiss());

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
