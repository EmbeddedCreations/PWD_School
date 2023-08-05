package com.example.pwdschool;

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
        Picasso.get().load(model.getImageUrl()).into(holder.imageView);
        holder.descriptionTextView.setText(model.getDescription());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView descriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}

