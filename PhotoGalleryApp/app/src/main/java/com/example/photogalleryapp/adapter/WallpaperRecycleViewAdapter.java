package com.example.photogalleryapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photogalleryapp.R;
import com.example.photogalleryapp.util.IntToDay;

public class WallpaperRecycleViewAdapter extends RecyclerView.Adapter<WallpaperRecycleViewAdapter.ViewHolder> {
    private SparseArrayCompat<String> list;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public WallpaperRecycleViewAdapter(Context context, SparseArrayCompat<String> list) {
        this.mInflater = LayoutInflater.from(context);
        this.list = list;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.wallpaper_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String filename = list.get(position);

        if (filename == null) {
            holder.button_edit.setImageResource(android.R.drawable.ic_menu_add);
            filename = "NOT_SET";
        } else {
            holder.button_edit.setImageResource(android.R.drawable.ic_delete);
        }

        holder.text_day.setText(IntToDay.convert(position));
        holder.text_filename.setText(filename);
        holder.button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null)
                    mClickListener.onItemClick(position);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return list.size();
    }


    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_day;
        TextView text_filename;
        ImageButton button_edit;

        ViewHolder(View itemView) {
            super(itemView);
            text_day = itemView.findViewById(R.id.text_day);
            text_filename = itemView.findViewById(R.id.text_filename);
            button_edit = itemView.findViewById(R.id.button_edit);
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(int index);
    }
}
