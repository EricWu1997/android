package com.example.photogalleryapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photogalleryapp.R;
import com.example.photogalleryapp.util.DecodeTask;
import com.example.photogalleryapp.util.PhotoInfo;

public class WallpaperSelectRecycleViewAdapter extends RecyclerView.Adapter<WallpaperSelectRecycleViewAdapter.ViewHolder> {

    private SparseArrayCompat<PhotoInfo> list;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public WallpaperSelectRecycleViewAdapter(Context context, SparseArrayCompat<PhotoInfo> list) {
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.wallpaperselect_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PhotoInfo info = list.valueAt(position);
        holder.text_caption.setText(list.valueAt(position).getFilename());
        new DecodeTask(holder.image_thumbnail, info.getFilepath()).execute();
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null)
                    mClickListener.onItemClick(info);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_caption;
        ImageView image_thumbnail;
        View view;

        ViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            text_caption = itemView.findViewById(R.id.text_caption);
            image_thumbnail = itemView.findViewById(R.id.image_thumbnail);
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(PhotoInfo info);
    }
}
