package com.example.photogalleryapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photogalleryapp.fragment.NumberPickerDialog;
import com.example.photogalleryapp.manager.CameraManager;
import com.example.photogalleryapp.manager.PhotoManager;
import com.example.photogalleryapp.util.DateParser;
import com.example.photogalleryapp.util.FileNameParser;
import com.example.photogalleryapp.util.ImgDecoder;
import com.example.photogalleryapp.util.PhotoInfo;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private float x1, x2, y1, y2;
    private int displayWidth;
    private int displayHeight;

    private PhotoManager photoManager;
    private CameraManager cameraManager;

    private ImageView image_mainDisplay;
    private TextView text_caption;
    private TextView text_timeStamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Debug.startMethodTracing("sample");
        super.onCreate(savedInstanceState);

        //show the activity in full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        // Find display dimension
        image_mainDisplay = findViewById(R.id.image_mainDisplay);

        // Caption
        text_caption = findViewById(R.id.textView);

        // Time Stamp
        text_timeStamp = findViewById(R.id.textView2);

        cameraManager = new CameraManager(this);

        photoManager = PhotoManager.getInstance();
        photoManager.readFromFolder(getBaseContext());
        Debug.stopMethodTracing();
    }

    private void inflateRenameDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.rename_dialog);
        dialog.setTitle("Enter new name");
        final EditText editText = dialog.findViewById(R.id.editText);
        editText.setText(photoManager.currentPhoto().getFilename());
        Button button_confirm = dialog.findViewById(R.id.button4);
        button_confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String new_name = editText.getText().toString();
                if (!new_name.matches("^[a-zA-Z0-9]*$")) {
                    Toast.makeText(getApplicationContext(), "only alphanumeric allowed", Toast.LENGTH_SHORT).show();
                } else {
                    if (new_name.equals(""))
                        new_name = "UNTITLED";
                    String filepath = photoManager.currentPhoto().getFilepath();
                    String new_filepath
                            = FileNameParser.newPathWithName(filepath, new_name, '/', '-');
                    if (new_filepath == null) {
                        Toast.makeText(getApplicationContext(), "Rename failed, unsupported filename", Toast.LENGTH_SHORT).show();
                    } else {
                        File file = new File(filepath);
                        File newFile = new File(new_filepath);
                        if (file.renameTo(newFile)) {
                            photoManager.updateActivePhotoInfo(new_filepath, new_name);
                            updateDisplay(photoManager.currentPhoto());
                            Toast.makeText(getApplicationContext(), "Rename success!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Rename failed ..", Toast.LENGTH_SHORT).show();
                        }
                    }
                    dialog.dismiss();
                }
            }
        });
        Button button_cancel = dialog.findViewById(R.id.button5);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void inflateRemoveConfirmDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.remove_confirm_dialog);
        Button button_confirm = dialog.findViewById(R.id.button_confirm);
        button_confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                File file = new File(photoManager.currentPhoto().getFilepath());
                boolean deleted = file.delete();
                photoManager.deleteCurrentPhoto();
                updateDisplay(photoManager.currentPhoto());
                if (deleted) {
                    Toast.makeText(getApplicationContext(), "Photo deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to delete photo", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        Button button_cancel = dialog.findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void inflateNumberPickerDialog() {
        NumberPickerDialog newFragment = new NumberPickerDialog(1, 10);
        newFragment.setValueChangeListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Intent intent = new Intent(getApplicationContext(), CarouselActivity.class);
                long interval = newVal * 1000;
                intent.putExtra("INTERVAL", interval);
                startActivity(intent);
            }
        });
        newFragment.show(getSupportFragmentManager(), "interval picker");
    }

    private void updateDisplay(PhotoInfo photoInfo) {
        if (photoInfo != null) {
            try {
                image_mainDisplay.setImageBitmap(ImgDecoder.decode(photoInfo.getFilepath(),
                        displayWidth,
                        displayHeight));
            } catch (Exception e) {

                e.printStackTrace();
            }
            text_caption.setText(photoInfo.getFilename());
            text_timeStamp.setText(DateParser.parseDate(photoInfo.getTimeStamp()));
        } else {
            text_caption.setText("");
            text_timeStamp.setText("");
            image_mainDisplay.setImageResource(android.R.drawable.ic_menu_report_image);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDisplay(photoManager.currentPhoto());
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            displayHeight = image_mainDisplay.getWidth();
            displayWidth = image_mainDisplay.getHeight();
            updateDisplay(photoManager.currentPhoto());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 < x2) {
                    updateDisplay(photoManager.nextPhoto());
                } else if (x1 > x2) {
                    updateDisplay(photoManager.prevPhoto());
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.item_search:
                intent = new Intent(this, GalleryActivity.class);
                startActivity(intent);
                return true;

            case R.id.item_camera:
                cameraManager.dispatchTakePictureIntent();
                return true;

            case R.id.item_edit:
                inflateRenameDialog();
                return true;

            case R.id.item_remove:
                if (photoManager.getListSize() != 0) {
                    inflateRemoveConfirmDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "Nothing to remove", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.item_carousel:
                if (photoManager.getListSize() != 0) {
                    inflateNumberPickerDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "0 image found, cant start carousel", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.item_wallpaper:
                intent = new Intent(this, WallpaperActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Display photo upon picture taken
        if (requestCode == CameraManager.REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                PhotoInfo temp;
                if ((temp = cameraManager.getLastTakenPicture()) != null) {
                    photoManager.addToList(temp);
                    updateDisplay(photoManager.lastPhoto());
                }
            } else {
                File file = new File(cameraManager.getLastTakenPicture().getFilepath());
                boolean deleted = file.delete();
                if (!deleted) {
                    Toast.makeText(getApplicationContext()
                            , "Error, Temp file not removed"
                            , Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
