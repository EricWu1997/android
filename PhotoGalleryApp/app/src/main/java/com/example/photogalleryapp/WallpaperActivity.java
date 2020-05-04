package com.example.photogalleryapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.photogalleryapp.adapter.WallpaperRecycleViewAdapter;
import com.example.photogalleryapp.fragment.WallpaperSetFragment;
import com.example.photogalleryapp.manager.PhotoManager;
import com.example.photogalleryapp.receiver.SetWallpaperReceiver;
import com.example.photogalleryapp.util.FileCopier;
import com.example.photogalleryapp.util.FileNameParser;
import com.example.photogalleryapp.util.ImgDecoder;
import com.example.photogalleryapp.util.IntToDay;
import com.example.photogalleryapp.util.PhotoInfo;
import com.example.photogalleryapp.util.WallpaperFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class WallpaperActivity extends AppCompatActivity
        implements WallpaperRecycleViewAdapter.ItemClickListener, WallpaperSetFragment.OnInputListener {

    private WallpaperRecycleViewAdapter adapter;
    private RecyclerView recyclerView;
    private SparseArrayCompat<String> list;

    // 0 1 2 3 4 5 6 { mon, tue, wed, thur, fir, sat }
    private int day_setting = -1;

    Context c = this;

    public WallpaperActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initScheduleToggle();

        list = loadListFromStorage();

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WallpaperRecycleViewAdapter(this, list);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        Button button = findViewById(R.id.button8);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private SparseArrayCompat<String> loadListFromStorage() {
        SparseArrayCompat<String> list = new SparseArrayCompat<>();
        File file = new File(this.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "WALLPAPER");

        for (int i = 0; i < 7; i++) {
            list.put(i, null);
        }

        boolean success = true;
        if (!file.exists()) {
            success = file.mkdirs();
        } else {
            File[] fList = file.listFiles();
            String temp;
            if (fList != null) {
                for (File f : fList) {
                    temp = f.getName();
                    if (FileNameParser.matchExtension(temp, ".jpg", ".png", ".jpeg")) {
                        int key = Character.getNumericValue(temp.charAt(0));
                        list.put(key, temp.substring(2));
                    }
                }
            }
        }
        return list;
    }

    private void initScheduleToggle() {
        final AlarmManager alarmMgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        final Intent receiverIntent = new Intent(c, SetWallpaperReceiver.class);

        ToggleButton toggle = findViewById(R.id.toggleButton);

        boolean alarmUp = (PendingIntent.getBroadcast(c, 1234,
                new Intent(c, SetWallpaperReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        toggle.setChecked(alarmUp);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT).show();
                    PendingIntent alarmIntent = PendingIntent.getBroadcast(c, 1234, receiverIntent, 0); //The second parameter is unique to this PendingIntent,

                    //if you want to make more alarms,
                    //make sure to change the 0 to another integer
//                    int hour = 16;
//                    int minute = 30;

                    int hour = 0;
                    int minute = 0;

                    Calendar alarmCalendarTime = Calendar.getInstance(); //Convert to a Calendar instance to be able to get the time in milliseconds to trigger the alarm
                    alarmCalendarTime.set(Calendar.HOUR_OF_DAY, hour);
                    alarmCalendarTime.set(Calendar.MINUTE, minute);
                    alarmCalendarTime.set(Calendar.SECOND, 0); //Must be set to 0 to start the alarm right when the minute hits 30

                    //Add a day if alarm is set for before current time, so the alarm is triggered the next day
                    if (alarmCalendarTime.before(Calendar.getInstance())) {
                        alarmCalendarTime.add(Calendar.DAY_OF_MONTH, 1);
                    }

                    // For testing, runs every 1 min
//                    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendarTime.getTimeInMillis(),
//                            1000 * 60 * 1, alarmIntent);
                    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                            alarmCalendarTime.getTimeInMillis(),
                            TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS), alarmIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(c, 1234, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT); //Remember that the second
                    pendingIntent.cancel();
                    alarmMgr.cancel(pendingIntent);
                }

                // Apply wallpaper for today
                applyWallpaperForToday();
            }
        });
    }

    private void applyWallpaperForToday() {
        File file = new File(this.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "WALLPAPER");
        File[] fList = file.listFiles();
        File wallpaper_file = null;
        if (fList == null) return;
        char day = ((char) (IntToDay.getCurrentDay() + '0'));
        for (File f : fList) {
            char f_day = f.getName().charAt(0);
            if (f_day == day) {
                wallpaper_file = f;
                break;
            }
        }

        WindowManager windowManager = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE));
        try {
            if (wallpaper_file != null && windowManager != null) {
                Display display = windowManager.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int height = size.x;
                int width = size.y;
                Bitmap bitmap = ImgDecoder.decode(wallpaper_file.getAbsolutePath(), width, height);
                WallpaperManager manager = WallpaperManager.getInstance(this);
                manager.setBitmap(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(int index) {
        if (list.valueAt(index) == null) {
            if (PhotoManager.getInstance().getListSize() == 0) {
                Toast.makeText(getApplicationContext()
                        , "No filtered image, make sure at least one image display in gallery"
                        , Toast.LENGTH_SHORT).show();
                return;
            }
            day_setting = index;
            WallpaperSetFragment wallpaperSetFragment = new WallpaperSetFragment();
            wallpaperSetFragment.show(getSupportFragmentManager(), "Select photo Popup");
        } else {
            File file = new File(this.getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES), "WALLPAPER");
            File[] fList = file.listFiles(new WallpaperFileFilter((char) (index + '0')));
            if (fList != null && fList.length != 0) {
                if (fList[0].delete()) {
                    list.put(index, null);
                    adapter.notifyItemChanged(index);
                }
            }
        }
    }

    @Override
    public void onPhotoSelected(PhotoInfo info) {
        if (day_setting != -1) {
            String filename = info.getFilename();
            File file = new File(info.getFilepath());
            File file2 = new File(this.getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES)
                    , "WALLPAPER/" + day_setting + "_" + filename + ".jpg");
            FileCopier.copy(file, file2);

            list.put(day_setting, filename + ".jpg");
            adapter.notifyItemChanged(day_setting);

            Toast.makeText(this,
                    IntToDay.convert(day_setting) + ": " + filename, Toast.LENGTH_SHORT).show();
        }
        day_setting = -1;
    }
}
