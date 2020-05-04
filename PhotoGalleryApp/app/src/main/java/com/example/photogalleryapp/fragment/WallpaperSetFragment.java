package com.example.photogalleryapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photogalleryapp.R;
import com.example.photogalleryapp.adapter.WallpaperSelectRecycleViewAdapter;
import com.example.photogalleryapp.manager.PhotoManager;
import com.example.photogalleryapp.util.PhotoInfo;

public class WallpaperSetFragment extends DialogFragment implements WallpaperSelectRecycleViewAdapter.ItemClickListener {

    private OnInputListener onInputListener;


    public interface OnInputListener {
        void onPhotoSelected(PhotoInfo info);
    }

    @Override
    public void onItemClick(PhotoInfo info) {
        onInputListener.onPhotoSelected(info);
        dismiss();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.setwallpaper_popup, container, false);

        PhotoManager photoManager = PhotoManager.getInstance();
        SparseArrayCompat<PhotoInfo> list = photoManager.getList();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        WallpaperSelectRecycleViewAdapter adapter =
                new WallpaperSelectRecycleViewAdapter(view.getContext(), list);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onInputListener = (OnInputListener) getActivity();
        } catch (ClassCastException e) {
            Log.e("onAttach", "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
