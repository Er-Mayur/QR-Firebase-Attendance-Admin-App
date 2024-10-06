package com.ermayurmahajan.mcoeadminapp;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MediaRecyclerAdapter extends RecyclerView.Adapter<MediaRecyclerAdapter.MediaViewHolder> {

    private List<Uri> mediaUris;

    public MediaRecyclerAdapter(List<Uri> mediaUris) {
        this.mediaUris = mediaUris;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_card_view, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        Uri mediaUri = mediaUris.get(position);
        holder.bind(mediaUri);
    }

    @Override
    public int getItemCount() {
        return mediaUris.size();
    }

    // ViewHolder class to bind each media item
    static class MediaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMediaName;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMediaName = itemView.findViewById(R.id.tv_media_name);
        }

        public void bind(Uri mediaUri) {
            // Extract and display the file name
            String fileName = mediaUri.getLastPathSegment();
            tvMediaName.setText(fileName != null ? fileName : "Unknown file");
        }
    }
}
