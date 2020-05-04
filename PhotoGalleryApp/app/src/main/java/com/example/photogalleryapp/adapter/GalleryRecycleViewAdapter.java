package com.example.photogalleryapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import com.example.photogalleryapp.util.ImgDecoder;
import com.example.photogalleryapp.util.PhotoInfo;

import java.lang.ref.WeakReference;

public class GalleryRecycleViewAdapter
        extends RecyclerView.Adapter<GalleryRecycleViewAdapter.ViewHolder> {

    private SparseArrayCompat<PhotoInfo> list;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public GalleryRecycleViewAdapter(Context context, SparseArrayCompat<PhotoInfo> list) {
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.gallery_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PhotoInfo info = list.valueAt(position);
        holder.setKey(position);
        holder.text_caption.setText(info.getFilename());
        new DecodeTask(holder.image_thumbnail, info.getFilepath()).execute();
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return list.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView text_caption;
        ImageView image_thumbnail;
        int key;

        ViewHolder(View itemView) {
            super(itemView);
            text_caption = itemView.findViewById(R.id.text_caption);
            image_thumbnail = itemView.findViewById(R.id.image_thumbnail);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, key);
        }

        void setKey(int key) {
            this.key = key;
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int index);
    }
}