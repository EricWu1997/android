package com.example.photogalleryapp.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class DecodeTask extends AsyncTask<Void, Void, Bitmap> {

    private final WeakReference<ImageView> imageViewReference;
    private String filepath;

    public DecodeTask(ImageView imageView, String filepath) {
        imageViewReference = new WeakReference<>(imageView);
        this.filepath = filepath;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            return ImgDecoder.decode(filepath, 150, 150);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
