package com.example.photogalleryapp.receiver;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;

import com.example.photogalleryapp.util.ImgDecoder;
import com.example.photogalleryapp.util.IntToDay;

import java.io.File;
import java.io.IOException;

public class SetWallpaperReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO code executed on alarm trigger

        IntToDay.getCurrentDay();
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "WALLPAPER");
        File[] fList = file.listFiles();
        File wallpaper_file = null;
        if (fList != null) {
            for (File f : fList) {
                if (f.getName().charAt(0) == ((char) (IntToDay.getCurrentDay() + '0'))) {
                    wallpaper_file = f;
                    break;
                }
            }
        }
        if (wallpaper_file != null) {
            try {
                WindowManager windowManager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
                if (windowManager != null) {
                    Display display = windowManager.getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int height = size.x;
                    int width = size.y;
                    Bitmap bitmap = ImgDecoder.decode(wallpaper_file.getAbsolutePath(), width, height);
                    WallpaperManager manager = WallpaperManager.getInstance(context);
                    manager.setBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
