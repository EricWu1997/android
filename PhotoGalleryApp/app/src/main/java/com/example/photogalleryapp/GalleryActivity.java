package com.example.photogalleryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.photogalleryapp.adapter.GalleryRecycleViewAdapter;
import com.example.photogalleryapp.fragment.SearchFragment;
import com.example.photogalleryapp.manager.PhotoManager;
import com.example.photogalleryapp.util.PhotoInfo;

import java.util.Date;

public class GalleryActivity extends AppCompatActivity
        implements GalleryRecycleViewAdapter.ItemClickListener, SearchFragment.OnInputListener {

    private PhotoManager photoManager;
    private RecyclerView recyclerView;
    private SparseArrayCompat<PhotoInfo> list;
    private GalleryRecycleViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hide the title and title bar
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getSupportActionBar().hide();

        //show the activity in full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_gallery);

        photoManager = PhotoManager.getInstance();
        list = photoManager.getList().clone();

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GalleryRecycleViewAdapter(this, list);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        Button button_filter = findViewById(R.id.button);
        button_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        SearchFragment searchInputFragment = new SearchFragment();
                        searchInputFragment.show(getSupportFragmentManager(), "Search Fragment Popup");
                    }
                });
            }
        });

        Button button_remove = findViewById(R.id.button2);
        button_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        photoManager.removeFilter();
                        list = photoManager.getList();
                        updateRecyclerView();
                    }
                });
            }
        });

        Button button_back = findViewById(R.id.button3);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void updateRecyclerView() {
        adapter = new GalleryRecycleViewAdapter(
                this, list);
        adapter.setClickListener(this);
        // Need to change this, this "swapping" is way too stupid
        recyclerView.swapAdapter(adapter, false);
    }

    @Override
    public void onItemClick(View view, int index) {
        photoManager.setCurrentIndexTo(index);
        finish();
    }

    @Override
    public void onFilterSet(String keyword, Date start, Date end) {
        photoManager.applyFilter(keyword, start, end);
        list = photoManager.getList();
        updateRecyclerView();
    }
}
