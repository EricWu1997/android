package com.example.photogalleryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.SparseArrayCompat;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.photogalleryapp.manager.PhotoManager;
import com.example.photogalleryapp.util.ImgDecoder;
import com.example.photogalleryapp.util.PhotoInfo;

import java.io.IOException;

public class CarouselActivity extends AppCompatActivity {

    private Handler handler;
    private Runnable runnableCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //show the activity in full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_carousel);

        long interval = getIntent().getLongExtra("INTERVAL", 2000);
        Log.d("TEST", "" + interval);
        startCarousel(interval);
    }

    private void startCarousel(final long interval) {
        PhotoManager photoManager = PhotoManager.getInstance();
        final SparseArrayCompat<PhotoInfo> list = photoManager.getList();
        final ImageView view = findViewById(R.id.imageView);
        WindowManager windowManager = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE));
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int height = size.x;
        final int width = size.y;
        handler = new Handler();
        runnableCode = new Runnable() {
            int index = 0;

            @Override
            public void run() {
                try {
                    view.setImageBitmap(ImgDecoder.decode(list.valueAt(index).getFilepath(), width, height));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                index = (++index) % list.size();
                handler.postDelayed(this, interval);
            }
        };
        handler.post(runnableCode);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnableCode);
    }
}
